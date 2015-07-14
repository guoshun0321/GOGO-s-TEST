package jetsennet.jnmp.exception;

/**
 * SMI连接CIMOM异常
 * @version 1.0
 * @author xli
 */
public class SMIException extends Exception
{

    /**
     * 构造方法
     */
    public SMIException()
    {
    }

    /**
     * 构造方法
     * @param msg 参数
     */
    public SMIException(String msg)
    {
        super(msg);
    }

    /**
     * 构造方法
     * @param t 参数
     */
    public SMIException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造方法
     * @param msg 参数
     * @param t 参数
     */
    public SMIException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
