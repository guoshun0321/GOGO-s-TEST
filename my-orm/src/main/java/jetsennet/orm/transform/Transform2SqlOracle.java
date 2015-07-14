package jetsennet.orm.transform;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.util.SpecialCharOracle;
import jetsennet.util.TwoTuple;

/**
 * Oracle格式转换
 * 
 * @author 郭祥
 */
public class Transform2SqlOracle extends Transform2Sql
{

    public Transform2SqlOracle(Configuration config)
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
        sb.append("SELECT * FROM (");
        sb.append("SELECT ROWNUM AS RN, ORIGIN.* FROM (");
        sb.append(this.transSelect(select, false));
        sb.append(") ORIGIN WHERE ROWNUM <= ").append(PageSqlEntity.END);
        sb.append(") SUB WHERE RN >= ").append(PageSqlEntity.BEGIN);

        return new PageSqlEntity(sb.toString(), countSql, page, pageSize);
    }

    @Override
    protected String formatDateTimeString(String s)
    {
        return "to_date(\'" + s + "\','YYYY-MM-DD HH24:MI:SS')";
    }

    protected String paramString(String str)
    {
        return SpecialCharOracle.trans(str);
    }

    protected TwoTuple<String, Boolean> paramStringLike(String str)
    {
        return SpecialCharOracle.transLikeParam(str);
    }

    @Override
    public String getLimitString(String sql, int offset, int limit)
    {
        sql = sql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        final int forUpdateIndex = sql.toLowerCase().lastIndexOf("for update");
        if (forUpdateIndex > -1)
        {
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        if (offset > 0)
        {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        }
        else
        {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (offset > 0)
        {
            pagingSelect.append(" ) row_ where rownum <= ").append((limit + offset)).append(") where rownum_ > ").append(offset);
        }
        else
        {
            pagingSelect.append(" ) where rownum <= ").append(limit);
        }

        if (isForUpdate)
        {
            pagingSelect.append(' ');
            pagingSelect.append(forUpdateClause);
        }

        return pagingSelect.toString();
    }

    @Override
    public String getSelectGUIDString()
    {
        return "select rawtohex(sys_guid()) from dual";
    }

    @Override
    public String getSequenceNextValString(String tablename)
    {
        return "select " + getSelectSequenceNextValString(fixOracleSeqName(tablename)) + " from dual";
    }

    public String getSelectSequenceNextValString(String sequenceName)
    {
        return sequenceName + ".nextval";
    }

    /**
     * oracle seq name 不能超过30个字符，超过从后往前截取30个字符当行的seq
     * @param seqname
     * @return
     */
    private String fixOracleSeqName(String tablename)
    {
        String seqname = this.getDefaultSequenceName(tablename);
        if (seqname.length() > 30)
        {
            seqname = seqname.substring(seqname.length() - 30);
        }
        return seqname;
    }

}
