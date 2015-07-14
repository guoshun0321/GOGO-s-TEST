/************************************************************************
日 期：2011-11-30
作 者: 郭祥
版 本：v1.3
描 述: 监控对象组实体类
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 监控对象组实体类
 * @author 郭祥
 */
@Table(name = "BMP_OBJGROUP")
public class ObjGroupEntity
{

    /**
     * ID
     */
    @Id
    @Column(name = "GROUP_ID")
    private int groupId;
    /**
     * 组，类型。0：缺省，一般组， 1：系统，对应于一个实际系统 2：设备，对应于一个实际设备 3：采集 4：网段 5：拓扑 100：自动创建
     */
    @Column(name = "GROUP_TYPE")
    private int groupType;
    /**
     * 组编码
     */
    @Column(name = "GROUP_CODE")
    private String groupCode;
    /**
     * 组名称
     */
    @Column(name = "GROUP_NAME")
    private String groupName;
    /**
     * 组描述
     */
    @Column(name = "GROUP_DESC")
    private String groupDesc;
    /**
     * 状态
     */
    @Column(name = "GROUP_STATE")
    private int groupState;
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
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 参数
     */
    @Column(name = "GROUP_PARAM")
    private String groupParam;

    // 以下为自定义字段
    @Column(name = "NUM_VAL1")
    private int numVal1;
    @Column(name = "NUM_VAL2")
    private int numVal2;
    @Column(name = "NUM_VAL3")
    private int numVal3;
    @Column(name = "NUM_VAL4")
    private int numVal4;
    @Column(name = "NUM_VAL5")
    private int numVal5;
    @Column(name = "FIELD_1")
    private String field1;
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
    /**
     * 组类型，默认
     */
    public static final int GROUP_TYPE_DEFAULT = 0;
    /**
     * 组类型，系统组
     */
    public static final int GROUP_TYPE_SYSTEM = 1;
    /**
     * 组类型，设备组
     */
    public static final int GROUP_TYPE_DEVICE = 2;
    /**
     * 组类型，采集
     */
    public static final int GROUP_TYPE_COLLECT = 3;
    /**
     * 组类型，网段
     */
    public static final int GROUP_TYPE_IPSEGMENT = 4;
    /**
     * 组类型，拓扑
     */
    public static final int GROUP_TYPE_TOPO = 5;
    /**
     * 组类型，信号
     */
    public static final int GROUP_TYPE_SIGNAL = 6;
    /**
     * 组类型，自动创建
     */
    public static final int GROUP_TYPE_AUTO = 100;
    /**
     * 默认状态
     */
    public static final int GROUP_STATE_DEFAULT = 0;

    /**
     * @return the groupId
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    /**
     * @return the groupType
     */
    public int getGroupType()
    {
        return groupType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(int groupType)
    {
        this.groupType = groupType;
    }

    /**
     * @return the groupName
     */
    public String getGroupName()
    {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    /**
     * @return the groupDesc
     */
    public String getGroupDesc()
    {
        return groupDesc;
    }

    /**
     * @param groupDesc the groupDesc to set
     */
    public void setGroupDesc(String groupDesc)
    {
        this.groupDesc = groupDesc;
    }

    /**
     * @return the groupState
     */
    public int getGroupState()
    {
        return groupState;
    }

    /**
     * @param groupState the groupState to set
     */
    public void setGroupState(int groupState)
    {
        this.groupState = groupState;
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
     * @return the groupCode
     */
    public String getGroupCode()
    {
        return groupCode;
    }

    /**
     * @param groupCode the groupCode to set
     */
    public void setGroupCode(String groupCode)
    {
        this.groupCode = groupCode;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getGroupParam()
    {
        return groupParam;
    }

    public void setGroupParam(String groupParam)
    {
        this.groupParam = groupParam;
    }

}
