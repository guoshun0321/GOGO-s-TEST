/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

/**日志记录器接口
 * @author 李小敏
 *
 */
public interface ILogger {
	
    /**写日志
     * @param logName
     * @param level
     * @param message
     * @param exception
     */
    void log(String logName, LogLevel level, String message, Exception exception);
   
    /**注册日志记录器
     * @param appender
     */
    void registLogAppender(ILogAppender appender);
}