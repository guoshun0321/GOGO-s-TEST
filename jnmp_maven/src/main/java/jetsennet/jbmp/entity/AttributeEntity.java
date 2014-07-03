package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.jbmp.util.BMPConstants;

/**
 * 属性
 * @author GUO
 */
@Table(name = "BMP_ATTRIBUTE")
public class AttributeEntity implements Cloneable
{

    /**
     * 属性ID
     */
    @Id
    @Column(name = "ATTRIB_ID")
    private int attribId;
    /**
     * 属性名称
     */
    @Column(name = "ATTRIB_NAME")
    private String attribName;
    /**
     * 对于SNMP存放该属性对应的MIB标识，目前无用
     */
    @Column(name = "ATTRIB_VALUE")
    private String attribValue;
    /**
     * 属性值类别，用于SNMP属性，当值为枚举值时，用于查找相关的枚举值解释
     */
    @Column(name = "VALUE_TYPE")
    private int valueType;
    /**
     * 编码
     */
    @Column(name = "DATA_ENCODING")
    private String dataEncoding;
    /**
     * 属性类别
     */
    @Column(name = "CLASS_TYPE")
    private String classType;
    /**
     * 属性类型
     */
    @Column(name = "ATTRIB_TYPE")
    private int attribType;
    /**
     * 属性采集模式
     */
    @Column(name = "ATTRIB_MODE")
    private int attribMode;
    /**
     * SNMP属性，对应该属性的采集公式
     */
    @Column(name = "ATTRIB_PARAM")
    private String attribParam;
    /**
     * 数据类型。0，未知类型；1，整型；2，字符型；3，日期型；4，浮点型。
     */
    @Column(name = "DATA_TYPE")
    private int dataType;
    /**
     * 数据单位
     */
    @Column(name = "DATA_UNIT")
    private String dataUnit;
    /**
     * 属性编码
     */
    @Column(name = "ATTRIB_CODE")
    private String attribCode;
    /**
     * 描述
     */
    @Column(name = "ATTRIB_DESC")
    private String attribDesc;
    /**
     * 是否可见。0，不可见；1，可见。
     */
    @Column(name = "IS_VISIBLE")
    private int isVisible;
    /**
     * 采集间隔
     */
    @Column(name = "COLL_TIMESPAN")
    private int collTimeSpan;
    /**
     * 显示类型
     */
    @Column(name = "VIEW_TYPE")
    private String viewType;
    /**
     * 报警ID
     */
    @Column(name = "ALARM_ID")
    private int alarmId;
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
     * FIELD_1
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 可用性属性ID
     */
    public static final int VALID_ATTRIB_ID = 40001;
    /**
     * 未知类型
     */
    public static final int DATA_TYPE_UNKNOWN = 0;
    /**
     * 整型
     */
    public static final int DATA_TYPE_INTEGER = 1;
    /**
     * 字符型
     */
    public static final int DATA_TYPT_STRING = 2;
    /**
     * 日期型
     */
    public static final int DATA_TYPE_DATE = 3;
    /**
     * 线状图
     */
    public static final String VIEW_TYPE_LINE = "LINE";
    /**
     * 饼状图
     */
    public static final String VIEW_TYPE_PIE = "PIE";
    /**
     * 列表
     */
    public static final String VIEW_TYPE_LIST = "LIST";
    /**
     * 柱状图
     */
    public static final String VIEW_TYPE_COLUMN = "COLUMN";
    /**
     * 文本
     */
    public static final String VIEW_TYPE_LABEL = "LABEL";

    /**
     * 构造函数
     */
    public AttributeEntity()
    {
        this.dataEncoding = BMPConstants.DEFAULT_SNMP_CODING;
    }

