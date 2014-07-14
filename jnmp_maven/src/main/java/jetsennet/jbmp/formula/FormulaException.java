/************************************************************************
日 期: 2012-2-1
作 者: 郭祥
版 本: v1.3
描 述: 公式解析异常
历 史:
 ************************************************************************/
package jetsennet.jbmp.formula;

/**
 * 公式解析异常
 * @author 郭祥
 */
public class FormulaException extends RuntimeException
{

    /**
     * 构造函数
     */
    public FormulaException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public FormulaException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public FormulaException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public FormulaException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
