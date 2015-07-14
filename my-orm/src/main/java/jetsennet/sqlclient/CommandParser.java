/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * SQL命令解析
 * @author 李小敏
 *
 */
public class CommandParser {
	
	protected final String REGEX_STRING = "n?'(([^']|'')*)'";
    protected final String REGEX_BRACKETS = "\\s*\\(([^\\(\\)]*)\\)";

    protected String commandString;
    protected String originalString;
    protected int matchCount = 0;       
    protected HashMap<String, String> stringFields;
       
    /**替换引号
     * @throws Exception
     */
    protected void replaceStringField() throws Exception
    {
        matchCount = 0;
        
        Pattern pattern = Pattern.compile(REGEX_STRING, Pattern.CASE_INSENSITIVE);        
		Matcher m = pattern.matcher(commandString);		
		StringBuffer sb = new StringBuffer();
		
		while(m.find())
		{
			matchCount++;
	        String _key = "[_" + (matchCount - 1) + "]";
	        this.stringFields.put(_key, m.group(1).replace("''", "'"));
	        m.appendReplacement(sb, _key);;
		}
		
		m.appendTail(sb);       
		commandString = sb.toString();
			
        if (commandString.indexOf("'") >= 0)
            throw new Exception("无效的Sql命令,缺少单引号!");
    }
   
    /**替换括号
     * @throws Exception
     */
    protected void replaceBrackets() throws Exception
    {
        matchCount = 0;
        
        Pattern pattern = Pattern.compile(REGEX_BRACKETS, Pattern.CASE_INSENSITIVE);        
				
        while (commandString.indexOf(")") >= 0)
        {
        	String odlCommandString = commandString;
        	
        	Matcher m = pattern.matcher(commandString);    		
    		StringBuffer sb = new StringBuffer();

    		while(m.find())
    		{
    			matchCount++;
    	        String key = "[_" + (matchCount - 1 + 1000) + "]";
    	        this.stringFields.put(key, m.group(1));
    	        m.appendReplacement(sb, key);;
    		}    		
            
    		m.appendTail(sb);       
    		commandString = sb.toString();
    		
            if (odlCommandString.equals(commandString))
                throw new Exception("无效的Sql命令,缺少括号!");
        }        
		
        if (commandString.indexOf(")") >= 0)
            throw new Exception("无效的Sql命令,缺少括号!");
    }       

    
    /**替换所有字符或括号值
     * @param str
     * @return
     */
    protected String replaceAllStringFieldValue(String str)
    {
        return replaceAllStringFieldValue(str, false);
    }
    
    /**替换所有字符或括号值
     * @param str
     * @param withQoutOrBrackets
     * @return
     */
    protected String replaceAllStringFieldValue(String str, boolean withQoutOrBrackets)
    {
        String strResult = str;
        int index = 0;
        while (strResult.indexOf("[_") >= 0 && index < 100)
        {
            strResult = replaceAllFieldValue(strResult, withQoutOrBrackets);
            index++;
        }
        return strResult;
    }
    
    /**替换所有字符或括号值
     * @param str
     * @param withQoutOrBrackets
     * @return
     */
    private String replaceAllFieldValue(String str, boolean withQoutOrBrackets)
    {
        for(String key : this.stringFields.keySet())
        {
            if (str.indexOf(key) >= 0)
            {
                String value = this.stringFields.get(key);
                str = str.replace(key, withQoutOrBrackets ? (key.length() > 6 ? "(" + value + ")" : "'" + value.replace("'", "''") + "'") : value);
            }
        }
        return str;
    }
   
    /**替换字符或括号值
     * @param key
     * @return
     * @throws Exception
     */
    protected String getStringFieldValue(String key) throws Exception
    {
        if (this.stringFields.containsKey(key))
        {
            return this.stringFields.get(key);
        }
        else
            throw new Exception("无效的Sql命令,错误出现在：" + replaceAllStringFieldValue(key, true));
    }
}