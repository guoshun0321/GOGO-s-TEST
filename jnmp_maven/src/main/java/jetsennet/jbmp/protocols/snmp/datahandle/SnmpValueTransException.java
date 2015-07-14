/************************************************************************
日 期: 2011-12-29
作 者: 郭祥
版 本: v1.3
描 述: SNMP值转换异常
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp.datahandle;

/**
 * SNMP值转换异常
 * @author 郭祥
 */
public class SnmpValueTransException extends RuntimeException
{
    /**
     * 构造函数
     */
    public SnmpValueTransException()
    {
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public SnmpValueTransException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public SnmpValueTransException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public SnmpValueTransException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
