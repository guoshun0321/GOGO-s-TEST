/************************************************************************
日 期: 2012-6-6
作 者: 郭祥
版 本: v1.3
描 述: 通用报警事件处理
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.relevance.AlarmEventFilterManager;
import jetsennet.jbmp.dataaccess.AlarmEventDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;

/**
 * 通用报警事件处理。所有事件当做新事件处理。
 * @author 郭祥
 */
public class DefAlarmEventHandle extends AbsAlarmEventHandle
{

    private AlarmEventDal aedal;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DefAlarmEventHandle.class);

    /**
     * 构造函数
     */
    public DefAlarmEventHandle()
    {
        super();
        aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
    }

    @Override
    protected void submit()
    {
        future = single.submit(new DelAlarmEventHandleCallable());
    }

    /**
     * 处理告警事件
     * @param newEvent
     */
    private ArrayList<AlarmEventEntity> handleEvent(AlarmEventEntity newEvent)
    {
        if (newEvent == null)
        {
            return null;
        }
        if (!AlarmEventFilterManager.getInstance().filter(newEvent))
        {
            logger.debug("相关性分析：存在相关报警，丢弃报警：" + newEvent);
            return null;
        }
        ArrayList<AlarmEventEntity> retval = new ArrayList<AlarmEventEntity>();
        try
        {
            aedal.insert(newEvent);
            this.sendAlarmEvent(newEvent, AlarmEventEntity.ALARM_SEND_NEW);
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
                    handleEvent(event);
                }
                break;
            case AlarmEventEntityX.EVENT_TYPE_STOP:
                logger.info("报警事件处理（通用）线程：从阻塞状态被唤醒，准备关闭该线程。");
                break;
            default:
                logger.debug("报警事件处理（通用）线程：不处理状态为" + eventType + "的数据。");
            }
        }
    }
}
