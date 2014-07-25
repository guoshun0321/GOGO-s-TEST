package jetsennet.orm.sql.cascade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

/**
 * 批量操作实体
 * 
 * @author 郭祥
 */
public class CascadeSqlEntity
{

    /**
     * 被操作表
     */
    protected String tableName;
    /**
     * 数据库表信息
     */
    protected TableInfo tableInfo;
    /**
     * 操作类型
     */
    protected SqlTypeEnum type;
    /**
     * 操作值
     */
    protected Map<String, Object> valueMap;
    /**
     * 需要进行宏替换的值
     */
    protected Map<String, CascadeRepEntity> repMap;
    /**
     * 父SQL
     */
    protected CascadeSqlEntity parent;
    /**
     * 子SQL
     */
    protected List<CascadeSqlEntity> subs;

    public CascadeSqlEntity(String tableName)
    {
        this.tableName = tableName;
        this.subs = new ArrayList<CascadeSqlEntity>(2);
        this.valueMap = new HashMap<String, Object>(15);
        this.repMap = new HashMap<String, CascadeRepEntity>(2);
    }

    /**
     * 添加操作值
     * 
     * @param key
     * @param value
     */
    public void addValue(String key, Object value)
    {
        this.valueMap.put(key, value);
    }

    public void addValue(Map<String, String> map)
    {
        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            this.valueMap.put(key, map.get(key));
        }
    }

    /**
     * 添加宏替换
     * 
     * @param key
     * @param rep
     */
    public void addRep(String key, CascadeRepEntity rep)
    {
        this.repMap.put(key, rep);
    }

    /**
     * 添加宏替换
     * 
     * @param key
     * @param rep
     */
    public void addRep(String key, String pTable, String pField)
    {
        this.repMap.put(key, new CascadeRepEntity(pTable, pField));
    }

    /**
     * 添加子SQL语句
     * 
     * @param sub
     */
    public void addSub(CascadeSqlEntity sub)
    {
        sub.parent = this;
        this.subs.add(sub);
    }

    /**
     * 宏替换操作
     */
    public void marcoReplace()
    {
        Set<String> keys = repMap.keySet();
        for (String key : keys)
        {
            CascadeRepEntity value = this.repMap.get(key);
            if (value != null)
            {
                this.valueMap.put(key, this.searchKey(value.table, value.field));
            }
        }
    }

    /**
     * 向前搜索key和值
     * @param tableName
     * @param fieldName
     * @return
     */
    private Object searchKey(String tableName, String fieldName)
    {
        Object retval = null;
        CascadeSqlEntity parent = this.parent;
        while (parent != null)
        {
            if (parent.tableName.equals(tableName) && parent.valueMap.containsKey(fieldName))
            {
                retval = parent.valueMap.get(fieldName);
                break;
            }
            else
            {
                parent = parent.parent;
            }
        }
        return retval;
    }

    /**
     * 解析参数，参数样式为：$TABLENAME.FIELD
     * 
     * @param param
     * @return 长度为2的String数组，[TABLENAME, FIELD]
     */
    public String[] splitParam(String param)
    {
        param = param.substring(1);
        String temp[] = param.split("\\.");
        if (temp != null && temp.length == 2)
        {
            return temp;
        }
        else
        {
            throw new UncheckedOrmException("无效参数：" + param);
        }
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public TableInfo getTableInfo()
    {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo)
    {
        this.tableInfo = tableInfo;
    }

    public SqlTypeEnum getType()
    {
        return type;
    }

    public void setType(SqlTypeEnum type)
    {
        this.type = type;
    }

    public Map<String, Object> getValueMap()
    {
        return valueMap;
    }

    public void setValueMap(Map<String, Object> valueMap)
    {
        this.valueMap = valueMap;
    }

    public CascadeSqlEntity getParent()
    {
        return parent;
    }

    public void setParent(CascadeSqlEntity parent)
    {
        this.parent = parent;
    }

    public List<CascadeSqlEntity> getSubs()
    {
        return subs;
    }

    public void setSubs(List<CascadeSqlEntity> subs)
    {
        this.subs = subs;
    }

    public Map<String, CascadeRepEntity> getRepMap()
    {
        return repMap;
    }

    public void setRepMap(Map<String, CascadeRepEntity> repMap)
    {
        this.repMap = repMap;
    }

}
