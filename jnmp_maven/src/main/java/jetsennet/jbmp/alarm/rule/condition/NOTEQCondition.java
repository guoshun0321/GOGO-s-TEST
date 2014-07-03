/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 条件：等于
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

import jetsennet.jbmp.util.ConvertUtil;

/**
 * 等于
 * @author 郭祥
 */
public class NOTEQCondition extends SingleNumParamCondition
{

    @Override
    public boolean compare(Object obj)
    {
        Double temp = ConvertUtil.stringToDouble(obj);
        // temp值为null时，返回true
        if (temp == null || temp != threshold)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "不等于";
    }
}
