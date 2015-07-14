/************************************************************************
日 期: 2012-3-26
作 者: 郭祥
版 本: v1.3
描 述: 连接信息
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols;

import jetsennet.jbmp.util.PropertiesFileUtil;

/**
 * 连接信息
 * @author 郭祥
 */
public class ConnectInfo
{

    // ARP
    public static final int ARP_TIMEOUT;
    public static final int ARP_RETRY;
    // TCP
    public static final int TCP_TIMEOUT;
    public static final int TCP_RETRY;
    // SNMP
    public static final int SNMP_TIMEOUT;
    public static final int SNMP_RETRY;
    // DEFAULT
    public static final int DEFAULT_TIMEOUT = 1500;
    public static final int DEFAULT_RETRY = 3;
    public static final String CONN_INFO_FILE = "conninfo.properties";

    static
    {
        PropertiesFileUtil props = PropertiesFileUtil.get(CONN_INFO_FILE);
        ARP_TIMEOUT = props.getInteger("arp.timeout", DEFAULT_TIMEOUT);
        ARP_RETRY = props.getInteger("arp.retry", DEFAULT_RETRY);
        TCP_TIMEOUT = props.getInteger("tcp.timeout", DEFAULT_TIMEOUT);
        TCP_RETRY = props.getInteger("tcp.retry", DEFAULT_RETRY);
        SNMP_TIMEOUT = props.getInteger("snmp.timeout", DEFAULT_TIMEOUT);
        SNMP_RETRY = props.getInteger("snmp.retry", DEFAULT_RETRY);
    }
}
