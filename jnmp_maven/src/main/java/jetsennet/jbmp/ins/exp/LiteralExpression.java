/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ins.exp;

/**
 * 无子表达式的表达式，用于组成其他表达式
 * @author Guo
 */
public class LiteralExpression extends RegularExpression
{

    private TerminalExpression te;

    /**
     * @param te 参数
     */
    public LiteralExpression(TerminalExpression te)
    {
        this.te = te;
    }

    @Override
    public boolean interpret(Formula f)
    {
        return te.interpret(f);
    }

    @Override
    public void addExpression(RegularExpression exp)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString()
    {
        return te.getFt().getRegex();
    }
}
