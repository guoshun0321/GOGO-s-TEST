package jetsennet.orm.configuration;

import java.util.HashMap;

public class ConfigurationBuilder implements IConfigurationBuilder
{

    private final ConnectionInfo connInfo;

    public ConfigurationBuilder(String driver, String url, String user, String pwd)
    {
        this.connInfo = new ConnectionInfo(driver, url, user, pwd);
    }

    @Override
    public Configuration genConfiguration()
    {
        DbPoolInfo poolInfo = new DbPoolInfo("dbcp", new HashMap<String, String>());
        return new Configuration(connInfo, poolInfo, null);
    }

    @Override
    public int hashCode()
    {
        if (this.connInfo != null)
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
        if (connInfo != null && obj != null && obj instanceof ConfigurationBuilder)
        {
            ConfigurationBuilder temp = (ConfigurationBuilder) obj;
            return this.connInfo.equals(temp.connInfo);
        }
        else
        {
            return super.equals(obj);
        }
    }
}
