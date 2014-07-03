/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 单参数条件，参数必须是可以转换成int的类型
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

import jetsennet.jbmp.util.ConvertUtil;

/**
 * 单数值形参数条件。参数必须是可以转换成double的类型。
 * @author 郭祥
 */
public abstract class SingleNumParamCondition extends AbsCondition
{

    protected double threshold;

    @Override
    public boolean validate()
    {
        if (params != null && params.length == 1 && params[0] != null)
        {
            Double temp = ConvertUtil.stringToDouble(params[0]);
            if (temp != null)
            {
                threshold = temp;
                return true;
            }
        }
        return false;
    }
}
