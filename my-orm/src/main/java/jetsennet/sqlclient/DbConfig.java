/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import jetsennet.util.ConfigUtil;
import jetsennet.util.StringUtil;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库配置
 * @author 李小敏
 */
public class DbConfig
{
	private static Integer lock = new Integer(1);
	private static Properties props = null;
	private static Map<String, ComboPooledDataSource> dataSources = new Hashtable<String, ComboPooledDataSource>();
	private final static String CONFIG_FILE_PATH = "dbconfig.properties";

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
				input = ConfigUtil.class.getResourceAsStream(""+classLoader.getResource(CONFIG_FILE_PATH));				
			}
			
			if(input==null)
			{
				String configPath = StringUtil.trimEnd(
						System.getProperty("user.dir"),'/','\\')+"/"+CONFIG_FILE_PATH;
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
	 * 取得数据库配置属性
	 * 
	 * @param propName 属性名
	 * @return 属性值
	 */
	public static String getProperty(String propName)
	{
		loadProperties();
		return props.getProperty(propName);
	}

	//http://svn.apache.org/viewvc/commons/proper/dbcp/trunk/doc/ManualPoolingDataSourceExample.java?view=markup
	/**
	 * 取得数据源
	 * @param cInfo	
	 * @return Connection
	 * @throws
	 */
	public static Connection getConnection(ConnectionInfo cInfo) throws SQLException
	{
		if(cInfo==null || StringUtil.isNullOrEmpty(cInfo.getDbDriver()))
		{
			new SQLException("无效的数据库连接");
		}
		
		synchronized (lock)
		{
			if (cInfo.getMaxPoolSize() == 0)
			{
				try
				{
					Class.forName(cInfo.getDbDriver());
				}
				catch (ClassNotFoundException e)
				{}
				
				Connection conn = DriverManager.getConnection(
						cInfo.getDbUrl(), cInfo.getDbUser(), cInfo.getDbPwd());
				return conn;
			}

			ComboPooledDataSource dataSource = dataSources.get(cInfo.toString());
			if (dataSource == null)
			{
				// 初始化cInfo对应的连接池，并put到dataSources MAP中

				dataSource = new ComboPooledDataSource();
				try
				{
					dataSource.setDriverClass(cInfo.getDbDriver());
				}
				catch (PropertyVetoException e)
				{
					throw new SQLException(e.getMessage());
				}
				dataSource.setCheckoutTimeout(5000);
				dataSource.setJdbcUrl(cInfo.getDbUrl());
				dataSource.setUser(cInfo.getDbUser());
				dataSource.setPassword(cInfo.getDbPwd());
				dataSource.setInitialPoolSize(cInfo.getInitialPoolSize());
				dataSource.setMinPoolSize(cInfo.getMinPoolSize());
				dataSource.setMaxPoolSize(cInfo.getMaxPoolSize());
				dataSource.setAcquireIncrement(cInfo.getAcquireIncrement());
				dataSource.setMaxIdleTime(cInfo.getMaxIdleTime());
				dataSource.setIdleConnectionTestPeriod(cInfo.getIdleConnectionTestPeriod());				

				dataSources.put(cInfo.toString(), dataSource);
				return dataSource.getConnection();

			}
			else
			{
				return dataSource.getConnection();
			}
		}
	}
}