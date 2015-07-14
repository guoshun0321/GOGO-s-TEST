/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

/**
 * 日志级别
 * @author 李小敏
 */
public enum LogLevel
{  
    /**
     * 禁止0
     */
    Off,    
    /**
     * 调试1
     */
    Debug,    
    /**
     * 信息2
     */
    Info,    
    /**
     * 警告3
     */
    Warn,   
    /**
     * 错误4
     */
    Error,    
    /**
     * 所有100
     */
    All;
    /**
     * @param val
     * @return LogLevel
     */
    public static LogLevel valueOf(int val)
    {
    	switch(val)
    	{
    	case 0:
    		return Off;
    	case 1:
    		return Debug;
    	case 2:
    		return Info;
    	case 3:
    		return Warn;
    	case 4:
    		return Error;
    	case 100:
    		return All;
    	}
    	return Error;
    }
    /**
     * @return LogLevel int
     */
    public int toInteger()
    {
    	switch(this)
    	{
    	case Off:
    		return 0;
    	case Debug:
    		return 1;
    	case Info:
    		return 2;
    	case Warn:
    		return 3;
    	case Error:
    		return 4;
    	case All:
    		return 100;
    	}
    	return 4;
    }
}