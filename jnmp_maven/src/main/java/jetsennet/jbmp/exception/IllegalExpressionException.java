package jetsennet.jbmp.exception;

/**
 * 非法表达式异常
 * @author Guo
 */
public class IllegalExpressionException extends Exception
{

    /**
     * 构造函数
     */
    public IllegalExpressionException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public IllegalExpressionException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public IllegalExpressionException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public IllegalExpressionException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
