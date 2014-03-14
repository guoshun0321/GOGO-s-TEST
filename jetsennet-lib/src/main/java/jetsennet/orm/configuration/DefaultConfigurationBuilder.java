package jetsennet.orm.configuration;

import java.io.InputStream;
import java.util.Properties;

import jetsennet.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigurationBuilder implements ConfigurationBuilder
{

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationBuilder.class);

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

    private static final String POOL_DATASOURCE = "pool.datasource";

    public Configuration genConfiguration(String src)
    {
        Configuration retval = null;
        if (src == null || src.isEmpty())
        {
            src = Configuration.DEFAULT_CONFIG;
        }
        retval = readFromProperties(src);
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

            ConnectionInfo connInfo = parseConnectionInfo(prop);
            DbPoolInfo poolInfo = parseDbPoolInfo(prop);

            retval = new Configuration(connInfo, poolInfo, src);
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
        return new DbPoolInfo(datasource);
    }

}
