/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 数据库连接信息
 * @author 李小敏
 */
public class ConnectionInfo
{
	static int INITIAL_POOL_SIZE = 10;
	static int MIN_POOL_SIZE = 10;
	static int MAX_POOL_SIZE = 200;
	static int ACQUIRE_INCREMENT = 10;
	
	private String dbDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private int initialPoolSize = INITIAL_POOL_SIZE; // 初始化时获取*个连接
	private int minPoolSize = MIN_POOL_SIZE; // 连接池中保留的最小连接数
	private int maxPoolSize = MAX_POOL_SIZE; // 连接池中保留的最大连接数
	private int acquireIncrement = ACQUIRE_INCREMENT; // 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数
	private int maxIdleTime = 600; // 最大空闲时间,*秒内未使用则连接被丢弃
	private int idleConnectionTestPeriod = 600; // 每*秒检查所有连接池中的空闲连接
	
	static
	{
		INITIAL_POOL_SIZE = getParamValue("initial_pool_size",INITIAL_POOL_SIZE);
		MIN_POOL_SIZE = getParamValue("min_pool_size",MIN_POOL_SIZE);
		MAX_POOL_SIZE = getParamValue("max_pool_size",MAX_POOL_SIZE);
		ACQUIRE_INCREMENT = getParamValue("acquire_increment",ACQUIRE_INCREMENT);		
	}
	
	/**获取连接快捷方式
	 * @param prefix
	 * @return
	 */
	public static ConnectionInfo createConnectionInfo(String prefix)
	{
		ConnectionInfo cinfo = new ConnectionInfo(
				DbConfig.getProperty(prefix + "_driver"), 
				DbConfig.getProperty(prefix + "_dburl"), 
				DbConfig.getProperty(prefix + "_dbuser"),
				DbConfig.getProperty(prefix + "_dbpwd"));
		
		cinfo.setInitialPoolSize(getParamValue(prefix+"_initial_pool_size",INITIAL_POOL_SIZE));
		cinfo.setMinPoolSize(getParamValue(prefix+"_min_pool_size",MIN_POOL_SIZE));
		cinfo.setMaxPoolSize(getParamValue(prefix+"_max_pool_size",MAX_POOL_SIZE));
		cinfo.setAcquireIncrement(getParamValue(prefix+"_acquire_increment",ACQUIRE_INCREMENT));
		
		return cinfo;
	}

	/**
	 * 数据库连接字串对象
	 */
	public ConnectionInfo(String driver, String url, String user, String pwd)
	{
		this.setDbDriver(driver);
		this.setDbUrl(url);
		this.setDbUser(user);
		this.setDbPwd(pwd);
	}

	public ConnectionInfo(String driver, String url, String user, String pwd, int initialPoolSize, int minPoolSize, int maxPoolSize, int maxIdleTime,
			int acquireIncrement, int idleConnectionTestPeriod)
	{
		this.setDbDriver(driver);
		this.setDbUrl(url);
		this.setDbUser(user);
		this.setDbPwd(pwd);
		this.setInitialPoolSize(initialPoolSize);
		this.setMaxIdleTime(maxIdleTime);
		this.setMaxPoolSize(maxPoolSize);
		this.setMinPoolSize(minPoolSize);
		this.setAcquireIncrement(acquireIncrement);
		this.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
	}	
	
	private static int getParamValue(String paramName,int defaultValue)
	{
		String  param = DbConfig.getProperty(paramName);
		if(param!=null && param.trim().length()>0){
			return Integer.parseInt(param.trim());
		}
		
		return defaultValue;
	}

	public String getDbDriver()
	{
		return this.dbDriver;
	}

	public void setDbDriver(String value)
	{
		this.dbDriver = value;
	}

	public String getDbUrl()
	{
		return this.dbUrl;
	}

	public void setDbUrl(String value)
	{
		this.dbUrl = value;
	}

	public String getDbUser()
	{
		return this.dbUser;
	}

	public void setDbUser(String value)
	{
		this.dbUser = value;
	}

	public String getDbPwd()
	{
		return this.dbPwd;
	}

	public void setDbPwd(String value)
	{
		this.dbPwd = value;
	}

	public int getInitialPoolSize()
	{
		return initialPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize)
	{
		this.initialPoolSize = initialPoolSize;
	}

	public int getMinPoolSize()
	{
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize)
	{
		this.minPoolSize = minPoolSize;
	}

	public int getMaxPoolSize()
	{
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize)
	{
		this.maxPoolSize = maxPoolSize;
	}

	public int getMaxIdleTime()
	{
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime)
	{
		this.maxIdleTime = maxIdleTime;
	}

	public int getAcquireIncrement()
	{
		return acquireIncrement;
	}

	public void setAcquireIncrement(int acquireIncrement)
	{
		this.acquireIncrement = acquireIncrement;
	}

	public int getIdleConnectionTestPeriod()
	{
		return idleConnectionTestPeriod;
	}

	public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod)
	{
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}

	public String toString()
	{
		return (this.getDbDriver() + this.getDbUrl() + this.getDbUser() + this.getDbPwd()).toUpperCase();
	}
}