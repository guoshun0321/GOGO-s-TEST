/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 告警条件
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.rule.AlarmRuleConstructionException;

/**
 * 生成告警条件。 告警条件包括以下几种： 
 * LT 小于 一个数字型参数 
 * LE 小于等于 一个数字型参数 
 * EQ 等于 一个数字型参数 
 * NOTEQ 等于 一个数字型参数 
 * GT 大于 一个数字型参数 
 * GE 大于等于 一个数字型参数 
 * LK 字符串匹配 一个参数 
 * EX 变量是否存在 多个个数字型参数 
 * IN 区间|范围 两个数字型参数 
 * NOTIN 区间外|范围 两个数字型参数
 * 
 * @author 郭祥
 */
public class AlarmCondition
{

    private AbsCondition condition;
    private static final Logger logger = Logger.getLogger(AlarmCondition.class);

    /**
     * @param condition 条件
     * @param param 参数
     * @throws AlarmRuleConstructionException 异常
     */
    public AlarmCondition(String condition, String param) throws AlarmRuleConstructionException
    {
        this.condition = null;
        this.init(condition, param.split(","));
    }

    /**
     * 新建报警条件，设置参数，并判断该条件是否合法
     * @param cond 报警条件
     * @param params 报警参数
     * @throws AlarmRuleConstructionException 报警规则构造异常
     */
    private void init(String cond, String[] params) throws AlarmRuleConstructionException
    {
        AlarmConditionEnum ace = AlarmConditionEnum.valueOf(cond);
        switch (ace)
        {
        case LT:
            condition = new LTCondition();
            break;
        case LE:
            condition = new LECondition();
            break;
        case EQ:
            condition = new EQCondition();
            break;
        case NOTEQ:
            condition = new NOTEQCondition();
            break;
        case GT:
            condition = new GTCondition();
            break;
        case GE:
            condition = new GECondition();
            break;
        case IN:
            condition = new INCondition();
            break;
        case NOTIN:
            condition = new NOTINCondition();
            break;
        case LK:
            condition = new LKCondition();
            break;
        case UNLK:
            condition = new UNLKCondition();
            break;
        case EX:
            condition = new EXCondition();
            break;
        default:
            condition = null;
            break;
        }
        if (condition != null)
        {
            condition.setParams(params);
            if (!condition.validate())
            {
                String msg = "报警模块：无效的报警条件：" + condition.toString() + "；";
                logger.warn(msg);
                condition = null;
                throw new AlarmRuleConstructionException(msg);
            }
        }
    }

    /**
     * 比较数据是否符合这个条件
     * @param obj 对象
     * @return 结果
     */
    public boolean compare(Object obj)
    {
        if (condition == null)
        {
            return false;
        }
        return condition.compare(obj);
    }

    /**
     * 该报警条件是否合法
     * @return 结果
     */
    public boolean isLegal()
    {
        if (condition == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public String toString()
    {
        if (condition != null)
        {
            return condition.toString();
        }
        else
        {
            return "非法条件";
        }
    }

    public String toString(String value)
    {
        if (condition != null)
        {
            return condition.toString(value);
        }
        else
        {
            return "非法条件";
        }
    }
}
