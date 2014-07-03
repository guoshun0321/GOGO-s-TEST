/************************************************************************
日 期：2012-2-28
作 者: 郭祥
版 本: v1.3
描 述: 报警生成工具类
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import jetsennet.jbmp.alarm.rule.condition.AlarmCondition;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;

/**
 * 报警生成抽象类
 *
 * @author 郭祥
 */
public class AlarmGenUtil
{

    /**
     * 生成报警事件
     * @param objId 对象ID
     * @param attrId 属性ID
     * @param objAttrId 对象属性ID
     * @param rule 报警规则
     * @param aal 报警级别
     * @param data 产生报警时的最后一条数据
     * @return
     */
    public static AlarmEventEntity genAlarmEvent(int objId, int attrId, int objAttrId, AbsAlarmRule rule, AbsAlarmLevel aal, HistoryDataEntry data)
    {
        if (aal == null || data == null)
        {
            return null;
        }
        AlarmLevelEntity level = aal.getLevel();
        AlarmCondition cond = aal.getCond();
        AlarmEventEntity aee = new AlarmEventEntity();
        aee.setObjAttrId(objAttrId);
        aee.setObjId(objId);
        aee.setAttribId(attrId);
        aee.setSourceId(0);
        aee.setSourceType(0);
        aee.setCollTime(data.time.getTime());
        aee.setCollValue(data.value);
        aee.setResumeTime(0);
        aee.setEventDuration(0);
        aee.setLevelId(level.getLevelId());
        aee.setAlarmId(level.getAlarmId());
        aee.setAlarmLevel(level.getAlarmLevel());
        aee.setSubLevel(level.getSubLevel());
        aee.setAlarmType(rule.getAlarm().getAlarmType());
        aee.setAlarmDesc(cond.toString());
        aee.setLevelName(level.getLevelName());

        String eventDesc = genAlarmDesc(rule, aal, data);
        aee.setEventDesc(eventDesc);

        aee.setEventState(AlarmEventEntity.EVENT_STATE_NOTACK);
        aee.setEventType(AlarmEventEntity.EVENT_TYPE_NORMAL);
        return aee;
    }

    private static String genAlarmDesc(AbsAlarmRule rule, AbsAlarmLevel level, HistoryDataEntry data)
    {
        String eventDesc = level.getLevel().getLevelDesc();
        if (eventDesc == null || eventDesc.trim().isEmpty())
        {
            eventDesc = rule.getAlarm().getAlarmDesc();
            if (eventDesc == null || eventDesc.trim().isEmpty())
            {
                eventDesc = level.getCond().toString(data.value);
            }
        }
        return eventDesc;
    }

}
