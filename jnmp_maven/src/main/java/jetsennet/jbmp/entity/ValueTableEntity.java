/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import java.util.ArrayList;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 属性字典表
 * @author Guo
 */
@Table(name = "BMP_VALUETABLE")
public class ValueTableEntity
{

    /**
     * ID
     */
    @Id
    @Column(name = "VALUE_ID")
    private int valueId;
    /**
     * 属性类型，为-1时，为字典类型列表
     */
    @Column(name = "VALUE_TYPE")
    private int valueType;
    /**
     * MIB类型
     */
    @Column(name = "MIB_ID")
    private int mibId;
    /**
     * 属性值
     */
    @Column(name = "ATTRIB_VALUE")
    private String attribValue;
    /**
     * 属性名称，英文
     */
    @Column(name = "VALUE_NAME")
    private String valueName;
    /**
     * 描述，中文
     */
    @Column(name = "VALUE_DESC")
    private String valueDesc;

    private ArrayList<ValueTableEntity> child = new ArrayList<ValueTableEntity>();

    /**
     * 属性类型，为-1时，为字典类型列表
     */
    public static final int VALUE_TYPE_ENUM_NAME = -1;

    /**
     * @return the valueId
     */
    public int getValueId()
    {
        return valueId;
    }

    /**
     * @param valueId the valueId to set
     */
    public void setValueId(int valueId)
    {
        this.valueId = valueId;
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
     * @return the valueName
     */
    public String getValueName()
    {
        return valueName;
    }

    /**
     * @param valueName the valueName to set
     */
    public void setValueName(String valueName)
    {
        this.valueName = valueName;
    }

    /**
     * @return the valueDesc
     */
    public String getValueDesc()
    {
        return valueDesc;
    }

    /**
     * @param valueDesc the valueDesc to set
     */
    public void setValueDesc(String valueDesc)
    {
        this.valueDesc = valueDesc;
    }

    public int getMibId()
    {
        return mibId;
    }

    public void setMibId(int mibId)
    {
        this.mibId = mibId;
    }

    public ArrayList<ValueTableEntity> getChild()
    {
        return child;
    }

    public void setChild(ArrayList<ValueTableEntity> child)
    {
        this.child = child;
    }

    /**
     * 父类增加子类
     * @param enums 实体
     */
    public void add(ValueTableEntity enums)
    {
        getChild().add(enums);
    }

}
