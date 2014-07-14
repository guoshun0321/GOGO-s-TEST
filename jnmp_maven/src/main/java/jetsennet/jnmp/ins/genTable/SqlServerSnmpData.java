/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins.genTable;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author Guo
 */
public class SqlServerSnmpData extends AbsCreateSnmpData
{

    private static final Logger logger = Logger.getLogger(SqlServerSnmpData.class);
    private ISqlExecutor exec;

    /**
     * 构造方法
     */
    public SqlServerSnmpData()
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

    @Override
    public boolean createTable(String tableName)
    {
        boolean result = false;
        String delSql = "drop table " + tableName;
        String createSql = "CREATE TABLE " + tableName + "(" + "OBJATTR_ID	Int NOT NULL," + "COLL_TIME	BigInt NOT NULL," + "NUM_VALUE	Int)";
        try
        {
            // exec.transBegin();
            if (this.checkExist(tableName))
            {
                exec.executeNonQuery(delSql);
            }
            exec.executeNonQuery(createSql);
            result = true;
            // exec.transCommit();
        }
        catch (Exception ex)
        {
            // exec.transRollback();
            result = false;
            logger.error(ex);
        }
        return result;
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        SqlServerSnmpData data = new SqlServerSnmpData();
        // data.checkExist("NMP_SNMPDATA");
        data.createTable("xxx");
    }
}
