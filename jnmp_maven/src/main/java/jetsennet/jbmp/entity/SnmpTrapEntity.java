package jetsennet.jbmp.entity;

import java.util.Date;

/**
 * SNMP TRAP
 */
public class SnmpTrapEntity
{

    /**
     * ID
     */
    private int trapId;
    /**
     * Trap类型。缺省为0。
     */
    private int TrapType;
    /**
     * Trap版本。
     */
    private int trapVer;
    /**
     * Trap MIB OID用于第一版的trap
     */
    private String trapOid;
    /**
     * trap值，用于第二版的trap
     */
    private String specificTrap;
    /**
     * 企业值
     */
    private String enterprise;
    /**
     * Trap名称
     */
    private String trapName;
    /**
     * snmp版本
     */
    private int snmpVersion;
    /**
     * trap的英文描述
     */
    private String trapInfo;
    /**
     * trap的中文描述
     */
    private String trapDesc;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    public static final int TRAP_VERSION_V1 = 0;
    public static final int TRAP_VERSION_V2C = 1;

    /**
     * 构造函数
     */
    public SnmpTrapEntity()
    {
    }

    /**
     * @return the trapId
     */
    public int getTrapId()
    {
        return trapId;
    }

    /**
     * @param trapId the trapId to set
     */
    public void setTrapId(int trapId)
    {
        this.trapId = trapId;
    }

    /**
     * @return the TrapType
     */
    public int getTrapType()
    {
        return TrapType;
    }

    /**
     * @param TrapType the TrapType to set
     */
    public void setTrapType(int TrapType)
    {
        this.TrapType = TrapType;
    }

    /**
     * @return the trapVer
     */
    public int getTrapVer()
    {
        return trapVer;
    }

    /**
     * @param trapVer the trapVer to set
     */
    public void setTrapVer(int trapVer)
    {
        this.trapVer = trapVer;
    }

    /**
     * @return the trapOid
     */
    public String getTrapOid()
    {
        return trapOid;
    }

    /**
     * @param trapOid the trapOid to set
     */
    public void setTrapOid(String trapOid)
    {
        this.trapOid = trapOid;
    }

    /**
     * @return the specificTrap
     */
    public String getSpecificTrap()
    {
        return specificTrap;
    }

    /**
     * @param specificTrap the specificTrap to set
     */
    public void setSpecificTrap(String specificTrap)
    {
        this.specificTrap = specificTrap;
    }

    /**
     * @return the enterprise
     */
    public String getEnterprise()
    {
        return enterprise;
    }

    /**
     * @param enterprise the enterprise to set
     */
    public void setEnterprise(String enterprise)
    {
        this.enterprise = enterprise;
    }

    /**
     * @return the trapName
     */
    public String getTrapName()
    {
        return trapName;
    }

    /**
     * @param trapName the trapName to set
     */
    public void setTrapName(String trapName)
    {
        this.trapName = trapName;
    }

    /**
     * @return the snmpVersion
     */
    public int getSnmpVersion()
    {
        return snmpVersion;
    }

    /**
     * @param snmpVersion the snmpVersion to set
     */
    public void setSnmpVersion(int snmpVersion)
    {
        this.snmpVersion = snmpVersion;
    }

    /**
     * @return the trapInfo
     */
    public String getTrapInfo()
    {
        return trapInfo;
    }

    /**
     * @param trapInfo the trapInfo to set
     */
    public void setTrapInfo(String trapInfo)
    {
        this.trapInfo = trapInfo;
    }

    /**
     * @return the trapDesc
     */
    public String getTrapDesc()
    {
        return trapDesc;
    }

    /**
     * @param trapDesc the trapDesc to set
     */
    public void setTrapDesc(String trapDesc)
    {
        this.trapDesc = trapDesc;
    }

    /**
     * @return the createUser
     */
    public String getCreateUser()
    {
        return createUser;
    }

    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
