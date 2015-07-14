package jetsennet.orm.sql.cascade;

import java.util.ArrayList;
import java.util.List;

import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.FilterUtil;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.util.UncheckedOrmException;

public class CascadeSqlDeleteEntity extends CascadeSqlEntity
{

    /**
     * 条件字符串的名称，用于删除操作的条件设置
     */
    private String filterField;
    /**
     * 是否自循环
     */
    private String selfLoop;
    /**
     * 是否会对其他后续操作造成影响
     */
    private boolean affected;
    /**
     * 占位符
     */
    public static final String AFFECTED_PLACEHOLDER = "INFO#AFFECTED";

    public CascadeSqlDeleteEntity(String tableName)
    {
        super(tableName);
    }

    public List<String> getKeyValues()
    {
        List<String> retval = new ArrayList<String>();
        Object obj = this.valueMap.get(tableInfo.getKey().getName());
        if (obj instanceof String)
        {
            retval.add((String) obj);
        }
        else if (obj instanceof List)
        {
            for (Object keyValue : (List<?>) obj)
            {
                retval.add((String) keyValue);
            }
        }
        else if (obj instanceof String[])
        {
            for (String keyValue : (String[]) obj)
            {
                retval.add(keyValue);
            }
        }
        else
        {
            throw new UncheckedOrmException("不合法的值：" + obj);
        }
        return retval;
    }

    /**
     * 生成删除用SQL语句
     * @return
     */
    public ISql genDeleteSql()
    {
        DeleteEntity retval = null;
        FilterNode filter = this.genFilter();
        if (filter != null)
        {
            retval = Sql.delete(this.tableName).where(this.genFilter());
        }
        return retval;
    }

    /**
     * 如果sql会对其他数据操作造成影响，调用此方法会生成造成影响的ISql。
     * 目前仅仅支持DELETE
     * 
     * @return
     */
    public ISql genAffectedSql()
    {
        ISql retval = null;
        if (affected)
        {
            retval = Sql.select(this.tableInfo.getKey().getName()).from(this.tableName).where(this.genFilter());
        }
        return retval;
    }

    /**
     * 生成条件
     * @return
     */
    public FilterNode genFilter()
    {
        FilterNode retval = null;
        Object filterValue = this.valueMap.get(this.filterField);
        if (filterValue != null)
        {
            if (filterValue instanceof List)
            {
                if (!((List) filterValue).isEmpty())
                {
                    List<Object> filterValues = this.tableInfo.fieldValueListTrans(this.filterField, (List) filterValue);
                    retval = FilterUtil.in(this.filterField, filterValues.toArray(new Object[0]));
                }
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

    public void addAffected(Object obj)
    {
        this.valueMap.put(AFFECTED_PLACEHOLDER, obj);
    }

    public String getFilterField()
    {
        return filterField;
    }

    public void setFilterField(String filterField)
    {
        this.filterField = filterField;
    }

    public boolean isAffected()
    {
        return affected;
    }

    public void setAffected(boolean affected)
    {
        this.affected = affected;
    }

    public String getSelfLoop()
    {
        return selfLoop;
    }

    public void setSelfLoop(String selfLoop)
    {
        this.selfLoop = selfLoop;
    }

}
