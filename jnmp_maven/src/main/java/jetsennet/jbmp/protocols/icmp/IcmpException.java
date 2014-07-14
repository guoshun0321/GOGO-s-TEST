/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: ICMP异常
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

/**
 * ICMP异常
 * @author 郭祥
 */
public class IcmpException extends RuntimeException
{
    /**
     * 构造函数
     */
    public IcmpException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public IcmpException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public IcmpException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public IcmpException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
