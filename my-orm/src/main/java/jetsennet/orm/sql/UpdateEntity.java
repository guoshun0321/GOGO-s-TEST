package jetsennet.orm.sql;

/**
 * UPDATE实体
 * 
 * @author 郭祥
 */
public class UpdateEntity implements ISql
{

    /**
     * 表名称
     */
    public final String table;
    /**
     * 参数长度
     */
    private int length;
    /**
     * 键
     */
    private String[] keys;
    /**
     * 值
     */
    private Object[] values;
    /**
     * 条件过滤器
     */
    private FilterNode filter;

    public UpdateEntity(String table)
    {
        this.table = table;
    }

    public final UpdateEntity columns(String... keys)
    {
        this.keys = keys;
        this.length = keys.length;
        return this;
    }

    public final UpdateEntity values(Object... values)
    {
        this.values = values;
        return this;
    }

    public final UpdateEntity set(Object... params)
    {
        if (params == null || params.length == 0)
        {
            throw new SqlFormatException("异常参数：" + params);
        }
        int remainder = params.length % 2;
        if (remainder != 0)
        {
            throw new SqlFormatException("异常参数：参数个数不是偶数");
        }
        length = params.length / 2;
        keys = new String[length];
        values = new Object[length];
        for (int i = 0; i < length; i++)
        {
            keys[i] = params[i * 2].toString();
            values[i] = params[i * 2 + 1];
        }
        return this;
    }

    public final UpdateEntity where(FilterNode filter)
    {
        this.filter = filter;
        return this;
    }

    public final SqlTypeEnum getSqlType()
    {
        return SqlTypeEnum.UPDATE;
    }

    public final int getLength()
    {
        return length;
    }

    public final String[] getKeys()
    {
        return keys;
    }

    public final Object[] getValues()
    {
        return values;
    }

    public final FilterNode getFilter()
    {
        return filter;
    }

}
