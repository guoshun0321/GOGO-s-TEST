/************************************************************************
日  期：		2009-06-30
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
 * 文件记录日志
 * @author 李小敏
 */
public class FileAppender implements ILogAppender{
	
	public FileAppender()
	{}
	
    /**文件记录日志
     * @param filePath
     */
    public FileAppender(String filePath)
    {
        if(!StringUtil.isNullOrEmpty(filePath))
        {
            this.filePath = StringUtil.trimEnd(filePath,'\\','/') + "/";
            File f = new File(this.filePath);
        	if (!f.exists()) {          
        		f.mkdirs();
        	}
        }
    }
    private String filePath = System.getProperty("user.dir")+ "/";
    
    
    /** 记录日志
     * @see jetsennet.logger.ILogAppender#appenderLog(java.lang.String, jetsennet.logger.LogLevel, java.lang.String, java.lang.Exception)
     */
    public void appenderLog(String logName, LogLevel level, String message, Exception exception)
    {
    	if(StringUtil.isNullOrEmpty(message) && exception==null){
    		return;
    	}
    	
        String strFileName = String.format("%sLog%s.jslog", filePath, DateUtil.formatDateString(new Date(),"yyyyMMdd"));
        //message = string.IsNullOrEmpty(message) ? exception.Message : message;
        try
        {            
            File f = new File(strFileName);
        	if (!f.exists()) {          
        		f.createNewFile(); 
        	}
    	    BufferedWriter output = new BufferedWriter(new FileWriter(f,true));
    	    output.write(String.format("[%s：%s %s\r\n%s]",level.toString(),DateUtil.formatDateString(new Date(),"HH:mm:ss"),logName,message));
    	               
            if(exception != null)
            {
            	output.write("\r\n");
            	
            	StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw, true);
                exception.printStackTrace(pw);
                pw.flush();
                sw.flush();
                
            	output.write(jetsennet.util.StringUtil.left(sw.toString(),1000)); 
            }   
    	    output.write("\r\n\r\n");
    	    output.close();
          
        }
        catch(Exception ex) { }
    }
}