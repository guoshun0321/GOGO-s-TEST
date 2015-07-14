/************************************************************************
日 期：2012-2-22
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.util;

import java.io.InputStream;

import jetsennet.jbmp.exception.ConfigureException;

/**
 * @author 郭祥
 */
public class ConfigFileUtil
{

    /**
     * 获取配置文件
     * @param fileName 文件名
     * @return 结果
     * @throws ConfigureException 异常
     */
    public static InputStream getConfigFile(String fileName) throws ConfigureException
    {
        InputStream result = null;
        try
        {
            String path = "/" + fileName;
            result = ConfigFileUtil.class.getResourceAsStream(path);
        }
        catch (Exception ex)
        {
            throw new ConfigureException("读取文件" + fileName + "失败", ex);
        }
        return result;
    }

    /**
     * 获取报警配置文件
     * @return 结果
     * @throws ConfigureException 异常
     */
    public static InputStream getAlarmConfigFile() throws ConfigureException
    {
        InputStream result = null;
        try
        {
            // String path = System.getProperty("user.dir");
            // path = path + "/alarm.xml";
            // result = new FileInputStream(path);
            // logger.info("读取配置文件 : " + path);
            result = ConfigFileUtil.class.getResourceAsStream("/alarm.xml");
        }
        catch (Exception ex)
        {
            throw new ConfigureException(ex);
        }
        return result;
    }

    /**
     * 获取实例化配置文件
     * @return 结果
     * @throws ConfigureException 异常
     */
    public static InputStream getInsConfigFile() throws ConfigureException
    {
        InputStream result = null;
        try
        {
            result = ConfigFileUtil.class.getResourceAsStream("/ins.xml");
        }
        catch (Exception ex)
        {
            throw new ConfigureException(ex);
        }
        return result;
    }

    /**
     * 获取自动发现配置文件
     * @return 结果
     * @throws ConfigureException 异常
     */
    public static InputStream getAutoDisConfigFile() throws ConfigureException
    {
        InputStream result = null;
        try
        {
            result = ConfigFileUtil.class.getResourceAsStream("/autodis.xml");
        }
        catch (Exception ex)
        {
            throw new ConfigureException(ex);
        }
        return result;
    }
}
