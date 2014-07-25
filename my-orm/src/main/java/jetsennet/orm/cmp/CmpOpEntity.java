package jetsennet.orm.cmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CmpOpEntity
{

    /**
     * 表名
     */
    private String tableName;
    /**
     * 条件字段名称
     */
    private String filterName;
    /**
     * 条件字段的值
     */
    private String filterValue;
    /**
     * 操作
     */
    private CmpOpEnum action;
    /**
     * 操作值
     */
    private Map<String, String> valueMap;
    /**
     * 子操作
     */
    private List<CmpOpEntity> subs;

    public CmpOpEntity()
    {
        this.valueMap = new HashMap<String, String>();
        this.subs = new ArrayList<CmpOpEntity>();
    }

    public CmpOpEntity(String tableName, String action)
    {
        this();
        this.tableName = tableName;
        this.action = CmpOpEnum.ignoreCaseValueOf(action);
    }

    public static CmpOpEntity genInsert(String tableName)
    {
        CmpOpEntity retval = new CmpOpEntity();
        retval.tableName = tableName;
        retval.action = CmpOpEnum.INSERT;
        return retval;
    }

    public static CmpOpEntity genUpdate(String tableName)
    {
        CmpOpEntity retval = new CmpOpEntity();
        retval.tableName = tableName;
        retval.action = CmpOpEnum.UPDATE;
        return retval;
    }

    public static CmpOpEntity genDelete(String tableName, String filterName, String filterValue)
    {
        CmpOpEntity retval = new CmpOpEntity();
        retval.tableName = tableName;
        retval.filterName = filterName;
        retval.filterValue = filterValue;
        retval.valueMap.put(filterName, filterValue);
        retval.action = CmpOpEnum.DELETE;
        return retval;
    }

    public static CmpOpEntity genSelect(String tableName, String filterName, String filterValue)
    {
        CmpOpEntity retval = new CmpOpEntity();
        retval.tableName = tableName;
        retval.filterName = filterName;
        retval.filterValue = filterValue;
        retval.valueMap.put(filterName, filterValue);
        retval.action = CmpOpEnum.SELECT;
        return retval;
    }

    public void addValue(String key, String value)
    {
        this.valueMap.put(key, value);
    }

    public void addSub(CmpOpEntity sub)
    {
        this.subs.add(sub);
    }

    public String toXml(StringBuilder sb)
    {
        boolean isRoot = false;
        if (sb == null)
        {
            sb = new StringBuilder(200);
            isRoot = true;
        }
        if (isRoot)
        {
            sb.append("<ops>");
        }
        sb.append("<op table=\"").append(this.tableName).append("\" action=\"").append(this.action.name()).append("\"");
        if (this.filterName != null)
        {
            sb.append(" filter=\"").append(filterName).append("\"");
        }
        sb.append(">");
        Set<String> keys = this.valueMap.keySet();
        for (String key : keys)
        {
            String value = this.valueMap.get(key);
            if (value != null)
            {
                sb.append("<field name=\"").append(key).append("\" value=\"").append(value).append("\"/>");
            }
        }
        for (CmpOpEntity sub : subs)
        {
            sub.toXml(sb);
        }
        sb.append("</op>");
        if (isRoot)
        {
            sb.append("</ops>");
        }
        return sb.toString();
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public CmpOpEnum getAction()
    {
        return action;
    }

    public void setAction(CmpOpEnum action)
    {
        this.action = action;
    }

    public Map<String, String> getValueMap()
    {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap)
    {
        this.valueMap = valueMap;
    }

    public List<CmpOpEntity> getSubs()
    {
        return subs;
    }

    public void setSubs(List<CmpOpEntity> subs)
    {
        this.subs = subs;
    }

    public String getFilterName()
    {
        return filterName;
    }

    public void setFilterName(String filterName)
    {
        this.filterName = filterName;
    }

    public String getFilterValue()
    {
        return filterValue;
    }

    public void setFilterValue(String filterValue)
    {
        this.filterValue = filterValue;
    }

}
