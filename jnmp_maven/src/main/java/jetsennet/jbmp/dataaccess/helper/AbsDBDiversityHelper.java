/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.dataaccess.helper;

import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.sqlclient.DbConfig;

/**
 * @author Guo
 */
public abstract class AbsDBDiversityHelper
{

    /**
     * 判断表是否存在
     * @param tableName 表名
     * @return 结果
     */
    public abstract boolean checkExist(String tableName);

    /**
     * @return 结果
     */
    public static AbsDBDiversityHelper getInstance()
    {
        String dbDriver = DbConfig.DEFAULT_CONN.getDbDriver();
        dbDriver = dbDriver == null ? "" : dbDriver.toUpperCase();
        if (dbDriver.indexOf("SQLSERVERDRIVER") >= 0)
        {
            return new SqlServerDiversityHelper();
        }
        else if (dbDriver.indexOf("DB2DRIVER") >= 0)
        {
            throw new UnsupportedOperationException("暂不支持DB2");
        }
        else if (dbDriver.indexOf("ORACLEDRIVER") >= 0)
        {
            return new OracleDiversityHelper();
        }
        else if (dbDriver.indexOf("ASEOLEDBPROVIDER") >= 0)
        {
            throw new UnsupportedOperationException("暂不支持ASEOLEDBPROVIDER");
        }
        else
        {
            throw new UnsupportedOperationException("暂不支持");
        }
    }
}
