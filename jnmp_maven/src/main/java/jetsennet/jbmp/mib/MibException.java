/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib;

/**
 * 解析MIB文件时抛出的异常
 * @author Guo
 */
public class MibException extends RuntimeException
{
    /**
     * 构造函数
     */
    public MibException()
    {

    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public MibException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public MibException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public MibException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
