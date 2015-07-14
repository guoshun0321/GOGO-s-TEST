/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.ins.exp;

/**
 * @author Guo
 */
public abstract class RegularExpression
{

    /**
     * @param f 参数
     * @return 结果
     */
    public abstract boolean interpret(Formula f);

    /**
     * @param exp 参数
     */
    public abstract void addExpression(RegularExpression exp);
}
