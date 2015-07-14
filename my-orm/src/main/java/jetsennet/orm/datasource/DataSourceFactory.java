package jetsennet.orm.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.util.UncheckedOrmException;

public abstract class DataSourceFactory
{

    public static final String _POOL_TYPE_C3P0 = "c3p0";
    public static final String _POOL_TYPE_BONECP = "BoneCP";
    public static final String _POOL_TYPE_DBCP = "dbcp";
    public static final String _POOL_TYPE_JDBC_POOL = "jdbc-pool";
    public static final String _POOL_TYPE_DRUID = "druid";
    /**
     * 日志
     */
    public static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    public static DataSource getDataSource(Configuration config)
    {
        DataSource retval = null;
        try
        {
            String type = config.poolInfo.poolType;
            if (_POOL_TYPE_DBCP.equals(type))
            {
                retval = new DbcpDataSourceCreator().createDatasource(config.connInfo, config.poolInfo);
                if (config.isDebug)
                {
                    // 调试文本，打印信息，用于判断连接池状态。
                    BasicDataSource bds = (BasicDataSource) retval;
                    logger.debug(String.format("active_num : %s; idle_num : %s", bds.getNumActive(), bds.getNumIdle()));
                }
            }
            else if (_POOL_TYPE_C3P0.equals(type))
            {
                retval = new C3p0DataSourceCreator().createDatasource(config.connInfo, config.poolInfo);
            }
            else
            {
                throw new UncheckedOrmException("暂不支持连接池:" + type);
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

}
