/************************************************************************
日 期: 2012-6-6
作 者: 郭祥
版 本: v1.3
描 述: TRAP报警事件处理
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.handle.TrapHandle;
import jetsennet.jbmp.alarm.relevance.AlarmEventFilterManager;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.TrapEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * TRAP报警事件处理。所有事件当做新事件处理。
 * @author 郭祥
 */
public class TrapAlarmEventHandle extends AbsAlarmEventHandle
{

    private AlarmEventDal aedal;
    private TrapEventDal tedal;
    private AlarmEventBuffer eventBuffer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TrapAlarmEventHandle.class);

    /**
     * 构造方法
     */
    public TrapAlarmEventHandle()
    {
        super();
        aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
        tedal = ClassWrapper.wrapTrans(TrapEventDal.class);
        eventBuffer = AlarmEventBuffer.getInstance();
    }

    @Override
    protected void submit()
    {
        future = single.submit(new DelAlarmEventHandleCallable());
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

    /**
     * 处理告警事件
     * @param newEvent
     */
    private ArrayList<AlarmEventEntity> handleEvent(AlarmEventEntity newEvent, int trapId)
    {
        if (newEvent == null || newEvent.getObjId() < 0)
        {
            return null;
        }
        if (!AlarmEventFilterManager.getInstance().filter(newEvent))
        {
            logger.debug("相关性分析：存在相关报警，丢弃报警：" + newEvent);
            return null;
        }
        ArrayList<AlarmEventEntity> retval = new ArrayList<AlarmEventEntity>();

        int objAttribId = newEvent.getObjAttrId();
        // 是否存在旧报警
        AlarmEventEntity preEvent = eventBuffer.get(objAttribId);
        try
        {
            int alarmEvtId = -1;
            // 不存在旧报警
            if (preEvent == null)
            {
                eventBuffer.insert(objAttribId, newEvent);
                alarmEvtId = aedal.insert(newEvent);
                this.sendAlarmEvent(newEvent, AlarmEventEntity.ALARM_SEND_NEW);
            }
            else
            {
                // 存在旧报警
                // 新旧报警不属于同一报警
                if (preEvent.getAlarmId() != newEvent.getAlarmId())
                {
                    eventBuffer.update(objAttribId, newEvent);
                    alarmEvtId = aedal.insert(newEvent);
                    this.sendAlarmEvent(newEvent, AlarmEventEntity.ALARM_SEND_NEW);
                }
                else
                {
                    // 新旧报警属于同一报警
                    preEvent.setAlarmCount(preEvent.getAlarmCount() + 1);
                    alarmEvtId = preEvent.getAlarmEvtId();
                    aedal.updateEventCount(preEvent.getAlarmEvtId(), preEvent.getAlarmCount());
                    this.sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_UPDATE_COUNT);
                }
            }
            tedal.updateAlarmEvtId(trapId, alarmEvtId);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 报警事件处理线程
     */
    private class DelAlarmEventHandleCallable implements Callable<Integer>
    {

        @Override
        public Integer call() throws Exception
        {
            while (!isStop)
            {
                try
                {
                    AlarmEventEntityX aeex = buffer.get();
                    if (aeex != null)
                    {
                        this.handle(aeex);
                    }
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
            return CALL_RETURN;
        }

        private void handle(AlarmEventEntityX aee)
        {
            int eventType = aee.getEventType();
            switch (eventType)
            {
            case AlarmEventEntityX.EVENT_TYPE_COMMEN:
                AlarmEventEntity event = aee.getAlarm();
                if (event != null)
                {
                    if (aee.getParams(TrapHandle.TRAP_EVENT_ID) == null)
                    {
                        logger.error("错误的Trap报警");
                        return;
                    }
                    int trapId = Integer.valueOf(aee.getParams(TrapHandle.TRAP_EVENT_ID));
                    if (event != null)
                    {
                        handleEvent(event, trapId);
                    }
                }
                else
                {
                    // 处理报警恢复，Trap忽略这一项
                }
                break;
            case AlarmEventEntityX.EVENT_TYPE_STOP:
                logger.info("报警模块：TRAP报警事件处理线程。从阻塞状态被唤醒，准备关闭该线程。");
                break;
            default:
                logger.debug(String.format("报警模块：%s不处理状态为%s的数据。", "TRAP报警事件处理线程", eventType));
            }
        }
    }
}
