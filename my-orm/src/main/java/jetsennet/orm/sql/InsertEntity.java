package jetsennet.orm.sql;

public class InsertEntity implements ISql
{

    /**
     * 表名
     */
    public final String table;
    /**
     * 列名
     * 可以为null
     */
    private String[] keys;
    /**
     * 列值
     */
    private Object[] values;

    public InsertEntity(String table)
    {
        this.table = table;
    }

    public InsertEntity columns(String... keys)
    {
        this.keys = keys;
        return this;
    }

    public InsertEntity values(Object... values)
    {
        this.values = values;
        return this;
    }

    public String[] getKeys()
    {
        return keys;
    }

    public Object[] getValues()
    {
        return values;
    }

    public SqlTypeEnum getSqlType()
    {
        return SqlTypeEnum.INSERT;
    }

}
