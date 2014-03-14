package jetsennet.orm.configuration;

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
     * 默认配置文件
     */
    public static final String DEFAULT_CONFIG = "/dbconfig.properties";

    public Configuration(ConnectionInfo connInfo, DbPoolInfo poolInfo, String configFile)
    {
        this.connInfo = connInfo;
        this.configFile = configFile;
        this.poolInfo = poolInfo;
    }

}
