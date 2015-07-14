/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.parse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.entity.ValueTableEntity;

/**
 * SNMP 协议中的枚举类型
 * @author Guo
 */
public class SnmpEnumEntity
{

    private SnmpNodesEntity moid;
    private String name;
    private Map<Integer, String> id2Value;

    /**
     * 构造函数
     */
    public SnmpEnumEntity()
    {
    }

    @Override
    public String toString()
    {
        if (id2Value == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (Integer key : id2Value.keySet())
        {
            sb.append("<");
            sb.append(key);
            sb.append("，");
            sb.append(id2Value.get(key));
            sb.append(">");
        }
        return sb.toString();
    }

    /**
     * @param id 参数
     * @param value 值
     */
    public void add(int id, String value)
    {
        if (id2Value == null)
        {
            id2Value = new LinkedHashMap<Integer, String>();
        }
        id2Value.put(id, value);
    }

    /**
     * @return 结果
     */
    public ArrayList<ValueTableEntity> genValueTable()
    {
        ArrayList<ValueTableEntity> result = new ArrayList<ValueTableEntity>();
        ValueTableEntity entity = new ValueTableEntity();
        entity.setValueType(-1);
        entity.setValueName(name);
        result.add(entity);
        for (Integer key : id2Value.keySet())
        {
            ValueTableEntity temp = new ValueTableEntity();
            temp.setAttribValue(key.toString());
            temp.setValueName(id2Value.get(key));
            result.add(temp);
        }
        return result;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the id2Value
     */
    public Map<Integer, String> getId2Value()
    {
        return id2Value;
    }

    /**
     * @param id2Value the id2Value to set
     */
    public void setId2Value(Map<Integer, String> id2Value)
    {
        this.id2Value = id2Value;
    }

    /**
     * @return the moid
     */
    public SnmpNodesEntity getMoid()
    {
        return moid;
    }

    /**
     * @param moid the moid to set
     */
    public void setMoid(SnmpNodesEntity moid)
    {
        this.moid = moid;
    }
}
