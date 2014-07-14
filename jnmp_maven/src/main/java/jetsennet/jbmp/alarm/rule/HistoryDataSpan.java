package jetsennet.jbmp.alarm.rule;

import java.util.Date;

import org.apache.log4j.Logger;

public class HistoryDataSpan extends AbsHistoryData
{

    /**
     * 数据缓存
     */
    private TimeQueue<HistoryDataEntry> queue;

    private static final Logger logger = Logger.getLogger(HistoryDataSpan.class);

    public HistoryDataSpan(int objAttrId, AbsAlarmRule rule)
    {
        super(objAttrId, rule);
        this.init();
    }

    private void init()
    {
        long time = rule.getAlarm().getCheckSpan();
        queue = new TimeQueue<HistoryDataEntry>(time);
    }

    @Override
    public boolean add(String value, Date time)
    {
        boolean retval = true;
        try
        {
            queue.add(new HistoryDataEntry(value, time), time.getTime());
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = false;
        }
        return retval;
    }

    @Override
    public boolean clear()
    {
        queue.clear();
        return true;
    }

    public TimeQueue<HistoryDataEntry> getQueue()
    {
        return queue;
    }

}
