package jetsennet.jbmp.protocols.linklayer.util;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.protocols.linklayer.AddressForwardTableEntity;
import jetsennet.jbmp.protocols.linklayer.LinkLayerDev;

public class SnmpNetInterface
{

    /**
     * 接口号
     */
    private int interf;
    /**
     * 描述
     */
    private String desc;
    /**
     * 物理地址
     */
    private String mac;
    /**
     * 接口类型
     */
    private int ifType;
    /**
     * IP地址
     */
    private SnmpNetIp ip;
    /**
     * 物理端口号，不一定存在
     */
    private int port;
    /**
     * 下一条，子网，或者路由器
     */
    private Object nextHop;
    /**
     * 接口的地址转发表
     */
    private AddressForwardTableEntity aft;
    /**
     * 对应的对象
     */
    private MObjectEntity mo;
    /**
     * 关联对象
     */
    private List<Object> relDevs;
    /**
     * 父对象
     */
    private LinkLayerDev parent;

    public SnmpNetInterface()
    {
        relDevs = new ArrayList<Object>();
    }

    public SnmpNetInterface(int interf, String desc, String mac, int ifType)
    {
        this();
        this.interf = interf;
        this.desc = desc;
        this.mac = mac;
        this.ifType = ifType;
    }

    /**
     * 添加相关联设备
     * @param relDev
     */
    public void addLink(Object relInterf)
    {
        this.relDevs.add(relInterf);
    }

    /**
     * 移除相关联设备
     * @param rel
     */
    public void removeLink(Object relInterf)
    {
        this.relDevs.remove(relInterf);
    }

    public SnmpNetIp getIp()
    {
        return ip;
    }

    public void setIp(SnmpNetIp ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getMac()
    {
        return mac;
    }

    public void setMac(String mac)
    {
        this.mac = mac;
    }

    public Object getNextHop()
    {
        return nextHop;
    }

    public void setNextHop(Object nextHop)
    {
        this.nextHop = nextHop;
    }

    public int getInterf()
    {
        return interf;
    }

    public void setInterf(int interf)
    {
        this.interf = interf;
    }

    public int getIfType()
    {
        return ifType;
    }

    public void setIfType(int ifType)
    {
        this.ifType = ifType;
    }

    public AddressForwardTableEntity getAft()
    {
        return aft;
    }

    public void setAft(AddressForwardTableEntity aft)
    {
        this.aft = aft;
    }

    public MObjectEntity getMo()
    {
        return mo;
    }

    public void setMo(MObjectEntity mo)
    {
        this.mo = mo;
    }

    public List<Object> getRelDevs()
    {
        return relDevs;
    }

    public void setRelDevs(List<Object> relDevs)
    {
        this.relDevs = relDevs;
    }

    public LinkLayerDev getParent()
    {
        return parent;
    }

    public void setParent(LinkLayerDev parent)
    {
        this.parent = parent;
    }

}
