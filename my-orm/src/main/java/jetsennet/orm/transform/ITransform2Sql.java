package jetsennet.orm.transform;

import java.util.List;
import java.util.Map;

import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.util.TwoTuple;

public interface ITransform2Sql
{

    /**
     * 将Sql对象转换成sql语句
     * 
     * @param sql
     * @return
     */
    public String trans(ISql sql);

    /**
     * 生成分页查询语句。
     * 
     * 
     * @param sql
     * @return
     */
    public PageSqlEntity pageSelect(SelectEntity sql, int page, int pageSize);

    /**
     * 调整Map里面参数的顺序，使得符合对象中参数的排列顺序
     * 
     * @param cls
     * @param map
     * @return
     */
    public List<Map<String, Object>> prepareInsertMap(TableInfo tableInfo, List<Map<String, Object>> list);

    /**
     * 生成用于preparedStatement的参数，Map中字段的顺序与类中字段的排列顺序相同
     * 
     * @param tableInfo
     * @param list
     * @return 
     */
    public List<Map<String, Object>> prepareInsertObj(TableInfo tableInfo, List<Object> list);

    // =============== 以下接口从UORM拷贝org.uorm.dao.dialect.Dialect===============
    /**
     * is support offset feature
     * 
     * @return
     */
    public boolean supportsOffset();

    /**
     * 生成带分页条件的语句
     * 
     * @param query
     * @param offset
     * @param limit
     * @return
     */
    public String getLimitString(String query, int offset, int limit);

    /**
     * 生成带计数条件的语句
     * 
     * @param query
     * @return
     */
    public String getCountSql(String query);

    /**
     * GUID 语句
     * 
     * @return
     */
    public String getSelectGUIDString();

    /**
     * get Sequence Next Value sql
     * @param tablename
     * @return
     */
    public String getSequenceNextValString(String tableName);

}
