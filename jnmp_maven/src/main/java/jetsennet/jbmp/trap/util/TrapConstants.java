package jetsennet.jbmp.trap.util;

import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * @author lianghongjie
 * Trap常量
 */
public class TrapConstants
{
    /**
     * Trap配置文件
     */
    public static final String TRAP_CFG_FILE = "trap";
    
    /**
     * Trap接收器配置信息
     */
    public static final String TRAP_THREAD_POOL_SIZE_CFG = "server" + XmlCfgUtil.CFG_PRE + "threadPoolSize" + XmlCfgUtil.CFG_POS;
    public static final String TRAP_PROTOCOL_CFG = "server" + XmlCfgUtil.CFG_PRE + "protocol" + XmlCfgUtil.CFG_POS;
    public static final String TRAP_IP_CFG = "server" + XmlCfgUtil.CFG_PRE + "ip" + XmlCfgUtil.CFG_POS;
    public static final String TRAP_PORT_CFG = "server" + XmlCfgUtil.CFG_PRE + "port" + XmlCfgUtil.CFG_POS;
    public static final int DEFAULT_THREAD_POOL_SIZE = 3;
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final String DEFAULT_IP = "127.0.0.1";
    public static final int DEFAULT_PORT = 162;

    /**
     * OID常量
     */
    public static final String SNMP_SYSUPTIME_OID = "1.3.6.1.2.1.1.3.0";
    public static final String SNMP_TRAP_OID = "1.3.6.1.6.3.1.1.4.1.0";
    public static final String SNMP_TRAP_ADDRESS_OID = "1.3.6.1.6.3.18.1.3.0";
}
