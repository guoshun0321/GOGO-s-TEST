package jetsennet.orm.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.IConfigurationBuilder;
import jetsennet.orm.configuration.ConfigurationBuilderProp;

public class SqlSessionFactoryBuilder
{

    /**
     * SessionFacotry缓存
     */
    private static ConcurrentMap<Configuration, SqlSessionFactory> factoryMap = new ConcurrentHashMap<Configuration, SqlSessionFactory>();
    
    /**
     * 获取SessionFacotry
     * 
     * @param src 配置文件路径
     * @return
     */
    public static SqlSessionFactory builder(Configuration config)
    {
        SqlSessionFactory retval = factoryMap.get(config);

        if (retval == null)
        {
            retval = new SqlSessionFactory(config);
            SqlSessionFactory temp = factoryMap.putIfAbsent(config, retval);
            if (temp != null)
            {
                retval = temp;
            }
        }
        return retval;
    }

    /**
     * 获取SessionFacotry
     * 
     * @param src 配置文件路径
     * @return
     */
    public static SqlSessionFactory builder(IConfigurationBuilder builder)
    {
        Configuration config = builder.genConfiguration();
        return builder(config);
    }

    /**
     * 创建SessionFacotry
     * 
     * @return
     */
    public static SqlSessionFactory builder()
    {
        return builder(new ConfigurationBuilderProp(null));
    }
    
    /**
     * 创建SessionFacotry
     * 
     * @param file 配置文件路径
     * @return
     */
    public static SqlSessionFactory builder(String file)
    {
        return builder(new ConfigurationBuilderProp(file));
    }
}
