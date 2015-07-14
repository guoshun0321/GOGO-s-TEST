package jetsennet.orm.configuration;

import jetsennet.orm.util.UncheckedOrmException;

/**
 * 数据库类型
 * 
 * @author 郭祥
 */
public enum DatabaseType
{

    SQL_SERVER, ORACLE, MYSQL, DB2, SYBASE, DEFAULT;

    /**
     * 确定数据库类型
     * 
     * @param driver 数据库驱动
     * @return
     */
    public static DatabaseType ensureDBType(String driver)
    {
        if (driver == null)
        {
            throw new UncheckedOrmException("传入数据库驱动为NULL。");
        }
        driver = driver.toUpperCase();
        DatabaseType retval = DEFAULT;
        if (driver.indexOf("SQLSERVERDRIVER") >= 0)
        {
            retval = SQL_SERVER;
        }
        else if (driver.indexOf("ORACLEDRIVER") >= 0)
        {
            retval = ORACLE;
        }
        else if (driver.indexOf("MYSQL") >= 0)
        {
            retval = MYSQL;
        }
        else if (driver.indexOf("DB2DRIVER") >= 0)
        {
            retval = DB2;
        }
        else if (driver.indexOf("ASEOLEDBPROVIDER") >= 0)
        {
            retval = SYBASE;
        }
        return retval;
    }

}
