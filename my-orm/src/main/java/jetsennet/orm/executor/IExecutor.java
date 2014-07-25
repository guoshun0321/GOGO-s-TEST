package jetsennet.orm.executor;

import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.resultset.IResultSetHandle;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transaction.ITransactionManager;

public interface IExecutor
{

    public static final int DEFAULT_RETURN = -1;

    /**
     * 执行单条sql语句
     * @param conn
     * @param sql
     * @return
     */
    public int update(ITransactionManager trans, String sql);

    /**
     * 批量执行sql语句
     * @param conn
     * @param sql
     * @return
     */
    public int[] update(ITransactionManager trans, String[] sql);
    
    /**
     * 采用prepared的方式执行sql语句
     * @param conn
     * @param sql
     * @param params
     * @return
     */
    public int[] update(ITransactionManager trans, String sql, TableInfo tableInfo, List<Map<String, Object>> objValMaps);
    
    public int update(ITransactionManager trans, String sql, Object[] values);

    /**
     * 执行查询语句
     * @param conn
     * @param sql
     * @param handle
     * @return
     */
    public <T> T query(ITransactionManager trans, String sql, IResultSetHandle<T> handle);

    /**
     * 采用prepared的方式执行查询语句
     * @param conn
     * @param sql
     * @param handle
     * @return
     */
    public <T> T query(ITransactionManager trans, String sql, TableInfo tableInfo, Map<String, Object> objValMap, IResultSetHandle<T> handle);
    
    /**
     * 采用prepared的方式执行查询语句
     * @param trans
     * @param sql
     * @param values
     * @param handle
     * @return
     */
    public <T> T query(ITransactionManager trans, String sql, Object[] values, IResultSetHandle<T> handle);
    
    public boolean executor(ITransactionManager trans, String sql, Object[] values);

}
