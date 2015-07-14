package jetsennet.jbmp.exception;

/**
 * UI层异常，在UI层把下层异常转换成UI层异常抛出
 * @author GUO
 */
public class UIException extends Exception
{

    /**
     * 构造函数
     */
    public UIException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public UIException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public UIException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public UIException(String msg, Throwable t)
    {
        super(msg, t);
    }

}
