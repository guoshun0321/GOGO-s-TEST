package jetsennet.jbmp.alarm.rule;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.alarm.handle.TrapParseEntity;
import jetsennet.jbmp.entity.AlarmEntity;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;

public class AlarmRuleTrap extends AbsAlarmRule
{

    public AlarmRuleTrap(AlarmEntity alarm) throws AlarmRuleConstructionException
    {
        super(alarm);
    }

    /**
     * 构造报警规则
     * @param iAlarm 参数
     * @throws AlarmRuleConstructionException 异常
     */
    @Override
    protected void build() throws AlarmRuleConstructionException
    {
        List<AlarmLevelEntity> als = alarm.getLevels();
        if (als != null && !als.isEmpty())
        {
            for (AlarmLevelEntity al : als)
            {
                AlarmLevelTrap temp = new AlarmLevelTrap(al);
                this.levels.add(temp);
                this.id2level.put(al.getLevelId(), temp);
            }
        }
    }

    @Override
    public List<AlarmEventEntity> genAlarmEvent(AbsHistoryData data, Object obj, int objId, int attrId, int objAttrId, String desc)
    {
        throw new UnsupportedOperationException("AlarmRuleTrap.genAlarmEvent");
    }

    /**
     * 是否产生报警。
     * 
     * @param obj
     * @return null，不产生报警；报警级别，产生报警的报警级别
     */
    public AbsAlarmLevel isAlarm(TrapParseEntity trap)
    {
        AbsAlarmLevel retval = null;
        ArrayList<AbsAlarmLevel> levels = this.getLevels();

        AbsAlarmLevel selLevel = null;
        for (AbsAlarmLevel level : levels)
        {
            String value = trap.getBy(level.getLevel().getVarName());
            if (level.isAlarm(value) != null)
            {
                if (selLevel != null)
                {
                    if (level.level.getAlarmLevel() > selLevel.level.getAlarmLevel())
                    {
                        selLevel = level;
                    }
                }
                else
                {
                    selLevel = level;
                }
            }
        }
        retval = selLevel;
        return retval;
    }

    /**
     * 生成报警描述
     * @return
     */
    public String genAlarmDesc()
    {
        StringBuilder sb = new StringBuilder();
        for (AbsAlarmLevel level : levels)
        {
            sb.append(level.getLevel().getVarName()).append(level.getCond()).append(";");
        }
        return sb.toString();
    }

    @Override
    public int sizeOfData()
    {
        throw new UnsupportedOperationException("AlarmRuleTrap.sizeOfData");
    }

}
