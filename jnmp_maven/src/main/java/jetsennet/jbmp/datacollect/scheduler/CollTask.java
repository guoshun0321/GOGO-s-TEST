/************************************************************************
日 期：
作 者: 梁洪杰
版 本：v1.3
描 述: 采集任务
历 史：
 ************************************************************************/
package jetsennet.jbmp.datacollect.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.alarm.eventhandle.ObjCollStateEntity;
import jetsennet.jbmp.alarm.eventhandle.ObjCollStateUtil;
import jetsennet.jbmp.alarm.eventhandle.ShootThroughtAlarmUtil;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.rrd.RrdHelper;
import jetsennet.jbmp.datacollect.collector.ICollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.datasource.DataAgentManager;
import jetsennet.jbmp.datacollect.datasource.DataAgentUtil;
import jetsennet.jbmp.datacollect.datasource.IDataAgent;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.formula.FormulaTransUtil;
import jetsennet.jbmp.log.OperatorLog;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.jbmp.util.XmlCfgUtil;
import jetsennet.util.StringUtil;

import org.apache.log4j.Logger;

/**
 * 采集任务
 * @author lianghongjie
 */
public class CollTask implements Runnable
{
    private static final Logger logger = Logger.getLogger(CollTask.class);
    /**
     * 任务信息
     */
    protected CollectTaskEntity taskInfo;
    /**
     * 监控对象
     */
    protected MObjectEntity mo;
    /**
     * 采集间隔
     */
    protected int interval;
    /**
     * 采集属性列表
     */
    protected List<TwoTuple<ObjAttribEntity, AtomicInteger>> oaLst;
    /**
     * 可用性对象属性
     */
    protected ObjAttribEntity validObjAttr;
    /**
     * 子对象列表
     */
    protected List<CollTask> childLst;
    /**
     * 采集器
     */
    protected ICollector coll;
    // 数据库服务
    protected MObjectDal modao;
    protected ObjAttribDal oadao;
    /**
     * 默认时间间隔（秒）
     */
    private static int DEFAULT_COLL_SPAN = 300;

    /**
     * 构造方法
     * @param mo 对象
     */
    public CollTask(MObjectEntity mo)
    {
        this.mo = mo;
        modao = ClassWrapper.wrapTrans(MObjectDal.class);
        oadao = ClassWrapper.wrapTrans(ObjAttribDal.class);

        // 根据监控对象的对象类型classType，查找配置文件，创建并初始化相应的采集器ICollector和ICollectorErrorHandler
        String classType = mo.getClassType();
        coll = (ICollector) createInstance(CollConstants.COLL_CLASS_CFG_PRE, classType, mo.getParentId() > 0);
        if (coll == null)
        {
            return;
        }
        coll.setMonitorObject(mo);

        // 初始化各个对象属性的采集间隔数
        oaLst = new ArrayList<TwoTuple<ObjAttribEntity, AtomicInteger>>();
        for (ObjAttribEntity entity : mo.getAttrs())
        {
            if (entity.getAttribId() == AttributeEntity.VALID_ATTRIB_ID)
            {
                validObjAttr = entity;
            }
            oaLst.add(new TwoTuple<ObjAttribEntity, AtomicInteger>(entity, new AtomicInteger(1)));
        }
    }

    /**
     * @param mo 对象
     * @param parentColl 参数
     */
    public CollTask(MObjectEntity mo, ICollector parentColl)
    {
        this(mo);
        coll.setParentCollector(parentColl);
    }

    @Override
    public void run()
    {
        try
        {
            ObjCollStateEntity stat = new ObjCollStateEntity();
            this.runMethod(stat);
            ObjCollStateUtil.sendObjState(mo, stat);
        }
        catch (Throwable ex)
        {
            logger.error("", ex);
        }
    }

    private void runMethod(ObjCollStateEntity stat)
    {
        // 获取当次需要采集的对象属性
        List<ObjAttribEntity> collObjAttrs = getCollObjAttrs();

        // 对象采集结果
        Integer collState = null;

        // 获取数据
        TransMsg msg = DataAgentUtil.wrapObjAttrib(mo, collObjAttrs);
        IDataAgent agent = DataAgentManager.getAgent();
        msg = agent.getData(msg);

        // 处理结果
        int state = msg.getCollState();
        switch (state)
        {
        case TransMsg.COLL_STATE_SUC:
            Map<ObjAttribEntity, Object> dataMap = DataAgentUtil.unwrapTransMsg(msg);

            // 处理数据
            // rrdMap用于保存需要存储到rrd文件里面的数据
            Map<String, Double> rrdMap = new HashMap<String, Double>();
            for (Entry<ObjAttribEntity, Object> oaEntity : dataMap.entrySet())
            {
                processData(oaEntity, rrdMap);
            }
            sendValidData(CollConstants.STATUS_UP_VALUE, rrdMap, null);
            collState = MObjectEntity.COLL_STATE_OK;
            break;
        case TransMsg.COLL_STATE_FAILD:
            sendValidData(CollConstants.STATUS_DOWN_VALUE, null, msg.getMsg());
            collState = MObjectEntity.COLL_STATE_FAILED;
            break;
        case TransMsg.COLL_STATE_EMPTY:
            break;
        case TransMsg.COLL_STATE_UPDATE:
            String endOid = msg.getMsg();
            this.updateProc(endOid, mo.getObjId());
            break;
        default:
            break;
        }

        if (collState != null)
        {
            if (mo.isSubObj())
            {
                stat.addSub(mo.getObjId(), collState);
            }
            else
            {
                stat.setObj(mo.getObjId(), collState);
            }
        }

        // 父对象采集成功后，才采集子对象
        if (state == TransMsg.COLL_STATE_SUC)
        {
            if (childLst == null)
            {
                this.initChildLst();
            }
            for (CollTask child : childLst)
            {
                logger.debug("开始采集子对象：" + child.mo.getObjName());
                child.runMethod(stat);
            }
        }
    }

