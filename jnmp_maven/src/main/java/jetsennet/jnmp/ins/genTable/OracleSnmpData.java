/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins.genTable;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author Guo
 */
public class OracleSnmpData extends AbsCreateSnmpData
{

    private static final Logger logger = Logger.getLogger(OracleSnmpData.class);
    private ISqlExecutor exec;

    /**
     * 实例化
     */
    public OracleSnmpData()
    {
        exec = SqlExecutorFacotry.getSqlExecutor();
    }

    @Override
    public boolean checkExist(String tableName)
    {
        boolean result = false;
        try
        {
            String sql = "SELECT COUNT(Tname) FROM TAB WHERE Tname = UPPER('" + tableName + "')";
            Object obj = exec.executeScalar(sql);
            if (obj != null && obj instanceof BigDecimal && ((BigDecimal) obj).intValue() == 1)
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
        String createSql =
            "CREATE TABLE " + tableName + "(" + "OBJATTR_ID	INTEGER NOT NULL," + "COLL_TIME	Decimal(15) NOT NULL," + "NUM_VALUE	INTEGER"
                + ") TABLESPACE TBS_JSNET";
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
        OracleSnmpData data = new OracleSnmpData();
        // data.checkExist("NMP_SNMPDATA");
        data.createTable("XXX");
    }
}
