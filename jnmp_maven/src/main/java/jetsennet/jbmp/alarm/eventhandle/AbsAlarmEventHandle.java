/************************************************************************
日 期: 2012-1-10
作 者: 郭祥
版 本: v1.3
描 述: 报警事件处理
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.RegResource;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.ClusterManager;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件处理
 * @author 郭祥
 */
public abstract class AbsAlarmEventHandle
{

    /**
     * 报警事件处理线程
     */
    protected ExecutorService single;
    /**
     * 判断线程结束
     */
    protected Future<Integer> future;
    /**
     * 缓存
     */
    protected AlarmEventXBuffer buffer;
    /**
     * flag
     */
    protected volatile boolean isStop;
    /**
     * 锁
     */
    protected Lock lock;
    /**
     * 数据库访问
     */
    protected AlarmEventDal aedao;
    /**
     * 集群
     */
    protected ClusterManager cluster;
    /**
     * 线程默认返回值
     */
    public static final int CALL_RETURN = 1;
    /**
     * 线程名称
     */
    protected String threadName;
    /**
     * 报警默认初始计数
     */
    protected static final int INITIAL_ALARM_COUNT = 1;
    /**
     * 日志
     */
    public static Logger logger = Logger.getLogger(AbsAlarmEventHandle.class);

    /**
     * 构造方法
     */
    public AbsAlarmEventHandle()
    {
        isStop = true;
        lock = new ReentrantLock();
        buffer = new AlarmEventXBuffer();
        aedao = ClassWrapper.wrapTrans(AlarmEventDal.class);
    }

    /**
     * 开始
     */
    public void start()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (isStop)
            {
                isStop = false;
                single = Executors.newSingleThreadExecutor();
                submit();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
    }

    protected abstract void submit();

    /**
     * 结束
     */
    public void stop()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (!isStop)
            {
                isStop = true;
                buffer.putStopEvent();
                if (future != null)
                {
                    future.get();
                }
                if (single != null)
                {
                    single.shutdownNow();
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 放置数据
     * @param alm 数据
     */
    public void put(AlarmEventEntity alm)
    {
        buffer.put(alm);
    }

    /**
     * 插入数据
     * @param alm 数据
     */
    public void put(AlarmEventEntityX alm)
    {
        buffer.put(alm);
    }

    protected void ensureCluster()
    {
        if (cluster == null)
        {
            Object obj = RegResource.get(RegResource.RESOURCE_CLUSTER_NAME);
            if (obj != null && obj instanceof ClusterManager)
            {
                cluster = (ClusterManager) obj;
            }
        }
    }

    /**
     * 发送报警
     * @param event
     */
    protected void sendAlarmEvent(AlarmEventEntity event, int sendType)
    {
        this.ensureCluster();
        if (cluster != null && event != null)
        {
            event.setAlarmSend(sendType);
            cluster.sendMessage(event);
        }
    }

    /**
     * 批量发送报警
     * @param events
     */
    protected void sendAlarmEvent(List<AlarmEventEntity> events, int sendType)
    {
        this.ensureCluster();
        if (events != null)
        {
            for (AlarmEventEntity event : events)
            {
                this.sendAlarmEvent(event, sendType);
            }
        }
    }

    /**
     * 通用日志格式
     * @param msg
     * @return
     */
    protected String debugMsg(String msg)
    {
        return String.format("%s线程：%s", threadName, msg);
    }
}
