/************************************************************************
日 期：
作 者: 梁洪杰
版 本：v1.3
描 述: 采集管理器
历 史：
 ************************************************************************/
package jetsennet.jbmp.datacollect.scheduler;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.eventhandle.AlarmEventBuffer;
import jetsennet.jbmp.dataaccess.CollectTaskDal;
import jetsennet.jbmp.dataaccess.CollectorDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.CollectorEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jbmp.util.TaskUtil;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * @author lianghongjie 采集管理器
 */
public final class CollManager
{

    private static final Logger logger = Logger.getLogger(CollManager.class);
    /**
     * 单例
     */
    private static CollManager instance = new CollManager();
    /**
     * 采集进程对应的采集器对象，每个进程唯一
     */
    private CollectorEntity collector;
    /**
     * 采集任务调度器
     */
    private CollTaskScheduler scheduler;
    /**
     * 上载管理器
     */
    private UploadManager uploadMgr;
    /**
     * 状态标记
     */
    private boolean isStart;
    /**
     * 数据库服务
     */
    private CollectTaskDal ctdao;
    private CollectorDal cdao;

    private CollManager()
    {
        ctdao = ClassWrapper.wrapTrans(CollectTaskDal.class);
        cdao = ClassWrapper.wrapTrans(CollectorDal.class);
    }

    public static CollManager getInstance()
    {
        return instance;
    }

