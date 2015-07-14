/************************************************************************
日 期: 2012-2-28
作 者: 郭祥
版 本: v1.3
描 述: 报警级别
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.rule.condition.AlarmCondition;
import jetsennet.jbmp.entity.AlarmLevelEntity;

/**
 * 报警级别
 * @author 郭祥
 */
public abstract class AbsAlarmLevel
{

    /**
     * 报警级别
     */
    protected AlarmLevelEntity level;
    /**
     * 报警条件
     */
    protected AlarmCondition cond;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AbsAlarmLevel.class);

    /**
     * 构造函数
     * @param iLevel 参数
     * @throws AlarmRuleConstructionException 异常
     */
    public AbsAlarmLevel(AlarmLevelEntity iLevel) throws AlarmRuleConstructionException
    {
        if (iLevel == null)
        {
            throw new NullPointerException();
        }
        this.level = iLevel;
        cond = new AlarmCondition(level.getCondition(), level.getThreshold());
    }

    /**
     * 是否产生该级别的报警
     * @param obj 对象
     * @return 结果
     */
    public AlarmLevelEntity isAlarm(Object obj)
    {
        if (obj instanceof String)
        {
            try
            {
                if (cond != null && cond.compare(obj))
                {
                    return level;
                }
            }
            catch (Exception ex)
            {
                logger.warn("", ex);
            }
        }
        return null;
    }

    /**
     * 是否需要更新
     * @param iLevel 参数
     * @return 结果
     */
    public abstract boolean needUpdate(AlarmLevelEntity iLevel);

    /**
     * 获取条件
     * @return 结果
     */
    public AlarmCondition getCond()
    {
        return cond;
    }

    /**
     * @return the level
     */
    public AlarmLevelEntity getLevel()
    {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(AlarmLevelEntity level) throws AlarmRuleConstructionException
    {
        this.level = level;
        cond = new AlarmCondition(level.getCondition(), level.getThreshold());
    }
}
