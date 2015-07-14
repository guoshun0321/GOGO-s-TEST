package jetsennet.orm.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CscQueryResult
{

    /**
     * 表名
     */
    private String tableName;
    /**
     * 值
     */
    private Map<String, String> values;
    /**
     * 子表
     */
    private List<CscQueryResult> subs;

    public CscQueryResult()
    {
        this.subs = new ArrayList<CscQueryResult>();
    }

    public void addValue(String key, String value)
    {
        if (values == null)
        {
            values = new HashMap<String, String>();
        }
        values.put(key, value);
    }

    public void addSub(CscQueryResult csc)
    {
        this.subs.add(csc);
    }

    public void addSub(List<CscQueryResult> cscs)
    {
        this.subs.addAll(cscs);
    }

    /**
     * 结果快照，用于测试
     * 
     * @param sb
     * @return
     */
    public String getDesc(StringBuilder sb, String[] includes)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        sb.append("{");
        sb.append("\"name\":\"").append(this.tableName).append("\",");

        sb.append("\"values\":{");

        for (String include : includes)
        {
            Set<String> keys = values.keySet();
            for (String key : keys)
            {
                if (key.contains(include))
                {
                    String value = values.get(key);
                    sb.append("\"").append(key).append("\":\"").append(value).append("\",");
                    break;
                }
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");

        if (this.subs.size() > 0)
        {
            sb.append(",\"subs\":[");
            for (CscQueryResult sub : subs)
            {
                sub.getDesc(sb, includes);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }
        sb.append("}");

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

    public Map<String, String> getValues()
    {
        return values;
    }

    public void setValues(Map<String, String> values)
    {
        this.values = values;
    }

    public List<CscQueryResult> getSubs()
    {
        return subs;
    }

    public void setSubs(List<CscQueryResult> subs)
    {
        this.subs = subs;
    }

}
