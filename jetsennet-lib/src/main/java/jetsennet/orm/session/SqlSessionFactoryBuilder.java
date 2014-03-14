package jetsennet.orm.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilder;
import jetsennet.orm.configuration.DefaultConfigurationBuilder;

public class SqlSessionFactoryBuilder
{

    /**
     * SessionFacotry缓存
     */
    private static ConcurrentMap<String, SqlSessionFactory> factoryMap = new ConcurrentHashMap<String, SqlSessionFactory>();

    /**
     * 获取SessionFacotry
     * 
     * @param src 配置文件路径
     * @return
     */
    public static SqlSessionFactory builder(String src)
    {
        if (src == null)
        {
            src = Configuration.DEFAULT_CONFIG;
        }
        SqlSessionFactory retval = factoryMap.get(src);

        if (retval == null)
        {
            // 解析配置文件
            final Configuration config = ensureConfigurationBuilder(src).genConfiguration(src);
            retval = new SqlSessionFactory(config);

            if (retval != null)
            {
                SqlSessionFactory temp = factoryMap.putIfAbsent(src, retval);
                if (temp != null)
                {
                    retval = temp;
                }
            }
        }
        return retval;
    }

    /**
     * 创建SessionFacotry
     * 
     * @param src 配置文件路径
     * @return
     */
    public static SqlSessionFactory builder()
    {
        return builder(null);
    }

    /**
     * 确定使用的配置文件解析器，作为以后的扩展点
     * 
     * @param src
     * @return
     */
    private static ConfigurationBuilder ensureConfigurationBuilder(String src)
    {
        return new DefaultConfigurationBuilder();
    }
}
