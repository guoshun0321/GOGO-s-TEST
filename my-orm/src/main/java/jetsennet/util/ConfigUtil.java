/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
 ************************************************************************/
package jetsennet.util;

import java.util.Properties;
import java.io.*;
import java.lang.IllegalAccessException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * 配置读取
 * @author 李小敏
 *
 */
public class ConfigUtil
{
	private static Properties props = null;
	private final static String CONFIG_FILE_PATH = "config.properties";

	/**
	 * 先从根目录读取
	 * 再从上一级目录读取
	 */
	private synchronized static void loadProperties()
	{
		if (props != null)
			return;			

		props = new Properties();
		try
		{
			InputStream input = ConfigUtil.class.getResourceAsStream("/"+CONFIG_FILE_PATH);
			
			if (input == null)
			{
				ClassLoader classLoader = ConfigUtil.class.getClassLoader();
				/**classLoader = getTCL();
				/ClassLoader.getSystemResource(CONFIG_FILE_PATH)*/
				input = ConfigUtil.class.getResourceAsStream(""+classLoader.getResource(CONFIG_FILE_PATH));
			}
			
			if(input==null)
			{
				String configPath = StringUtil.trimEnd(System.getProperty("user.dir"),'/','\\')+"/"+CONFIG_FILE_PATH;
				input = new BufferedInputStream(new FileInputStream(configPath));				
			}
						
			if(input==null)
			{
				return;
			}		
			
			props.load(input);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取config.properties文件中的属性值
	 * 
	 * @param propName 属性名
	 * @return 属性值
	 */
	public static String getProperty(String propName)
	{
		loadProperties();
		return props.getProperty(propName);
	}

	@SuppressWarnings("unchecked")
	private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException
	{		
		Method method = null;
		try
		{
			method = Thread.class.getMethod("getContextClassLoader", null);
		}
		catch (NoSuchMethodException e)
		{
			return null;
		}

		return (ClassLoader) method.invoke(Thread.currentThread(), null);
	}
}