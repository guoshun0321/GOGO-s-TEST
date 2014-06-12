package jetsennet.jsmp.nav.cache.xmem;

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

public class MemcachedOp
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
    private static final Logger logger = LoggerFactory.getLogger(MemcachedOp.class);

    private static final MemcachedOp instance = new MemcachedOp();

    private MemcachedOp()
    {
        try
        {
            builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(Config.CACHE_SERVERS));
            //        builder.setFailureMode(true);
            // 使用二进制文件  
            builder.setCommandFactory(new BinaryCommandFactory());
            // 连接池大小
            builder.setConnectionPoolSize(Config.CACHE_POOLSIZE);
            client = builder.build();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            client = null;
        }
    }

    public static MemcachedOp getInstance()
    {
        return instance;
    }

    public void deleteAll()
    {
        try
        {
            client.flushAll();
        }
        catch (Exception ex)
        {
            this.exceptionHandle(ex);
        }
    }

    public void put(String key, Object value)
    {
        try
        {
            client.set(key, Config.CACHE_TIMEOUT, value);
        }
        catch (Exception ex)
        {
            this.exceptionHandle(ex);
        }
    }

    public <T> T get(String key)
    {
        return this.get(key, false);
    }

    public int getInt(String key)
    {
        return (int) this.get(key, false);
    }

    public <T> T get(String key, boolean isNullable)
    {
        Object retval = null;
        try
        {
            retval = client.get(key, Config.CACHE_TIMEOUT);
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

    public List<Integer> getListInt(String key)
    {
        List<Integer> retval = null;
        try
        {
            retval = client.get(key, Config.CACHE_TIMEOUT);
        }
        catch (Exception ex)
        {
            this.exceptionHandle(ex);
        }
        return retval;
    }

    public List<String> getListString(String key)
    {
        List<String> retval = null;
        try
        {
            retval = client.get(key, Config.CACHE_TIMEOUT);
        }
        catch (Exception ex)
        {
            this.exceptionHandle(ex);
        }
        return retval;
    }

    public Map<String, Object> gets(List<String> keys)
    {
        Map<String, Object> retval = null;
        try
        {
            retval = client.get(keys, Config.CACHE_TIMEOUT);
        }
        catch (Exception ex)
        {
            this.exceptionHandle(ex);
        }
        return retval;
    }

    public Object del(String key)
    {
        Object retval = null;
        try
        {
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
                this.client = builder.build();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new CacheException(ex);
        }
        finally
        {
            this.client = null;
        }
    }

    /**
     * 关闭client。同步操作由调用者实现。
     */
    public void shutdown()
    {
        try
        {
            this.client.shutdown();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new CacheException(ex);
        }
        finally
        {
            this.client = null;
        }
    }

    private void exceptionHandle(Exception ex)
    {
        if (ex instanceof MemcachedException)
        {
            logger.error("Memcached客户端操作失败！", ex);
            throw new CacheException(ex);
        }
        else if (ex instanceof TimeoutException)
        {
            logger.error("Memcached客户端操作超时！", ex);
            throw new CacheException(ex);
        }
        else
        {
            logger.error("", ex);
            throw new CacheException(ex);
        }
    }
}
