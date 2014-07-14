/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ins.exp;

import java.util.ArrayList;

/**
 * @author Guo
 */
public class SequenceExpression extends RegularExpression
{

    private ArrayList<RegularExpression> exps;

    /**
     * 构造函数
     */
    public SequenceExpression()
    {
        exps = new ArrayList<RegularExpression>();
    }

    @Override
    public boolean interpret(Formula formula)
    {
        boolean match = false;
        if (exps != null && exps.size() > 0)
        {
            for (int i = 0; i < exps.size(); i++)
            {
                if (!exps.get(i).interpret(formula))
                {
                    match = false;
                    break;
                }
                else
                {
                    match = true;
                }
            }
        }
        return match;
    }

    @Override
    public void addExpression(RegularExpression exp)
    {
        if (exps == null)
        {
            exps = new ArrayList<RegularExpression>();
        }
        exps.add(exp);
    }
}
