/************************************************************************
日 期: 2012-6-6
作 者: 郭祥
版 本: v1.3
描 述: 第三方报警事件处理
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 报警事件直接处理。直接接收报警。
 * @author 郭祥
 */
public class ShootThroughtEventHandle extends AbsAlarmEventHandle
{

    private AlarmEventDal aedal;
    /**
     * 报警事件缓存
     */
    private Map<String, AlarmEventEntity> alarmBuffer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ShootThroughtEventHandle.class);

    /**
     * 构造方法
     */
    public ShootThroughtEventHandle()
    {
        super();
        this.threadName = "报警事件处理（直通）";
        this.alarmBuffer = new HashMap<String, AlarmEventEntity>();
        aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
    }

    @Override
    protected void submit()
    {
        future = single.submit(new ShootThroughtAlarmEventHandleCallable());
    }

    /**
     * 处理告警事件
     * @param newEvent
     */
    private void newEvent(AlarmEventEntity event) throws Exception
    {
        String key = this.genKey(event.getObjAttrId(), event.getAlarmId(), event.getLevelId());
        AlarmEventEntity preEvent = this.ensureAlarm(key);
        if (preEvent == null)
        {
            event.setAlarmCount(INITIAL_ALARM_COUNT);
            alarmBuffer.put(key, event);
            aedao.handleAlarmEvent(event);
            sendAlarmEvent(event, AlarmEventEntity.ALARM_SEND_NEW);
        }
        else
        {
            if (preEvent.getAlarmLevel() == event.getAlarmLevel())
            {
                preEvent.setAlarmCount(preEvent.getAlarmCount() + 1);
                aedal.updateEventCount(preEvent.getAlarmEvtId(), preEvent.getAlarmCount());
                sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_UPDATE_COUNT);
            }
            else
            {
                // 恢复就报警
                preEvent.setResumeTime(new Date().getTime());
                preEvent.resume();
                aedal.handleAlarmEventResume(preEvent);
                alarmBuffer.remove(key);
                // 发送报警恢复
                sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_RESUME);
                // 参数新报警
                event.setAlarmCount(INITIAL_ALARM_COUNT);
                alarmBuffer.put(key, event);
                aedao.handleAlarmEvent(event);
                sendAlarmEvent(event, AlarmEventEntity.ALARM_SEND_NEW);
            }
        }
    }

    private void recoverEvent(AlarmEventEntity event) throws Exception
    {
        String key = this.genKey(event.getObjAttrId(), event.getAlarmId(), event.getLevelId());
        AlarmEventEntity preEvent = this.ensureAlarm(key);
        if (preEvent != null)
        {
            preEvent.setResumeTime(event.getResumeTime());
            preEvent.resume();
            aedal.handleAlarmEventResume(preEvent);
            alarmBuffer.remove(key);
            // 发送报警恢复
            sendAlarmEvent(preEvent, AlarmEventEntity.ALARM_SEND_RESUME);
        }
    }

    /**
     * 生成缓存KEY
     * @param objAttrId
     * @param alarmId
     * @return
     */
    private String genKey(int objAttrId, int alarmId, int levelId)
    {
        return String.format("%s_%s_%s", objAttrId, alarmId, levelId);
    }

    private AlarmEventEntity ensureAlarm(String key)
    {
        return alarmBuffer.get(key);
    }

    /**
     * 报警事件处理线程
     */
    private class ShootThroughtAlarmEventHandleCallable implements Callable<Integer>
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
                catch (Throwable t)
                {
                    logger.error("", t);
                }
            }
            return CALL_RETURN;
        }

        private void handle(AlarmEventEntityX aee) throws Exception
        {
            int eventType = aee.getEventType();
            switch (eventType)
            {
            case ShootThroughtAlarmUtil.ALARM_TYPE_GEN:
                AlarmEventEntity event = aee.getAlarm();
                if (event != null)
                {
                    newEvent(event);
                }
                break;
            case ShootThroughtAlarmUtil.ALARM_TYPE_RE:
                AlarmEventEntity reEvent = aee.getAlarm();
                if (reEvent != null)
                {
                    recoverEvent(reEvent);
                }
                break;
            case AlarmEventEntityX.EVENT_TYPE_STOP:
                logger.info("报警事件处理（直通）线程：从阻塞状态被唤醒，准备关闭该线程。");
                break;
            default:
                logger.debug("报警事件处理（直通）线程：不处理状态为" + eventType + "的数据。");
            }
        }
    }
}
