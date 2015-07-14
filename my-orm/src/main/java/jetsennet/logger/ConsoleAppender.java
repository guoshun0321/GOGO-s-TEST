/************************************************************************
日  期：		2012-04-18
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.logger;

import java.util.*;
import java.io.*;

import jetsennet.util.*;

/** 
 * 控制台输出日志
 * @author 李小敏
 */
public class ConsoleAppender implements ILogAppender{
	
	public ConsoleAppender()
	{}
   
    
    
    /** 记录日志
     * @see jetsennet.logger.ILogAppender#appenderLog(java.lang.String, jetsennet.logger.LogLevel, java.lang.String, java.lang.Exception)
     */
    public void appenderLog(String logName, LogLevel level, String message, Exception exception)
    {
    	if(StringUtil.isNullOrEmpty(message) && exception==null){
    		return;
    	}    	
        try
        {  
    	    System.out.println(String.format("[%s：%s %s\r\n%s]",level.toString(),DateUtil.formatDateString(new Date(),"HH:mm:ss"),logName,message));
    	               
            if(exception != null)
            {
            	StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw, true);
                exception.printStackTrace(pw);
                pw.flush();
                sw.flush();
                
                System.out.println(sw.toString()); 
            } 
        }
        catch(Exception ex) { }
    }
}