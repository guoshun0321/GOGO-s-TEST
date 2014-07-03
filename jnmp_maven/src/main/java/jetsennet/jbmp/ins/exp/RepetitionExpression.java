/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ins.exp;

import java.util.ArrayList;

/**
 * @author Guo
 */
public class RepetitionExpression extends RegularExpression
{

    ArrayList<RegularExpression> exps;

    @Override
    public boolean interpret(Formula formula)
    {
        boolean result = false;
        boolean last = true;
        while (last)
        {
            boolean isMatch = false;
            for (int i = 0; i < exps.size(); i++)
            {
                RegularExpression exp = exps.get(i);
                if (exp.interpret(formula))
                {
                    isMatch = true;
                    result = true;
                    break;
                }
            }
            if (!isMatch)
            {
                last = false;
            }
        }
        return result;
    }

    @Override
    public void addExpression(RegularExpression exp)
    {
        if (exps == null)
        {
            this.exps = new ArrayList<RegularExpression>();
        }
        exps.add(exp);
    }
}
