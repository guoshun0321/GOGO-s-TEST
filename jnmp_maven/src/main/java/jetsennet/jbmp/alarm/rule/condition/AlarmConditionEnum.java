/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 告警条件枚举
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

/**
 * @author 郭祥
 */
public enum AlarmConditionEnum
{

    LT, // 小于
    LE, // 小于等于
    EQ, // 等于
    NOTEQ, // 不等于
    GT, // 大于
    GE, // 大于等于
    IN, // 区间
    NOTIN, // 开区间外
    LK, // LIKE
    UNLK, // unlike
    EX, // 存在

}
