/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 性能数据报警事件处理
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;

import jetsennet.jbmp.alarm.relevance.AlarmEventFilterManager;
import jetsennet.jbmp.entity.AlarmEventEntity;

import org.apache.log4j.Logger;

/**
 * 性能数据的报警事件处理
 * @author 郭祥
 */
public class PerformanceAlarmEventHandle extends AbsAlarmEventHandle
{

    /**
     * 报警事件缓存
     */
    private AlarmEventBuffer eventBuffer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(PerformanceAlarmEventHandle.class);

    /**
     * 构造函数
     */
    public PerformanceAlarmEventHandle()
    {
        super();
        this.threadName = "通用性能报警事件处理";
        eventBuffer = AlarmEventBuffer.getInstance();
    }

    @Override
    protected void submit()
    {
        future = single.submit(new AlarmEventHandleCallable());
    }

    /**
     * 处理报警产生
     * @param objAttribId
     * @param newEvent
     */
    private ArrayList<AlarmEventEntity> handleEvent(AlarmEventEntity newEvent) throws Exception
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
        AlarmEventEntity preEvent = eventBuffer.get(objAttribId);

        if (preEvent == null)
        {
            // 不存在旧报警
            // 插入新报警，且更新缓存
            logger.debug("报警模块：不存在旧报警，插入新报警。");
            this.handleNewAlarm(objAttribId, newEvent);
        }
        else
        {
            logger.debug("报警模块：存在旧报警。");
            // 存在旧报警
            // 旧报警和新报警非同一级别的报警
            // 不考虑：第一次产生报警和数据库最后一条报警LEVEL_ID相同，报警规则有变化的情况
            if (preEvent.getLevelId() != newEvent.getLevelId())
            {
                logger.debug("报警模块：新旧报警不属于同一级别，恢复旧报警，插入新报警");
                // 设置恢复时间
                preEvent.setResumeTime(newEvent.getCollTime());
                // 恢复报警
                preEvent.resume();
                // 清理缓存
                eventBuffer.delete(objAttribId);
                // 更新数据库
                aedao.handleAlarmEventResume(preEvent);
                // 发送报警恢复
                sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_RESUME);
                // 插入新报警，更新缓存，并发送
                this.handleNewAlarm(objAttribId, newEvent);
            }
            else
            // 旧报警和新报警是同一级别的报警
            {
                // 报警统计+1
                preEvent.setAlarmCount(preEvent.getAlarmCount() + 1);
                // 更新缓存
                eventBuffer.update(objAttribId, preEvent);
                // 更新数据库
                aedao.updateEventCount(preEvent.getAlarmEvtId(), preEvent.getAlarmCount());
                // 2013-01-11 gx 貌似不用发送 报警统计+1 的事件
                // 发送报警更新
                // sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_UPDATE_COUNT);
            }
        }

        return retval;
    }

    /**
     * 处理新事件。向缓存插入事件，存入数据库，并发送新事件。
     * @param objAttribId
     * @param event
     */
    private void handleNewAlarm(int objAttrId, AlarmEventEntity event)
    {
        eventBuffer.insert(objAttrId, event);
        aedao.handleAlarmEvent(event);
        sendAlarmEvent(event, AlarmEventEntity.ALARM_SEND_NEW);
    }

    /**
     * 用于由报警规则变化产生的事件。恢复最后一条报警，处理如同自动清除。
     * @param objAttribId
     * @param time
     */
    private void ruleChange(int objAttrId)
    {
        AlarmEventEntity preEvent = eventBuffer.get(objAttrId);
        if (preEvent != null)
        {
            // 设置恢复时间
            preEvent.setResumeTime(new Date().getTime());
            // 恢复报警
            preEvent.resume();
            // 清理缓存
            eventBuffer.delete(objAttrId);
            // 更新数据库并发送
            this.alarmResumeAndSend(preEvent);
        }
    }

    /**
     * 恢复并移除某个对象属性最后一次报警，更新数据库。用于报警恢复。
     * @param objAttrId
     * @param eventId
     */
    private void resumeLastAlarm(int objAttrId, long time)
    {
        AlarmEventEntity preEvent = eventBuffer.get(objAttrId);
        if (preEvent != null && time > 0)
        {
            logger.debug(String.format("报警模块：恢复对象属性<%s>最后一次报警<%s>", objAttrId, preEvent));
            preEvent.setResumeTime(time);
            preEvent.resume();
            this.alarmResumeAndSend(preEvent);
            logger.debug("报警模块：报警恢复，对象属性ID：" + objAttrId + "；报警ID：" + preEvent.getAlarmEvtId() + "。");
        }
        eventBuffer.delete(objAttrId);
    }

    /**
     * 手动清理报警，一般用于远程调用
     * @param objAttribId
     * @param eventId
     */
    private void manuClean(int objAttrId, int eventId)
    {
        if (objAttrId <= 0 || eventId <= 0)
        {
            return;
        }
        AlarmEventEntity preEvent = eventBuffer.delete(objAttrId, eventId);
        if (preEvent != null)
        {
            logger.debug("报警模块：手动移除报警，对象属性ID：" + objAttrId + "；报警ID：" + eventId + "。");
        }
    }

    /**
     * 报警恢复时，更新数据库，并发送。
     * @param objAttribId
     * @param event
     */
    private void alarmResumeAndSend(AlarmEventEntity event)
    {
        if (aedao.handleAlarmEventResume(event))
        {
            sendAlarmEvent(event, AlarmEventEntity.ALARM_SEND_RESUME);
        }
    }

    /**
     * 处理报警事件的线程
     */
    class AlarmEventHandleCallable implements Callable<Integer>
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
            logger.info("报警事件处理（性能）线程：从阻塞状态被唤醒，准备关闭报警处理线程。");
            return CALL_RETURN;
        }

        private void handle(AlarmEventEntityX aee) throws Exception
        {
            int eventType = aee.getEventType();
            switch (eventType)
            {
            case AlarmEventEntityX.EVENT_TYPE_COMMEN:
                AlarmEventEntity event = aee.getAlarm();
                if (event != null)
                {
                    logger.debug("报警事件处理（性能）线程：报警产生。对象属性ID：" + event.getObjAttrId());
                    // 产生报警
                    handleEvent(event);
                }
                else
                {
                    logger.debug("报警事件处理（性能）线程：报警恢复。对象属性ID：" + aee.getObjAttrId());
                    // 报警恢复
                    resumeLastAlarm(aee.getObjAttrId(), aee.getRecoverTime());
                }
                break;
            case AlarmEventEntityX.EVENT_TYPE_MANU:
                logger.debug("报警事件处理（性能）线程：手动清除。对象属性ID：" + aee.getObjAttrId());
                manuClean(aee.getObjAttrId(), aee.getEventId());
                break;
            case AlarmEventEntityX.EVENT_TYPE_CHANGE:
                logger.debug("报警事件处理（性能）线程：规则改变。对象属性ID：" + aee.getObjAttrId());
                ruleChange(aee.getObjAttrId());
                break;
            case AlarmEventEntityX.EVENT_TYPE_STOP:
                break;
            default:
                logger.debug("报警事件处理（性能）线程：不处理状态为" + eventType + "的数据。");
            }
        }
    }
}
