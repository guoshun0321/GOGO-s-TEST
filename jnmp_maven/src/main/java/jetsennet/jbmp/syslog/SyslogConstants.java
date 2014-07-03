package jetsennet.jbmp.syslog;

import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * @author lianghongjie Syslog常量
 */
public class SyslogConstants
{
    /**
     * Syslog配置文件
     */
    public static final String SYSLOG_CFG_FILE = "syslog";

    /**
     * Syslog接收器配置信息
     */
    public static final String SYSLOG_THREAD_POOL_SIZE_CFG = "server" + XmlCfgUtil.CFG_PRE + "threadPoolSize" + XmlCfgUtil.CFG_POS;
    public static final String SYSLOG_IP_CFG = "server" + XmlCfgUtil.CFG_PRE + "ip" + XmlCfgUtil.CFG_POS;
    public static final String SYSLOG_PORT_CFG = "server" + XmlCfgUtil.CFG_PRE + "port" + XmlCfgUtil.CFG_POS;
    public static final int DEFAULT_THREAD_POOL_SIZE = 3;
    public static final String DEFAULT_IP = "127.0.0.1";
    public static final int DEFAULT_PORT = 514;
}
