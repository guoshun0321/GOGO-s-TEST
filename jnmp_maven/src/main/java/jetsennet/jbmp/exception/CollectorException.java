package jetsennet.jbmp.exception;

/**
 * 采集器异常
 * @author Guo
 */
public class CollectorException extends Exception
{

    /**
     * 构造函数
     */
    public CollectorException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public CollectorException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public CollectorException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public CollectorException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
