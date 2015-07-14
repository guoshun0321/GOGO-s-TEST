/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 监控对象实体类
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author 郭祥
 */
@Table(name = "BMP_OBJECT")
public class MObjectEntity implements Serializable
{

    /**
     * 对象名称
     */
    @Id
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 父对象ID，无父对象为0
     */
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 父对象
     */
    private MObjectEntity parent;
    /**
     * 对象类型
     */
    @Column(name = "CLASS_ID")
    private int classId;
    /**
     * 对象类型，冗余
     */
    @Column(name = "CLASS_TYPE")
    private String classType;
    /**
     * 分类
     */
    @Column(name = "CLASS_GROUP")
    private int classGroup;
    /**
     * 对象名称
     */
    @Column(name = "OBJ_NAME")
    private String objName;
    /**
     * 对象状态
     */
    @Column(name = "OBJ_STATE")
    private int objState;
    /**
     * 采集状态。正常、采集失败、未知（父对象采集失败时，子对象的状态）
     */
    @Column(name = "RECEIVE_ENABLE")
    private int collState;
    /**
     * IP地址
     */
    @Column(name = "IP_ADDR")
    private String ipAddr;
    /**
     * IP端口
     */
    @Column(name = "IP_PORT")
    private int ipPort;
    /**
     * 用户名，对于SNMP而言，存community
     */
    @Column(name = "USER_NAME")
    private String userName;
    /**
     * 密码
     */
    @Column(name = "USER_PWD")
    private String userPwd;
    /**
     * 对象参数，备用
     */
    @Column(name = "OBJ_PARAM")
    private String objParam;
    /**
     * 对象参数，备用
     */
    @Column(name = "MAN_ID")
    private int manId;
    /**
     * 对象描述
     */
    @Column(name = "OBJ_DESC")
    private String objDesc;
    /**
     * 版本
     */
    @Column(name = "VERSION")
    private String version;
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
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    /**
     * 对象标识
     */
    @Column(name = "OBJ_IDENT")
    private String objIdent;
    /**
     * 属性集合
     */
    private transient ArrayList<ObjAttribEntity> attrs;
    // 自定义字段
    @Column(name = "NUM_VAL1")
    private int numVal1;
    /**
     * 用于存储设备的链路层设备类型（用于链路层自动发现），链路层设备类型包括：路由器、交换机、主机（包括其他设备）、未知
     */
    @Column(name = "NUM_VAL2")
    private int numVal2;
    /**
     * 对于接口设备，存储接口的编号
     */
    @Column(name = "NUM_VAL3")
    private int numVal3;
    @Column(name = "NUM_VAL4")
    private int numVal4;
    @Column(name = "NUM_VAL5")
    private int numVal5;
    /**
     * 该字段存数据库名称
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 对子对象，填子对象附加名称；对SNMP设备，该字段用于存放设备的MAC地址
     */
    @Column(name = "FIELD_2")
    private String field2;
    @Column(name = "FIELD_3")
    private String field3;
    @Column(name = "FIELD_4")
    private String field4;
    @Column(name = "FIELD_5")
    private String field5;
    @Column(name = "FIELD_6")
    private String field6;
    @Column(name = "FIELD_7")
    private String field7;
    @Column(name = "FIELD_8")
    private String field8;
    @Column(name = "FIELD_9")
    private String field9;
    @Column(name = "FIELD_10")
    private String field10;
    @Column(name = "FIELD_11")
    private String field11;
    /**
     * 点播节目：表示版权开始时间，单位秒
     */
    @Column(name = "LONG_VAL1")
    private long longVal1;
    /**
     * 点播节目：表示版权结束时间，单位秒
     */
    @Column(name = "LONG_VAL2")
    private long longVal2;

    /**
     * 维护中设备
     */
    public static final int OBJ_STATE_MAINTAIN = 1;
    /**
     * 可管理设备
     */
    public static final int OBJ_STATE_MANAGEABLE = 0;
    /**
     * PARENT_ID字段默认值，无父对象
     */
    public static final int PARENT_ID_DEFAULT = 0;
    /**
     * CLASS_GROUP 设备
     */
    public static final int CLASS_GROUP_DEV = 1;
    /**
     * CLASS_GROUP 码流
     */
    public static final int CLASS_GROUP_TS = 10;
    /**
     * CLASS_GROUP 节目
     */
    public static final int CLASS_GROUP_PGM = 20;
    /**
     * SNMPv1
     */
    public static String VERSION_SNMP_V1 = "snmpv1";
    /**
     * SNMPv2c
     */
    public static String VERSION_SNMP_V2C = "snmpv2c";
    /**
     * 路由器
     */
    public static final int LINKLAYER_TYPE_ROUTER = 1;
    /**
     * 交换机
     */
    public static final int LINKLAYER_TYPE_SWITCH = 2;
    /**
     * 除路由器和交换机外，其他支持SNMP的设备
     */
    public static final int LINKLAYER_TYPE_HOST = 3;
    /**
     * 不支持SNMP的设备
     */
    public static final int LINKLAYER_TYPE_UNKNOWN = 4;
    /**
     * 采集状态，正常
     */
    public static final int COLL_STATE_OK = 0;
    /**
     * 采集状态，采集失败
     */
    public static final int COLL_STATE_FAILED = 1;
    /**
     * 采集状态，未知
     */
    public static final int COLL_STATE_UNKNOWN1 = 2;
    /**
     * 采集状态，不存在
     */
    public static final int COLL_STATE_UNKNOWN2 = -1;

