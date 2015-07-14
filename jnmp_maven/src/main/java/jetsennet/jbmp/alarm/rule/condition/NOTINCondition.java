/************************************************************************
日 期: 2012-2-13
作 者: 郭祥
版 本: v1.3
描 述:
历 史: 
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

import jetsennet.jbmp.util.ConvertUtil;

/**
 * 不在闭区间内。
 * @author 郭祥
 */
public class NOTINCondition extends AbsCondition
{

    private double upperLimit;
    private double lowerLimit;

    @Override
    public boolean validate()
    {
        if (params != null && params.length == 2 && params[0] != null && params[1] != null)
        {
            Double temp1 = ConvertUtil.stringToDouble(params[0]);
            Double temp2 = ConvertUtil.stringToDouble(params[1]);
            if (temp1 != null && temp2 != null)
            {
                if (temp1 <= temp2)
                {
                    lowerLimit = temp1;
                    upperLimit = temp2;
                }
                else
                {
                    lowerLimit = temp2;
                    upperLimit = temp1;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean compare(Object obj)
    {
        Double temp = ConvertUtil.stringToDouble(obj);
        if (obj != null && (temp < lowerLimit || temp > upperLimit))
        {
            return true;
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "区间外";
    }
}
