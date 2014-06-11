package jetsennet.jsmp.nav.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorServlet extends HttpServlet
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
    private static ArrayBlockingQueue<MonitorMsg> queue = new ArrayBlockingQueue<>(MAX_LENGTH);
    /**
     * 执行线程
     */
    private Thread mThread;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MonitorServlet.class);

    @Override
    public void init() throws ServletException
    {
        try
        {
            this.initMBeans();
            mThread = new Thread(new MonitorRunnable(), "NAV_MONITOR");
            mThread.setDaemon(true);
            mThread.start();
            logger.info("启动监控线程成功!");
        }
        catch (Exception ex)
        {
            logger.info("启动监控线程失败!", ex);
        }
    }

    @Override
    public void destroy()
    {
        try
        {
            unregisterMBeans();
            if (mThread != null)
            {
                mThread.interrupt();
            }
            queue.offer(new MonitorMsg());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        logger.info("关闭监控线程!");
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

    public static void put(MonitorMsg msg)
    {
        boolean isOk = queue.offer(msg);
        while (!isOk)
        {
            // 队列满时，从队列中取出100个数据并丢弃数据
            List<MonitorMsg> temp = new ArrayList<MonitorMsg>(DISCARD_LENGTH);
            queue.drainTo(temp, DISCARD_LENGTH);
            logger.error("从消息队列丢弃数据，条数:" + temp.size());
            isOk = queue.offer(msg);
        }
    }

    private class MonitorRunnable implements Runnable
    {
        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    MonitorMsg msg = queue.take();
                    this.handleMsg(msg);
                }
                catch (InterruptedException ex)
                {
                    logger.info("准备关闭监控线程！", ex);
                }
                catch (Exception ex)
                {
                    logger.info("", ex);
                }
            }
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
                    logger.info("准备关闭监控线程(msg)！");
                }
            }
            else
            {
                logger.debug("丢弃消息：" + msg.toString());
            }
        }
    }

}
