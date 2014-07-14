/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ins.exp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guo
 */
public class TerminalExpression extends AbstractExpression
{

    private FormulaTokenizer ft;
    private Pattern p;
    private Matcher matcher;

    /**
     * 构造函数
     * @param fr 参数
     */
    public TerminalExpression(FormulaTokenizer fr)
    {
        this.ft = fr;
        p = Pattern.compile(ft.getRegex());
    }

    @Override
    public boolean interpret(Formula formula)
    {
        String pStr = formula.getStr().substring(formula.getPos());
        matcher = p.matcher(pStr);
        boolean isMatch = matcher.lookingAt();
        if (isMatch)
        {
            formula.addNode(matcher.group(), ft.getType());
        }
        return isMatch;
    }

    /**
     * @return the ft
     */
    public FormulaTokenizer getFt()
    {
        return ft;
    }

    /**
     * @param ft the ft to set
     */
    public void setFt(FormulaTokenizer ft)
    {
        this.ft = ft;
    }
}
