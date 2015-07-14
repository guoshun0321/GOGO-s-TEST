package jetsennet.orm.configuration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jetsennet.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationBuilderProp implements IConfigurationBuilder
{

    /**
     * 配置文件
     */
    private final String src;
    /**
     * 驱动
     */
    private static final String DRIVER = "db.driver";
    /**
     * 连接字符串
     */
    private static final String DBURL = "db.url";
    /**
     * 用户名
     */
    private static final String DBUSER = "db.user";
    /**
     * 密码
     */
    private static final String DBPWD = "db.pwd";
    /**
     * 池配置
     */
    private static final String POOL_DATASOURCE = "pool.datasource";
    private static final String POOL_HEADER = "pool.";
    /**
     * DEBUG模式
     */
    private static final String IS_DEBUG = "sys.isdebug";
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBuilderProp.class);

    public ConfigurationBuilderProp(String src)
    {
        if (src == null || src.isEmpty())
        {
            this.src = Configuration.DEFAULT_CONFIG;
        }
        else
        {
            this.src = src;
        }
    }

    public Configuration genConfiguration()
    {
        Configuration retval = readFromProperties(src);
        return retval;
    }

    /**
     * 从properties文件获取配置信息
     * 
     * @param src
     * @return
     */
    private Configuration readFromProperties(String src)
    {
        Configuration retval = null;
        InputStream in = null;
        try
        {
            in = Configuration.class.getResourceAsStream(src);
            Properties prop = new Properties();
            prop.load(in);

            // 连接信息
            ConnectionInfo connInfo = parseConnectionInfo(prop);
            // 连接池信息
            DbPoolInfo poolInfo = parseDbPoolInfo(prop);
            // 是否DUBUG模式
            boolean isDebug = PropertiesUtil.getPropertiesBoolean(prop, IS_DEBUG, false);

            retval = new Configuration(connInfo, poolInfo, src, isDebug);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new ConfigurationException(ex);
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                in = null;
            }
        }
        return retval;
    }

    /**
     * 解析数据库连接信息
     * 
     * @param prop
     * @return
     */
    private ConnectionInfo parseConnectionInfo(Properties prop)
    {
        String driver = PropertiesUtil.getProperties(prop, DRIVER, null, false);
        String url = PropertiesUtil.getProperties(prop, DBURL, null, false);
        String user = PropertiesUtil.getProperties(prop, DBUSER, null, false);
        String pwd = PropertiesUtil.getProperties(prop, DBPWD, null, false);
        return new ConnectionInfo(driver, url, user, pwd);
    }

    /**
     * 解析连接池信息
     * 
     * @param prop
     * @return
     */
    private DbPoolInfo parseDbPoolInfo(Properties prop)
    {
        String datasource = PropertiesUtil.getProperties(prop, POOL_DATASOURCE, null, false);
        Map<String, String> map = new HashMap<String, String>();
        Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<Object, Object> entry = it.next();
            String key = (String) entry.getKey();
            if (key.startsWith(POOL_HEADER))
            {
                key = key.substring(POOL_HEADER.length());
                String value = (String) entry.getValue();
                map.put(key, value);
            }
        }
        return new DbPoolInfo(datasource, map);
    }

    @Override
    public int hashCode()
    {
        if (this.src != null)
        {
            return this.src.hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this.src != null && obj != null && obj instanceof ConfigurationBuilderProp)
        {
            ConfigurationBuilderProp temp = (ConfigurationBuilderProp) obj;
            return this.src.equals(temp.src);
        }
        else
        {
            return super.equals(obj);
        }
    }

}
