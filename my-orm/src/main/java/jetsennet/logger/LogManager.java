/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

import java.util.*;
import java.lang.reflect.*;

import jetsennet.util.*;


/**日志管理
 * @author 李小敏
 *
 */
public class LogManager {
	
    private static ILogger logger = createLogger();
    private static LogLevel logLevel = parseLogLevel();
    private static HashMap<String,ILog> hTabLog = new HashMap<String,ILog>();
    private static Integer lockLog = new Integer(1);
    
    /**取得日志实例
     * @param name
     * @return 日志实例
     */
    public static ILog getLogger(String name)
    {
        name = StringUtil.isNullOrEmpty(name) ? "log" : name;

        synchronized(lockLog)
        {
	        if (hTabLog.containsKey(name))
	        {
	            return (ILog)hTabLog.get(name);
	        }
	        else
	        {
	            ILog log = new Log(name, logLevel, logger);
	            try
	            {
	                hTabLog.put(name, log);
	            }
	            catch(Exception ex){ };
	            return log;
	        }       
        }
    }
    
    public static void setLogLevel(LogLevel level)
    {
    	logLevel = level;
    	
    	synchronized(lockLog)
        {
    		for (Iterator<String> iter = hTabLog.keySet().iterator(); iter.hasNext();)
			{
    			ILog log = hTabLog.get(iter.next());
    			log.setLogLevel(level);
			}	         	               
        }
    }
    
    public static void setLogLevel(String name,LogLevel level)
    {
    	logLevel = level;
    	
    	 synchronized(lockLog)
         {
 	        if (hTabLog.containsKey(name))
 	        {
 	            hTabLog.get(name).setLogLevel(level);
 	        } 	               
         }
    }
    
    public static void registLogAppender(ILogAppender appender)
    {
    	logger.registLogAppender(appender);
    }
    
    /**创建日志记录器
     * @return
     */
    @SuppressWarnings("unchecked")
    private static ILogger createLogger()
    {       
        ILogger logger = new Logger();        
        
        //预留五个日志记录器
        for(int i=0;i<5;i++)
        {
        	String logAppender = ConfigUtil.getProperty("LogAppender"+i);
        	if(!StringUtil.isNullOrEmpty(logAppender))
        	{
        		try{
        			String[] logAppenderArr = logAppender.split(",");
        			int paramLength = logAppenderArr.length;
        			Class appendClass = Class.forName(logAppenderArr[0]);
        			
        			if(paramLength==1)
        			{
        				logger.registLogAppender((ILogAppender) appendClass.newInstance());
        			}
        			else
        			{
        				Object[] params = new Object[paramLength-1];
        				for(int p=1;p<paramLength;p++)
        				{
        					params[p-1] = logAppenderArr[p];
        				}
        				
	        			Constructor[] constructors = appendClass.getConstructors();
	        			for(Constructor constructor: constructors)
	        			{
	        				if(constructor.getParameterTypes().length==paramLength-1)
	        				{
	        					logger.registLogAppender((ILogAppender)constructor.newInstance(params));
	        					break;
	        				}
	        			}
        			}
        			
        		}catch(Exception ex){}
        	}
        }
        return logger;
    }

    
    /**解析日志记录级别
     * @return
     */
    private static LogLevel parseLogLevel()
    {
    	String levelConfig = ConfigUtil.getProperty("LogLevel");
    	
        if (StringUtil.isNullOrEmpty(levelConfig))
            return LogLevel.Info;
       
        try
        {
            return LogLevel.valueOf(Integer.parseInt(levelConfig));
        }
        catch(Exception ex)
        {
            return LogLevel.Info;
        }
    }
}