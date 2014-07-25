/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

/**日志记录接口
 * @author 李小敏
 *
 */
public interface ILog {


    /**记录日志名称
     * @return 日志名称
     */
    String getName();

    /**
     * 是否启用记录级别
     * @param level
     * @return 启用记录级别
     */
    boolean isEnabledFor(LogLevel level);
   
    /**
     * 设置级别
     * @param level
     * @return
     */
    void setLogLevel(LogLevel level);
    
    /**
     * 记录
     * @param level
     * @param message
     * @param exception
     */
    void log(LogLevel level,String message, Exception exception);
    
    /**记录错误
     * @param message
     */
    void error(String message);

    /**记录错误
     * @param message
     * @param exception
     */
    void error(String message, Exception exception);

    /**记录警告
     * @param message
     */
    void warn(String message);
   
    /**记录警告
     * @param message
     * @param exception
     */
    void warn(String message, Exception exception);
    
    /**记录信息
     * @param message
     */
    void info(String message);

    /**记录信息
     * @param message
     * @param exception
     */
    void info(String message, Exception exception);
    
    /**记录调试信息
     * @param message
     */
    void debug(String message);
    
    /**记录调试信息
     * @param message
     * @param exception
     */
    void debug(String message, Exception exception);    

}