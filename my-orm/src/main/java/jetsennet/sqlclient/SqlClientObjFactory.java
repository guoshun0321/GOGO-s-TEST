/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
 ************************************************************************/
package jetsennet.sqlclient;

/**
 * SQL Client 对象工厂
 * @author 李小敏
 */
public class SqlClientObjFactory
{

	/**
	 * 创建Sql执行者
	 * 
	 * @param cInfo
	 * @return Sql执行者
	 */
	public static ISqlExecutor createSqlExecutor(ConnectionInfo cInfo)
	{

		ISqlExecutor ext = null;
		String dbDriver = cInfo.getDbDriver() == null ? "" : cInfo.getDbDriver().toUpperCase();
		if (dbDriver.indexOf("ORACLEDRIVER") >= 0)
		{
			ext = new OracleExecutor(cInfo);
			ext.setSqlParser(new OracleParser());
		}
		else if (dbDriver.indexOf("DB2DRIVER") >= 0)
		{
			ext = new DB2Executor(cInfo);
			ext.setSqlParser(new Db2Parser());			
		}
		else
		{
			ext = new SqlExecutor(cInfo);
			ext.setSqlParser(createSqlParser(dbDriver));
		}
		return ext;
	}

	/**
	 * 创建Sql解析器
	 * 
	 * @param dbDriver 驱动名称
	 * @return Sql解析器
	 */
	public static ISqlParser createSqlParser(String dbDriver)
	{
		dbDriver = dbDriver == null ? "" : dbDriver.toUpperCase();

		if (dbDriver.indexOf("SQLSERVERDRIVER") >= 0)
		{
			return new SqlServerParser();
		}
		else if (dbDriver.indexOf("DB2DRIVER") >= 0)
		{
			return new Db2Parser();
		}
		else if (dbDriver.indexOf("ORACLEDRIVER") >= 0)
		{
			return new OracleParser();
		}
		else if (dbDriver.indexOf("ASEOLEDBPROVIDER") >= 0)
		{
			return new SybaseParser();
		}
		else if (dbDriver.indexOf("MYSQL") >= 0)
		{
			return new MySqlParser();
		}
		else
		{
			return new SqlParser();
		}
	}
}