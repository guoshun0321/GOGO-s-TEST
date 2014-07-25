package jetsennet.orm.sql.cascade;

import java.util.List;

import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.FilterUtil;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.util.UncheckedOrmException;

public class CascadeSqlSelectEntity extends CascadeSqlEntity
{
    /**
     * 是否自循环
     */
    private String selfLoop;
    /**
     * 条件字符串的名称，用于设置查找条件
     */
    private String filterField;

    public CascadeSqlSelectEntity(String tableName)
    {
        super(tableName);
    }

    public ISql genSql()
    {
        return Sql.select("*").from(this.tableName).where(this.genFilter());
    }

    /**
     * 生成条件
     * @return
     */
    private FilterNode genFilter()
    {
        FilterNode retval = null;
        Object filterValue = this.valueMap.get(this.filterField);
        if (filterValue != null)
        {
            if (filterValue instanceof List<?>)
            {
                List<Object> filterValues = this.tableInfo.fieldValueListTrans(this.filterField, (List) filterValue);
                retval = FilterUtil.in(this.filterField, filterValues.toArray(new Object[0]));
            }
            else
            {
                retval = FilterUtil.eq(this.filterField, this.tableInfo.fieldValueTrans(this.filterField, filterValue));
            }
        }
        else
        {
            throw new UncheckedOrmException("找不到键对应的值：" + this.filterField);
        }
        return retval;
    }

    public String getSelfLoop()
    {
        return selfLoop;
    }

    public void setSelfLoop(String selfLoop)
    {
        this.selfLoop = selfLoop;
    }

    public String getFilterField()
    {
        return filterField;
    }

    public void setFilterField(String filterField)
    {
        this.filterField = filterField;
    }

}
