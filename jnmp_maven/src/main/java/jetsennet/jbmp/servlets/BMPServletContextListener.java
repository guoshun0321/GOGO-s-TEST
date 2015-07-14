package jetsennet.jbmp.servlets;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.AlarmStatistic;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.protocols.jgroup.AbsGroupServer;

/**
 * @author lianghongjie 集群监听器
 */
public class BMPServletContextListener extends AbsGroupServer implements ServletContextListener
{

    private static final Logger logger = Logger.getLogger(BMPServletContextListener.class);

    private static BMPServletContextListener instance;

    /**
     * 定时器：用于同步数据库中的对象/对象组信息
     */
    private Timer timer;

    public static BMPServletContextListener getInstance()
    {
        return instance;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        instance = this;
        try
        {
            // 在这里加入jgroup集群
            logger.info("AlarmServletContextListener init begin.");
            start(getClusterCfgFile(sce));

            timer = new Timer();
            int period = getPeriod(sce);
            timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    refresh();
                }
            }, period, period);
            logger.info("AlarmServletContextListener init end.");
        }
        catch (Exception e)
        {
            logger.error("AlarmServletContextListener init exception.", e);
        }
    }

    private String getClusterCfgFile(ServletContextEvent sce)
    {
        Object obj = sce.getServletContext().getInitParameter("clusterCfgFile");
        if (obj != null)
        {
            return "" + obj;
        }
        return "flush-tcp-web.xml";
    }

    private int getPeriod(ServletContextEvent sce)
    {
        int period = 30 * 1000;
        Object obj = sce.getServletContext().getInitParameter("refreshPeriod");
        if (obj != null)
        {
            try
            {
                period = Integer.parseInt("" + obj);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return period;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        logger.info("AlarmServletContextListener stop begin.");
        if (timer != null)
        {
            timer.cancel();
        }
        stop();
        logger.info("AlarmServletContextListener stop end.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see jetsennet.jnmp.protocols.jgroup.AbsGroupServer#syncDB()
     */
    protected void syncDB()
    {
        try
        {
            AlarmStatistic.getInstance().reset();
        }
        catch (Exception e)
        {
            logger.error("同步报警统计信息异常.", e);
        }
    }

    /**
     * 同步数据库
     */
    private void refresh()
    {
        try
        {
            super.refreshObj2ColMap();
            AlarmStatistic.getInstance().refresh();
        }
        catch (Exception e)
        {
            logger.error("同步对象/对象组信息异常.", e);
        }
    }

    @Override
    public void handle(AlarmEventEntity alarm)
    {
        int sendType = alarm.getAlarmSend();
        switch (sendType)
        {
        case AlarmEventEntity.ALARM_SEND_NEW:
            AlarmStatistic.getInstance().addAlarm(alarm);
            break;
        case AlarmEventEntity.ALARM_SEND_RESUME:
            if (alarm.getEventState() == AlarmEventEntity.EVENT_STATE_CLEAR)
            {
                AlarmStatistic.getInstance().removeAlarm(alarm, false);
            }
            else
            {
                AlarmStatistic.getInstance().updateAlarm(alarm);
            }
            break;
        case AlarmEventEntity.ALARM_SEND_UPDATE:
            AlarmStatistic.getInstance().updateAlarm(alarm);
            break;
        case AlarmEventEntity.OBJ_COLL_STATE:
            AlarmStatistic.getInstance().updateCollState(alarm.getAlarmDesc());
            break;
        default:
            logger.error(String.format("未知类型：%s，数据丢弃。", sendType));
        }
    }
}
