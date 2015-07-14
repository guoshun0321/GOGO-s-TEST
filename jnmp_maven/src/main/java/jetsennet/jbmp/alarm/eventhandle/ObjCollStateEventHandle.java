/************************************************************************
日 期: 2012-6-6
作 者: 郭祥
版 本: v1.3
描 述: 第三方报警事件处理
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.eventhandle;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.MObjectEntity;

import org.apache.log4j.Logger;

/**
 * 报警事件直接处理。直接接收报警。
 * 
 * @author 郭祥
 */
public class ObjCollStateEventHandle extends AbsAlarmEventHandle
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ObjCollStateEventHandle.class);
    // 数据库访问
    private MObjectDal modal;

    /**
     * 构造方法
     */
    public ObjCollStateEventHandle()
    {
        super();
        modal = ClassWrapper.wrapTrans(MObjectDal.class);
        this.threadName = "采集状态通知";
    }

    @Override
    protected void submit()
    {
        future = single.submit(new ObjCollStateEventHandleCallable());
    }

    /**
     * 报警事件处理线程
     */
    private class ObjCollStateEventHandleCallable implements Callable<Integer>
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
            case AlarmEventEntityX.EVENT_TYPE_COMMEN:
                AlarmEventEntity event = aee.getAlarm();
                if (event != null)
                {
                    // 更新数据库
                    int objId = event.getObjId();
                    int collState = event.getSourceId();
                    modal.updateCollState(objId, collState);
                    if (collState == MObjectEntity.COLL_STATE_FAILED)
                    {
                        modal.updateSubCollState(objId, MObjectEntity.COLL_STATE_UNKNOWN1);
                    }

                    String msg = event.getAlarmDesc();
                    ObjCollStateEntity entity = ObjCollStateEntity.fromTransMsg(msg);
                    if (entity != null)
                    {
                        // 更新对象
                        modal.updateCollState(entity.objId, entity.objState);
                        if (entity.objState == MObjectEntity.COLL_STATE_FAILED)
                        {
                            modal.updateSubCollState(entity.objId, MObjectEntity.COLL_STATE_UNKNOWN1);
                        }

                        // 更新子对象
                        if (entity.objState != MObjectEntity.COLL_STATE_FAILED && entity.subMap != null)
                        {
                            Set<Entry<Integer, Integer>> set = entity.subMap.entrySet();
                            for (Map.Entry<Integer, Integer> entry : set)
                            {
                                modal.updateCollState(entry.getKey(), entry.getValue());
                            }
                        }
                    }

                    // 发送报警
                    sendAlarmEvent(event, AlarmEventEntity.OBJ_COLL_STATE);
                }
                break;
            case AlarmEventEntityX.EVENT_TYPE_STOP:
                logger.info("报警事件处理（采集状态）线程：从阻塞状态被唤醒，准备关闭报警处理线程。");
                break;
            default:
                logger.debug("报警事件处理（采集状态）线程：不处理状态为" + eventType + "的数据。");
            }
        }
    }
}
