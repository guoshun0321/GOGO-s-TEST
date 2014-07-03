/************************************************************************
日 期: 2012-3-26
作 者: 郭祥
版 本: v1.3
描 述: 配置文件解析
历 史:
 ************************************************************************/
package jetsennet.jbmp.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 配置文件解析
 * @author 郭祥
 */
public final class PropertiesFileUtil
{

    private Properties props;
    private static final HashMap<String, PropertiesFileUtil> file2props = new HashMap<String, PropertiesFileUtil>();
    private static final Logger logger = Logger.getLogger(ConfigUtil.class);

    private PropertiesFileUtil(String fileName)
    {
        try
        {
            InputStream input = ConfigUtil.class.getResourceAsStream("/" + fileName);
            props = new Properties();
            props.load(input);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * @param fileName 文件名
     * @return 结果
     */
    public static PropertiesFileUtil get(String fileName)
    {
        PropertiesFileUtil retval = file2props.get(fileName);
        if (retval == null)
        {
            synchronized (file2props)
            {
                retval = file2props.get(fileName);
                if (retval == null)
                {
                    retval = new PropertiesFileUtil(fileName);
                    file2props.put(fileName, retval);
                }
            }
        }
        return retval;
    }

    /**
     * @param name 名称
     * @param def 参数
     * @return 结果
     */
    public String getString(String name, String def)
    {
        if (props == null)
        {
            return def;
        }
        try
        {
            return props.getProperty(name);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return def;
    }

    /**
     * @param name 名称
     * @param def 参数
     * @return 结果
     */
    public int getInteger(String name, int def)
    {
        if (props == null)
        {
            return def;
        }
        String sValue = null;
        Integer result = null;
        try
        {
            sValue = props.getProperty(name);
            if (sValue != null)
            {
                result = Integer.valueOf(sValue);
            }
        }
        catch (NumberFormatException ex)
        {
            logger.error("参数name配置错误：" + name + "=" + sValue, ex);
        }
        return result == null ? def : result;
    }
}
