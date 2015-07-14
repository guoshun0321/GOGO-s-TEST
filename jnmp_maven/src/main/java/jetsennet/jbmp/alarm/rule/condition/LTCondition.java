/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.alarm.rule.condition;

import jetsennet.jbmp.util.ConvertUtil;

/**
 * 小于
 * @author 郭祥
 */
public class LTCondition extends SingleNumParamCondition
{

    @Override
    public boolean compare(Object obj)
    {
        Double temp = ConvertUtil.stringToDouble(obj);
        if (obj != null && temp < threshold)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "小于";
    }
}
