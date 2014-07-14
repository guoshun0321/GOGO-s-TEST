package jetsennet.jbmp.dataaccess.base;

import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;

import org.apache.log4j.Logger;

/**
 * 数据库连接的工厂类
 * 
 * @author 郭祥
 */
public class SqlExecutorFacotry
{

    private static final Logger logger = Logger.getLogger(SqlExecutorFacotry.class);

    /**
     * 
     */
    public static void unbindSqlExecutor()
    {
        SqlClientObjFactory.cancelSqlExecutor();
    }

    /**
     * @return 结果
     */
    public static ISqlExecutor getSqlExecutor()
    {
        ISqlExecutor retval = SqlClientObjFactory.createSqlExecutor(DbConfig.DEFAULT_CONN);
        return retval;
    }
}
