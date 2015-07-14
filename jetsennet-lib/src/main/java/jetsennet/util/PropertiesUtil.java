package jetsennet.util;

import java.util.Properties;

public class PropertiesUtil
{

    public static final String getProperties(Properties prop, String key, String defaultValue, boolean acceptNull)
    {
        String retval = prop.getProperty(key);
        if (retval != null)
        {
            return retval;
        }
        else
        {
            if (acceptNull)
            {
                return defaultValue;
            }
            else
            {
                throw new NullPointerException("找不到对应的值，键名称：" + key);
            }
        }
    }

}
