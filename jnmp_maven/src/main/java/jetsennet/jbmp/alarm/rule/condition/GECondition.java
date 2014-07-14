/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.alarm.rule.condition;

import jetsennet.jbmp.util.ConvertUtil;

/**
 * 大于等于
 * @author Guo
 */
public class GECondition extends SingleNumParamCondition
{

    @Override
    public boolean compare(Object obj)
    {
        Double temp = ConvertUtil.stringToDouble(obj);
        if (temp != null && temp >= threshold)
        {
            return true;
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "大于等于";
    }
}
