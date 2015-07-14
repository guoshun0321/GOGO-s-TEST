/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

/**
 * ARP异常
 * @author Guo
 */
public class ARPException extends RuntimeException
{
    /**
     * 构造函数
     */
    public ARPException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public ARPException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public ARPException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public ARPException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
