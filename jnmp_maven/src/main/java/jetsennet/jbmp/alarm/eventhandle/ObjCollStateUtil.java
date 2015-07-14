package jetsennet.jbmp.alarm.eventhandle;

import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.MObjectEntity;

public class ObjCollStateUtil
{

    /**
     * 事件分发
     */
    private static AlarmEventDispatch disp = AlarmEventDispatch.getInstance();
    /**
     * 事件处理程序类型
     */
    private static final String HANDLE_TYPE = ObjCollStateEventHandle.class.getName();
    /**
     * 默认的报警事件ID
     */
    private static final int DEFAULT_OBJ_COLL_EVENT_ID = -1;

    /**
     * 发送对象状态
     * @param objId
     * @param collState
     * @param isSub
     */
    public static void sendObjState(MObjectEntity mo, ObjCollStateEntity stat)
    {
        AlarmEventEntity alarm = new AlarmEventEntity();
        alarm.setAlarmEvtId(DEFAULT_OBJ_COLL_EVENT_ID);
        alarm.setObjId(mo.getObjId());
        alarm.setAlarmDesc(stat.toTransMsg());
        disp.handleEvent(HANDLE_TYPE, alarm);
    }

}
