/**
 * 
 */
package jetsennet.jbmp.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.alarm.eventhandle.ObjCollStateEntity;
import jetsennet.jbmp.business.AlarmPubSub.AlarmSubEntity;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmInfoEntity;
import jetsennet.jbmp.entity.AlarmStatisticEntity;
import jetsennet.jbmp.entity.Group2GroupEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.servlets.BMPServletContextListener;
import jetsennet.jbmp.util.ThreeTuple;
import jetsennet.jbmp.util.TwoTuple;

/**
 * 告警统计类
 * 
 * @author lianghongjie 
 */
public final class AlarmStatistic
{

    private static final Logger logger = Logger.getLogger(AlarmStatistic.class);
    private static AlarmStatistic instance = new AlarmStatistic();

    /**
     * 数据库访问
     */
    private DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class);

    /**
     * 告警统计模型
     */
    private Map<Integer, AlarmStatisticEntity> objAlarmMap = new HashMap<Integer, AlarmStatisticEntity>();
    private Map<Integer, AlarmStatisticEntity> grpAlarmMap = new HashMap<Integer, AlarmStatisticEntity>();

    /**
     * 实时告警模型
     */
    private LinkedList<AlarmCacheEntity> curAlarmEvents = new LinkedList<AlarmCacheEntity>();
    private Map<Integer, LinkedList<AlarmCacheEntity>> curAlarmMap = new HashMap<Integer, LinkedList<AlarmCacheEntity>>();
    private long lastTime;

    /**
     * 读写锁
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private AlarmStatistic()
    {
    }

    /**
     * 单例模式
     * @return 单例
     */
    public static AlarmStatistic getInstance()
    {
        return instance;
    }

    /**
     * 初始化、集群恢复时调用，用于同步数据库中的对象模型、告警统计信息和实时告警列表
     * @throws Exception 异常
     */
    public void reset() throws Exception
    {
        logger.debug("AlarmStatistic reset begin.");

        ThreeTuple<Map<Integer, AlarmStatisticEntity>, Map<Integer, AlarmStatisticEntity>, List<AlarmInfoEntity>> tuple =
            this.refreshObjAndGroup(true);

        writeLock.lock();
        try
        {
            objAlarmMap = tuple.first;
            grpAlarmMap = tuple.second;
            ++lastTime;

            // 将数据库中的实时告警加载入内存
            curAlarmEvents.clear();
            curAlarmMap.clear();
            List<AlarmInfoEntity> aeEntityLst = tuple.third;
            for (AlarmInfoEntity entity : aeEntityLst)
            {
                AlarmCacheEntity cacheAlarm = new AlarmCacheEntity(++lastTime, entity);
                addAlarmEvent(entity.getObjId(), cacheAlarm);
            }
        }
        finally
        {
            writeLock.unlock();
        }

        broadcast("");
        logger.debug("AlarmStatistic reset end.");
    }

    /**
     * 周期刷新，在定时器中调用，用于同步数据库中的对象模型和告警统计信息
     * @throws Exception 异常
     */
    public void refresh() throws Exception
    {
        logger.debug("AlarmStatistic refresh begin.");

        ThreeTuple<Map<Integer, AlarmStatisticEntity>, Map<Integer, AlarmStatisticEntity>, List<AlarmInfoEntity>> tuple =
            this.refreshObjAndGroup(false);
        logger.debug("AlarmStatistic refresh update begin.");

        writeLock.lock();
        try
        {
            objAlarmMap = tuple.first;
            grpAlarmMap = tuple.second;
            ++lastTime;
        }
        finally
        {
            writeLock.unlock();
        }
        logger.debug("AlarmStatistic refresh update end.");

        broadcast("");
        logger.debug("AlarmStatistic refresh end.");
    }

    /**
     * 统计对象、对象组、对象报警信息
     * @return
     * @throws Exception
     */
    private ThreeTuple<Map<Integer, AlarmStatisticEntity>, Map<Integer, AlarmStatisticEntity>, List<AlarmInfoEntity>> refreshObjAndGroup(
            boolean freshAlarm) throws Exception
    {
        // 临时map
        Map<Integer, AlarmStatisticEntity> newObjAlarmMap = new HashMap<Integer, AlarmStatisticEntity>();
        Map<Integer, AlarmStatisticEntity> newGrpAlarmMap = new HashMap<Integer, AlarmStatisticEntity>();
        Map<Integer, Integer> obj2stateMap = new HashMap<Integer, Integer>();

        // 加载所有监控对象信息
        List<TwoTuple<Object, Object>> objLst = dal.getTwoList("SELECT OBJ_ID, RECEIVE_ENABLE FROM BMP_OBJECT");
        for (TwoTuple<Object, Object> obj : objLst)
        {
            int objId = Integer.valueOf(obj.first.toString());
            int collState = Integer.valueOf(obj.second.toString());
            newObjAlarmMap.put(objId, null);
            obj2stateMap.put(objId, collState);

            AlarmStatisticEntity asObjEntity = new AlarmStatisticEntity();
            asObjEntity.setId(objId);
            asObjEntity.setLevel50(collState);
            newObjAlarmMap.put(objId, asObjEntity);
        }

        // 加载所有对象组信息
        List grpIdLst = dal.getFirstLst("SELECT GROUP_ID FROM BMP_OBJGROUP");
        for (Object grpId : grpIdLst)
        {
            int groupId = Integer.valueOf(grpId.toString());
            newGrpAlarmMap.put(groupId, null);

            AlarmStatisticEntity asGrpEntity = new AlarmStatisticEntity();
            asGrpEntity.setId(groupId);
            asGrpEntity.setIsGroup(true);
            newGrpAlarmMap.put(groupId, asGrpEntity);
        }

        // 建立对象-对象关系
        List<Obj2ObjEntity> o2oEntityLst = dal.getLst(Obj2ObjEntity.class, "SELECT OBJ_ID,PARENT_ID FROM BMP_OBJECT WHERE PARENT_ID>0");
        for (Obj2ObjEntity entity : o2oEntityLst)
        {
            AlarmStatisticEntity asObjEntity = newObjAlarmMap.get(entity.getObjId());
            AlarmStatisticEntity asParentObjEntity = newObjAlarmMap.get(entity.getParentId());
            if (asObjEntity != null && asParentObjEntity != null)
            {
                asObjEntity.addParent(asParentObjEntity);
                asParentObjEntity.addChild(asObjEntity);
            }
        }

        // 建立对象-对象组关系，这里新建对象的统计数据
        List<Obj2GroupEntity> o2gEntityLst = dal.getLst(Obj2GroupEntity.class);
        for (Obj2GroupEntity entity : o2gEntityLst)
        {
            AlarmStatisticEntity asObjEntity = newObjAlarmMap.get(entity.getObjId());
            AlarmStatisticEntity asGrpEntity = newGrpAlarmMap.get(entity.getGroupId());
            if (asObjEntity != null && asGrpEntity != null)
            {
                asObjEntity.addParent(asGrpEntity);
                asGrpEntity.addChild(asObjEntity);
            }
        }

        // 建立对象组-对象组关系，这里新建组的统计数据
        List<Group2GroupEntity> g2gEntityLst = dal.getLst(Group2GroupEntity.class);
        for (Group2GroupEntity entity : g2gEntityLst)
        {
            AlarmStatisticEntity asGrpEntity = newGrpAlarmMap.get(entity.getGroupId());
            AlarmStatisticEntity asParentGrpEntity = newGrpAlarmMap.get(entity.getParentId());
            if (asGrpEntity != null && asParentGrpEntity != null)
            {
                asGrpEntity.addParent(asParentGrpEntity);
                asParentGrpEntity.addChild(asGrpEntity);
            }
        }

        // 加载监控对象的告警统计信息-自动地进行告警传递
        List<AlarmStatisticEntity> asEntityLst = dal.getLst(AlarmStatisticEntity.class, AlarmStatisticEntity.getInitSql());
        for (AlarmStatisticEntity entity : asEntityLst)
        {
            AlarmStatisticEntity asObjEntity = newObjAlarmMap.get(entity.getId());
            if (asObjEntity != null)
            {
                asObjEntity.update(entity);
            }
        }

        // 报警信息
        List<AlarmInfoEntity> aeEntityLst = null;
        if (freshAlarm)
        {
            aeEntityLst = dal.getLst(AlarmInfoEntity.class, AlarmInfoEntity.getInitSql());
        }

        return new ThreeTuple<Map<Integer, AlarmStatisticEntity>, Map<Integer, AlarmStatisticEntity>, List<AlarmInfoEntity>>(newObjAlarmMap,
            newGrpAlarmMap,
            aeEntityLst);
    }

    /**
     * 更新对象状态
     * @param objId
     * @param collState
     */
    public void updateCollState(String msg)
    {
        writeLock.lock();
        try
        {
            ObjCollStateEntity entity = ObjCollStateEntity.fromTransMsg(msg);
            if (entity != null)
            {
                // 更新对象
                AlarmStatisticEntity alarm = objAlarmMap.get(entity.objId);
                if (alarm != null)
                {
                    alarm.setLevel50(entity.objState);
                }

                // 更新子对象
                if (entity.objState != MObjectEntity.COLL_STATE_FAILED && entity.subMap != null)
                {
                    Set<Entry<Integer, Integer>> set = entity.subMap.entrySet();
                    for (Map.Entry<Integer, Integer> entry : set)
                    {
                        alarm = objAlarmMap.get(entry.getKey());
                        if (alarm != null)
                        {
                            alarm.setLevel50(entry.getValue());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            writeLock.unlock();
        }
    }

    /**
     * 处理新告警，收到集群通知时调用
     * @param alarm 报警
     */
    public void addAlarm(AlarmEventEntity alarm)
    {
        AlarmInfoEntity alarmInfoEntity = null;
        try
        {
            alarmInfoEntity = AlarmInfoEntity.createInstance(alarm.getAlarmEvtId());
        }
        catch (Exception e1)
        {
            logger.error(e1.getMessage(), e1);
        }
        if (alarmInfoEntity == null)
        {
            logger.info("告警不存在:" + alarm);
            return;
        }
        logger.debug("AlarmStatistic addAlarm begin.");
        writeLock.lock();
        try
        {
            // 更新告警统计并进行告警传递
            AlarmStatisticEntity asEntity = objAlarmMap.get(alarm.getObjId());
            if (asEntity == null)
            {
                asEntity = new AlarmStatisticEntity();
                asEntity.setId(alarm.getObjId());
                objAlarmMap.put(alarm.getObjId(), asEntity);
            }
            asEntity.addAlarm(alarm);

            // 添加新告警到内存中
            AlarmCacheEntity cacheAlarm = new AlarmCacheEntity(++lastTime, alarmInfoEntity);
            addAlarmEvent(alarm.getObjId(), cacheAlarm);
        }
        catch (Exception e)
        {
            logger.error("添加报警异常", e);
        }
        finally
        {
            writeLock.unlock();
        }
        broadcast("");
        logger.debug("AlarmStatistic addAlarm end.");
    }

    private void addAlarmEvent(int objId, AlarmCacheEntity cacheAlarm)
    {
        // 添加告警到内存
        curAlarmEvents.addFirst(cacheAlarm);
        LinkedList<AlarmCacheEntity> addAlarmLst = curAlarmMap.get(objId);
        if (addAlarmLst == null)
        {
            addAlarmLst = new LinkedList<AlarmCacheEntity>();
            curAlarmMap.put(objId, addAlarmLst);
        }
        addAlarmLst.addFirst(cacheAlarm);

        // 移除过期告警
        if (curAlarmEvents.size() > AlarmPubSub.REAL_ALARM_KEEP_SIZE)
        {
            AlarmCacheEntity rmEntity = curAlarmEvents.removeLast();
            LinkedList<AlarmCacheEntity> rmAlarmLst = curAlarmMap.get(rmEntity.getEvent().getObjId());
            if (rmAlarmLst != null && rmAlarmLst.size() > 0)
            {
                rmAlarmLst.removeLast();
            }
        }
    }

    /**
     * 处理老告警，故障处理完毕后调用
     * @param alarm 报警
     */
    public void removeAlarm(AlarmEventEntity alarm)
    {
        removeAlarm(alarm, true);
    }

    /**
     * 处理老告警，自动清除时调用
     * @param alarm 报警
     * @param notifyCollector 参数
     */
    public void removeAlarm(AlarmEventEntity alarm, boolean notifyCollector)
    {
        logger.debug("AlarmStatistic removeAlarm begin.");
        writeLock.lock();
        try
        {
            removeAlarmEvent(alarm, notifyCollector);
        }
        finally
        {
            writeLock.unlock();
        }
        broadcast("");
        logger.debug("AlarmStatistic removeAlarm end.");
    }

    /**
     * 批量处理老告警，故障处理完毕后调用
     * @param alarmLst 批量报警
     */
    public void removeAlarms(List<AlarmEventEntity> alarmLst)
    {
        logger.debug("AlarmStatistic removeAlarms begin.");
        writeLock.lock();
        try
        {
            for (AlarmEventEntity alarm : alarmLst)
            {
                removeAlarmEvent(alarm, true);
            }
        }
        finally
        {
            writeLock.unlock();
        }
        broadcast("");
        logger.debug("AlarmStatistic removeAlarms end.");
    }

    /**
     * 批量处理老告警，故障处理完毕后调用
     * @param alarms 批量报警
     */
    public void removeAlarms(AlarmEventEntity[] alarms)
    {
        logger.debug("AlarmStatistic removeAlarms begin.");
        writeLock.lock();
        try
        {
            for (AlarmEventEntity alarm : alarms)
            {
                removeAlarmEvent(alarm, true);
            }
        }
        finally
        {
            writeLock.unlock();
        }
        broadcast("");
        logger.debug("AlarmStatistic removeAlarms end.");
    }

    /**
     * 删除报警，用于前台处理、清除报警。采集器自动清除报警。
     * 这里可能存在以下问题：
     * 1、前台和后台同时清除一个报警，可能导致同一个报警被减了两次
     * @param alarm 报警事件
     * @param notifyCollector 是否通知采集器
     */
    private void removeAlarmEvent(AlarmEventEntity alarm, boolean notifyCollector)
    {
        // 现在内存中查找是否存在报警
        // 如果不存在则，不进行处理（在报警条数大于最大缓存条数时会有问题）
        // 如果存在，删除内存中的未处理告警引用
        AlarmCacheEntity cacheAlarm = new AlarmCacheEntity(lastTime, alarm);
        boolean isExist = removeAlarmEvent(alarm.getObjId(), cacheAlarm);

        if (isExist)
        {
            lastTime++;
            // 更新告警统计并进行告警传递
            AlarmStatisticEntity asEntity = objAlarmMap.get(alarm.getObjId());
            if (asEntity != null)
            {
                asEntity.removeAlarm(alarm);
            }

            // 通知采集器告警被处理了
            if (notifyCollector)
            {
                BMPServletContextListener.getInstance().callRemote("handleFinish", new Object[] { alarm }, new Class[] { AlarmEventEntity.class });
            }
        }
    }

    /**
     * 在报警记录模型中删除报警记录
     * @param objId
     * @param cacheAlarm
     * @return 返回该记录是否存在
     */
    private boolean removeAlarmEvent(int objId, AlarmCacheEntity cacheAlarm)
    {
        boolean isExist = curAlarmEvents.remove(cacheAlarm);
        LinkedList<AlarmCacheEntity> objAlarmLst = curAlarmMap.get(objId);
        if (objAlarmLst != null)
        {
            objAlarmLst.remove(cacheAlarm);
        }
        return isExist;
    }

    /**
     * 更新报警记录
     * @param alarm 报警
     */
    public void updateAlarm(AlarmEventEntity alarm)
    {
        logger.debug("AlarmStatistic updateAlarm begin.");
        writeLock.lock();
        try
        {
            // 更新告警统计并进行告警传递
            AlarmStatisticEntity asEntity = objAlarmMap.get(alarm.getObjId());
            if (asEntity != null)
            {
                asEntity.updateAlarm(alarm);
            }
            ++lastTime;

            // 更新告警信息
            updateAlarmEvent(alarm.getObjId(), alarm);
        }
        finally
        {
            writeLock.unlock();
        }
        broadcast("");
        logger.debug("AlarmStatistic updateAlarm end.");
    }

    /**
     * 更新报警记录
     * @param objId
     * @param alarm
     */
    private void updateAlarmEvent(int objId, AlarmEventEntity alarm)
    {
        LinkedList<AlarmCacheEntity> objAlarmLst = curAlarmMap.get(objId);
        if (objAlarmLst != null)
        {
            for (AlarmCacheEntity entity : objAlarmLst)
            {
                if (entity.update(alarm))
                {
                    break;
                }
            }
        }
    }

    /**
     * 广播告警事件，类似于listener模式，但功能更强，基于comet方式下的广播机制
     * @param obj
     */
    private void broadcast(Object obj)
    {
        if (BroadcasterFactory.getDefault() == null)
        {
            return;
        }
        for (Broadcaster bc : BroadcasterFactory.getDefault().lookupAll())
        {
            if (bc.getAtmosphereResources().size() > 0)
            {
                bc.broadcast(obj);
            }
        }
    }

    /**
     * 获取监控对象的告警统计信息
     * @param objIdLst 对象ids
     * @return 结果
     */
    public List<AlarmStatisticEntity> getObjStatisticInfos(List<Integer> objIdLst)
    {
        List<AlarmStatisticEntity> resultLst = new LinkedList<AlarmStatisticEntity>();
        readLock.lock();
        try
        {
            for (Integer objId : objIdLst)
            {
                resultLst.add(objAlarmMap.get(objId));
            }
        }
        finally
        {
            readLock.unlock();
        }
        return resultLst;
    }

    /**
     * 获取监控对象的告警统计信息
     * @param objIds 对象ids
     * @return 结果
     */
    public List<AlarmStatisticEntity> getObjStatisticInfos(int[] objIds)
    {
        List<AlarmStatisticEntity> resultLst = new LinkedList<AlarmStatisticEntity>();
        readLock.lock();
        try
        {
            for (Integer objId : objIds)
            {
                resultLst.add(objAlarmMap.get(objId));
            }
        }
        finally
        {
            readLock.unlock();
        }
        return resultLst;
    }

    /**
     * 获取监控对象的告警统计信息
     * @param objId 对象id
     * @return 结果
     */
    public AlarmStatisticEntity getObjStatisticInfo(int objId)
    {
        readLock.lock();
        try
        {
            return objAlarmMap.get(objId);
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 获取监控对象组的告警统计信息
     * @param grpIdLst 对象组
     * @return 结果
     */
    public List<AlarmStatisticEntity> getGrpStatisticInfos(List<Integer> grpIdLst)
    {
        List<AlarmStatisticEntity> resultLst = new LinkedList<AlarmStatisticEntity>();
        readLock.lock();
        try
        {
            for (Integer grpId : grpIdLst)
            {
                resultLst.add(grpAlarmMap.get(grpId));
            }
        }
        finally
        {
            readLock.unlock();
        }
        return resultLst;
    }

    /**
     * 获取监控对象组的告警统计信息
     * @param grpIds 对象组
     * @return 结果
     */
    public List<AlarmStatisticEntity> getGrpStatisticInfos(int[] grpIds)
    {
        List<AlarmStatisticEntity> resultLst = new LinkedList<AlarmStatisticEntity>();
        readLock.lock();
        try
        {
            for (Integer grpId : grpIds)
            {
                resultLst.add(grpAlarmMap.get(grpId));
            }
        }
        finally
        {
            readLock.unlock();
        }
        return resultLst;
    }

    /**
     * 获取监控对象组的告警统计信息
     * @param grpId 对象组
     * @return 结果
     */
    public AlarmStatisticEntity getGrpStatisticInfo(int grpId)
    {
        readLock.lock();
        try
        {
            return grpAlarmMap.get(grpId);
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 获取监控对象的所有子对象id
     * @param objId 对象
     * @param objSet 参数
     */
    public void fetchObjAllChilds(int objId, Set<Integer> objSet)
    {
        readLock.lock();
        try
        {
            AlarmStatisticEntity entity = objAlarmMap.get(objId);
            if (entity != null)
            {
                entity.fetchAllChildIds(objSet);
            }
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 获取监控对象组的所有对象及子对象id
     * @param grpId 对象组
     * @param objSet 参数
     */
    public void fetchGrpAllChilds(int grpId, Set<Integer> objSet)
    {
        readLock.lock();
        try
        {
            AlarmStatisticEntity entity = grpAlarmMap.get(grpId);
            if (entity != null)
            {
                entity.fetchAllChildIds(objSet, new HashSet<Integer>());
            }
        }
        finally
        {
            readLock.unlock();
        }
    }

    /**
     * 获取告警统计模型的最后更新时间
     * @return 结果
     */
    public long getLastTime()
    {
        return lastTime;
    }

    /**
     * 检查浏览器请求，如果请求需要返回，则组装数据返回前台
     * @param sub 参数
     * @param source 参数
     * @param suspendWhenNeed 参数
     * @return 结果
     * @throws IOException 异常
     */
    public boolean checkAndResponse(AlarmSubEntity sub, AtmosphereResource<HttpServletRequest, HttpServletResponse> source, boolean suspendWhenNeed)
            throws IOException
    {
        readLock.lock();
        logger.debug("begin to check the alarm request...");
        try
        {
            long tmpLastTime = sub.getLastTime();

            // 若最近没有告警、刷新，则挂起请求
            if (tmpLastTime == lastTime)
            {
                if (suspendWhenNeed)
                {
                    source.suspend(AlarmPubSub.ALARM_QUERY_SUSPEND_TIMEOUT, false);
                }
                logger.info("无新报警，挂起请求:" + source.getRequest().getAttribute(AlarmPubSub.MSGID));
                return false;
            }

            // 初始化响应消息头部
            Document doc = DocumentHelper.parseText(AlarmPubSub.EMPTYRESP);
            Element root = doc.getRootElement();
            root.addElement(AlarmPubSub.CLIENTID).setText(Long.toString(sub.getClientId()));
            String msgId = (String) source.getRequest().getAttribute(AlarmPubSub.MSGID);
            root.addElement(AlarmPubSub.MSGID).setText(msgId);

            // 检查告警是否更新
            boolean alwaysReturn = tmpLastTime == 0;
            boolean objsUpdateFlag = needToResponse(false, sub.getObjs(), root, AlarmPubSub.OBJECTS, alwaysReturn);
            boolean grpsUpdateFlag = needToResponse(true, sub.getGrps(), root, AlarmPubSub.GROUPS, alwaysReturn);
            boolean mapsUpdateFlag = needToResponse(true, sub.getMaps(), root, AlarmPubSub.MAPS, alwaysReturn);

            // 当前拓扑没有新告警的情况下的处理
            if (!sub.isGlobal() && !alwaysReturn && !objsUpdateFlag && !grpsUpdateFlag)
            {
                if (!mapsUpdateFlag)// 最近没有告警，则挂起请求
                {
                    sub.setLastTime(lastTime);
                    if (suspendWhenNeed)
                    {
                        source.suspend(AlarmPubSub.ALARM_QUERY_SUSPEND_TIMEOUT, false);
                    }
                    logger.info("无新报警，继续挂起请求:" + msgId);
                    return false;
                }
                else
                // 最近有告警，但是当前拓扑没有新告警的情况下，只需返回前台最新的告警状态
                {
                    sub.setLastTime(lastTime);
                    response(source, doc.asXML());
                    source.resume();
                    sub.setRequest(null);
                    logger.info("当前拓扑无新报警，全局有新报警返回:" + msgId);
                    return true;
                }
            }

            // 取最新的告警列表
            List<AlarmCacheEntity> newAlarmLst = getCurAlarmLst(sub);

            // 组装新的告警消息
            Element newAE = root.addElement(AlarmPubSub.NEW_ALARMS);
            Element hisAE = root.addElement(AlarmPubSub.HIS_ALARMS);
            Element oldAE = root.addElement(AlarmPubSub.OLD_ALARMS);
            Element updateAE = root.addElement(AlarmPubSub.UPDATE_ALARMS);
            if (alwaysReturn)
            {
                for (AlarmCacheEntity entity : newAlarmLst)
                {
                    Element elm = newAE.addElement(AlarmPubSub.ALARM);
                    entity.getEvent().toXml(elm);
                }
                logger.info("首次请求返回:" + msgId);
            }
            else
            {
                List<ThreeTuple<Long, Long, Integer>> oldLst = sub.getCurAlarmLst();
                compare(newAlarmLst, oldLst, newAE, hisAE, oldAE, updateAE);
                logger.info("后续请求返回:" + msgId);
            }

            // 更新当前客户端的缓存
            sub.setLastTime(lastTime);
            List<ThreeTuple<Long, Long, Integer>> newLst = new ArrayList<ThreeTuple<Long, Long, Integer>>();
            for (AlarmCacheEntity entity : newAlarmLst)
            {
                newLst.add(new ThreeTuple<Long, Long, Integer>(entity.getId(), entity.getLastUpdateTime(), entity.getEvent().getAlarmEvtId()));
            }
            sub.setAlarmLst(newLst);

            // 将告警信息发送回前台
            response(source, doc.asXML());
            source.resume();
            sub.setRequest(null);
            logger.info("正常返回:" + msgId);
            return true;
        }
        catch (DocumentException e)
        {
            logger.error("创建响应信息异常", e);
            return false;
        }
        finally
        {
            logger.debug("end to check the alarm request.");
            readLock.unlock();
        }
    }

    private boolean needToResponse(boolean isGrp, List<AlarmStatisticEntity> entitys, Element root, String name, boolean always)
    {
        boolean isChange = false;
        root = root.addElement(name);
        for (AlarmStatisticEntity entry : entitys)
        {
            AlarmStatisticEntity entity = null;
            if (isGrp)
            {
                entity = grpAlarmMap.get(entry.getId());
            }
            else
            {
                entity = objAlarmMap.get(entry.getId());
            }
            if (entity == null)
            {
                continue;
            }
            if (entry.compareAndSet(entity) || always)
            {
                isChange = true;
                Element elm = root.addElement(AlarmPubSub.NODE);
                entry.toXml(elm);
            }
        }
        return isChange;
    }

    private List<AlarmCacheEntity> getCurAlarmLst(AlarmSubEntity sub)
    {
        int count = AlarmPubSub.REAL_ALARM_FETCH_SIZE;
        if (sub.isGlobal())
        {
            List<AlarmCacheEntity> resultLst = new ArrayList<AlarmCacheEntity>(count);
            for (AlarmCacheEntity entity : curAlarmEvents)
            {
                if (sub.getFilterLevel() <= entity.getEvent().getAlarmLevel())
                {
                    resultLst.add(entity);
                    if (--count == 0)
                    {
                        break;
                    }
                }
            }
            return resultLst;
        }
        else
        {
            Set<Integer> objIdSet = sub.getObjSet();
            List<ThreeTuple<List<AlarmCacheEntity>, Integer, Integer>> tmpNewAlarmLst =
                new ArrayList<ThreeTuple<List<AlarmCacheEntity>, Integer, Integer>>(objIdSet.size());
            for (Integer objId : objIdSet)
            {
                List<AlarmCacheEntity> cacheAlarmLst = curAlarmMap.get(objId);
                if (cacheAlarmLst != null)
                {
                    tmpNewAlarmLst.add(new ThreeTuple(cacheAlarmLst, 0, cacheAlarmLst.size()));
                }
            }

            List<AlarmCacheEntity> resultLst = new ArrayList<AlarmCacheEntity>(count);
            AlarmCacheEntity maxValue = getMax(tmpNewAlarmLst);
            while (maxValue != null)
            {
                if (sub.getFilterLevel() <= maxValue.getEvent().getAlarmLevel())
                {
                    resultLst.add(maxValue);
                    if (--count == 0)
                    {
                        break;
                    }
                }
                maxValue = getMax(tmpNewAlarmLst);
            }
            return resultLst;
        }
    }

    /**
     * @param cacheAlarmLst
     * @return
     */
    private AlarmCacheEntity getMax(List<ThreeTuple<List<AlarmCacheEntity>, Integer, Integer>> cacheAlarmLst)
    {
        AlarmCacheEntity result = null;
        ThreeTuple<List<AlarmCacheEntity>, Integer, Integer> maxValue = null;
        for (ThreeTuple<List<AlarmCacheEntity>, Integer, Integer> value : cacheAlarmLst)
        {
            if (value.second >= value.third)
            {
                continue;
            }
            AlarmCacheEntity tmpEntity = value.first.get(value.second);
            if (result == null)
            {
                result = tmpEntity;
                maxValue = value;
            }
            else if (result.id < tmpEntity.id)
            {
                result = tmpEntity;
                maxValue = value;
            }
        }
        if (result != null)
        {
            maxValue.second += 1;
        }
        return result;
    }

    /**
     * @param newLst
     * @param oldLst
     * @param newAE
     * @param updateAE
     * @param oldAE
     * @param hisAE
     */
    private void compare(List<AlarmCacheEntity> newLst, List<ThreeTuple<Long, Long, Integer>> oldLst, Element newAE, Element hisAE, Element oldAE,
            Element updateAE)
    {
        int curNew = 0;
        int curOld = 0;
        int newSize = newLst.size();
        int oldSize = oldLst.size();
        long maxOld = oldSize > 0 ? oldLst.get(0).first : -1L;
        AlarmCacheEntity newAlarm = null;
        ThreeTuple<Long, Long, Integer> oldAlarm = null;

        // 比较新旧两个列表
        while (curNew < newSize || curOld < oldSize)
        {
            newAlarm = curNew < newSize ? newLst.get(curNew) : null;
            oldAlarm = curOld < oldSize ? oldLst.get(curOld) : null;
            if (newAlarm == null && oldAlarm != null)// 历史告警
            {
                Element elm = hisAE.addElement(AlarmPubSub.ALARM);
                elm.addAttribute("ALARMEVT_ID", String.valueOf(oldAlarm.third));
                curOld++;
            }
            else if (newAlarm != null && oldAlarm == null)
            {
                if (newAlarm.id > maxOld)// 新告警
                {
                    Element elm = newAE.addElement(AlarmPubSub.ALARM);
                    newAlarm.getEvent().toXml(elm);
                }
                else
                // 旧告警
                {
                    Element elm = oldAE.addElement(AlarmPubSub.ALARM);
                    newAlarm.getEvent().toXml(elm);
                }
                curNew++;
            }
            else if (newAlarm.id > oldAlarm.first)
            {
                if (newAlarm.id > maxOld)// 新告警
                {
                    Element elm = newAE.addElement(AlarmPubSub.ALARM);
                    newAlarm.getEvent().toXml(elm);
                }
                else
                // 旧告警
                {
                    Element elm = oldAE.addElement(AlarmPubSub.ALARM);
                    newAlarm.getEvent().toXml(elm);
                }
                curNew++;
            }
            else if (newAlarm.id == oldAlarm.first)
            {
                if (newAlarm.getLastUpdateTime() != oldAlarm.second)// 更新告警
                {
                    Element elm = updateAE.addElement(AlarmPubSub.ALARM);
                    newAlarm.getEvent().toXml(elm);
                }
                curNew++;
                curOld++;
            }
            else
            // 历史告警
            {
                Element elm = hisAE.addElement(AlarmPubSub.ALARM);
                elm.addAttribute("ALARMEVT_ID", String.valueOf(oldAlarm.third));
                curOld++;
            }
        }
    }

    /**
     * @param source 参数
     * @param result 参数
     * @throws IOException 异常
     */
    public void response(AtmosphereResource<HttpServletRequest, HttpServletResponse> source, String result) throws IOException
    {
        HttpServletResponse res = source.getResponse();
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain");
        res.getWriter().write(result);
        logger.info("返回结果:" + result);
        res.getWriter().flush();
        res.getWriter().close();
    }

    /**
     * @author lianghongjie 告警事件在内存的结构，方便处理
     */
    private class AlarmCacheEntity
    {
        private long id;
        private long lastUpdateTime;
        private AlarmInfoEntity event;

        public AlarmCacheEntity(long id, AlarmInfoEntity event)
        {
            super();
            this.id = id;
            this.lastUpdateTime = System.nanoTime();
            this.event = event;
        }

        public AlarmCacheEntity(long id, AlarmEventEntity alarm)
        {
            super();
            this.id = id;
            this.lastUpdateTime = System.nanoTime();
            this.event = new AlarmInfoEntity();
            event.setAlarmEvtId(alarm.getAlarmEvtId());
            event.setObjId(alarm.getObjId());
            event.setAlarmLevel(alarm.getAlarmLevel());
        }

        /**
         * @param alarm 报警
         * @return 结果
         */
        public boolean update(AlarmEventEntity alarm)
        {
            if (alarm.getAlarmEvtId() != event.getAlarmEvtId())
            {
                return false;
            }
            event.update(alarm);
            this.lastUpdateTime = System.nanoTime();
            return true;
        }

        public long getId()
        {
            return id;
        }

        public void setId(long id)
        {
            this.id = id;
        }

        public long getLastUpdateTime()
        {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(long lastTime)
        {
            this.lastUpdateTime = lastTime;
        }

        public AlarmInfoEntity getEvent()
        {
            return event;
        }

        public void setEvent(AlarmInfoEntity event)
        {
            this.event = event;
        }

        public String toString()
        {
            return Integer.toString(event.getAlarmEvtId());
        }

        public boolean equals(Object obj)
        {
            if (!(obj instanceof AlarmCacheEntity))
            {
                return false;
            }
            if (this.event.getAlarmEvtId() == ((AlarmCacheEntity) obj).event.getAlarmEvtId())
            {
                return true;
            }
            return false;
        }

        public int hashCode()
        {
            return event.getAlarmEvtId();
        }
    }

    /**
     * 内部类
     */
    @Table(name = "BMP_OBJECT")
    public static class Obj2ObjEntity
    {
        @Column(name = "OBJ_ID")
        private int objId;
        @Column(name = "PARENT_ID")
        private int parentId;

        public int getObjId()
        {
            return objId;
        }

        public void setObjId(int objId)
        {
            this.objId = objId;
        }

        public int getParentId()
        {
            return parentId;
        }

        public void setParentId(int groupId)
        {
            this.parentId = groupId;
        }
    }
}
