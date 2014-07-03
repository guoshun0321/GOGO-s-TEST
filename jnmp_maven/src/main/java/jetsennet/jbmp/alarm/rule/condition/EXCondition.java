/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 条件：存在
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule.condition;

/**
 * 存在
 * @author 郭祥
 */
public class EXCondition extends AbsCondition
{

    @Override
    public boolean validate()
    {
        return true;
    }

    @Override
    public boolean compare(Object obj)
    {
        if (obj != null)
        {
            String temp = obj.toString();
            for (String param : params)
            {
                if (param.equals(temp))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected String desc()
    {
        return "存在";
    }
}
