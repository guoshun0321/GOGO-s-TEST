/************************************************************************
日 期：2011-3-9
作 者: 郭祥
版 本：v1.3
描 述: 报警开始和关闭。
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollDataListener;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventBuffer;
import jetsennet.jbmp.alarm.eventhandle.AlarmEventDispatch;
import jetsennet.jbmp.alarm.rule.AlarmRuleManager;
import jetsennet.jbmp.dataaccess.buffer.DynamicMObject;

/**
 * 报警开始和关闭。 单例。
 * @author 郭祥
 */
public final class AlarmManager
{

    /**
     * 获取数据模块
     */
    private CollDataListener lis;
    /**
     * 报警规则
     */
    private AlarmRuleManager rule;
    /**
     * 报警事件处理模块
     */
    private AlarmEventDispatch manager;
    /**
     * 开始标志
     */
    private boolean startFlag = false;
    /**
     * 锁
     */
    private Lock lock;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AlarmManager.class);

    // <editor-fold defaultstate="collapsed" desc="单例">
    private static AlarmManager instance = new AlarmManager();

    private AlarmManager()
    {
        startFlag = false;
        lock = new ReentrantLock();
    }

    public static AlarmManager getInstance()
    {
        return instance;
    }

    // </editor-fold>

    /**
     * 开始报警
     */
    public synchronized void start()
    {
        Lock l = lock;
        l.lock();
        try
        {
            logger.info("报警模块：报警模块初始化。");
            this.initAlarmRule();
            this.initAlarmEventHandle();
            this.initGetCollData();
            this.initDBBuffer();
            startFlag = true;
            logger.info("报警模块：报警模块启动成功。");
        }
        catch (Exception ex)
        {
            String msg = "报警模块：报警模块启动失败。";
            logger.error(msg, ex);
            this.stop();
            throw new AlarmException(msg, ex);
        }
        finally
        {
            l.unlock();
        }
    }

    /**
     * 报警结束
     */
    public synchronized void stop()
    {
        Lock l = lock;
        l.lock();
        try
        {
            startFlag = false;
            if (lis != null)
            {
                try
                {
                    lis.stop();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    lis = null;
                }
            }
            if (rule != null)
            {
                try
                {
                    rule.stop();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    rule = null;
                }
            }
            if (manager != null)
            {
                try
                {
                    manager.stop();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    manager = null;
                }
            }
            try
            {
                // 关闭DynamicMObject
                DynamicMObject.getInstance().stop();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            logger.info("报警模块：报警结束。");
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
     * 阻塞报警事件处理
     */
    public void blockAlarmEventHandle()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (startFlag)
            {
                manager.block();
            }
            else
            {
                logger.error("报警模块：报警模块未开启，请先开启报警模块。(blockAlarmEventHandle)");
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
     * 恢复报警事件处理
     */
    public void unblockAlarmEventHandle()
    {
        Lock l = lock;
        l.lock();
        try
        {
            if (startFlag)
            {
                manager.unblock();
            }
            else
            {
                logger.error("报警模块：报警模块未开启，请先开启报警模块。(unblockAlarmEventHandle)");
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

    // <editor-fold defaultstate="collapsed" desc="初始化">
    /**
     * 报警事件处理模块开始
     */
    private void initAlarmEventHandle()
    {
        try
        {
            logger.info("报警模块：报警事件处理模块开始启动。");
            manager = AlarmEventDispatch.getInstance();
            manager.start();
            logger.info("报警模块：报警事件处理模块启动成功。");
        }
        catch (Exception ex)
        {
            String msg = "报警模块：报警事件处理模块启动失败。";
            throw new AlarmException(msg, ex);
        }
    }

    /**
     * 报警规则
     */
    private void initAlarmRule()
    {
        try
        {
            logger.info("报警模块：报警规则模块开始启动。");
            rule = AlarmRuleManager.getInstance();
            rule.start();
            logger.info("报警模块：报警规则模块启动成功。");
        }
        catch (Exception ex)
        {
            String msg = "报警模块：报警规则模块启动失败。";
            throw new AlarmException(msg, ex);
        }
    }

    /**
     * 开始获取数据线程
     */
    private void initGetCollData()
    {
        try
        {
            if (lis == null)
            {
                lis = new CollDataListener();
            }
            lis.start();
        }
        catch (Exception ex)
        {
            String msg = "报警模块：数据获取模块启动失败。";
            throw new AlarmException(msg, ex);
        }
    }

    /**
     * 开始数据库缓存
     */
    private void initDBBuffer()
    {
        try
        {
            logger.info("报警模块：采集缓存启动。");
            logger.info("报警模块：对象缓存启动。");
            DynamicMObject.getInstance().start();
            logger.info("报警模块：报警事件缓存启动。");
            AlarmEventBuffer.getInstance();
        }
        catch (Exception ex)
        {
            String msg = "报警模块：数据库缓存启动失败。";
            throw new AlarmException(msg, ex);
        }
    }

    // </editor-fold>

    /**
     * 主方法
     * @param args 参数
     * @throws InterruptedException 异常
     */
    public static void main(String[] args) throws InterruptedException
    {
        AlarmManager alarm = new AlarmManager();
        alarm.start();
        TimeUnit.SECONDS.sleep(10);
        alarm.stop();
    }
}
