/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.alarm.rule.condition;

/**
 * UNLIKE
 * 
 * @author 郭祥
 */
public class UNLKCondition extends AbsCondition
{

    private String like;

    @Override
    public boolean validate()
    {
        if (params != null && params.length == 1 & params[0] != null)
        {
            like = params[0];
            return true;
        }
        return false;
    }

    @Override
    public boolean compare(Object obj)
    {
        if (obj != null)
        {
            String temp = obj.toString();
            if (!temp.contains(like))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "UNLIKE";
    }
}
