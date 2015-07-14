package jetsennet.jbmp.alarm.eventhandle;

import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.rule.AlarmRuleManager;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.TwoTuple;

/**
 * @author 李巍改动
 */
public class ShootThroughtAlarmUtil
{

    /**
     * 报警事件分发
     */
    private static AlarmEventDispatch disp = AlarmEventDispatch.getInstance();
    public static final int ALARM_TYPE_GEN = 0;
    public static final int ALARM_TYPE_RE = 1;
    private static final String HANDLE_TYPE;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ShootThroughtAlarmUtil.class);

    static
    {
        HANDLE_TYPE = ShootThroughtEventHandle.class.getName();
    }

    /**
     * 插入报警
     * @param alarm 报警
     */
    public static void alarmGen(AlarmEventEntity alarm)
    {
        disp.handleEvent(alarm, ALARM_TYPE_GEN, HANDLE_TYPE);
    }

    /**
     * 报警恢复
     * @param alarm 报警
     */
    public static void alarmRe(AlarmEventEntity alarm)
    {
        disp.handleEvent(alarm, ALARM_TYPE_RE, HANDLE_TYPE);
    }

    /**
     * 报警恢复
     * @param objAttrId 参数
     * @param alarmId 参数
     * @param levelId 参数
     * @param recoverTime 恢复时间
     */
    public static void alarmRe(int objAttrId, int alarmId, int levelId, long recoverTime)
    {
        AlarmEventEntity event = new AlarmEventEntity();
        event.setObjAttrId(objAttrId);
        event.setAlarmId(alarmId);
        event.setLevelId(levelId);
        event.setResumeTime(recoverTime);
        alarmRe(event);
    }

    /**
     * 插入采集数据失败报警
     * @param objId 参数
     * @param attrId 参数
     * @param objAttrId 参数
     * @param objAttrName 参数
     */
    public static void alarmGenCollMiss(int objId, int attrId, int objAttrId, String objAttrName, String msg)
    {
        try
        {
            AlarmRuleManager mana = AlarmRuleManager.getInstance();
            TwoTuple<AlarmEntity, AlarmLevelEntity> temp = mana.getFirstLevel(BMPConstants.COLL_MISS_ALARM_ID);
            if (temp != null && temp.first != null && temp.second != null)
            {
                AlarmEventEntity event = new AlarmEventEntity();
                event.setObjId(objId);
                event.setAttribId(attrId);
                event.setObjAttrId(objAttrId);
                event.setLevel(temp.first, temp.second);
                event.setCollTime(new Date().getTime());
                String errorMsg = String.format("对象属性(%s)采集失败。%s", objAttrName, msg);
                event.setEventDesc(errorMsg);
                alarmGen(event);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 恢复采集数据失败报警
     * @param objId 参数
     * @param attrId 参数
     * @param objAttrId 参数
     */
    public static void alarmReCollMiss(int objId, int attrId, int objAttrId)
    {
        alarmRe(objAttrId, BMPConstants.COLL_MISS_ALARM_ID, BMPConstants.COLL_MISS_ALARM_LEVEL_ID, new Date().getTime());
    }

}
