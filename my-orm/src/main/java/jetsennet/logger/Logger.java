/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

import java.util.*;

/**
 * 日志记录
 * @author 李小敏
 */
public class Logger implements ILogger{
	   
	private static Integer lockLogs = new Integer(1);
	
    /** 记录日志
     * @see jetsennet.logger.ILogger#log(java.lang.String, jetsennet.logger.LogLevel, java.lang.String, java.lang.Exception)
     */
    public void log(String logName,LogLevel level, String message, Exception exception)
    {
    	synchronized(lockLogs)
    	{
	        for(ILogAppender item : logAppender)
	        {
	            item.appenderLog(logName,level, message,exception);
	        }
    	}
    }

    private List<ILogAppender> logAppender = new ArrayList<ILogAppender>();
    
    /**注册日志记录器
     * @see jetsennet.logger.ILogger#registLogAppender(jetsennet.logger.ILogAppender)
     */
    public void registLogAppender(ILogAppender appender)
    {
        logAppender.add(appender);
    }
}