/************************************************************************
日 期: 2012-1-12
作 者: 郭祥
版 本: v1.3
描 述: 报警事件缓存
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件缓存
 * @author 郭祥
 */
public class AlarmEventXBuffer
{

    protected LinkedBlockingQueue<AlarmEventEntityX> queue;
    private static final Logger logger = Logger.getLogger(AlarmEventXBuffer.class);

    /**
     * 构造方法
     */
    public AlarmEventXBuffer()
    {
        queue = new LinkedBlockingQueue<AlarmEventEntityX>();
    }

    /**
     * 获取数据
     * @return 异常时返回NULL
     */
    public AlarmEventEntityX get()
    {
        try
        {
            return queue.take();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return null;
    }

    /**
     * 添加数据
     * @param event 参数
     */
    public void put(AlarmEventEntityX event)
    {
        queue.offer(event);
    }

    /**
     * 添加一般报警
     * @param event 参数
     */
    public void put(AlarmEventEntity event)
    {
        AlarmEventEntityX aeex = new AlarmEventEntityX();
        aeex.setAlarm(event);
        aeex.setEventType(AlarmEventEntityX.EVENT_TYPE_COMMEN);
        this.put(aeex);
    }

    /**
     * 添加上一次报警恢复事件
     * @param objAttribId 参数
     * @param time 参数
     */
    public void putRecoverEvent(int objAttribId, long time)
    {
        AlarmEventEntityX recover = new AlarmEventEntityX();
        recover.setAlarm(null);
        recover.setEventType(AlarmEventEntityX.EVENT_TYPE_COMMEN);
        recover.setObjAttrId(objAttribId);
        recover.setRecoverTime(time);
        this.put(recover);
    }

    /**
     * 添加手动清除事件
     * @param objAttribId 参数
     * @param eventId 参数
     */
    public void putRecoverManu(int objAttribId, int eventId)
    {
        AlarmEventEntityX recover = new AlarmEventEntityX();
        recover.setAlarm(null);
        recover.setEventType(AlarmEventEntityX.EVENT_TYPE_MANU);
        recover.setObjAttrId(objAttribId);
        recover.setEventId(eventId);
        this.put(recover);
    }

    /**
     * 添加规则改变事件
     * @param objAttribId 参数
     */
    public void putRecoverRule(int objAttribId)
    {
        AlarmEventEntityX recover = new AlarmEventEntityX();
        recover.setAlarm(null);
        recover.setEventType(AlarmEventEntityX.EVENT_TYPE_CHANGE);
        recover.setObjAttrId(objAttribId);
        this.put(recover);
    }

    /**
     * 添加空类型数据，用于线程停止
     */
    public void putStopEvent()
    {
        AlarmEventEntityX empty = new AlarmEventEntityX();
        empty.setAlarm(null);
        empty.setEventType(AlarmEventEntityX.EVENT_TYPE_STOP);
        this.put(empty);
    }
}
