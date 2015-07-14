/************************************************************************
日 期：2012-2-29
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmLevelEntity;

/**
 * @author 郭祥
 */
public class AlarmLevelSpan extends AbsAlarmLevel
{

    private static final Logger logger = Logger.getLogger(AlarmLevelSpan.class);

    /**
     * @param iLevel 参数
     * @throws AlarmRuleConstructionException 异常
     */
    public AlarmLevelSpan(AlarmLevelEntity iLevel) throws AlarmRuleConstructionException
    {
        super(iLevel);
    }

    @Override
    public boolean needUpdate(AlarmLevelEntity iLevel)
    {
        if (level.getAlarmLevel() != iLevel.getAlarmLevel() || !level.getCondition().equals(iLevel.getCondition())
            || !level.getThreshold().equals(iLevel.getThreshold()) || !level.getWeekMask().equals(iLevel.getWeekMask())
            || !level.getHourMask().equals(iLevel.getHourMask()))
        {
            return true;
        }
        level.setLevelDesc(iLevel.getLevelDesc());
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(level.getLevelName());
        sb.append("[");
        sb.append("级别ID：").append(level.getLevelId());
        sb.append("；报警条件：").append(cond);
        sb.append("；更新时间：").append(level.getUpdateTime().toString());
        sb.append("；星期掩码：").append(level.getWeekMask());
        sb.append("；小时掩码：").append(level.getHourMask());
        sb.append("]");
        return sb.toString();
    }
}
