/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：     20111220 增加 load(Class<T>, String,int,SqlValue...)接口
					 增加 find(String sql,SqlValue... sqlValues)接口
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import org.dom4j.Document;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL执行器接口
 * @author 李小敏
 */
public interface ISqlExecutor {
	/**
	 * 数据库连接字串
	 * 
	 * @return
	 */
	ConnectionInfo getConnectionInfo();

	void setConnectionInfo(ConnectionInfo value);

	/**
	 * 事务是否执行中
	 * 
	 * @return
	 */
	boolean getIsTransing();

	//void setIsTransing(boolean value);

	/**
	 * Sql解析器
	 * 
	 * @return
	 */
	ISqlParser getSqlParser();

	void setSqlParser(ISqlParser value);

	/**
	 * 加载数据列表
	 * 
	 * @param <T>
	 * @param type
	 *            基于IValueSet的实体对象
	 * @param sqlQuery            
	 * @return
	 * @throws SQLException
	 */
	<T extends IValueSet> List<T> load(Class<T> type, SqlQuery sqlQuery) throws SQLException;
	

	/**加载数据列表
	 * @param <T>
	 * @param type
	 * @param sql
	 * @param requestRows
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	<T extends IValueSet> List<T> load(Class<T> type, String sql,int requestRows,SqlValue... sqlValues)
	throws SQLException;
	
	<T extends IValueSet> List<T> load(Class<T> type, String sql,SqlValue... sqlValues)
	throws SQLException;
	/**
	 * 加载数据列表的第一列，返回第一列的数组
	 * 
	 * @param sql
	 * @param ignoreEmpty
	 * @return
	 * @throws SQLException
	 */
	List<String> load(String sql, boolean ignoreEmpty) throws SQLException;

	/**
	 * 取得指定记录行
	 * 
	 * @param <T>
	 * @param type
	 * @param sql
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	<T extends IValueSet> T find(Class<T> type, String sql,SqlValue... sqlValues) throws SQLException;
	
	/**取得指定记录行,返回第一行记录的数组，无记录为null
	 * @param sql
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	String[] find(String sql,SqlValue... sqlValues) throws SQLException;
	/**
	 * 填充数据列表
	 * 
	 * @param sql
	 * @param rootName
	 * @param itemName
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	Document fill(String sql, String rootName, String itemName,SqlValue... sqlValues)
			throws SQLException;

	/**填充数据列表
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	Document fill(String sql)throws SQLException;
	/**
	 * 填充数据列表
	 * 
	 * @param sqlQuery
	 * @return
	 * @throws SQLException
	 */
	Document fill(SqlQuery sqlQuery) throws SQLException;

	/**
	 * 填充数据列表
	 * 
	 * @param sqlQuery
	 * @return 返回JSON格式
	 * @throws SQLException
	 */
	String load(SqlQuery sqlQuery) throws SQLException;
	
	/**
	 * 执行SQL，返回影响的记录数
	 * 
	 * @param command
	 * @return
	 * @throws SQLException
	 */	
	int executeNonQuery(DbCommand command) throws SQLException;
	int executeNonQuery(String sql,SqlValue... sqlValues) throws SQLException;
	
	/**执行SQL,返回第一行第一列数据
	 * @param sql
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	Object executeScalar(String sql,SqlValue... sqlValues) throws SQLException;

	/**
	 * 执行SQL,返回ResultSet
	 * 
	 * @param sql
	 * @param sqlValues
	 * @return
	 * @throws SQLException
	 */
	ResultSet executeReader(String sql,SqlValue... sqlValues) throws SQLException;

	/**
	 * 取得数据表新Id
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	int getNewId(String tableName) throws Exception;
	
	/**更新数据表新Id
	 * @param tableName
	 * @param newId	 
	 * @throws Exception
	 */
	void updateNewId(String tableName,int newId) throws Exception;
	/**获取数据结构
	 * @return
	 * @throws Exception
	 */
	Document getSchema() throws Exception;
	
	/**获取表的字段列表
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	Document getTableColumns(String tableName) throws Exception;
	/**
	 * 取得缓存参数
	 * 
	 * @param key
	 * @return
	 */
	SqlField[] getCachedParameters(String key);

	/**
	 * 缓存参数
	 * 
	 * @param key
	 * @param val
	 */
	void cacheParameters(String key, SqlField[] val);
	
	/**
	 * 打开连接
	 */
	void openConnection();

	/**
	 * 关闭连接
	 */
	void closeConnection();

	/**
	 * 开始一个事务
	 */
	void transBegin();

	/**
	 * 事务提交
	 * 
	 * @throws Exception
	 */
	void transCommit() throws Exception;

	/**
	 * 事务回滚
	 */
	void transRollback();
}