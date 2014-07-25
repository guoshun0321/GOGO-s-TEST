package jetsennet.orm.transform;

public class PageResult
{

    /**
     * 总页数
     */
    public final int count;
    /**
     * 当前页
     */
    public final int cur;
    /**
     * 查询语句
     */
    public final String pageSql;

    public PageResult(int count, int cur, String pageSql)
    {
        this.count = count;
        this.cur = cur;
        this.pageSql = pageSql;
    }

}
