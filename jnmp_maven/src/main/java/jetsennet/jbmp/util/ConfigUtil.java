package jetsennet.jbmp.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author？
 */
public class ConfigUtil
{

    private static Properties props;
    private static final Logger logger = Logger.getLogger(ConfigUtil.class);

    static
    {
        props = new Properties();
        reLoad();
    }

    private static void reLoad()
    {
        try
        {
            // String path = System.getProperty("user.dir");
            // path = path + "\\configuration.properties";
            // FileInputStream input = new FileInputStream(path);
            // logger.info("配置模块：读取配置文件 : " + path);
            InputStream input = ConfigUtil.class.getResourceAsStream("/configuration.properties");
            props.load(input);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    // public static void save() {
    // try {
    // String path = System.getProperty("user.dir");
    // path = path + "\\configuration.properties";
    // FileInputStream input = new FileInputStream(path);
    // FileOutputStream output = new FileOutputStream(path);
    // logger.info("写配置文件 : " + path);
    // props.store(output, null);
    // } catch (Exception ex) {
    // logger.error(ex);
    // }
    // }
    //
    // public static void set(String name, String value) {
    // props.setProperty(name, value);
    // }
    // <editor-fold defaultstate="collapsed" desc="从配置文件中获取信息">
    /**
     * @param name 名称
     * @return 结果
     */
    public static String getString(String name)
    {
        try
        {
            return props.getProperty(name);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            return null;
        }
    }

    /**
     * @param name 名称
     * @param def 参数
     * @return 结果
     */
    public static int getInteger(String name, int def)
    {
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

    /**
     * @param name 名称
     * @param def 参数
     * @return 结果
     */
    public static String getString(String name, String def)
    {
        String retval = null;
        try
        {
            retval = props.getProperty(name);
            retval = retval == null ? def : retval;
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="数据库信息相关">
    public static String getDriver()
    {
        return getString("db.driver");
    }

    public static String getDbUrl()
    {
        return getString("db.url");
    }

    public static String getUser()
    {
        return getString("db.user");
    }

    public static String getPassword()
    {
        return getString("db.pwd");
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="报警设置相关">
    /**
     * 报警轮询间隔
     * @param span 间隔
     * @return 结果
     */
    public static Integer getInterval(int span)
    {
        return getInteger("alarm.interval", span);
    }

    /**
     * 报警线程池大小
     * @param defaultSize 默认值
     * @return 结果
     */
    public static int getAlarmPoolSize(int defaultSize)
    {
        return getInteger("alarm.poolsize", defaultSize);
    }

    /**
     * 报警检查的lookback时间，单位是分钟
     * @param defaultSize 默认时间
     * @return 结果
     */
    public static int getAlarmLookBackTime(int defaultSize)
    {
        return getInteger("alarm.lookback", defaultSize);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="备份相关">
    /**
     * NMP_SNMPDATA_ID表转存时间
     * @param data 单位是天，一般为3-7天
     * @return 结果
     */
    public static int getUnloadingSpan(int data)
    {
        return getInteger("backup.day", data);
    }

    /**
     * NMP_SNMPWEEKDATA表的统计时间
     * @param hour 单位是小时，不超过24小时
     * @return 结果
     */
    public static int getStatisticsSpan(int hour)
    {
        return getInteger("backup.statistics", hour);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PING相关">
    public static String getPingGateWay()
    {
        return getString("ping.gateway");
    }

    /**
     * 本机IP，当本机存在多IP时使用
     * @return 结果
     */
    public static String getPingLocalHost()
    {
        return getString("ping.localhost");
    }

    /**
     * arp扫描的时间间隔
     * @return 结果
     */
    public static int getPingArpTime()
    {
        return getInteger("ping.arptime", 5000);
    }

    /**
     * 超时
     * @param def 参数
     * @return 结果
     */
    public static int getPingTimeout(int def)
    {
        return getInteger("ping.timeout", def);
    }

    /**
     * 最少多长时间检查一次捕获包
     * @param def 参数
     * @return 结果
     */
    public static int getPingMixCheck(int def)
    {
        return getInteger("ping.mixcheck", def);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="邮件信息相关">
    public static String getEmailHost()
    {
        return getString("email.host");
    }

    public static String getEmailUserName()
    {
        return getString("email.username");
    }

    public static String getEmailPassword()
    {
        return getString("email.password");
    }

    public static String getEmailFrom()
    {
        return getString("email.from");
    }

    // </editor-fold>

    public static String getMibDir()
    {
        return getString("unused");
    }

    public static String getReportProName()
    {
        return getString("jnmp_sc.reportProName");
    }

    public static String getReportProIp()
    {
        return getString("jnmp_sc.reportProIp");
    }

    public static String getReportProPort()
    {
        return getString("jnmp_sc.reportProPort");
    }

    public static String getReportWebPath()
    {
        return getString("report.reportWebPath");
    }

    public static String getSobeyServiceUrl()
    {
        return getString("sobey.serviceUrl");
    }

    public static String getSobeyUserName()
    {
        return getString("sobey.sUserName");
    }

    public static String getSobeyUserPassword()
    {
        return getString("sobey.sPassword");
    }

    public static String getSobeyUserIp()
    {
        return getString("sobey.sIp");
    }

    public static String getSobeyTime()
    {
        return getString("sobey.time");
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        System.out.println(getReportProPort());
        System.out.println(getReportWebPath());
    }
}
