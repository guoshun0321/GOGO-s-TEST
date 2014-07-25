package jetsennet.orm.sql;

import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.util.UncheckedOrmException;

public class DeleteEntity implements ISql
{

    /**
     * 表
     */
    public final String table;
    /**
     * 过滤器
     */
    private FilterNode filter;

    public DeleteEntity(String table)
    {
        this.table = table;
    }

    public DeleteEntity where(FilterNode filter)
    {
        this.filter = filter;
        return this;
    }

    public SqlTypeEnum getSqlType()
    {
        return SqlTypeEnum.DELETE;
    }

    public FilterNode getFilter()
    {
        return filter;
    }

    /**
     * 转换成对主键搜索的Sql
     * 
     * @return
     */
    public SelectEntity selectKey(TableInfoMgr mgr)
    {
        SelectEntity retval = null;
        TableInfo info = mgr.getTableInfo(table);
        if (info != null)
        {
            String key = info.getKey().getName();
            retval = Sql.select(key).from(table).where(this.filter);
        }
        else
        {
            throw new UncheckedOrmException("未注册的表信息：" + table);
        }
        return retval;
    }
}
