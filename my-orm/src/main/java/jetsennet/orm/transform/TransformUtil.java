package jetsennet.orm.transform;

public class TransformUtil
{

    /**
     * sql含有“distinct”关键字，返回null
     * 没有，返回去除 select部分剩余sql
     * 注意：从UORM(org.uorm.dao.common.JdbcUtils)拷贝
     * 
     * @param sql
     * @return
     */
    public static String removeSelect(String sql)
    {
        sql = sql.toUpperCase();
        int beginPos = sql.indexOf("FROM");
        int dispos = sql.substring(0, beginPos).indexOf("DISTINCT");
        if (dispos > 0)
        {
            return null;
        }
        else
        {
            return sql.substring(beginPos);
        }
    }

}
