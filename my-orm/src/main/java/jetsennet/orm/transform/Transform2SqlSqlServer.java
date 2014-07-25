package jetsennet.orm.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.util.SpecialCharSqlServer;
import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.TwoTuple;

/**
 * TODO 需要进一步完善分页部分（getLimitString）
 * @author GoGo
 *
 */
public class Transform2SqlSqlServer extends Transform2Sql
{

    private static final String SELECT = "select";
    private static final String FROM = "from";
    private static final String DISTINCT = "distinct";
    private static final String ORDER_BY = "order by";
    public static final String SELECT_WITH_SPACE = SELECT + ' ';
    private static final Pattern ALIAS_PATTERN = Pattern.compile("\\sas\\s[^,]+(,?)");

    public Transform2SqlSqlServer(Configuration config)
    {
        super(config);
    }

    @Override
    public PageSqlEntity pageSelect(SelectEntity select, int page, int pageSize)
    {
        String order = select.getOrder();
        if (order == null || order.isEmpty())
        {
            order = "CURRENT_TIMESTAMP";
        }
        // 统计sql
        String countSql = this.transSelect(select, true);

        // 查询sql
        select.order(null);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM (");
        sb.append("SELECT TOP ").append(PageSqlEntity.END).append(" ROW_NUMBER() OVER(ORDER BY ").append(order).append(") AS RN, * FROM (");
        sb.append(this.transSelect(select, false));
        sb.append(") ORIGIN");
        sb.append(") SUB WHERE RN >= ").append(PageSqlEntity.BEGIN);

        // 重新设置order字段，保证输入的select对象不变
        select.order(order);

        return new PageSqlEntity(sb.toString(), countSql, page, pageSize);
    }

    protected String paramString(String str)
    {
        return SpecialCharSqlServer.trans(str);
    }

    protected TwoTuple<String, Boolean> paramStringLike(String str)
    {
        return SpecialCharSqlServer.transLikeParam(str);
    }

    @Override
    public boolean supportsOffset()
    {
        return true;
    }

    @Override
    public String getLimitString(String query, int offset, int limit)
    {
        StringBuilder sb = new StringBuilder(query.trim());

        int orderByIndex = shallowIndexOfWord(sb, ORDER_BY, 0);
        CharSequence orderby = orderByIndex > 0 ? sb.subSequence(orderByIndex, sb.length()) : "ORDER BY CURRENT_TIMESTAMP";

        // Delete the order by clause at the end of the query
        if (orderByIndex > 0)
        {
            sb.delete(orderByIndex, orderByIndex + orderby.length());
        }
        // HHH-5715 bug fix
        replaceDistinctWithGroupBy(sb);
        insertRowNumberFunction(sb, orderby);
        // Wrap the query within a with statement:
        sb.insert(0, "WITH query AS (").append(") SELECT * FROM query ");
        sb.append("WHERE __UORM_ROW_NR__ >= ").append(offset + 1).append(" AND __UORM_ROW_NR__ < ").append(offset + limit + 1);

        return sb.toString();
    }

    static int getAfterSelectInsertPoint(String sql)
    {
        int selectIndex = sql.toLowerCase().indexOf("select");
        final int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

    @Override
    public String getSelectGUIDString()
    {
        return "select newid()";
    }

    protected static void replaceDistinctWithGroupBy(StringBuilder sql)
    {
        int distinctIndex = shallowIndexOfWord(sql, DISTINCT, 0);
        int selectEndIndex = shallowIndexOfWord(sql, FROM, 0);
        if (distinctIndex > 0 && distinctIndex < selectEndIndex)
        {
            sql.delete(distinctIndex, distinctIndex + DISTINCT.length() + " ".length());
            sql.append(" group by").append(getSelectFieldsWithoutAliases(sql));
        }
    }

    protected void insertRowNumberFunction(StringBuilder sql, CharSequence orderby)
    {
        // Find the end of the select clause
        int selectEndIndex = shallowIndexOfWord(sql, FROM, 0);

        // Insert after the select clause the row_number() function:
        sql.insert(selectEndIndex - 1, ", ROW_NUMBER() OVER (" + orderby + ") as __UORM_ROW_NR__");
    }

    /**
     * Returns index of the first case-insensitive match of search term surrounded by spaces
     * that is not enclosed in parentheses.
     *
     * @param sb String to search.
     * @param search Search term.
     * @param fromIndex The index from which to start the search.
     * @return Position of the first match, or {@literal -1} if not found.
     */
    private static int shallowIndexOfWord(final StringBuilder sb, final String search, int fromIndex)
    {
        final int index = shallowIndexOf(sb, ' ' + search + ' ', fromIndex);
        return index != -1 ? (index + 1) : -1; // In case of match adding one because of space placed in front of search term.
    }

    /**
     * Returns index of the first case-insensitive match of search term that is not enclosed in parentheses.
     *
     * @param sb String to search.
     * @param search Search term.
     * @param fromIndex The index from which to start the search.
     * @return Position of the first match, or {@literal -1} if not found.
     */
    private static int shallowIndexOf(StringBuilder sb, String search, int fromIndex)
    {
        final String lowercase = sb.toString().toLowerCase(); // case-insensitive match
        final int len = lowercase.length();
        final int searchlen = search.length();
        int pos = -1, depth = 0, cur = fromIndex;
        do
        {
            pos = lowercase.indexOf(search, cur);
            if (pos != -1)
            {
                for (int iter = cur; iter < pos; iter++)
                {
                    char c = sb.charAt(iter);
                    if (c == '(')
                    {
                        depth = depth + 1;
                    }
                    else if (c == ')')
                    {
                        depth = depth - 1;
                    }
                }
                cur = pos + searchlen;
            }
        }
        while (cur < len && depth != 0 && pos != -1);
        return depth == 0 ? pos : -1;
    }

    protected static CharSequence getSelectFieldsWithoutAliases(StringBuilder sql)
    {
        final int selectStartPos = shallowIndexOf(sql, SELECT_WITH_SPACE, 0);
        final int fromStartPos = shallowIndexOfWord(sql, FROM, selectStartPos);
        String select = sql.substring(selectStartPos + SELECT.length(), fromStartPos);

        // Strip the as clauses
        return stripAliases(select);
    }

    protected static String stripAliases(String str)
    {
        Matcher matcher = ALIAS_PATTERN.matcher(str);
        return matcher.replaceAll("$1");
    }

}
