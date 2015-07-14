package jetsennet.jbmp.entity;

import java.util.Date;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 将监控属性按照监控对象种类进行分类
 * @author GUO
 */
@Table(name = "BMP_ATTRIBCLASS")
public class AttribClassEntity
{

    /**
     * 属性集ID
     */
    @Id
    @Column(name = "CLASS_ID")
    private int classId;
    /**
     * 分类名称
     */
    @Column(name = "CLASS_NAME")
    private String className;
    /**
     * 分类类型，该字段同时用于区分广电或IT监控
     */
    @Column(name = "CLASS_TYPE")
    private String classType;
    /**
     * 级别
     */
    @Column(name = "CLASS_LEVEL")
    private int classLevel;
    /**
     * 属性集组类型
     */
    @Column(name = "CLASS_GROUP")
    private int classGroup;
    /**
     * mib类型，引用BMP_SNMPNODES表格
     */
    @Column(name = "MIB_ID")
    private int mibId;
    /**
     * 图标
     */
    @Column(name = "ICON_SRC")
    private String iconSrc;
    /**
     * 厂商id
     */
    @Column(name = "MAN_ID")
    private int manId;
    /**
     * 分类描述
     */
    @Column(name = "CLASS_DESC")
    private String classDesc;
    /**
     * 排序号
     */
    @Column(name = "VIEW_POS")
    private int viewPos;
    /**
     * 创建用户
     */
    @Column(name = "CREATE_USER")
    private String createUser;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;
    /**
     * 保留字段1
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 监控分类的属性
     */
    private List<AttributeEntity> attrs;
    // <editor-fold defaultstate="collapsed" desc="CLASS_LEVEL">
    // <editor-fold defaultstate="collapsed" desc="可新建对象的分类">
    /**
     * 监控对象分类
     */
    public static final int CLASS_LEVEL_CAT = 0;
    /**
     * 监控对象类型。该分类对应一个监控对象类型，如Windows主机
     */
    public static final int CLASS_LEVEL_OBJ = 1;
    /**
     * 监控对象子类型。该分类对应一个监控子对象，如板卡。
     */
    public static final int CLASS_LEVEL_SUB = 2;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="不可新建对象的分类">
    /**
     * 缺省。该类属性不能被子类继承，实例化之后能够让用户输入属性值。
     */
    public static final int CLASS_LEVEL_CUSTOM = 100;
    /**
     * 配置信息类型。该分类的属性只在实例化时采集一次数据。后续可以通过刷新重新采。
     */
    public static final int CLASS_LEVEL_CONFIG = 101;
    /**
     * 监测指标类型。该分类的属性周期地采集数据并检测，但数据不保存。
     */
    public static final int CLASS_LEVEL_MONITOR = 102;
    /**
     * 性能指标类型。该分类的属性周期地采集数据并检测，且数据保存。
     */
    public static final int CLASS_LEVEL_PERF = 103;
    /**
     * Trap
     */
    public static final int CLASS_LEVEL_TRAP = 104;
    /**
     * 信号属性
     */
    public static final int CLASS_LEVEL_SIGNAL = 105;
    /**
     * 表格
     */
    public static final int CLASS_LEVEL_TABLE = 106;
    /**
     * Syslog
     */
    public static final int CLASS_LEVEL_SYSLOG = 107;
    /**
     * 其他
     */
    public static final int CLASS_LEVEL_OTHER = 999;
    /**
     * 通用类型
     */
    public static final int COLL_TYPE_COMMEN = 0;
    // </editor-fold>
    // </editor-fold>
    /**
     * SNMP
     */
    public static final String ATTRIBCLASS_SNMP = "SNMP";
    /**
     * SNMP主机
     */
    public static final String ATTRIBCLASS_SNMP_HOST = "SNMP_HOST";
    /**
     * SNMP进程
     */
    public static final String ATTRIBCLASS_SNMP_HOST_PROCESS = "SNMP_HOST_PROCESS";
    /**
     * SNMP网络设备
     */
    public static final String ATTRIBCLASS_SNMP_NETDEV = "SNMP_NETDEV";
    /**
     * SNMP_CATV
     */
    public static final String ATTRIBCLASS_SNMP_CATV = "SNMP_CATV";
    /**
     * CATV板卡类型
     */
    public static final String ATTRIBCLASS_SNMP_CATV_CARDTYPE = "SNMP_CATV_CARDTYPE";
    /**
     * CATV板卡
     */
    public static final String ATTRIBCLASS_SNMP_CATV_CARD = "SNMP_CATV_CARD";
    /**
     * 应用服务器
     */
    public static final String ATTRIBCLASS_APP = "APP";
    /**
     * 应用服务器，TOMCAT
     */
    public static final String ATTRIBCLASS_APP_TOMCAT = "APP_TOMCAT";
    /**
     * 应用服务器，WEBLOGIC
     */
    public static final String ATTRIBCLASS_APP_WEBLOGIC = "APP_WEBLOGIC";
    /**
     * 应用服务器，WEBSPHERE
     */
    public static final String ATTRIBCLASS_APP_WEBSPHERE = "APP_WEBSPHERE";
    /**
     * 应用服务器，WEBSPHERE
     */
    public static final String ATTRIBCLASS_APP_JBOSS = "APP_JBOSS";
    /**
     * WEB服务器
     */
    public static final String ATTRIBCLASS_WEB = "WEB";
    /**
     * WEB服务器，IIS
     */
    public static final String ATTRIBCLASS_WEB_IIS = "WEB_IIS";
    /**
     * WEB服务器，APACHE
     */
    public static final String ATTRIBCLASS_WEB_APACHE = "WEB_APACHE";
    /**
     * 数据库
     */
    public static final String ATTRIBCLASS_DB = "DB";
    /**
     * 数据库，ORACLE
     */
    public static final String ATTRIBCLASS_DB_ORACLE = "DB_ORACLE";
    /**
     * 数据库，DB2
     */
    public static final String ATTRIBCLASS_DB_DB2 = "DB_DB2";
    /**
     * 数据库，SQLSERVER
     */
    public static final String ATTRIBCLASS_DB_SQLSERVER = "DB_SQLSERVER";
    /**
     * 数据库，SYBASE
     */
    public static final String ATTRIBCLASS_DB_SYBASE = "DB_SYBASE";
    // 常用的默认端口
    public static final int PORT_ORACLE = 1521;
    public static final int PORT_DB2 = 50000;
    public static final int PORT_SQLSERVER = 1433;
    public static final int PORT_WEBLOGIC = 7001;
    public static final int PORT_WEBSPHERE = 9080;
    public static final int PORT_TOMCAT = 8080;
    public static final int PORT_IIS = 80;
    public static final int PORT_SNMP = 161;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.className);
        sb.append("<");
        sb.append(this.classId);
        sb.append(">");
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the className
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the classType
     */
    public String getClassType()
    {
        return classType;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(String classType)
    {
        this.classType = classType;
    }

    /**
     * @return the classLevel
     */
    public int getClassLevel()
    {
        return classLevel;
    }

    /**
     * @param classLevel the classLevel to set
     */
    public void setClassLevel(int classLevel)
    {
        this.classLevel = classLevel;
    }

    /**
     * @return the manId
     */
    public int getManId()
    {
        return manId;
    }

    /**
     * @param manId the manId to set
     */
    public void setManId(int manId)
    {
        this.manId = manId;
    }

    /**
     * @return the classDesc
     */
    public String getClassDesc()
    {
        return classDesc;
    }

    /**
     * @param classDesc the classDesc to set
     */
    public void setClassDesc(String classDesc)
    {
        this.classDesc = classDesc;
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

    /**
     * @return the attrs
     */
    public List<AttributeEntity> getAttrs()
    {
        return attrs;
    }

    /**
     * @param attrs the attrs to set
     */
    public void setAttrs(List<AttributeEntity> attrs)
    {
        this.attrs = attrs;
    }

    /**
     * @return the classId
     */
    public int getClassId()
    {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public String getIconSrc()
    {
        return iconSrc;
    }

    public void setIconSrc(String iconSrc)
    {
        this.iconSrc = iconSrc;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the classGroup
     */
    public int getClassGroup()
    {
        return classGroup;
    }

    /**
     * @param classGroup the classGroup to set
     */
    public void setClassGroup(int classGroup)
    {
        this.classGroup = classGroup;
    }

    // </editor-fold>.

    public int getMibId()
    {
        return mibId;
    }

    public void setMibId(int mibId)
    {
        this.mibId = mibId;
    }

    public int getViewPos()
    {
        return viewPos;
    }

    /**
     * @param viewPos the viewPos to set
     */
    public void setViewPos(int viewPos)
    {
        this.viewPos = viewPos;
    }
}
