package jetsennet.jsmp.nav.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;

import jetsennet.jsmp.nav.util.ThreadWaitFutrue;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor
{

    /**
     * 方法调用监控MBean
     */
    private MethodInvoke mi;
    /**
     * 一次性丢弃长度
     */
    private static final int DISCARD_LENGTH = 100;
    /**
     * 最大长度
     */
    private static final int MAX_LENGTH = 10000;
    /**
     * 消息队列
     */
    private LinkedBlockingQueue<MonitorMsg> queue;
    /**
     * 执行线程
     */
    private Thread mThread;
    /**
     * 线程等待句柄
     */
    private ThreadWaitFutrue wait;
    /**
     * 模块状态
     */
    private volatile boolean isStart = false;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MonitorServlet.class);

    private static final Monitor instance = new Monitor();

    private Monitor()
    {
    }

    public static final Monitor getInstance()
    {
        return instance;
    }

    public synchronized void start()
    {
        try
        {
            if (!isStart)
            {
                // 新建队列
                queue = new LinkedBlockingQueue<MonitorMsg>(MAX_LENGTH);
                // 注册MBeans
                this.initMBeans();
                // 启动执行线程
                wait = new ThreadWaitFutrue();
                mThread = new Thread(new MonitorRunnable(wait), "NAV_MONITOR");
                mThread.setDaemon(true);
                mThread.start();
                // 设置状态
                logger.info("监控模块启动成功!");
                this.isStart = true;
            }
            else
            {
                logger.info("监控模块重复启动!");
            }
        }
        catch (Exception ex)
        {
            logger.info("监控模块启动失败!", ex);
        }
    }

    public synchronized void stop()
    {
        try
        {
            if (isStart)
            {
                // 设置状态为停止，阻止进一步存放数据
                this.isStart = false;
                // 确认关闭线程
                if (mThread != null)
                {
                    mThread.interrupt();
                }
                queue.offer(new MonitorMsg());
                wait.get(false);
                // 反注册MBeans
                unregisterMBeans();
                // 清空队列
                queue.clear();
                logger.info("监控模块关闭成功!");
            }
            else
            {
                logger.info("监控模块重复关闭!");
            }
        }
        catch (Exception ex)
        {
            this.isStart = false;
            logger.error("监控模块关闭失败！", ex);
        }
    }

    public boolean isStart()
    {
        return this.isStart;
    }

    private void initMBeans()
    {
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            // 方法调用监控
            mi = new MethodInvoke();
            ObjectName miName = new ObjectName("jetsen.jsmp.nav:name=methodInvoke");
            mbs.registerMBean((MethodInvokeMBean) mi, miName);
        }
        catch (Exception ex)
        {
            throw new UncheckedNavException(ex);
        }
    }

    private void unregisterMBeans()
    {
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            // 卸载方法调用监控
            ObjectName miName = new ObjectName("jetsen.jsmp.nav:name=methodInvoke");
            mbs.unregisterMBean(miName);
        }
        catch (Exception ex)
        {
            throw new UncheckedNavException(ex);
        }
    }

    /**
     * 向监控模块放入消息。当监控模块队列满时，从队列中取出100个数据并丢弃数据。
     * 
     * @param msg
     */
    public void put(MonitorMsg msg)
    {
        if (isStart)
        {
            boolean isOk = queue.offer(msg);
            while (!isOk)
            {
                // 队列满时，从队列中取出100个数据并丢弃数据
                List<MonitorMsg> temp = new ArrayList<MonitorMsg>(DISCARD_LENGTH);
                queue.drainTo(temp, DISCARD_LENGTH);
                logger.error("消息队列满，准备丢弃数据。条数:" + temp.size());
                isOk = queue.offer(msg);
            }
        }
        else
        {
            logger.info("监控模块未启动，丢弃监控消息！");
        }
    }

    private class MonitorRunnable implements Runnable
    {

        private ThreadWaitFutrue futrue;

        public MonitorRunnable(ThreadWaitFutrue futrue)
        {
            this.futrue = futrue;
        }

        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    MonitorMsg msg = queue.poll(1000, TimeUnit.MILLISECONDS);
                    if (msg != null)
                    {
                        this.handleMsg(msg);
                    }
                }
                catch (InterruptedException ex)
                {
                    logger.info("准备关闭监控模块！", ex);
                    Thread.currentThread().interrupt();
                }
                catch (Exception ex)
                {
                    logger.info("", ex);
                }
            }
            futrue.put(null);
        }

        private void handleMsg(MonitorMsg msg)
        {
            if (msg instanceof MethodInvokeMMsg)
            {
                mi.add((MethodInvokeMMsg) msg);
            }
            else if (msg instanceof MonitorMsg)
            {
                if (Thread.currentThread().isInterrupted())
                {
                    logger.info("准备关闭监控模块(msg)！");
                }
            }
            else
            {
                logger.debug("丢弃消息：" + msg.toString());
            }
        }
    }

}