    /**
     * 监测状态，未监测
     */
    public static final int MONITOR_STATE_NO = 0;
    /**
     * 监测状态，一对一监测
     */
    public static final int MONITOR_STATE_ONE_TO_ONE = 1;
    /**
     * 监测状态，轮巡监测
     */
    public static final int MONITOR_STATE_POLL = 2;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    /**
     * 构造函数
     * @param mo 对象
     */
    public void copyToSub(MObjectEntity mo)
    {
        mo.setObjId(this.getObjId());
        mo.setParentId(this.getParentId());
        mo.setClassId(this.getClassId());
        mo.setClassType(this.getClassType());
        mo.setObjName(this.getObjName());
        mo.setObjState(this.getObjState());
        mo.setIpAddr(this.getIpAddr());
        mo.setIpPort(this.getIpPort());
        mo.setUserName(this.getUserName());
        mo.setUserPwd(this.getUserPwd());
        mo.setObjParam(this.getObjParam());
        mo.setManId(this.manId);
        mo.setObjDesc(this.getObjDesc());
        mo.setVersion(this.getVersion());
        mo.setCreateUser(this.getCreateUser());
        mo.setCreateTime(this.getCreateTime());
        mo.setField1(this.getField1());
    }

    /**
     * 复制该对象
     * @return 结果
     */
    public MObjectEntity copy()
    {
        MObjectEntity mo = new MObjectEntity();
        mo.setObjId(this.getObjId());
        mo.setParentId(this.getParentId());
        mo.setClassId(this.getClassId());
        mo.setClassType(this.getClassType());
        mo.setObjName(this.getObjName());
        mo.setObjState(this.getObjState());
        mo.setIpAddr(this.getIpAddr());
        mo.setIpPort(this.getIpPort());
        mo.setUserName(this.getUserName());
        mo.setUserPwd(this.getUserPwd());
        mo.setObjParam(this.getObjParam());
        mo.setManId(this.manId);
        mo.setObjDesc(this.getObjDesc());
        mo.setVersion(this.getVersion());
        mo.setCreateUser(this.getCreateUser());
        mo.setCreateTime(this.getCreateTime());
        mo.setField1(this.getField1());
        return mo;
    }

