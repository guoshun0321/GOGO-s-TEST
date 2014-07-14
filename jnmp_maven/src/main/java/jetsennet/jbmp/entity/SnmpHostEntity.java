package jetsennet.jbmp.entity;

import org.snmp4j.mp.SnmpConstants;

/**
 * SNMP对象。 默认的端口号为161，community为public，版本号为v2c。
 * @author GUO
 */
public class SnmpHostEntity extends MObjectEntity
{

    /**
     * 共同体
     */
    private String community;
    /**
     * SNMP版本
     */
    private int iVersion;

    /**
     * 构造函数
     */
    public SnmpHostEntity()
    {
        super();
        this.setIpPort(161);
        this.community = "public";
        this.iVersion = SnmpConstants.version2c;
    }

    /**
     * 构造函数
     * @param ip_addr ip地址
     * @param ip_port 端口
     * @param user_name 用户名
     * @param user_pwd 密码
     * @param trap_enable 表名
     * @param community 参数
     */
    public SnmpHostEntity(String ip_addr, int ip_port, String user_name, String user_pwd, int trap_enable, String community)
    {
        this();
        this.setIpAddr(ip_addr);
        this.setIpPort(ip_port);
        this.setUserName(user_name);
        this.setUserPwd(user_pwd);
        this.community = community;
    }

    /**
     * 生成该对象的一个进程对象
     * @param name 名
     * @return 对象的一个进程对象
     */
    public SnmpHostEntity genSubProcess(String name)
    {
        SnmpHostEntity shost = new SnmpHostEntity();
        this.copyToSub(shost);
        shost.setObjId(-1);
        shost.setParentId(this.getObjId());
        shost.setParent(this);
        shost.setClassType(AttribClassEntity.ATTRIBCLASS_SNMP_HOST_PROCESS);
        shost.setObjName(name);
        shost.setIpAddr(this.getIpAddr());
        shost.setIpPort(this.getIpPort());
        shost.setUserName(this.getUserName());
        shost.setUserPwd(this.getUserPwd());
        shost.setCommunity(this.getCommunity());
        return shost;
    }

    /**
     * 给该对象的子对象赋值
     * @param mo 对象
     * @return 结果
     */
    public SnmpHostEntity getSub(MObjectEntity mo)
    {
        SnmpHostEntity shost = new SnmpHostEntity();
        mo.copyToSub(shost);
        shost.setIpAddr(this.getIpAddr());
        shost.setIpPort(this.getIpPort());
        shost.setUserName(this.getUserName());
        shost.setUserPwd(this.getUserPwd());
        shost.setCommunity(this.getCommunity());
        return shost;
    }

    @Override
    public String toString()
    {
        return this.getObjName();
    }

    /**
     * @return the community
     */
    public String getCommunity()
    {
        return community;
    }

    /**
     * @param community the community to set
     */
    public void setCommunity(String community)
    {
        this.community = community;
    }

    /**
     * @return the version
     */
    public int getIVersion()
    {
        return iVersion;
    }

    /**
     * @param version the version to set
     */
    public void setIVersion(int version)
    {
        this.iVersion = version;
    }
}
