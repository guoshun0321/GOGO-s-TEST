package jetsennet.orm.sql;

public class SelectEntity implements ISql
{

    private boolean distinct;
    /**
     * 结果字段
     */
    private String column;
    /**
     * 表
     */
    private String table;
    /**
     * 子查询
     */
    private SelectEntity subTable;
    /**
     * 子查询别名
     */
    private String subTableAlias;
    /**
     * 条件
     */
    private FilterNode filter;
    /**
     * 分组信息
     */
    private String group;
    /**
     * having条件。如果需要的话，可以在这里做类似Filter的扩展
     */
    private String having;
    /**
     * union条件
     */
    private SelectEntity union;
    /**
     * union all条件
     */
    private SelectEntity unionAll;
    /**
     * order
     */
    private String order;

    public SelectEntity()
    {
        this.distinct = false;
    }

    public SelectEntity(String column)
    {
        this();
        this.column = column;
    }

    public SelectEntity distinct()
    {
        this.distinct = true;
        return this;
    }

    public SelectEntity column(String column)
    {
        this.column = column;
        return this;
    }

    public SelectEntity from(String table)
    {
        this.subTable = null;
        this.table = table;
        return this;
    }

    public SelectEntity from(SelectEntity subTable, String alias)
    {
        this.table = null;
        this.subTable = subTable;
        this.subTableAlias = alias;
        return this;
    }

    public SelectEntity where(FilterNode filter)
    {
        this.filter = filter;
        return this;
    }

    public SelectEntity group(String group)
    {
        this.group = group;
        return this;
    }

    public SelectEntity having(String having)
    {
        this.having = having;
        return this;
    }

    public SelectEntity union(SelectEntity sel)
    {
        this.union = sel;
        return this;
    }

    public SelectEntity unionAll(SelectEntity sel)
    {
        this.unionAll = sel;
        return this;
    }

    public SelectEntity order(String order)
    {
        this.order = order;
        return this;
    }

    public SqlTypeEnum getSqlType()
    {
        return SqlTypeEnum.SELECT;
    }

    public boolean isDistinct()
    {
        return distinct;
    }

    public String getColumn()
    {
        return column;
    }

    public String getTable()
    {
        return table;
    }

    public SelectEntity getSubTable()
    {
        return this.subTable;
    }

    public String getSubTableAlias()
    {
        return subTableAlias;
    }

    public FilterNode getFilter()
    {
        return filter;
    }

    public String getGroup()
    {
        return group;
    }

    public String getHaving()
    {
        return having;
    }

    public SelectEntity getUnion()
    {
        return union;
    }

    public SelectEntity getUnionAll()
    {
        return unionAll;
    }

    public String getOrder()
    {
        return order;
    }
}
