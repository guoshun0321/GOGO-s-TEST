package jetsennet.orm.sql;

public class Sql
{

    /**
     * insert语句
     * @return
     */
    public static InsertEntity insert(String table)
    {
        return new InsertEntity(table);
    }

    /**
     * update语句
     * @return
     */
    public static UpdateEntity update(String table)
    {
        return new UpdateEntity(table);
    }

    /**
     * delete语句
     * @param table
     * @return
     */
    public static DeleteEntity delete(String table)
    {
        return new DeleteEntity(table);
    }

    /**
     * select语句
     * @param column 结果列表
     * @return
     */
    public static SelectEntity select(String column)
    {
        return new SelectEntity(column);
    }

}
