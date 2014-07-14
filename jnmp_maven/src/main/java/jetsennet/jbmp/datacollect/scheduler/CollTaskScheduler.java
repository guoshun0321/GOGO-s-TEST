/**********************************************************************
 * 日 期： 2012-07-05
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  CollTaskScheduler.java
 * 历 史： 
 *********************************************************************/
package jetsennet.jbmp.datacollect.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.util.TwoTuple;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集任务调度器 通过包装采集线程池CollThreadPoolExecutor实现其调度策略
 * @author lianghongjie
 */
public class CollTaskScheduler
{

    /**
     * 任务ID、Obj2Grp Map
     */
    private Map<Integer, List<Obj2GroupEntity>> obj2GrpMap;

    /**
     * 对象ID、Future Map
     */
    private Map<Integer, TwoTuple<Future<Object>, AtomicInteger>> taskFutureMap;

    /**
     * 采集线程池
     */
    private CollThreadPoolExecutor executor;

    /**
     * 数据库操作类
     */
    private Obj2GroupDal obj2GrpDal;
    private MObjectDal moDal;
    protected ObjAttribDal oadao;

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(CollTaskScheduler.class);

    /**
     * 初始化
     */
    public void init()
    {
        obj2GrpMap = new HashMap<Integer, List<Obj2GroupEntity>>();
        taskFutureMap = new HashMap<Integer, TwoTuple<Future<Object>, AtomicInteger>>();
        int size =
            XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_THREAD_POOL_SIZE_CFG, CollConstants.DEFAULT_THREAD_POOL_SIZE);
        int maxSize =
            XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE,
                CollConstants.COLL_THREAD_POOL_MAX_SIZE_CFG,
                CollConstants.DEFAULT_THREAD_POOL_MAX_SIZE);
        int keepAliveTime =
            XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_THREAD_KEEP_ALIVE_CFG, CollConstants.DEFAULT_THREAD_KEEP_ALIVE);
        executor = new CollThreadPoolExecutor(size, maxSize, keepAliveTime);
        obj2GrpDal = ClassWrapper.wrapTrans(Obj2GroupDal.class);
        moDal = ClassWrapper.wrapTrans(MObjectDal.class);
        oadao = ClassWrapper.wrapTrans(ObjAttribDal.class);
    }

    /**
     * @param task 任务
     * @return 结果
     * @throws Exception 异常
     */
    public synchronized Map<Integer, Integer> getCollectTaskInfo(CollectTaskEntity task) throws Exception
    {
        List<Obj2GroupEntity> obj2GrpLst = obj2GrpMap.get(task.getTaskId());
        if (obj2GrpLst == null)
        {
            obj2GrpLst = obj2GrpDal.getObjLst(task.getGroupId());
        }

        Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
        for (Obj2GroupEntity obj2Grp : obj2GrpLst)
        {
            TwoTuple<Future<Object>, AtomicInteger> future = taskFutureMap.get(obj2Grp.getObjId());
            if (future == null)
            {
                resultMap.put(obj2Grp.getObjId(), CollectTaskEntity.TASK_STATE_NEW);
            }
            else
            {
                resultMap.put(obj2Grp.getObjId(), CollectTaskEntity.TASK_STATE_RUNNING);
            }
        }
        return resultMap;
    }

    /**
     * 调度任务
     * @param task 任务
     * @throws Exception 异常
     */
    public synchronized void schedule(CollectTaskEntity task) throws Exception
    {
        List<Obj2GroupEntity> obj2GrpLst = obj2GrpMap.get(task.getTaskId());
        if (obj2GrpLst != null)
        {
            logger.info("任务:" + task.getTaskId() + "已启动");
            return;
        }
        obj2GrpLst = obj2GrpDal.getObjLst(task.getGroupId());
        obj2GrpMap.put(task.getTaskId(), obj2GrpLst);

        for (Obj2GroupEntity obj2Grp : obj2GrpLst)
        {
            TwoTuple<Future<Object>, AtomicInteger> future = taskFutureMap.get(obj2Grp.getObjId());
            if (future == null)
            {
                MObjectEntity mo = moDal.get(obj2Grp.getObjId());
                if (mo == null)
                {
                    logger.warn("对象:" + obj2Grp.getObjId() + "不存在，不能创建相应的采集任务");
                    continue;
                }
                ArrayList<ObjAttribEntity> attrs = oadao.getCollObjAttribByID(mo.getObjId());
                if (attrs == null || attrs.size() == 0)
                {
                    logger.warn("对象:" + mo.getObjName() + "无相应的采集指标");
                    continue;
                }
                int interval = computeInterval(attrs);

                CollectTaskEntity newTask = task.clone();
                newTask.setMo(mo);
                mo.setAttrs(attrs);
                newTask.setCollTimespan(interval);

                logger.info("启动对象:" + mo.getObjName() + "的采集任务,采集间隔:" + interval);
                Future<Object> taskFu = executor.scheduleAtFixedRate(newTask);
                AtomicInteger count = new AtomicInteger(1);
                future = new TwoTuple<Future<Object>, AtomicInteger>(taskFu, count);
                taskFutureMap.put(obj2Grp.getObjId(), future);
            }
            else
            {
                future.second.incrementAndGet();
            }
        }
    }

    /**
     * @param objId 对象id
     * @param task 任务
     * @throws Exception 异常
     */
    public synchronized void schedule(CollectTaskEntity task, int objId) throws Exception
    {
        List<Obj2GroupEntity> obj2GrpLst = obj2GrpMap.get(task.getTaskId());
        if (obj2GrpLst == null)
        {
            throw new Exception("任务未启动，请先启动相应任务");
        }

        TwoTuple<Future<Object>, AtomicInteger> future = taskFutureMap.get(objId);
        if (future == null)
        {
            MObjectEntity mo = moDal.get(objId);
            if (mo == null)
            {
                throw new Exception("对象:" + objId + "不存在，不能创建相应的采集任务");
            }
            ArrayList<ObjAttribEntity> attrs = oadao.getCollObjAttribByID(mo.getObjId());
            if (attrs == null || attrs.size() == 0)
            {
                throw new Exception("对象:" + mo.getObjName() + "无相应的采集指标");
            }
            int interval = computeInterval(attrs);

            CollectTaskEntity newTask = task.clone();
            newTask.setMo(mo);
            mo.setAttrs(attrs);
            newTask.setCollTimespan(interval);

            logger.info("启动对象:" + mo.getObjName() + "的采集任务,采集间隔:" + interval);
            Future<Object> taskFu = executor.scheduleAtFixedRate(newTask);
            AtomicInteger count = new AtomicInteger(1);
            future = new TwoTuple<Future<Object>, AtomicInteger>(taskFu, count);
            taskFutureMap.put(objId, future);
        }
    }

    /**
     * 计算监控对象的采集间隔(取各个对象属性采集间隔的最大公约数)
     * @param attrs
     * @return
     */
    private int computeInterval(ArrayList<ObjAttribEntity> attrs)
    {
        ObjAttribEntity first = attrs.get(0);
        if (first.getCollTimespan() <= 0)
        {
            first.setCollTimespan(300);
        }
        int temp = first.getCollTimespan();
        for (int i = 1; i < attrs.size(); i++)
        {
            if (attrs.get(i).getCollTimespan() <= 0)
            {
                attrs.get(i).setCollTimespan(300);
            }
            temp = gys(temp, attrs.get(i).getCollTimespan());
        }
        return temp;
    }

    /**
     * 计算最大公约数
     * @param paramA
     * @param paramB
     * @return
     */
    private static int gys(int paramA, int paramB)
    {
        if (paramA > paramB)
        {
            if (paramA % paramB == 0)
            {
                return paramB;
            }
            else
            {
                return gys(paramB, paramA % paramB);
            }
        }
        else
        {
            if (paramB % paramA == 0)
            {
                return paramA;
            }
            else
            {
                return gys(paramA, paramB % paramA);
            }
        }
    }

    /**
     * 中断任务
     * @param task 任务
     * @throws Exception 异常
     */
    public synchronized void interrupt(CollectTaskEntity task) throws Exception
    {
        List<Obj2GroupEntity> obj2GrpLst = obj2GrpMap.get(task.getTaskId());
        if (obj2GrpLst == null)
        {
            return;
        }
        obj2GrpMap.remove(task.getTaskId());

        for (Obj2GroupEntity obj2Grp : obj2GrpLst)
        {
            TwoTuple<Future<Object>, AtomicInteger> future = taskFutureMap.get(obj2Grp.getObjId());
            if (future == null)
            {
                continue;
            }
            if (future.second.decrementAndGet() <= 0)
            {
                logger.info("中止对象:" + obj2Grp.getObjId() + "采集任务");
                taskFutureMap.remove(obj2Grp.getObjId());
                if (!future.first.isCancelled() && !future.first.isDone())
                {
                    future.first.cancel(true);
                }
            }
        }
    }

    /**
     * 中断任务
     * @param objId 对象id
     * @param task 任务
     * @throws Exception 异常
     */
    public synchronized void interrupt(CollectTaskEntity task, int objId) throws Exception
    {
        List<Obj2GroupEntity> obj2GrpLst = obj2GrpMap.get(task.getTaskId());
        if (obj2GrpLst == null)
        {
            return;
        }

        TwoTuple<Future<Object>, AtomicInteger> future = taskFutureMap.get(objId);
        if (future == null)
        {
            return;
        }
        logger.info("中止对象:" + objId + "采集任务");
        taskFutureMap.remove(objId);
        if (!future.first.isCancelled() && !future.first.isDone())
        {
            future.first.cancel(true);
        }
    }

    /**
     * 注销资源
     */
    public synchronized void dispose()
    {
        if (executor != null)
        {
            executor.shutdownNow();
            executor = null;
        }
        if (obj2GrpMap != null)
        {
            obj2GrpMap.clear();
            obj2GrpMap = null;
        }
        if (taskFutureMap != null)
        {
            taskFutureMap.clear();
            taskFutureMap = null;
        }
    }

    /**
     * 获取当前任务数量
     * 
     * @return
     */
    public int getTaskNum()
    {
        return taskFutureMap.size();
    }

    public int getPoolSize()
    {
        return executor.getPoolSize();
    }

    public int getCorePoolSize()
    {
        return executor.getCorePoolSize();
    }

    public int getMaxPoolSize()
    {
        return executor.getMaximumPoolSize();
    }

    public int getActivePoolSize()
    {
        return executor.getActiveCount();
    }

    public int getQueueSize()
    {
        return executor.getQueue().size();
    }
}
