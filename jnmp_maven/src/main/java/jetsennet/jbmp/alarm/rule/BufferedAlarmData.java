package jetsennet.jbmp.alarm.rule;

import jetsennet.jbmp.entity.AlarmEventEntity;

import org.apache.log4j.Logger;

/**
 * 报警事件缓存
 * 
 * @author 郭祥
 */
public class BufferedAlarmData
{

    /**
     * 保留的报警
     */
    private CircularArray<AlarmEventEntity> alarms;
    /**
     * 检测时间，单位（豪秒）
     */
    private long timeSpan;
    /**
     * 缓存大小
     */
    private int bufferNum;
    /**
     * 最大时间
     */
    private long maxTime;
    /**
     * 最小时间
     */
    private long minTime;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HistoryDataTime.class);

    /**
     * 构造函数
     * @param timeSpan 时间间隔，单位（秒）
     * @param bufferNum 参数
     */
    public BufferedAlarmData(long timeSpan, int bufferNum)
    {
        // 传入单位为秒，需要转换成毫秒
        this.timeSpan = timeSpan * 1000;
        this.bufferNum = bufferNum;
        this.clear();
    }

    /**
     * 清除
     */
    public void clear()
    {
        if (bufferNum > 0)
        {
            alarms = new CircularArray<AlarmEventEntity>(AlarmEventEntity.class, bufferNum);
        }
        else
        {
            throw new IllegalArgumentException();
        }
        maxTime = -1;
        minTime = -1;
    }

    /**
     * @param iEvent 参数
     * @param timeSpan 参数
     * @param bufferNum 参数
     * @return 结果
     */
    public boolean addAndJudge(AlarmEventEntity iEvent, long timeSpan, int bufferNum)
    {
        if (this.timeSpan != timeSpan || this.bufferNum != bufferNum)
        {
            this.timeSpan = timeSpan;
            this.bufferNum = bufferNum;
            this.clear();
        }
        alarms.add(iEvent);
        this.ensureTime(iEvent.getCollTime());
        if (alarms.isFull() && (maxTime - minTime) > this.timeSpan)
        {
            return true;
        }
        return false;
    }

    /**
     * 确定最大最小时间
     * @param collTime 当前时间
     */
    public void ensureTime(long collTime)
    {
        if (maxTime < collTime || maxTime == -1)
        {
            maxTime = collTime;
        }
        if (minTime > collTime || minTime == -1)
        {
            minTime = collTime;
        }
    }

}