    /**
     * 处理数据
     * @param oaEntity
     * @param rrdMap
     */
    protected void processData(Entry<ObjAttribEntity, Object> oaEntity, Map<String, Double> rrdMap)
    {
        try
        {
            Object data = oaEntity.getValue();
            ObjAttribEntity oa = oaEntity.getKey();
            if (data != null)
            {
                if (data instanceof CollData)
                {
                    sendData(data, oa, rrdMap);
                }
                // 恢复数据采集失败报警
                ShootThroughtAlarmUtil.alarmReCollMiss(oa.getObjId(), oa.getAttribId(), oa.getObjAttrId());
            }
            else
            {
                // 产生数据采集失败报警
                logger.info("监控对象:" + mo.getObjName() + "的属性:" + oaEntity.getKey().getObjattrName() + "采集失败。");
                ShootThroughtAlarmUtil.alarmGenCollMiss(oa.getObjId(), oa.getAttribId(), oa.getObjAttrId(), oa.getObjattrName(), "");
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发送采集数据
     * @param data
     * @param rrdMap
     */
    private void sendData(Object data, ObjAttribEntity oa, Map<String, Double> rrdMap)
    {
        if (data instanceof CollData)
        {
            CollData col = (CollData) data;

            // 将性能数据放到rrdMap中
            if (validObjAttr != null && oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF)
            {

                rrdMap.put(Integer.toString(col.objAttrID), Double.valueOf(col.value));
                if (col.params != null)
                {
                    col.params.put(CollData.PARAMS_DATA, null);
                }
            }
            logger.debug("对象id:" + col.objID + " 对象属性id:" + col.objAttrID + " 值:" + col.value);
            CollDataBus.getInstance().put((CollData) data);
        }
        else if (data instanceof List)
        {
            for (Object subData : (List<?>) data)
            {
                sendData(subData, oa, rrdMap);
            }
        }
    }

    /**
     * 发送对象通断性状态
     * @param value
     * @param dataMap
     * @param msg
     */
    private void sendValidData(String value, Map<String, Double> dataMap, String msg)
    {
        if (validObjAttr == null)
        {
            return;
        }
        CollData colldata = new CollData();
        colldata.objID = validObjAttr.getObjId();
        colldata.objAttrID = validObjAttr.getObjAttrId();
        colldata.attrID = validObjAttr.getAttribId();
        colldata.dataType = CollData.DATATYPE_PERF;
        colldata.value = value;
        colldata.srcIP = mo.getIpAddr();
        colldata.time = new Date();

        // 采集失败。如果出现通断性异常，将异常信息放入CollData.COLL_INFO字段
        if (value == CollConstants.STATUS_DOWN_VALUE)
        {
            if (msg != null)
            {
                colldata.put(CollData.COLL_INFO, msg);
            }
        }
        else
        {
            //采集成功。 发送通断性数据时，顺带发送采集数据。
            if (dataMap == null)
            {
                dataMap = new HashMap<String, Double>();
            }
            dataMap.put(String.valueOf(validObjAttr.getObjAttrId()), Double.parseDouble(value));
            colldata.put(CollData.PARAMS_DATA, dataMap);
        }
        CollDataBus.getInstance().put(colldata);
    }

    /**
     * 初始化子对象列表
     */
    protected void initChildLst()
    {
        try
        {
            childLst = new ArrayList<CollTask>();
            List<MObjectEntity> subLst = modao.getByParentId(mo.getObjId());
            if (subLst != null)
            {
                for (MObjectEntity child : subLst)
                {
                    ArrayList<ObjAttribEntity> attrs = oadao.getCollObjAttribByID(child.getObjId());
                    if (attrs == null || attrs.size() == 0)
                    {
                        logger.warn("对象:" + child.getObjName() + "无相应的采集指标");
                        continue;
                    }
                    child.setAttrs(attrs);
                    CollTask subTask = new CollTask(child, coll);
                    subTask.setTaskInfo(taskInfo);
                    subTask.setInterval(interval);
                    childLst.add(subTask);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 设置采集间隔
     * @param interval 参数
     */
    public void setInterval(int interval)
    {
        this.interval = interval;

        // 计算需要保存性能数据的对象属性ID列表
        List<String> oaIdLst = new ArrayList<String>(oaLst.size());
        for (TwoTuple<ObjAttribEntity, AtomicInteger> objAttr : oaLst)
        {
            int counter = this.ensureCollSpan(objAttr.first.getCollTimespan());
            objAttr.second.set(counter);
            if (objAttr.first.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF || objAttr.first.getAttribId() == AttributeEntity.VALID_ATTRIB_ID)
            {
                oaIdLst.add("" + objAttr.first.getObjAttrId());
            }
        }

        // 检查rrd文件是否存在|是否需要更新
        if (oaIdLst.size() > 0)
        {
            try
            {
                RrdHelper.getInstance().checkRrdFile("" + mo.getObjId(), interval, oaIdLst.toArray(new String[oaIdLst.size()]));
            }
            catch (Exception e)
            {
                logger.error("监控对象:" + mo.getObjName() + "的rrd文件检查失败", e);
            }
        }
    }

    /**
     * 获取当次需要采集的对象属性。对象属性的采集间隔减少为0时，该对象属性需要采集，同时将间隔设置为最大间隔。
     * @return
     */
    private List<ObjAttribEntity> getCollObjAttrs()
    {
        List<ObjAttribEntity> result = new ArrayList<ObjAttribEntity>();
        for (TwoTuple<ObjAttribEntity, AtomicInteger> objAttr : oaLst)
        {
            if (objAttr.second.decrementAndGet() == 0 && objAttr.first.getAttribId() != AttributeEntity.VALID_ATTRIB_ID)
            {
                int temp = this.ensureCollSpan(objAttr.first.getCollTimespan());
                objAttr.second.set(temp);
                result.add(objAttr.first);
            }
        }
        return result;
    }

    /**
     * 确认采集间隔
     * @param collTimeSpan
     * @return
     */
    private int ensureCollSpan(int collTimeSpan)
    {
        collTimeSpan = collTimeSpan > 0 ? collTimeSpan : DEFAULT_COLL_SPAN;
        int retval = collTimeSpan / this.interval;

        // 当子对象的最小采集间隔比父对象的小时，使用父对象的最小采集间隔
        // 防止子对象的采集间隔比父对象的采集间隔的最小公约数小
        retval = retval < 1 ? 1 : retval;
        return retval;
    }

    /**
     * 创建类实例
     * @param pre
     * @param name
     * @param isSubObj
     * @return
     */
    private static Object createInstance(String pre, String name, boolean isSubObj)
    {
        String className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + name + XmlCfgUtil.CFG_POS, null);
        if (className == null)
        {
            if (!isSubObj)
            {
                className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + XmlCfgUtil.DEFAULT + XmlCfgUtil.CFG_POS, null);
            }
            else
            {
                className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + XmlCfgUtil.DEFAULT + "Sub" + XmlCfgUtil.CFG_POS, null);
            }
            if (className == null)
            {
                return null;
            }
        }
        try
        {
            Class target = Class.forName(className.trim());
            return target.newInstance();
        }
        catch (Exception e)
        {
            logger.error("创建类实例失败,类名:" + className, e);
            return null;
        }
    }

    private void log(boolean success, Object... args)
    {
        if (coll.getParentCollector() != null)
        {
            return;
        }

        if (success)
        {
            OperatorLog.log(0, "SYSTEM", String.format("采集成功。任务编号:%s，对象名称：%s，采集间隔：%s秒，采集时间：%s秒", args));
        }
        else
        {
            OperatorLog.log(0, "SYSTEM", String.format("采集失败。任务编号:%s，对象名称：%s，采集间隔：%s秒", args));
        }
    }

    /**
     * 更新对象属性，仅用于进程对象
     * @param endOid
     */
    private void updateProc(String endOid, int objId)
    {
        if (endOid == null)
        {
            return;
        }
        try
        {
            mo.setField1(endOid);
            modao.update(mo);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        // 更新缓存
        for (TwoTuple<ObjAttribEntity, AtomicInteger> objAttr : oaLst)
        {
            ObjAttribEntity oa = objAttr.first;
            String param = oa.getAttribParam();
            if (!StringUtil.isNullOrEmpty(param))
            {
                param = FormulaTransUtil.replaceEndOid(param, endOid);
                oa.setAttribParam(param);
            }
        }
        // 更新数据库
        ArrayList<ObjAttribEntity> oas = oadao.getByID(objId);
        if (oas != null)
        {
            for (ObjAttribEntity oa : oas)
            {
                try
                {
                    String param = oa.getAttribParam();
                    if (!StringUtil.isNullOrEmpty(param))
                    {
                        param = FormulaTransUtil.replaceEndOid(param, endOid);
                        oa.setAttribParam(param);
                        oadao.update(oa);
                    }
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
    }

    /**
     * @return the taskInfo
     */
    public CollectTaskEntity getTaskInfo()
    {
        return taskInfo;
    }

    /**
     * @param taskInfo the taskInfo to set
     */
    public void setTaskInfo(CollectTaskEntity taskInfo)
    {
        this.taskInfo = taskInfo;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println(new Date(1353477330994l));
    }
}
