package jetsennet.jbmp.exception;

/**
 * 对象实例化时抛出的异常
 * @author GUO
 */
public class InstanceException extends Exception
{

    /**
     * * 构造函数
     */
    public InstanceException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public InstanceException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public InstanceException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public InstanceException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
