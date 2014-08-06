package jetsennet.jsmp.nav.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.cache.xmem.CacheException;
import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.util.IOUtil;
import jetsennet.util.PropertiesUtil;

public class Config
{

	/**
	 * memcached集群ip地址
	 */
	public static final String CACHE_SERVERS;
	/**
	 * memcached连接池大小
	 */
	public static final int CACHE_POOLSIZE;
	/**
	 * memcached超时时间
	 */
	public static final int CACHE_TIMEOUT;
	/**
	 * activeMQ服务器
	 */
	public static final String MQ_SERVERS;
	/**
	 * activeMQ用户名
	 */
	public static final String MQ_USER;
	/**
	 * activeMQ密码
	 */
	public static final String MQ_PWD;
	/**
	 * activeMQ队列名称
	 */
	public static final String MQ_QUEUE;
	/**
	 * SM系统rtsp地址
	 */
	public static final String SM_RTSP;
	/**
	 * SM服务器
	 */
	public static final String SM_SERVERID;
	/**
	 * SM交互Token保存时间
	 */
	public static final int SM_TIMEOUT;
	/**
	 * 是否调试状态
	 */
	public static boolean ISDEBUG = true;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	static
	{
		InputStream in = null;
		try
		{
			in = DataCacheOp.class.getResourceAsStream("/config.properties");
			Properties prop = new Properties();
			prop.load(in);

			CACHE_SERVERS = PropertiesUtil.getProperties(prop, "memcached.servers", null, false);
			CACHE_POOLSIZE = PropertiesUtil.getProperties(prop, "memcached.poolsize", 1, true);
			CACHE_TIMEOUT = PropertiesUtil.getProperties(prop, "memcached.timeout", 30 * 60, true);

			MQ_SERVERS = PropertiesUtil.getProperties(prop, "mq.servers", null, false);
			MQ_USER = PropertiesUtil.getProperties(prop, "mq.user", null, false);
			MQ_PWD = PropertiesUtil.getProperties(prop, "mq.pwd", null, false);
			MQ_QUEUE = PropertiesUtil.getProperties(prop, "mq.queue", null, false);

			SM_RTSP = PropertiesUtil.getProperties(prop, "sm.rtsp", null, false);
			SM_TIMEOUT = PropertiesUtil.getProperties(prop, "sm.timeout", 30 * 60, true);
			SM_SERVERID = PropertiesUtil.getProperties(prop, "sm.serverId", null, false);

			ISDEBUG = PropertiesUtil.getPropertiesBoolean(prop, "sys.isdebug", true);
			
			logger.info("memecached地址：" + CACHE_SERVERS);
			logger.info("activeMQ地址：" + MQ_SERVERS + " : " + MQ_QUEUE);
			logger.info("rtsp地址：" + SM_RTSP);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new CacheException(ex);
		}
		finally
		{
			IOUtil.close(in);
		}
	}

}
