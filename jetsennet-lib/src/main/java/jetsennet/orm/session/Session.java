package jetsennet.orm.session;

import java.sql.Connection;

import jetsennet.orm.executor.Executors;
import jetsennet.orm.executor.IExecutor;
import jetsennet.orm.executor.resultSet.IResultSetHandle;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.ITransform2Page;
import jetsennet.orm.transform.ITransform2Sql;

public class Session
{

    /**
     * 连接和事务管理
     */
    private final ITransactionManager trans;
    /**
     * page转换
     */
    private final ITransform2Page pageTrans;
    /**
     * sql转换
     */
    private final ITransform2Sql sqlTrans;

    public Session(ITransactionManager trans, ITransform2Page pageTrans, ITransform2Sql sqlTrans)
    {
        this.trans = trans;
        this.pageTrans = pageTrans;
        this.sqlTrans = sqlTrans;
    }

    public void insert(String sql)
    {
        Connection conn = trans.openConnection();
        IExecutor exec = Executors.getReusedExecutor();
        exec.update(conn, sql);
    }

    public void insert(String[] sqls)
    {
        Connection conn = trans.openConnection();
        IExecutor exec = Executors.getReusedExecutor();
        exec.update(conn, sqls);
    }

}