    /**
     * 判断对象是否为子对象
     * @return true,对象为子对象。false，对象不为子对象。
     */
    public boolean isSubObj()
    {
        if (this.parentId != PARENT_ID_DEFAULT)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        String str = "被监控对象：ID:<%s>，对象名称：<%s>，对象类型：<%s>，对象IP：<%s>，对象状态：<%s>";
        str = String.format(str, this.objId, this.objName, this.classId, this.ipAddr, this.objState);
        return str;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    /**
     * @return the parentId
     */
    public int getParentId()
    {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public MObjectEntity getParent()
    {
        return parent;
    }

    public void setParent(MObjectEntity parent)
    {
        this.parent = parent;
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
     * @return the objName
     */
    public String getObjName()
    {
        return objName;
    }

    /**
     * @param objName the objName to set
     */
    public void setObjName(String objName)
    {
        this.objName = objName;
    }

    /**
     * @return the objState
     */
    public int getObjState()
    {
        return objState;
    }

    /**
     * @param objState the objState to set
     */
    public void setObjState(int objState)
    {
        this.objState = objState;
    }

    /**
     * @return the ipAddr
     */
    public String getIpAddr()
    {
        return ipAddr;
    }

    /**
     * @param ipAddr the ipAddr to set
     */
    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
    }

    /**
     * @return the ipPort
     */
    public int getIpPort()
    {
        return ipPort;
    }

    /**
     * @param ipPort the ipPort to set
     */
    public void setIpPort(int ipPort)
    {
        this.ipPort = ipPort;
    }

    /**
     * @return the userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the userPwd
     */
    public String getUserPwd()
    {
        return userPwd;
    }

    /**
     * @param userPwd the userPwd to set
     */
    public void setUserPwd(String userPwd)
    {
        this.userPwd = userPwd;
    }

    /**
     * @return the objParam
     */
    public String getObjParam()
    {
        return objParam;
    }

    public Element getParam() throws DocumentException
    {
        return DocumentHelper.parseText(objParam).getRootElement();
    }

    /**
     * @param objParam the objParam to set
     */
    public void setObjParam(String objParam)
    {
        this.objParam = objParam;
    }

    /**
     * @return the objDesc
     */
    public String getObjDesc()
    {
        return objDesc;
    }

    /**
     * @param objDesc the objDesc to set
     */
    public void setObjDesc(String objDesc)
    {
        this.objDesc = objDesc;
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
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    /**
     * @return the numVal1
     */
    public int getNumVal1()
    {
        return numVal1;
    }

    /**
     * @param numVal1 the numVal1 to set
     */
    public void setNumVal1(int numVal1)
    {
        this.numVal1 = numVal1;
    }

    /**
     * @return the numVal2
     */
    public int getNumVal2()
    {
        return numVal2;
    }

    /**
     * @param numVal2 the numVal2 to set
     */
    public void setNumVal2(int numVal2)
    {
        this.numVal2 = numVal2;
    }

    /**
     * @return the numVal3
     */
    public int getNumVal3()
    {
        return numVal3;
    }

    /**
     * @param numVal3 the numVal3 to set
     */
    public void setNumVal3(int numVal3)
    {
        this.numVal3 = numVal3;
    }

    /**
     * @return the numVal4
     */
    public int getNumVal4()
    {
        return numVal4;
    }

    /**
     * @param numVal4 the numVal4 to set
     */
    public void setNumVal4(int numVal4)
    {
        this.numVal4 = numVal4;
    }

    /**
     * @return the numVal5
     */
    public int getNumVal5()
    {
        return numVal5;
    }

    /**
     * @param numVal5 the numVal5 to set
     */
    public void setNumVal5(int numVal5)
    {
        this.numVal5 = numVal5;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the field2
     */
    public String getField2()
    {
        return field2;
    }

    /**
     * @param field2 the field2 to set
     */
    public void setField2(String field2)
    {
        this.field2 = field2;
    }

    /**
     * @return the field3
     */
    public String getField3()
    {
        return field3;
    }

    /**
     * @param field3 the field3 to set
     */
    public void setField3(String field3)
    {
        this.field3 = field3;
    }

    /**
     * @return the field4
     */
    public String getField4()
    {
        return field4;
    }

    /**
     * @param field4 the field4 to set
     */
    public void setField4(String field4)
    {
        this.field4 = field4;
    }

    /**
     * @return the field5
     */
    public String getField5()
    {
        return field5;
    }

    /**
     * @param field5 the field5 to set
     */
    public void setField5(String field5)
    {
        this.field5 = field5;
    }

    /**
     * @return the field6
     */
    public String getField6()
    {
        return field6;
    }

    /**
     * @param field6 the field6 to set
     */
    public void setField6(String field6)
    {
        this.field6 = field6;
    }

    /**
     * @return the field7
     */
    public String getField7()
    {
        return field7;
    }

    /**
     * @param field7 the field7 to set
     */
    public void setField7(String field7)
    {
        this.field7 = field7;
    }

    /**
     * @return the field8
     */
    public String getField8()
    {
        return field8;
    }

    /**
     * @param field8 the field8 to set
     */
    public void setField8(String field8)
    {
        this.field8 = field8;
    }

    /**
     * @return the field9
     */
    public String getField9()
    {
        return field9;
    }

    /**
     * @param field9 the field9 to set
     */
    public void setField9(String field9)
    {
        this.field9 = field9;
    }

    /**
     * @return the field10
     */
    public String getField10()
    {
        return field10;
    }

    /**
     * @param field10 the field10 to set
     */
    public void setField10(String field10)
    {
        this.field10 = field10;
    }

    /**
     * @return the field11
     */
    public String getField11()
    {
        return field11;
    }

    /**
     * @param field11 the field11 to set
     */
    public void setField11(String field11)
    {
        this.field11 = field11;
    }

    /**
     * @return the attrs
     */
    public ArrayList<ObjAttribEntity> getAttrs()
    {
        return attrs;
    }

    /**
     * @param attrs the attrs to set
     */
    public void setAttrs(ArrayList<ObjAttribEntity> attrs)
    {
        this.attrs = attrs;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
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

    public String getObjIdent()
    {
        return objIdent;
    }

    public void setObjIdent(String objIdent)
    {
        this.objIdent = objIdent;
    }

    public int getCollState()
    {
        return collState;
    }

    public void setCollState(int collState)
    {
        this.collState = collState;
    }

    /**
     * @return the longVal1
     */
    public long getLongVal1()
    {
        return longVal1;
    }

    /**
     * @param longVal1 the longVal1 to set
     */
    public void setLongVal1(long longVal1)
    {
        this.longVal1 = longVal1;
    }

    /**
     * @return the longVal2
     */
    public long getLongVal2()
    {
        return longVal2;
    }

    /**
     * @param longVal2 the longVal2 to set
     */
    public void setLongVal2(long longVal2)
    {
        this.longVal2 = longVal2;
    }
}
