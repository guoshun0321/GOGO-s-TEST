package jetsennet.orm.session;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jetsennet.orm.executor.Executors;
import jetsennet.orm.executor.IExecutor;
import jetsennet.orm.executor.resultset.IResultSetHandle;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.ITransform2Sql;

public class SessionBase extends SessionHelper
{

    protected SessionBase(ITransactionManager transaction, SqlSessionFactory factory)
    {
        super(transaction, factory);
    }

    /**
     * 获取连接
     * 
     * @return
     */
    public Connection openConnection()
    {
        return transaction.openConnection();
    }

    /**
     * 关闭连接
     */
    public void closeConnection()
    {
        transaction.closeConnection();
    }

    /**
     * 连接是否打开
     * @return
     */
    public boolean isConnectionOpen()
    {
        return transaction.isConnectionOpen();
    }

    /**
     * 开启事务
     */
    public boolean transBegin()
    {
        return transaction.transBegin();
    }

    /**
     * 提交事务
     */
    public void transCommit(boolean isSelf)
    {
        transaction.transCommit(isSelf);
    }

    /**
     * 事务回滚
     */
    public void transRollback(boolean isSelf)
    {
        transaction.transRollback(isSelf);
    }

    public boolean isTrans()
    {
        return transaction.isTrans();
    }

    public int update(String sql)
    {
        return exec.update(transaction, sql);
    }

    public int[] update(String[] sqls)
    {
        return exec.update(transaction, sqls);
    }

    public int[] update(String sql, String tableName, Map<String, Object>[] objValMaps)
    {
        List<Map<String, Object>> lst = (List<Map<String, Object>>) Arrays.asList(objValMaps);
        return exec.update(transaction, sql, factory.getTableInfo(tableName), lst);
    }

    public int[] update(String sql, String tableName, List<Map<String, Object>> objValMaps)
    {
        return exec.update(transaction, sql, factory.getTableInfo(tableName), objValMaps);
    }

    public int update(String sql, Object[] values)
    {
        return exec.update(transaction, sql, values);
    }

    public <T> T query(String sql, IResultSetHandle<T> handle)
    {
        return exec.query(transaction, sql, handle);
    }

    public <T> T query(String sql, Object[] values, IResultSetHandle<T> handle)
    {
        return exec.query(transaction, sql, values, handle);
    }

    public boolean executor(String sql, Object[] values)
    {
        return exec.executor(transaction, sql, values);
    }

    /**
     * 获取表信息
     * 
     * @param tableName
     * @return
     */
    public TableInfo getTableInfo(String tableName)
    {
        return this.tableInfoMgr.getTableInfo(tableName);
    }

    /**
     * 获取表信息
     * 
     * @param cls
     * @return
     */
    public TableInfo getTableInfo(Class<?> cls)
    {
        return this.tableInfoMgr.ensureTableInfo(cls);
    }

    /**
     * 是否处于调试模式
     * 
     * @return
     */
    public boolean isDebug()
    {
        return this.factory.isDebug();
    }

}