    /**
     * 生成对象属性
     * @return 结果
     */
    public ObjAttribEntity genObjAttrib()
    {
        ObjAttribEntity oa = new ObjAttribEntity();
        oa.setAttribId(this.attribId);
        oa.setAttribValue(this.attribValue);
        oa.setAttribParam(this.attribParam);
        oa.setObjattrName(this.attribName);
        oa.setDataEncoding(this.dataEncoding);
        oa.setAttribType(this.attribType);
        oa.setCollTimespan(this.collTimeSpan);
        oa.setIsVisible(this.isVisible);
        oa.setAlarmId(this.alarmId);
        oa.setField1(this.field1);
        return oa;
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the attribId
     */
    public int getAttribId()
    {
        return attribId;
    }

    /**
     * @param attribId the attribId to set
     */
    public void setAttribId(int attribId)
    {
        this.attribId = attribId;
    }

    /**
     * @return the attribName
     */
    public String getAttribName()
    {
        return attribName;
    }

    /**
     * @param attribName the attribName to set
     */
    public void setAttribName(String attribName)
    {
        this.attribName = attribName;
    }

    /**
     * @return the attribValue
     */
    public String getAttribValue()
    {
        return attribValue;
    }

    /**
     * @param attribValue the attribValue to set
     */
    public void setAttribValue(String attribValue)
    {
        this.attribValue = attribValue;
    }

    /**
     * @return the valueType
     */
    public int getValueType()
    {
        return valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(int valueType)
    {
        this.valueType = valueType;
    }

    /**
     * @return the dataEncoding
     */
    public String getDataEncoding()
    {
        return dataEncoding;
    }

    /**
     * @param dataEncoding the dataEncoding to set
     */
    public void setDataEncoding(String dataEncoding)
    {
        this.dataEncoding = dataEncoding;
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
     * @return the attribType
     */
    public int getAttribType()
    {
        return attribType;
    }

    /**
     * @param attribType the attribType to set
     */
    public void setAttribType(int attribType)
    {
        this.attribType = attribType;
    }

    /**
     * @return the attribMode
     */
    public int getAttribMode()
    {
        return attribMode;
    }

    /**
     * @param attribMode the attribMode to set
     */
    public void setAttribMode(int attribMode)
    {
        this.attribMode = attribMode;
    }

    /**
     * @return the attribParam
     */
    public String getAttribParam()
    {
        return attribParam;
    }

    /**
     * @param attribParam the attribParam to set
     */
    public void setAttribParam(String attribParam)
    {
        this.attribParam = attribParam;
    }

    /**
     * @return the dataType
     */
    public int getDataType()
    {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(int dataType)
    {
        this.dataType = dataType;
    }

    /**
     * @return the dataUnit
     */
    public String getDataUnit()
    {
        return dataUnit;
    }

    /**
     * @param dataUnit the dataUnit to set
     */
    public void setDataUnit(String dataUnit)
    {
        this.dataUnit = dataUnit;
    }

    /**
     * @return the attribCode
     */
    public String getAttribCode()
    {
        return attribCode;
    }

    /**
     * @param attribCode the dataField to set
     */
    public void setAttribCode(String attribCode)
    {
        this.attribCode = attribCode;
    }

    /**
     * @return the attribDesc
     */
    public String getAttribDesc()
    {
        return attribDesc;
    }

    /**
     * @param attribDesc the attribDesc to set
     */
    public void setAttribDesc(String attribDesc)
    {
        this.attribDesc = attribDesc;
    }

    /**
     * @return the isVisible
     */
    public int getIsVisible()
    {
        return isVisible;
    }

    /**
     * @param isVisible the isVisible to set
     */
    public void setIsVisible(int isVisible)
    {
        this.isVisible = isVisible;
    }

    /**
     * @return the viewType
     */
    public String getViewType()
    {
        return viewType;
    }

    /**
     * @param viewType the viewType to set
     */
    public void setViewType(String viewType)
    {
        this.viewType = viewType;
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
     * @return the collTimeSpan
     */
    public int getCollTimeSpan()
    {
        return collTimeSpan;
    }

    /**
     * @param collTimeSpan the collTimeSpan to set
     */
    public void setCollTimeSpan(int collTimeSpan)
    {
        this.collTimeSpan = collTimeSpan;
    }

    public int getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }

    // </editor-fold>

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }

}
