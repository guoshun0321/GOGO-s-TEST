/************************************************************************
日 期：2011-3-9
作 者: 郭祥
版 本：v1.3
描 述: 报警模块异常
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

/**
 * 报警模块异常
 * @author 郭祥
 */
public class AlarmException extends RuntimeException
{

    private static final long serialVersionUID = -1L;

    /**
     * 构造函数
     */
    public AlarmException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public AlarmException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public AlarmException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public AlarmException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
