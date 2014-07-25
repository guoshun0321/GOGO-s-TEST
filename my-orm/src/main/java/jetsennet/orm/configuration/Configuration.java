package jetsennet.orm.configuration;

/**
 * 数据库配置信息
 * 
 * @author 郭祥
 */
public class Configuration
{

    /**
     * 配置文件路径
     */
    public final String configFile;
    /**
     * 数据库连接信息
     */
    public final ConnectionInfo connInfo;
    /**
     * 连接池信息
     */
    public final DbPoolInfo poolInfo;
    /**
     * 数据库类型
     */
    public final DatabaseType dbType;
    /**
     * 是否为调试模式
     */
    public final boolean isDebug;
    /**
     * 默认配置文件
     */
    public static final String DEFAULT_CONFIG = "/dbconfig.properties";

    public Configuration(ConnectionInfo connInfo, DbPoolInfo poolInfo, String configFile, boolean isDebug)
    {
        this.connInfo = connInfo;
        this.configFile = configFile;
        this.poolInfo = poolInfo;
        this.dbType = DatabaseType.ensureDBType(connInfo.driver);
        this.isDebug = isDebug;
    }

    public Configuration(ConnectionInfo connInfo, DbPoolInfo poolInfo, String configFile)
    {
        this(connInfo, poolInfo, configFile, true);
    }

    @Override
    public int hashCode()
    {
        if (connInfo != null)
        {
            return connInfo.hashCode();
        }
        else
        {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (connInfo != null && obj != null && obj instanceof Configuration)
        {
            Configuration temp = (Configuration) obj;
            return connInfo.equals(temp.connInfo);
        }
        else
        {
            return super.equals(obj);
        }
    }

    public boolean isOracle()
    {
        return this.dbType.equals(DatabaseType.ORACLE);
    }

}
