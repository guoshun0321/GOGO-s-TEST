package jetsennet.orm.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.configuration.Configuration;

public abstract class DataSourceFactory
{

    /**
     * 日志
     */
    public static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    public abstract DataSource genDataSource(Configuration conf);

    /**
     * 获取DataSource
     * 
     * @param conf
     * @return
     */
    public static DataSource getDataSource(Configuration config)
    {
        DataSource retval = null;
        try
        {

            final BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(config.connInfo.driver);
            ds.setUrl(config.connInfo.url);
            ds.setUsername(config.connInfo.user);
            ds.setPassword(config.connInfo.pwd);

            //            ds.setInitialSize(DBCP_PARAMS[0]);
            //            ds.setMaxActive(DBCP_PARAMS[1]);
            //            ds.setMaxIdle(DBCP_PARAMS[2]);
            //            ds.setMinIdle(DBCP_PARAMS[3]);
            //            ds.setMaxWait(DBCP_PARAMS[4]);
            //            ds.setTestWhileIdle(true);
            //            ds.setTimeBetweenEvictionRunsMillis(DBCP_PARAMS[5]);

            retval = ds;

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }

        // 调试文本，打印信息，用于判断连接池状态。
        //        BasicDataSource bds = (BasicDataSource) retval;
        //        logger.debug(String.format("active_num : %s; idle_num : %s", bds.getNumActive(), bds.getNumIdle()));

        return retval;
    }

}
