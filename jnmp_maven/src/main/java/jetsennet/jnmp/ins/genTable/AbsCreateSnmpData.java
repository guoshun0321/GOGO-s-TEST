/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins.genTable;

import jetsennet.jbmp.util.ConfigUtil;

/**
 * @author Guo
 */
public abstract class AbsCreateSnmpData
{

    /**
     * @param tableName 表名
     * @return 结果
     */
    public abstract boolean checkExist(String tableName);

    /**
     * @param tableName 表名
     * @return 结果
     */
    public abstract boolean createTable(String tableName);

    /**
     * @return 结果
     */
    public static AbsCreateSnmpData getInstance()
    {
        String dbDriver = ConfigUtil.getDriver();
        dbDriver = dbDriver == null ? "" : dbDriver.toUpperCase();
        if (dbDriver.indexOf("SQLSERVERDRIVER") >= 0)
        {
            return new SqlServerSnmpData();
        }
        else if (dbDriver.indexOf("DB2DRIVER") >= 0)
        {
            throw new UnsupportedOperationException("暂不支持DB2");
        }
        else if (dbDriver.indexOf("ORACLEDRIVER") >= 0)
        {
            return new OracleSnmpData();
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
