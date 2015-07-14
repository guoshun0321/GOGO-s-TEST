/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

/**添加日志接口
 * @author 李小敏
 *
 */
public interface ILogAppender {

	
    /**记录日志
     * @param logName
     * @param level
     * @param message
     * @param exception
     */
    void appenderLog(String logName, LogLevel level, String message, Exception exception); }