package jetsennet.jbmp.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import jetsennet.cache.CacheManager;
import jetsennet.sqlclient.DbConfig;

import org.apache.log4j.Logger;

public class BMPCacheServlet extends HttpServlet
{

    /**
     * 缓存管理
     */
    private CacheManager manager;
    private static final long serialVersionUID = 1L;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BMPCacheServlet.class);

    @Override
    public void init() throws ServletException
    {
        try
        {
            logger.debug("分布式缓存启动开始。");
            manager = CacheManager.getInstance();
            manager.start("ehcache.xml", DbConfig.DEFAULT_CONN, CacheManager.TYPE_SERVER);
            this.initManualCache();
            logger.debug("分布式缓存启动结束。");
        }
        catch (Exception ex)
        {
            logger.error("分布式缓存启动异常。", ex);
        }
    }

    /**
     * 添加自定义缓存
     */
    protected void initManualCache()
    {

    }

}
