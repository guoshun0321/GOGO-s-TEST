package jetsennet.jbmp.syslog;

/**
 * Syslog解析异常
 * @author 郭祥
 */
public class SyslogParseException extends Exception
{
    /**
     * 构造方法
     */
    public SyslogParseException()
    {
        super();
    }

    /**
     * 构造方法
     * @param msg 参数
     */
    public SyslogParseException(String msg)
    {
        super(msg);
    }

    /**
     * 构造方法
     * @param t 参数
     */
    public SyslogParseException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造方法
     * @param t 参数
     * @param msg 参数
     */
    public SyslogParseException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
