package jetsennet.orm.configuration;

import java.util.Map;

public class DbPoolInfo
{

    public final String poolType;

    private final Map<String, String> props;

    public DbPoolInfo(String poolType, Map<String, String> props)
    {
        this.poolType = poolType;
        this.props = props;
    }

    public String get(String key)
    {
        return props.get(key);
    }

}
