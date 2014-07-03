/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 表达式抽象类
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.exp;

/**
 * 表达式抽象类
 * @author 郭祥
 */
public abstract class AbstractExpression
{

    /**
     * @param formula 参数
     * @return 结果
     */
    abstract public boolean interpret(Formula formula);
}
