package jetsennet.jbmp.exception;

/**
 * 不受查的数据库异常
 * @author GUO
 */
public class UncheckedSQLException extends RuntimeException
{

    /**
     * 构造函数
     */
    public UncheckedSQLException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public UncheckedSQLException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public UncheckedSQLException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public UncheckedSQLException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
