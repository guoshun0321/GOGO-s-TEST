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

    public static final int getProperties(Properties prop, String key, int defaultValue, boolean acceptNull)
    {
        int retval = defaultValue;

        boolean catched = false;
        String temp = prop.getProperty(key);
        if (temp != null)
        {
            try
            {
                retval = Integer.valueOf(temp);
                catched = true;
            }
            catch (Exception ex)
            {
                // ignore
            }
        }
        if (!catched && !acceptNull)
        {
            if (temp == null)
            {
                throw new NullPointerException("找不到对应的值，键名称：" + key);
            }
            else
            {
                throw new NullPointerException(String.format("值无法转换为int，key:%s, value:%s", key, temp));
            }
        }
        return retval;
    }

    public static final long getProperties(Properties prop, String key, long defaultValue, boolean acceptNull)
    {
        long retval = defaultValue;

        boolean catched = false;
        String temp = prop.getProperty(key);
        if (temp != null)
        {
            try
            {
                retval = Long.valueOf(temp);
                catched = true;
            }
            catch (Exception ex)
            {
                // ignore
            }
        }
        if (!catched && !acceptNull)
        {
            if (temp == null)
            {
                throw new NullPointerException("找不到对应的值，键名称：" + key);
            }
            else
            {
                throw new NullPointerException(String.format("值无法转换为long，key:%s, value:%s", key, temp));
            }
        }
        return retval;
    }

    public static final boolean getPropertiesBoolean(Properties prop, String key, boolean defaultValue)
    {
        boolean retval = defaultValue;
        String temp = prop.getProperty(key);
        if (temp != null && temp.equalsIgnoreCase("true"))
        {
            retval = true;
        }
        return retval;
    }

}
