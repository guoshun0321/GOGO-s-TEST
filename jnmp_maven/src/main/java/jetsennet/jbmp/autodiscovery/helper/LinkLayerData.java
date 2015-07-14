package jetsennet.jbmp.autodiscovery.helper;

import java.io.Serializable;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.mib.node.SnmpTable;

public class LinkLayerData implements Serializable
{

    /**
     * 对象
     */
    private MObjectEntity mo;
    /**
     * MAC地址
     */
    private String mac;
    /**
     * 用于判断设备的类型
     */
    private String sysServices;
    /**
     * 用于判断设备的类型
     */
    private String ipForwarding;
    /**
     * 用于判断设备的类型
     */
    private String dot1dBaseNumPorts;
    /**
     * 链路层类型
     */
    private int linkType;
    /**
     * 地址转发数据
     */
    private SnmpTable aftMap;
    /**
     * 接口数据
     */
    private SnmpTable ifMap;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    public MObjectEntity getMo()
    {
        return mo;
    }

    public void setMo(MObjectEntity mo)
    {
        this.mo = mo;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public String getSysServices()
    {
        return sysServices;
    }

    public void setSysServices(String sysServices)
    {
        this.sysServices = sysServices;
    }

    public String getIpForwarding()
    {
        return ipForwarding;
    }

    public void setIpForwarding(String ipForwarding)
    {
        this.ipForwarding = ipForwarding;
    }

    public SnmpTable getAftMap()
    {
        return aftMap;
    }

    public void setAftMap(SnmpTable aftMap)
    {
        this.aftMap = aftMap;
    }

    public SnmpTable getIfMap()
    {
        return ifMap;
    }

    public void setIfMap(SnmpTable ifMap)
    {
        this.ifMap = ifMap;
    }

    public String getDot1dBaseNumPorts()
    {
        return dot1dBaseNumPorts;
    }

    public void setDot1dBaseNumPorts(String dot1dBaseNumPorts)
    {
        this.dot1dBaseNumPorts = dot1dBaseNumPorts;
    }

    public int getLinkType()
    {
        return linkType;
    }

    public void setLinkType(int linkType)
    {
        this.linkType = linkType;
    }

}
