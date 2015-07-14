package jetsennet.orm.transform;

public class PageSqlEntity
{

    /**
     * 查询记录
     */
    public final String pageSql;
    /**
     * 查询数量
     */
    public final String counterSql;
    /**
     * 页码
     */
    public final int page;
    /**
     * 页长度
     */
    public final int pageSize;
    /**
     * 占位符，开始
     */
    public static final String BEGIN = "<%BEGIN%>";
    /**
     * 占位符，结束
     */
    public static final String END = "<%END%>";
    /**
     * 占位符，SIZE
     */
    public static final String SIZE = "<%SIZE%>";

    public PageSqlEntity(String pageSql, String counterSql, int page, int pageSize)
    {
        if (page <= 0 || pageSize <= 0)
        {
            throw new IllegalArgumentException();
        }
        this.pageSql = pageSql;
        this.counterSql = counterSql;
        this.page = page;
        this.pageSize = pageSize;
    }

    /**
     * 根据数据条数来生成查询用sql语句
     * 
     * @param count
     * @return
     */
    public PageResult querySql(int count)
    {
        // 计算总页数
        int pageCount = count / pageSize;
        int last = count % pageSize;
        if (last > 0)
        {
            pageCount++;
        }

        // 当前页
        int curPage = page;

        // 生成查询语句
        int exceptBegin = (page - 1) * pageSize + 1;
        int exceptEnd = page * pageSize;
        int begin = -1;
        int end = -1;
        if (count >= exceptEnd)
        {
            begin = exceptBegin;
            end = exceptEnd;
        }
        else if (count >= exceptBegin)
        {
            begin = exceptBegin;
            end = count;
        }
        else
        {
            begin = (pageCount - 1) * pageSize + 1;
            end = count;
            curPage = pageCount;
        }
        String sql = pageSql.replace(BEGIN, Integer.toString(begin)).replace(END, Integer.toString(end)).replace(SIZE, Integer.toString(pageSize));

        return new PageResult(pageCount, curPage, sql);
    }
}
