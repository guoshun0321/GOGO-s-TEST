package jetsennet.orm.session;

import jetsennet.orm.executor.Executors;
import jetsennet.orm.executor.IExecutor;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.orm.util.UncheckedOrmException;

public class SessionHelper
{

    /**
     * session工厂
     */
    protected final SqlSessionFactory factory;
    /**
     * 连接和事务管理
     */
    protected final ITransactionManager transaction;
    /**
     * 执行器
     */
    protected IExecutor exec;
    /**
     * sql转换
     */
    protected final ITransform2Sql transform;
    /**
     * 表信息
     */
    protected final TableInfoMgr tableInfoMgr;

    protected SessionHelper(ITransactionManager transaction, SqlSessionFactory factory)
    {
        this.transaction = transaction;
        this.factory = factory;
        this.transform = this.factory.getTransform();
        this.tableInfoMgr = this.factory.getTableInfoMgr();
        this.exec = Executors.getSimpleExecutor();
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
     * 获取第一个主键
     * 
     * @param tableName
     * @return
     */
    public FieldInfo getFirstkey(String tableName)
    {
        FieldInfo retval = null;
        TableInfo tableInfo = getTableInfo(tableName);
        if (tableInfo != null)
        {
            retval = tableInfo.getKey();
        }
        else
        {
            throw new UncheckedOrmException("表未注册：" + tableName);
        }
        return retval;
    }

    /**
     * 获取ISql转换器
     * 
     * @return
     */
    public ITransform2Sql getTransform()
    {
        return this.transform;
    }

    /**
     * 转换ISql
     * 
     * @param sql
     * @return
     */
    public String trans(ISql sql)
    {
        return transform.trans(sql);
    }

    public String getCountSql(String sql)
    {
        return transform.getCountSql(sql);
    }

}
