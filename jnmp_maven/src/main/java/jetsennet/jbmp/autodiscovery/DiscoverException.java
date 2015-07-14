/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.autodiscovery;

/**
 * 自动发现时发现异常
 * @author Guo
 */
public class DiscoverException extends Exception
{

    /**
     * 构造方法
     */
    public DiscoverException()
    {
        super();
    }

    /**
     * 构造方法
     * @param msg 参数
     */
    public DiscoverException(String msg)
    {
        super(msg);
    }

    /**
     * 构造方法
     * @param t 参数
     */
    public DiscoverException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造方法
     * @param msg 参数
     * @param t 参数
     */
    public DiscoverException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
