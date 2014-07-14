/************************************************************************
日 期：2011-12-10
作 者: 郭祥
版 本：v1.3
描 述: 错误信息常量
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

/**
 * 错误信息常量
 * @author 郭祥
 */
public class ErrorMessageConstant
{

    /**
     * 自动发现时，无法连接到指定采集器
     */
    public static final String AUTODIS_ERROR_NOCONN = "无法连接到指定采集器。";
    /**
     * 远程调用方法失败
     */
    public static final String RMI_ERROR = "远程调用方法<%s>失败。";
    /**
     * 报警时间处理模块未开启
     */
    public static final String EVENT_HANDLE_NOSTART = "报警事件处理模块未开启，请先开启报警事件处理模块。";
}
