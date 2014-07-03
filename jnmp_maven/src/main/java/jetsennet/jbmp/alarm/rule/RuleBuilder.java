/************************************************************************
日 期: 2012-2-28
作 者: 郭祥
版 本: v1.3
描 述: 规则生成器
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEntity;

/**
 * 报警规则生成器
 * @author 郭祥
 */
public final class RuleBuilder
{

    /**
     * 按次数
     */
    public static final int RULE_TYPE_TIME = 0;
    /**
     * 按时间间隔
     */
    public static final int RULE_TYPE_SPAN = 1;
    /**
     * TRAP类型
     */
    public static final int RULE_TYPE_TRAP = 2;
    /**
     * 未知类型
     */
    public static final int RULE_TYPE_UNKNOWN = -1;
    /**
     * 无效报警
     */
    public static final int RULE_TYPE_UNVALIDATE = -2;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(RuleBuilder.class);
    private static RuleBuilder builder = new RuleBuilder();

    private RuleBuilder()
    {
    }

    public static RuleBuilder getInstance()
    {
        return builder;
    }

    /**
     * 生成规则
     * @param alarm 参数
     * @return 结果
     */
    public AbsAlarmRule genRule(AlarmEntity alarm)
    {
        return genRuleDef(alarm);
    }

    /**
     * 一般规则
     * @param alarm
     * @return
     */
    private AbsAlarmRule genRuleDef(AlarmEntity alarm)
    {

        AbsAlarmRule retval = null;
        try
        {
            int type = this.validate(alarm);
            switch (type)
            {
            case RULE_TYPE_SPAN:
                retval = new AlarmRuleSpan(alarm);
                break;
            case RULE_TYPE_TIME:
                retval = new AlarmRuleTime(alarm);
                break;
            case RULE_TYPE_TRAP:
                retval = new AlarmRuleTrap(alarm);
                break;
            case RULE_TYPE_UNVALIDATE:
                break;
            case RULE_TYPE_UNKNOWN:
                logger.debug("丢弃报警规则：" + alarm);
                break;
            default:
                break;
            }
        }
        catch (Exception ex)
        {
            logger.debug(ex.getMessage());
        }
        return retval;
    }

    /**
     * 验证报警规则，并返回报警类型
     * @param alarm
     * @return
     */
    private int validate(AlarmEntity alarm)
    {
        int retval = RULE_TYPE_UNKNOWN;
        if (alarm == null)
        {
            logger.error("报警规则：传入报警规则为NULL。");
        }
        else if (alarm.getLevels() == null || alarm.getLevels().isEmpty())
        {
            logger.error("报警规则：传入报警规则的报警级别为NULL。");
        }
        else if (alarm.getIsValid() == AlarmEntity.IS_VALID_FALSE)
        {
            retval = RULE_TYPE_UNVALIDATE;
        }
        else
        {
            int checkNum = alarm.getCheckNum();
            int overNum = alarm.getOverNum();
            int checkSpan = alarm.getCheckSpan();
            if (checkNum == 1 && overNum == 1 && checkSpan == 1)
            {
                retval = RULE_TYPE_TRAP;
            }
            else if (checkNum > 0 && checkSpan == 0 && overNum > 0 && checkNum >= overNum)
            {
                retval = RULE_TYPE_TIME;
            }
            else if (checkNum == 0 && checkSpan > 0 && overNum > 0)
            {
                retval = RULE_TYPE_SPAN;
            }
            else
            {
                logger.info("报警规则：不合法的报警规则：" + alarm);
            }
        }
        return retval;
    }

}
