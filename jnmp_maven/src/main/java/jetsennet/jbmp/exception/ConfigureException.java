/************************************************************************
日 期: 2012-2-22
作 者: 郭祥
版 本: v1.3
描 述: 配置异常
历 史:
 ************************************************************************/
package jetsennet.jbmp.exception;

/**
 * 配置异常
 * @author 郭祥
 */
public class ConfigureException extends RuntimeException
{

    /**
     * 构造函数
     */
    public ConfigureException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public ConfigureException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public ConfigureException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public ConfigureException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
