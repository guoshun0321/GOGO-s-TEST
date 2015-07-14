/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.dataaccess.helper;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author Guo
 */
public class SqlServerDiversityHelper extends AbsDBDiversityHelper
{

    private static final Logger logger = Logger.getLogger(SqlServerDiversityHelper.class);
    private ISqlExecutor exec;

    /**
     * 构造方法
     */
    public SqlServerDiversityHelper()
    {
        exec = SqlExecutorFacotry.getSqlExecutor();
    }

    @Override
    public boolean checkExist(String tableName)
    {
        boolean result = false;
        try
        {
            String sql = "SELECT OBJECTPROPERTY(object_id('dbo." + tableName + "'),'IsUserTable')";
            Object obj = exec.executeScalar(sql);
            if (obj != null && obj instanceof Integer && ((Integer) obj) == 1)
            {
                result = true;
            }
        }
        catch (SQLException ex)
        {
            logger.error(ex);
        }
        return result;
    }
}