    /**
     * 开始执行任务
     */
    public void start()
    {
        if (isStart())
        {
            return;
        }
        try
        {
            initCollector();
            initTask();
            initScheduler();
            initUploadMgr();
            dispatchTask();
            setStart(true);
            logger.info("采集器已启动");
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 结束执行
     */
    public void stop()
    {
        if (!isStart())
        {
            return;
        }
        if (scheduler != null)
        {
            scheduler.dispose();
        }
        if (uploadMgr != null)
        {
            uploadMgr.dispose();
        }
        try
        {
            initTask();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        AlarmEventBuffer.getInstance().clear();
        setStart(false);
        logger.info("采集器已停止");
    }

    /**
     * 初始化采集器
     * @throws Exception
     */
    private void initCollector() throws Exception
    {
        CollectorEntity coll = null;
        int collId = XmlCfgUtil.getIntValue(CollConstants.COLL_CFG_FILE, CollConstants.COLL_ID_CFG, -1);
        if (collId < 0)
        {
            throw new CollectorException("采集器没有设置");
        }
        else
        {
            coll = cdao.get(collId);
            if (coll == null)
            {
                throw new CollectorException("数据库中找不到编号为" + collId + "的采集器");
            }
        }
        logger.info("初始化采集器");
        collector = coll;
    }

    /**
     * 初始化所有采集任务，将状态为启动的任务修改为停止
     */
    private void initTask() throws CollectorException
    {
        try
        {
            ctdao.initCollectTask(collector.getCollId());
        }
        catch (Exception ex)
        {
            logger.error("初始化采集任务失败。");
        }
    }

    /**
     * 初始化调度器
     */
    private void initScheduler()
    {
        if (scheduler != null)
        {
            scheduler.dispose();
        }
        scheduler = new CollTaskScheduler();
        scheduler.init();
    }

    /**
     * 初始化上载管理器
     */
    private void initUploadMgr()
    {
        if (uploadMgr != null)
        {
            uploadMgr.dispose();
        }
        uploadMgr = new UploadManager();
        uploadMgr.init();
    }

    /**
     * 任务分发
     * @throws Exception
     */
    private void dispatchTask() throws Exception
    {
        ArrayList<CollectTaskEntity> ctasks = ctdao.getByCollId(collector.getCollId());
        logger.info("启动采集任务,采集任务数量:" + ctasks.size());
        for (CollectTaskEntity ctask : ctasks)
        {
            this.handleSchedule(ctask);
        }
    }

    /**
     * @param task 任务
     * @return 结果
     */
    public Map<Integer, Integer> getCollectTaskInfo(CollectTaskEntity task)
    {
        try
        {
            return scheduler.getCollectTaskInfo(task);
        }
        catch (Exception e)
        {
            logger.error("获取任务:" + task.getTaskId() + "的状态异常");
            return null;
        }
    }

    /**
     * 调度任务
     * @param task 任务
     * @return 结果
     */
    public boolean handleSchedule(CollectTaskEntity task)
    {
        try
        {
            if (TaskUtil.isTimeout(task))
            {
                updateStatus(task, CollectTaskEntity.TASK_STATE_DELETE);
                logger.info("任务:" + task.getTaskId() + "已完成");
            }
            else
            {
                scheduler.schedule(task);
                updateStatus(task, CollectTaskEntity.TASK_STATE_RUNNING);
                logger.info("启动任务:" + task.getTaskId() + "成功");
            }
            return true;
        }
        catch (Exception e)
        {
            logger.error("启动任务:" + task.getTaskId() + "异常", e);
            return false;
        }
    }

    /**
     * 调度任务
     * @param task 任务
     * @param objId 对象id
     * @return 结果
     */
    public boolean handleSchedule(CollectTaskEntity task, int objId)
    {
        if (!isStart())
        {
            return false;
        }
        try
        {
            if (TaskUtil.isTimeout(task))
            {
                updateStatus(task, CollectTaskEntity.TASK_STATE_DELETE);
                logger.info("任务:" + task.getTaskId() + "已完成");
            }
            else
            {
                scheduler.schedule(task, objId);
                updateStatus(task, CollectTaskEntity.TASK_STATE_RUNNING);
                logger.info("启动任务:" + task.getTaskId() + "成功");
            }
            return true;
        }
        catch (Exception e)
        {
            logger.error("启动任务:" + task.getTaskId() + "异常");
            return false;
        }
    }

    /**
     * 处理过期的任务
     * @param task 任务
     */
    public void handleTimeout(CollectTaskEntity task)
    {
        try
        {
            updateStatus(task, CollectTaskEntity.TASK_STATE_DELETE);
        }
        catch (Exception e)
        {
            logger.error("更新任务状态:" + task.getTaskId() + "异常");
        }
    }

    /**
     * 中断某个任务执行
     * @param task 任务
     * @return 结果
     */
    public boolean handleInterrupt(CollectTaskEntity task)
    {
        try
        {
            scheduler.interrupt(task);
            updateStatus(task, CollectTaskEntity.TASK_STATE_NEW);
            logger.info("停止任务:" + task.getTaskId() + "成功");
            return true;
        }
        catch (Exception e)
        {
            logger.error("停止任务:" + task.getTaskId() + "异常");
            return false;
        }
    }

    /**
     * 中断某个任务执行
     * @param objId 对象ID
     * @param task 任务
     * @return 结果
     */
    public boolean handleInterrupt(CollectTaskEntity task, int objId)
    {
        if (!isStart())
        {
            return false;
        }
        try
        {
            scheduler.interrupt(task, objId);
            // updateStatus(task, CollectTaskEntity.TASK_STATE_NEW);
            logger.info("停止任务:" + task.getTaskId() + "成功");
            return true;
        }
        catch (Exception e)
        {
            logger.error("停止任务:" + task.getTaskId() + "异常");
            return false;
        }
    }

    /**
     * 更新任务状态
     * @param task
     * @param status
     * @throws CollectorException
     */
    private void updateStatus(CollectTaskEntity task, int status) throws CollectorException
    {
        try
        {
            task.setTaskState(status);
            ctdao.updateState(task);
        }
        catch (Exception ex)
        {
            throw new CollectorException(ex.getMessage(), ex);
        }
    }

    public boolean isStart()
    {
        return isStart;
    }

    /**
     * @param isStart 开始
     */
    public void setStart(boolean isStart)
    {
        this.isStart = isStart;
    }
}
