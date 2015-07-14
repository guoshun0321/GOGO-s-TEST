package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.util.SpecialCharMySql;
import jetsennet.util.TwoTuple;

public class Transform2SqlMySql extends Transform2Sql
{

    public Transform2SqlMySql(Configuration config)
    {
        super(config);
    }

    @Override
    public PageSqlEntity pageSelect(SelectEntity select, int page, int pageSize)
    {
        // 统计sql
        String countSql = this.transSelect(select, true);

        // 查询sql
        StringBuilder sb = new StringBuilder();
        sb.append(this.transSelect(select, false)).append(" LIMIT ").append(PageSqlEntity.BEGIN).append(",").append(PageSqlEntity.SIZE);

        return new PageSqlEntity(sb.toString(), countSql, page, pageSize);
    }

    protected String paramString(String str)
    {
        return SpecialCharMySql.trans(str);
    }

    protected TwoTuple<String, Boolean> paramStringLike(String str)
    {
        return SpecialCharMySql.transLikeParam(str);
    }

    @Override
    public String getLimitString(String query, int offset, int limit)
    {
        StringBuilder sb = new StringBuilder(query.length() + 20);
        sb.append(query);
        if (offset > 0)
        {
            sb.append(" limit ").append(offset).append(',').append(limit);
        }
        else
        {
            sb.append(" limit ").append(limit);
        }
        return sb.toString();
    }

    @Override
    public String getSelectGUIDString()
    {
        return "select uuid()";
    }

}
