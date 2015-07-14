package jetsennet.jbmp.datacollect.collector;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.ObjectName;

import jetsennet.jbmp.manage.MBeanRegister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统计采集器运行情况
 * 
 * @author 郭祥
 *
 */
public class CollectorStatistic implements CollectorStatisticMBean
{

    /**
     * 统计采集执行时间
     */
    private ConcurrentHashMap<Class<? extends ICollector>, Long> spanStatis;
    /**
     * 统计采集执行次数
     */
    private ConcurrentHashMap<Class<? extends ICollector>, Long> timeStatis;

    private LinkedBlockingQueue<StatisticEntry> stats;

    private static final Logger logger = LoggerFactory.getLogger(CollectorStatistic.class);

    private static final CollectorStatistic instance = new CollectorStatistic();

    private CollectorStatistic()
    {
        timeStatis = new ConcurrentHashMap<Class<? extends ICollector>, Long>();
        spanStatis = new ConcurrentHashMap<Class<? extends ICollector>, Long>();
        stats = new LinkedBlockingQueue<CollectorStatistic.StatisticEntry>();

        try
        {
            MBeanRegister.register("JBMP.CollectorStatistic:type=statistic", this);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }

        CollectorStatisticThread thread = new CollectorStatisticThread("CollectorStatisticThread");
        thread.start();
    }

    public static CollectorStatistic getInstance()
    {
        return instance;
    }

    public void add(Class<? extends ICollector> clz, int objId, long time)
    {
        stats.add(new StatisticEntry(clz, objId, time));
    }

    @Override
    public String getTotal()
    {
        StringBuilder sb = new StringBuilder();
        Set<Class<? extends ICollector>> keys = spanStatis.keySet();
        for (Class<? extends ICollector> key : keys)
        {
            Long span = spanStatis.get(key);
            Long time = timeStatis.get(key);
            sb.append(key.getName()).append(":").append(span).append(":").append(time).append(";");
        }
        return sb.toString();
    }

    @Override
    public long getSnmp()
    {
        long retval = -1;
        Long span = spanStatis.get(SNMPCollector.class);
        Long time = timeStatis.get(SNMPCollector.class);
        if (span != null && time != null)
        {
            retval = span / time;
        }
        return retval;
    }

    private class StatisticEntry
    {
        public final Class<? extends ICollector> clz;
        public final int objId;
        public final long time;

        public StatisticEntry(Class<? extends ICollector> clz, int objId, long time)
        {
            this.clz = clz;
            this.objId = objId;
            this.time = time;
        }
    }

    private class CollectorStatisticThread extends Thread
    {

        public CollectorStatisticThread(String name)
        {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run()
        {
            StatisticEntry entry = null;
            try
            {
                while (true)
                {
                    entry = stats.take();
                    Long spanL = spanStatis.get(entry.clz);
                    Long timeL = timeStatis.get(entry.clz);
                    if (spanL == null)
                    {
                        spanStatis.put(entry.clz, entry.time);
                        timeStatis.put(entry.clz, 1l);
                    }
                    else
                    {
                        spanL += entry.time;
                        timeL += 1;
                        spanStatis.put(entry.clz, spanL);
                        timeStatis.put(entry.clz, timeL);
                    }
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
    }

}
