package jetsennet.jbmp.entity;

import java.io.Serializable;

/**
 * 性能查询结果
 * @author lianghongjie
 */
public class QueryResult implements Serializable
{
    private long collTime;
    private String value;

    public long getCollTime()
    {
        return collTime;
    }

    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
