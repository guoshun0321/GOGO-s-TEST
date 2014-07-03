/************************************************************************
日 期: 2012-2-16
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.exception;

/**
 * 发送SNMP协议到目标地址超时
 * @author 郭祥
 */
public class SnmpException extends Exception
{

    /**
     * 构造函数
     */
    public SnmpException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public SnmpException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public SnmpException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public SnmpException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
