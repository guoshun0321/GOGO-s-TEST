package jetsennet.orm.transform;

import java.util.Map;

import jetsennet.orm.sql.Sql;

public interface ITransform2Sql
{

    /**
     * 将Sql对象转换成sql语句
     * 
     * @param sql
     * @return
     */
    public String trans(Sql sql);

    /**
     * 将POJO转换成sql语句
     * 
     * @param cls
     * @param obj
     * @return
     */
    public <T> String trans(Class<T> cls, T obj);

    /**
     * 将HashMap转换成sql语句
     * 
     * @param map
     * @param obj
     * @return
     */
    public <T> String trans(Map<String, String> map, T obj);

}
