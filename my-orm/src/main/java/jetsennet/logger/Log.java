/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

/**
 * 日志记录器
 * @author 李小敏
 */
public class Log implements ILog {
	public Log(String name,LogLevel level,ILogger logger)
    {
        this.name = name;
        this.level = level;
        this.logger = logger;
    }

    private String name;
    public String getName()
    {
        return name;
    }

    ILogger logger;
    ILogger getLogger()
    {
        return logger;
    }

    private LogLevel level;
    
    /**
     * 设置级别
     * @param level
     * @return
     */
    public void setLogLevel(LogLevel level)
    {
    	this.level = level;
    }
    
    public boolean isEnabledFor(LogLevel logLevel)
    {
        if (logLevel == LogLevel.All)
            return true;
        if (logLevel == LogLevel.Off)
            return false;
        if (logLevel.toInteger() >= level.toInteger())
            return true;
        
        return false;
    }

    /**
     * 记录
     * @param level
     * @param message
     * @param exception
     */
    public void log(LogLevel level,String message, Exception exception)
    {
    	if(level == LogLevel.Error)
    	{
    		error(message,exception);
    	}
    	else if(level == LogLevel.Warn)
    	{
    		warn(message,exception);
    	}
    	else if(level == LogLevel.Info)
    	{
    		info(message,exception);
    	}
    	else if(level == LogLevel.Debug)
    	{
    		debug(message,exception);
    	}
    }
    
    public void error(String message)
    {
        error(message, null);
    }
    public void error(String message, Exception exception)
    {
        if (isEnabledFor(LogLevel.Error))
            getLogger().log(getName(),LogLevel.Error, message, exception);
    }

    public void warn(String message)
    {
        warn(message, null);
    }
    public void warn(String message, Exception exception)
    {
        if (isEnabledFor(LogLevel.Warn))
            getLogger().log(getName(), LogLevel.Warn, message, exception);
    }

    public void info(String message)
    {
        info(message, null);
    }
    public void info(String message, Exception exception)
    {
        if (isEnabledFor(LogLevel.Info))
            getLogger().log(getName(), LogLevel.Info, message, exception);
    }

    public void debug(String message)
    {
        debug(message, null);
    }
    public void debug(String message, Exception exception)
    {
        if (isEnabledFor(LogLevel.Debug))
            getLogger().log(getName(), LogLevel.Debug, message, exception);
    }
}