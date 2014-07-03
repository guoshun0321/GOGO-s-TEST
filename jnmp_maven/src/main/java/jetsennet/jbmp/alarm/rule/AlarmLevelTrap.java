/************************************************************************
日 期：2012-2-29
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.handle.TrapParseEntity;
import jetsennet.jbmp.entity.AlarmLevelEntity;

/**
 * @author 郭祥
 */
public class AlarmLevelTrap extends AbsAlarmLevel
{

    private static final Logger logger = Logger.getLogger(AlarmLevelTrap.class);

    /**
     * @param iLevel 参数
     * @throws AlarmRuleConstructionException 异常
     */
    public AlarmLevelTrap(AlarmLevelEntity iLevel) throws AlarmRuleConstructionException
    {
        super(iLevel);
    }

    /**
     * 判断level是否需要更新。在不需要更新时，更新原始的级别描述。
     */
    @Override
    public boolean needUpdate(AlarmLevelEntity iLevel)
    {
        if (level.getAlarmLevel() != iLevel.getAlarmLevel() || !level.getVarName().equals(iLevel.getVarName())
            || !level.getCondition().equals(iLevel.getCondition()) || !level.getThreshold().equals(iLevel.getThreshold())
            || !level.getWeekMask().equals(iLevel.getWeekMask()) || !level.getHourMask().equals(iLevel.getHourMask()))
        {
            return true;
        }
        level.setLevelDesc(iLevel.getLevelDesc());
        return false;
    }

    /**
     * 级别描述宏替换
     * 
     * @param trap
     * @return
     */
    public String macroDesc(TrapParseEntity trap)
    {
        String desc = level.getLevelDesc();
        if (desc == null)
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = desc.length() - 1; i <= length;)
        {
            char c = desc.charAt(i);
            if (c == '$' && i != length)
            {
                char n = desc.charAt(i + 1);
                if (n >= '1' && n <= '9')
                {
                    sb.append(trap.getMacroValue("$" + n));
                    i += 2;
                    continue;
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(level.getLevelName());
        sb.append("[");
        sb.append("级别ID：").append(level.getLevelId());
        sb.append("；报警条件：").append(level.getVarName()).append(" ").append(cond);
        sb.append("；更新时间：").append(level.getUpdateTime().toString());
        sb.append("；星期掩码：").append(level.getWeekMask());
        sb.append("；小时掩码：").append(level.getHourMask());
        sb.append("]");
        return sb.toString();
    }
}
