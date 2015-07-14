package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * SNMP设备型号
 * @author GUO
 */
@Table(name = "BMP_SNMPOBJTYPE")
public class SnmpObjTypeEntity
{

    /**
     * 型号id
     */
    @Id
    @Column(name = "TYPE_ID")
    private int typeId;
    /**
     * SNMP设备的SYSOID值
     */
    @Column(name = "SNMP_SYSOID")
    private String snmpSysoid;
    /**
     * 标识值
     */
    @Column(name = "SNMP_VALUE")
    private String snmpValue;
    /**
     * 识别方式 EX:存在 EQ:等于 LK:Like IN:In
     */
    @Column(name = "CONDITION")
    private String condition;
    /**
     * 设备对应的属性类别
     */
    @Column(name = "CLASS_ID")
    private int classId;
    /**
     * 型号描述
     */
    @Column(name = "FIELD_1")
    private String field1;

    public static final String CONDITION_EX = "EX";
    public static final String CONDITION_EQ = "EQ";
    public static final String CONDITION_LIKE = "LK";
    public static final String CONDITION_IN = "IN";

    public SnmpObjTypeEntity()
    {
    }

    /**
     * @return the typeId
     */
    public int getTypeId()
    {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }

    /**
     * @return the snmpSysoid
     */
    public String getSnmpSysoid()
    {
        return snmpSysoid;
    }

    /**
     * @param snmpSysoid the snmpSysoid to set
     */
    public void setSnmpSysoid(String snmpSysoid)
    {
        this.snmpSysoid = snmpSysoid;
    }

    /**
     * @return the snmpValue
     */
    public String getSnmpValue()
    {
        return snmpValue;
    }

    /**
     * @param snmpValue the snmpValue to set
     */
    public void setSnmpValue(String snmpValue)
    {
        this.snmpValue = snmpValue;
    }

    /**
     * @return the condition
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
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
}
