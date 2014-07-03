/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.exception;

/**
 * 数据转换出错时抛出的异常
 * @author Guo
 */
public class ConvertException extends RuntimeException
{

    /**
     * 构造函数
     */
    public ConvertException()
    {
        super();
    }

    /**
     * 构造函数
     * @param msg 参数
     */
    public ConvertException(String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * @param t 参数
     */
    public ConvertException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * @param msg 参数
     * @param t 参数
     */
    public ConvertException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
