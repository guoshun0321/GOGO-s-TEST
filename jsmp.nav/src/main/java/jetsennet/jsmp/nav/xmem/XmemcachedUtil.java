package jetsennet.jsmp.nav.xmem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.yanf4j.core.impl.StandardSocketOption;

/**
 * memcached操作。非线程安全
 * 
 * @author 郭祥
 */
public class XmemcachedUtil
{

	/**
	 * XMemcached client生成器
	 */
	private XMemcachedClientBuilder builder;
	/**
	 * XMemcached client
	 */
	private volatile MemcachedClient client;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(XmemcachedUtil.class);

	private static final XmemcachedUtil instance = new XmemcachedUtil();

	private XmemcachedUtil()
	{
		try
		{
			builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(Config.CACHE_SERVERS));
			//			        builder.setFailureMode(true);
			// 使用二进制文件  
			builder.setCommandFactory(new BinaryCommandFactory());
			// 连接池大小
			//			builder.setConnectionPoolSize(Config.CACHE_POOLSIZE);
			builder.setSocketOption(StandardSocketOption.TCP_NODELAY, false);
			client = builder.build();
			if (Config.ISDEBUG)
			{
				logger.debug("初始化缓存");
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			client = null;
		}
	}

	public static XmemcachedUtil getInstance()
	{
		return instance;
	}

	/**
	 * 清空缓存
	 */
	public void deleteAll()
	{
		try
		{
			if (Config.ISDEBUG)
			{
				logger.debug("清空缓存");
			}
			client.flushAll();
		}
		catch (Exception ex)
		{
			this.exceptionHandle(ex);
		}
	}

	/**
	 * put数据，数据过期时间为默认过期时间（Config.CACHE_TIMEOUT）
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value)
	{
		putTimeout(key, value, Config.CACHE_TIMEOUT);
	}

	/**
	 * 往缓存放置数据，并设置过期时间
	 * 
	 * @param key
	 * @param value
	 * @param timeout 单位为秒
	 */
	public void putTimeout(String key, Object value, int timeout)
	{
		try
		{
			if (Config.ISDEBUG)
			{
				logger.debug(String.format("向缓存添加数据: %s , %s, %s秒", key, value, timeout));
			}
			client.set(key, timeout, value);
		}
		catch (Exception ex)
		{
			this.exceptionHandle(ex);
		}
	}

	/**
	 * 从缓存获取数据，结果为null（key不存在或者对应值为null时），直接返回null
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key)
	{
		return this.get(key, true);
	}

	/**
	 * 从缓存获取数据
	 * 
	 * @param key 数据key
	 * @param isNullable 为null时是否抛出异常
	 * @return
	 */
	public <T> T get(String key, boolean isNullable)
	{
		Object retval = null;
		try
		{
			if (key != null)
			{
				retval = client.get(key, Config.CACHE_TIMEOUT);
			}
			if (Config.ISDEBUG)
			{
				logger.debug(String.format("从缓存获取数据: %s, %s", key, retval));
			}
		}
		catch (Exception ex)
		{
			this.exceptionHandle(ex);
		}
		if (retval == null)
		{
			if (isNullable)
			{
				return null;
			}
			else
			{
				throw new UncheckedNavException("取值为null：" + key);
			}
		}
		else
		{
			return (T) retval;
		}
	}

	/**
	 * 从缓存批量获取数据
	 * 
	 * @param key 数据key
	 * @param isNullable 为null时是否抛出异常
	 * @return
	 */
	public Map<String, Object> gets(List<String> keys)
	{
		if (keys == null || keys.isEmpty())
		{
			return new HashMap<String, Object>(0);
		}
		Map<String, Object> retval = null;
		try
		{
			if (Config.ISDEBUG)
			{
				logger.debug(String.format("从缓存批量获取数据: %s", keys));
			}
			retval = client.get(keys, Config.CACHE_TIMEOUT);
		}
		catch (Exception ex)
		{
			this.exceptionHandle(ex);
		}
		return retval;
	}

	/**
	 * 从缓存中删除数据
	 * 
	 * @param key
	 * @return
	 */
	public Object del(String key)
	{
		Object retval = null;
		try
		{
			if (Config.ISDEBUG)
			{
				logger.debug(String.format("从缓存删除数据: %s", key));
			}
			retval = client.delete(key);
		}
		catch (Exception ex)
		{
			this.exceptionHandle(ex);
		}
		return retval;
	}

	/**
	 * 开启client。同步操作由调用者实现。
	 */
	public void setup()
	{
		try
		{
			if (this.client != null)
			{
				if (Config.ISDEBUG)
				{
					logger.debug("开启缓存");
				}
				this.client = builder.build();
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			this.client = null;
			throw new XmemcachedException(ex);
		}
	}

	/**
	 * 关闭client。同步操作由调用者实现。
	 */
	public void shutdown()
	{
		try
		{
			if (this.client != null)
			{
				if (Config.ISDEBUG)
				{
					logger.debug("关闭缓存");
				}
				this.client.shutdown();
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new XmemcachedException(ex);
		}
		finally
		{
			this.client = null;
		}
	}

	/**
	 * 异常处理
	 * 
	 * @param ex
	 */
	private void exceptionHandle(Exception ex)
	{
		if (ex instanceof MemcachedException)
		{
			logger.error("Memcached客户端操作失败！", ex);
			throw new XmemcachedException(ex);
		}
		else if (ex instanceof TimeoutException)
		{
			logger.error("Memcached客户端操作超时！", ex);
			throw new XmemcachedException(ex);
		}
		else
		{
			logger.error("", ex);
			throw new XmemcachedException(ex);
		}
	}

	/**
	 * 获取原生client
	 * @return
	 */
	public MemcachedClient getClient()
	{
		return this.client;
	}
}
