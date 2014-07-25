/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jetsennet.util.*;
import jetsennet.logger.LogManager;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * SQL执行类
 * @author 李小敏
 */
public class SqlExecutor implements ISqlExecutor {

	private static jetsennet.logger.ILog logger = LogManager.getLogger("jetsennet.sqlclient.SqlExecutor");
	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public SqlExecutor(ConnectionInfo cInfo) {
		this.setConnectionInfo(cInfo);
	}

	protected void finalize() {
		if (this.getIsTransing()) {
			this.transRollback();
		}
		else if (this.getIsConnectionOpen()) {
			this.closeConnection();
		}
	}

	private ConnectionInfo connectionInfo;

	/**数据库连接字串
	*/
	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo value) {
		this.connectionInfo = value;
	}

	private boolean isTransing = false;

	/**事务是否执行中
	 */
	public boolean getIsTransing() {
		return this.isTransing;
	}
	
	void setIsTransing(boolean value) {
		this.isTransing = value;
	}
	
	private boolean isConnectionOpen = false;

	/**事务是否执行中
	 */
	public boolean getIsConnectionOpen() {
		return this.isTransing || this.isConnectionOpen;
	}

	public void setIsConnectionOpen(boolean value) {
		this.isConnectionOpen = value;
	}

	private ISqlParser sqlParser;

	/**
	 * SQL解析器	
	 */
	public ISqlParser getSqlParser() {
		return this.sqlParser;
	}

	public void setSqlParser(ISqlParser value) {
		this.sqlParser = value;
	}
	
	/**
	 * ID生成类
	 */
	private ITableKeyGenerator keyGenerator = new SequenceKeyGenerator();
	
	public <T extends IValueSet> List<T> load(Class<T> type, String sql,SqlValue... sqlValues) throws SQLException {
		return load(type,sql,0,sqlValues);
	}
	
	/**
	 * 加载数据列表
	 * @param sql SQL语句
	 */
	public <T extends IValueSet> List<T> load(Class<T> type, String sql,int requestRows,SqlValue... sqlValues) throws SQLException {
		
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		return load(type,sqlCommand,0,requestRows);
	}
	
	public <T extends IValueSet> List<T> load(Class<T> type,SqlQuery sqlQuery) throws SQLException
	{
		if (sqlQuery.isPageResult) {
			
			String fromTable = sqlParser.parseQueryTable(sqlQuery.queryTable);
			
			// 对于需要分组的，应分别处理
			if (sqlQuery.groupFields != null && sqlQuery.groupFields.length() > 0) {
				
				// 分页
				String commandCount = "SELECT COUNT(0) FROM (" + this.getSqlParser().getSelectCommandString(
						fromTable, 
						0, 
						sqlQuery.isDistinct, 
						"count(0) as RECORD_COUNT", 
						sqlQuery.groupFields, 
						null, 
						sqlQuery.conditions) + ") A";
				
				int recordCount = Integer.parseInt(String.valueOf(this.executeScalar(commandCount)));
				
				if (sqlQuery.pageInfo == null)
					sqlQuery.pageInfo = new PageInfo();
				
				sqlQuery.pageInfo.setRowCount(recordCount);
				
				if ((recordCount % sqlQuery.pageInfo.getPageSize()) > 0)
				{
					sqlQuery.pageInfo.setPageCount(recordCount / sqlQuery.pageInfo.getPageSize() + 1);
				}
				else
				{
					sqlQuery.pageInfo.setPageCount(recordCount / sqlQuery.pageInfo.getPageSize());
				}

				// 记录的开始与结束索引
				int intStartIndex = sqlQuery.pageInfo.getCurrentPage() <= 1 ? 0 : (sqlQuery.pageInfo.getCurrentPage() - 1) * sqlQuery.pageInfo.getPageSize();
				int intEndIndex = intStartIndex + sqlQuery.pageInfo.getPageSize();
				intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;
				String cmd = null;

				if (recordCount == 0) {
					return new ArrayList<T>();
				}
				else {
					cmd = this.getSqlParser().getSelectCommandString(
							fromTable, 
							intEndIndex >= recordCount ? 0 : intEndIndex, 
							sqlQuery.isDistinct, 
							sqlQuery.resultFields, 
							sqlQuery.groupFields, 
							sqlQuery.orderString, 
							sqlQuery.conditions);
					
					return load(type,cmd,intStartIndex,intEndIndex);
				}
			}
			else {
				return load(type,
						sqlQuery.pageInfo, 
						sqlQuery.keyId, 
						sqlQuery.resultFields, 
						fromTable, 
						sqlQuery.orderString, 
						sqlQuery.conditions);
			}
		}
		else
			return load(type,sqlParser.getSelectCommandString(sqlQuery),0);
	}
	
	/**加载数据列表的第一列，返回第一列的集合
	 * @see jetsennet.sqlclient.ISqlExecutor#load(java.lang.String, boolean)
	 */
	public List<String> load(String sql, boolean ignoreEmpty) throws SQLException {
		List<String> items = new ArrayList<String>();
		ResultSet ret = null;
		Connection connection = null;
		
		if (this.getIsConnectionOpen()) 
		{
			ret = this.executeReader(sql);
		}
		else 
		{
			logger.debug(sql);
			connection = SqlHelper.getConnection(this.getConnectionInfo());			
			ret = connection.createStatement().executeQuery(sql);
		}
		
		try 
		{
			ResultSetMetaData rsmd = ret.getMetaData();
			while (ret.next()) 
			{
				String sTempValue = null;
				if (rsmd.getColumnType(1) == Types.TIMESTAMP) 
				{
					Timestamp timeDate = ret.getTimestamp(1);
					if (timeDate != null) {
						DateFormat df = new SimpleDateFormat(DATE_FORMAT);
						sTempValue = df.format(timeDate);
					}
				}
				else 
				{
					try 
					{
						sTempValue = ret.getString(1);
					}
					catch (Exception textnull) {
					}
				}

				if (!ignoreEmpty || !StringUtil.isNullOrEmpty(sTempValue))
					items.add(sTempValue);
			}
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				connection.close();
		}
		return items;
	}
	
	public String load(SqlQuery queryInfo) throws SQLException 
	{
		if (queryInfo.isPageResult) 
		{
			String fromTable = this.getSqlParser().parseQueryTable(queryInfo.queryTable);
			
			// 对于需要分组的，应分别处理
			if (queryInfo.groupFields != null && queryInfo.groupFields.length() > 0) 
			{
				// 分页
				String commandCount = "SELECT COUNT(0) FROM (" + this.getSqlParser().getSelectCommandString(
						fromTable, 
						0, 
						queryInfo.isDistinct, 
						"count(0) as RECORD_COUNT", 
						queryInfo.groupFields, 
						null, 
						queryInfo.conditions) + ") A";
				int recordCount = Integer.parseInt(String.valueOf(this.executeScalar(commandCount)));
				
				if (queryInfo.pageInfo == null)
					queryInfo.pageInfo = new PageInfo();
				
				queryInfo.pageInfo.setRowCount(recordCount);
				
				if ((recordCount % queryInfo.pageInfo.getPageSize()) > 0)
				{
					queryInfo.pageInfo.setPageCount(recordCount / queryInfo.pageInfo.getPageSize() + 1);
				}
				else
				{
					queryInfo.pageInfo.setPageCount(recordCount / queryInfo.pageInfo.getPageSize());
				}

				// 开始与结束记录的索引
				int intStartIndex = queryInfo.pageInfo.getCurrentPage() <= 1 ? 0 : (queryInfo.pageInfo.getCurrentPage() - 1) * queryInfo.pageInfo.getPageSize();
				int intEndIndex = intStartIndex + queryInfo.pageInfo.getPageSize();
				intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;
				String cmd = null;
				StringBuilder result = new StringBuilder();
				result.append("{");

				if (recordCount>0) 
				{
					cmd = this.getSqlParser().getSelectCommandString(
							fromTable, 
							intEndIndex >= recordCount ? 0 : intEndIndex, 
							queryInfo.isDistinct, 
							queryInfo.resultFields, 
							queryInfo.groupFields, 
							queryInfo.orderString, 
							queryInfo.conditions);

					if (getIsConnectionOpen())
						result.append(SqlHelper.executeJsonString(
								this.statement, 
								cmd, 								
								queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize()));
					else
						result.append(SqlHelper.executeJsonString(
								this.getConnectionInfo(), 
								cmd, 								
								queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize()));
				}

				if(result.length()>1)
				{
					result.append(",");
				}
				
				result.append("\"totalCount\":"+recordCount);				
				return result.toString();
			}
			else 
			{
				return this.load(queryInfo.pageInfo, 						
						queryInfo.recordName, 
						queryInfo.keyId, 
						queryInfo.resultFields, 
						fromTable, 
						queryInfo.orderString, 
						queryInfo.conditions);
			}
		}
		else
		{		
			StringBuilder result = new StringBuilder();
			result.append("{");
			result.append(this.load(this.getSqlParser().getSelectCommandString(queryInfo), 					
					queryInfo.recordName,0,0));
			result.append("}");
			return result.toString();
		}
	}	
	
	@SuppressWarnings("unchecked")
	public String load(PageInfo pInfo,String itemName, String keyID, String fields, String fromTable, String order, SqlCondition... conditions) throws SQLException {
		if (pInfo == null)
			pInfo = new PageInfo();

		String strWhere = this.getSqlParser().parseSqlCondition(conditions);
		String strOrder = order == null ? "" : order;
		String strFileds = StringUtil.isNullOrEmpty(fields) ? "*" : fields;
		String cmd = null;
		StringBuilder result = new StringBuilder();
		result.append("{");

		int recordCount = Integer.parseInt(this.executeScalar(
				this.getRecordCountCommandString(
				keyID, fromTable, strWhere)).toString());
		pInfo.setRowCount(recordCount);
		
		if ((recordCount % pInfo.getPageSize()) > 0)
		{
			pInfo.setPageCount(recordCount / pInfo.getPageSize() + 1);
		}
		else
		{
			pInfo.setPageCount(recordCount / pInfo.getPageSize());
		}

		int intStartIndex = pInfo.getCurrentPage() <= 1 ? 0 : (pInfo.getCurrentPage() - 1) * pInfo.getPageSize();
		int intEndIndex = intStartIndex + pInfo.getPageSize();
		intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;

		if (StringUtil.isNullOrEmpty(keyID) 
				|| keyID.indexOf(",") > 0 
				|| keyID.indexOf(" ") > 0 
				|| pInfo.getCurrentPage() <= 1) 
		{
			cmd = this.getSqlParser().getSelectCommandString(
					fromTable, 
					intEndIndex >= recordCount ? 0 : intEndIndex, 
					false, fields, null, strOrder, conditions);

			if (this.getIsConnectionOpen())
			{
				result.append(SqlHelper.executeJsonString(
						this.statement, 
						cmd, itemName, intStartIndex, pInfo.getPageSize()));
			}
			else
			{
				result.append(SqlHelper.executeJsonString(
						this.getConnectionInfo(), 
						cmd, itemName, intStartIndex, pInfo.getPageSize()));
			}
		}
		else 
		{
			String[] recordPage = getPageRecordIds(intStartIndex,intEndIndex,recordCount,
					fromTable,keyID,strOrder,pInfo,conditions);
			String strIds = recordPage[1];
			
			if (strIds.length() > 0) 
			{
				cmd = String.format(
						"SELECT %s FROM %s WHERE %s %s ", 
						strFileds, 
						fromTable, 
						this.getSqlParser().parseSqlCondition(
								null, 
								false, 
								new SqlCondition(keyID, strIds, SqlLogicType.And, SqlRelationType.In, "1".equals(recordPage[0]) ? SqlParamType.String : SqlParamType.Numeric)), strOrder);
			}
			else 
			{
				cmd = String.format("SELECT %s FROM %s WHERE 1=2 %s ", strFileds, fromTable, strOrder);
			}

			result.append(this.load(cmd, itemName,0,0));
		}

		if(result.length()>1)
		{
			result.append(",");
		}
		
		result.append("\"totalCount\":"+recordCount);
		result.append("}");
		
		return result.toString();
	}
		
	/**取得指定记录行
	 * @param sql SQL语句	
	 */
	public <T extends IValueSet> T find(Class<T> type, String sql,SqlValue... sqlValues) throws SQLException {
		
		T item = null;
		ResultSet ret = null;
		Connection connection = null;
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		
		if (this.getIsConnectionOpen())
		{
			ret = this.executeReader(sqlCommand);
		}
		else 
		{
			logger.debug(sqlCommand);
			connection = SqlHelper.getConnection(this.getConnectionInfo());			
			ret = connection.createStatement().executeQuery(sqlCommand);
		}
		
		try 
		{
			ResultSetMetaData rsmd = ret.getMetaData();
			if (ret.next()) {
				
				try {
					item = type.newInstance();
				}
				catch (Exception ex) {
					throw new SQLException("无法生成对象实例");
				}
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++) 
				{
					String sTempValue = null;
					if (rsmd.getColumnType(i) == Types.TIMESTAMP) 
					{
						Timestamp timeDate = ret.getTimestamp(i);
						if (timeDate != null) 
						{
							DateFormat df = new SimpleDateFormat(DATE_FORMAT);
							sTempValue = df.format(timeDate);
						}
					}
					else 
					{
						try {
							sTempValue = ret.getString(i);
						}
						catch (Exception textnull) {
						}
					}

					try {
						item.setValue(rsmd.getColumnName(i), sTempValue);
					}
					catch (Exception textnull) {
					}
				}
			}
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				connection.close();
		}
		return item;
	}
	
	/**取得指定记录行
	 * @param sql SQL语句
	 * 
	 */
	public String[] find(String sql,SqlValue... sqlValues) throws SQLException {
		
		String[] retArr = null;
		ResultSet ret = null;
		Connection innerConnection = null;
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		
		if (this.getIsConnectionOpen()) {
			ret = this.executeReader(sqlCommand);
		}
		else {
			logger.debug(sqlCommand);
			innerConnection = SqlHelper.getConnection(this.getConnectionInfo());
			ret = innerConnection.createStatement().executeQuery(sqlCommand);
		}
		try 
		{
			ResultSetMetaData rsmd = ret.getMetaData();
			
			if (ret.next())
			{
				retArr = new String[rsmd.getColumnCount()];
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++) 
				{
					String sTempValue = null;
					
					if (rsmd.getColumnType(i) == Types.TIMESTAMP) 
					{
						Timestamp timeDate = ret.getTimestamp(i);
						if (timeDate != null) {
							DateFormat df = new SimpleDateFormat(DATE_FORMAT);
							sTempValue = df.format(timeDate);
						}
					}
					else 
					{
						try 
						{
							sTempValue = ret.getString(i);
						}
						catch (Exception textnull) {
						}
					}
					
					retArr[i - 1] = sTempValue;
				}
			}
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				innerConnection.close();
		}
		return retArr;
	}

	
	/**填充数据列表	
	* @param sql SQL语句
	*/
	public Document fill(String sql) throws SQLException {
		return fill(sql, "DataSource", "DataTable");
	}

	
	/**填充数据列表
	 * @param pInfo 分页信息
	 * @param rootName DataSet名称
	 * @param itemName 表名称
	 * @param keyID 主键
	 * @param fields 列表字段
	 * @param fromTable 表源
	 * @param order 排序信息
	 * @param p 条件	
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Document fill(PageInfo pInfo, String rootName, String itemName, String keyID, String fields, String fromTable, String order, SqlCondition... p) throws SQLException {
		if (pInfo == null)
			pInfo = new PageInfo();

		String strWhere = this.sqlParser.parseSqlCondition(p);
		String strOrder = order == null ? "" : order;
		String strFileds = StringUtil.isNullOrEmpty(fields) ? "*" : fields;
		String cmd = null;
		Document dsResult = null;

		int recordCount = Integer.parseInt(this.executeScalar(
				this.getRecordCountCommandString(
						keyID, fromTable, strWhere)).toString());
		pInfo.setRowCount(recordCount);
		
		if ((recordCount % pInfo.getPageSize()) > 0)
			pInfo.setPageCount(recordCount / pInfo.getPageSize() + 1);
		else
			pInfo.setPageCount(recordCount / pInfo.getPageSize());

		int intStartIndex = pInfo.getCurrentPage() <= 1 ? 0 : (pInfo.getCurrentPage() - 1) * pInfo.getPageSize();
		int intEndIndex = intStartIndex + pInfo.getPageSize();
		intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;

		if (StringUtil.isNullOrEmpty(keyID) || keyID.indexOf(",") > 0 
				|| keyID.indexOf(" ") > 0 || pInfo.getCurrentPage() <= 1) {
			
			cmd = this.getSqlParser().getSelectCommandString(
					fromTable, 
					intEndIndex >= recordCount ? 0 : intEndIndex, 
					false, 
					fields, 
					null, 
					strOrder, 
					p);

			if (this.getIsConnectionOpen())
			{
				dsResult = SqlHelper.executeXmlDocument(this.statement, 
						cmd, 
						rootName, 
						itemName, 
						intStartIndex, 
						pInfo.getPageSize());
			}
			else
			{
				dsResult = SqlHelper.executeXmlDocument(this.getConnectionInfo(), 
						cmd, 
						rootName, 
						itemName, 
						intStartIndex, 
						pInfo.getPageSize());
			}
		}
		else {
			cmd = this.getSqlParser().getSelectCommandString(
					fromTable, 
					intEndIndex >= recordCount ? 0 : intEndIndex, 
					false, 
					keyID, 
					null, 
					strOrder, 
					p);
			
			dsResult = this.fill(cmd, "DataSource", "Table",intStartIndex,pInfo.getPageSize());
			List nodes = dsResult.getDocument().selectNodes("DataSource/Table");
			String strKey = keyID.indexOf('.') > 0 ? keyID.substring(keyID.lastIndexOf(".") + 1) : keyID;
			StringBuilder sbIds = new StringBuilder(500);		

			boolean bIsString = false;
			if (nodes.size() > 0) {
				String keyType = ((Element) nodes.get(0)).selectSingleNode(strKey).valueOf("@type");
				bIsString = "VARCHAR".equalsIgnoreCase(keyType) || "CHAR".equalsIgnoreCase(keyType);
			}
			
			for (int i = 0; i < nodes.size(); i++) {
				String strValue = ((Node) nodes.get(i)).selectSingleNode(strKey).getText();
				sbIds.append(strValue);
				sbIds.append(",");
			}
			
			if (sbIds.length() > 0) {
				sbIds = sbIds.deleteCharAt(sbIds.length() - 1);
			}
			
			String strIds = sbIds.toString();
			if (strIds.length() > 0) {
				if (bIsString) {
					strIds = strIds.replace("'", "''");
					strIds = strIds.replace(",", "','");
					strIds = "'" + strIds + "'";
				}
				cmd = String.format("SELECT %s FROM %s WHERE %s in (%s) %s ", 
						strFileds, fromTable, keyID, strIds, strOrder);
			}
			else {
				cmd = String.format("SELECT %s FROM %s WHERE 1=2 %s ", 
						strFileds, fromTable, strOrder);
			}

			dsResult = fill(cmd, rootName, itemName);
		}

		dsResult.getRootElement().addAttribute("TotalCount", String.valueOf(recordCount));
		//Element dataElement = ((Element) dsResult.getDocument().selectSingleNode(rootName)).addElement(itemName + "1");
		//dataElement.addElement("TotalCount").addText();

		return dsResult;
	}

	/**填充数据列表	
	 * @param sql SQL语句
	 * @param rootName DataSet名称
	 * @param itemName 表名称	
	 * 
	 */
	public Document fill(String sql, String rootName, String itemName,SqlValue... sqlValues) throws SQLException {		
		
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		return fill(sqlCommand, rootName, itemName, -1, -1);
	}
			
	public Document fill(SqlQuery queryInfo) throws SQLException 
	{
		if (queryInfo.isPageResult) 
		{
			String fromTable = sqlParser.parseQueryTable(queryInfo.queryTable);
			
			// 对于需要分组的，应分别处理
			if (queryInfo.groupFields != null && queryInfo.groupFields.length() > 0) 
			{				
				// 分页
				String commandCount = "SELECT COUNT(0) FROM (" + this.getSqlParser().getSelectCommandString(fromTable, 0, queryInfo.isDistinct, "count(0) as RECORD_COUNT", queryInfo.groupFields, null, queryInfo.conditions) + ") A";
				int recordCount = Integer.parseInt(String.valueOf(this.executeScalar(commandCount)));
				
				if (queryInfo.pageInfo == null)
					queryInfo.pageInfo = new PageInfo();
				
				queryInfo.pageInfo.setRowCount(recordCount);
				
				if ((recordCount % queryInfo.pageInfo.getPageSize()) > 0)
					queryInfo.pageInfo.setPageCount(recordCount / queryInfo.pageInfo.getPageSize() + 1);
				else
					queryInfo.pageInfo.setPageCount(recordCount / queryInfo.pageInfo.getPageSize());

				// 记录的开始与结束索引
				int intStartIndex = queryInfo.pageInfo.getCurrentPage() <= 1 ? 0 : (queryInfo.pageInfo.getCurrentPage() - 1) * queryInfo.pageInfo.getPageSize();
				int intEndIndex = intStartIndex + queryInfo.pageInfo.getPageSize();
				intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;
				String cmd = null;
				Document dsResult = null;

				if (recordCount == 0) {
					dsResult = DocumentHelper.createDocument();
					dsResult.addElement(queryInfo.recordSetName);
				}
				else 
				{
					cmd = this.getSqlParser().getSelectCommandString(
							fromTable, intEndIndex >= recordCount ? 0 : intEndIndex, 
							queryInfo.isDistinct, 
							queryInfo.resultFields, 
							queryInfo.groupFields, 
							queryInfo.orderString, 
							queryInfo.conditions);

					if (getIsConnectionOpen())
					{
						dsResult = SqlHelper.executeXmlDocument(
								this.statement, cmd, 
								queryInfo.recordSetName, queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize());
					}
					else
					{
						dsResult = SqlHelper.executeXmlDocument(
								this.connectionInfo, cmd, 
								queryInfo.recordSetName, queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize());
					}
				}

				dsResult.getRootElement().addAttribute("TotalCount", String.valueOf(recordCount));
				//Element dataElement = ((Element) dsResult.getDocument().selectSingleNode(queryInfo.recordSetName)).addElement(queryInfo.recordName + "1");
				//dataElement.addElement("TotalCount").addText(String.valueOf(recordCount));

				return dsResult;
			}
			else {
				return fill(queryInfo.pageInfo, 
						queryInfo.recordSetName, 
						queryInfo.recordName, 
						queryInfo.keyId, 
						queryInfo.resultFields, 
						fromTable, 
						queryInfo.orderString, 
						queryInfo.conditions);
			}
		}
		else
			return fill(
					sqlParser.getSelectCommandString(queryInfo), 
					queryInfo.recordSetName, 
					queryInfo.recordName);
	}

	
	
	/**执行SQL，无返回结果	
	 * @param sql SQL语句
	 * 
	 */
	public int executeNonQuery(String sql, SqlValue... sqlValues) throws SQLException {
		
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		
		if (this.getIsConnectionOpen()) {
			return SqlHelper.executeNonQuery(this.statement, sqlCommand);
		}
		else {
			return SqlHelper.executeNonQuery(this.getConnectionInfo(), sqlCommand);
		}		
	}

	public int executeNonQuery(DbCommand command) throws SQLException {
		return executeNonQuery(command.toString());
	}

	/**执行SQL,返回第一行第一列数据	
	 * @param sql sql语句
	 * 
	 */
	public Object executeScalar(String sql,SqlValue... sqlValues) throws SQLException {
		
		String sqlCommand = this.getSqlParser().formatCommand(sql,sqlValues);
		
		if (this.getIsConnectionOpen()) {
			return SqlHelper.executeScalar(this.statement, sqlCommand);
		}
		else {
			return SqlHelper.executeScalar(this.getConnectionInfo(), sqlCommand);
		}
	}

	/**
	 * 执行SQL,返回ResultSet	
	 * @param sql 语句
	 */
	public ResultSet executeReader(String sql,SqlValue... sqlValues) throws SQLException {
		
		String sqlCommand = this.getSqlParser().formatCommand(sql,sqlValues);
		
		if (this.getIsConnectionOpen())
			return SqlHelper.executeResultSet(this.statement, sqlCommand);
		else
			throw new SQLException("调用executeReader方法出错，请先调用openConnection或transBegin方法");
	}

	/**
	 * 取得数据表新Id
	 * @param tableName 表名
	 */
	public int getNewId(String tableName) throws Exception {
		
		return Integer.parseInt(keyGenerator.generateKey(this, tableName));
	}

	public void updateNewId(String tableName, int newId) throws Exception {
		
		keyGenerator.updateKey(this, tableName, newId+"");		
	}
	
	public Document getSchema() throws Exception
	{
		return this.fill("SELECT NAME as TABLE_NAME FROM SYS.TABLES");
	}
	
	public Document getTableColumns(String tableName) throws Exception
	{		
		ResultSet ret = null;
		Connection connection = null;
		String sqlCmd = "SELECT * FROM "+tableName.replace("'","").replace(";","")+" WHERE 1=2";
		StringBuilder sbResult = new StringBuilder();
		sbResult.append("<DataSouce>");		
		
		if (this.getIsConnectionOpen()) {
			ret = this.executeReader(sqlCmd);
		}
		else {
			logger.debug(sqlCmd);
			connection = SqlHelper.getConnection(this.getConnectionInfo());			
			ret = connection.createStatement().executeQuery(sqlCmd);
		}
		try {
			ResultSetMetaData rsmd = ret.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				sbResult.append("<DataTable>");
				
				sbResult.append(StringUtil.format("<Name>%s</Name>", rsmd.getColumnName(i)));
				sbResult.append(StringUtil.format("<Type>%s</Type>", rsmd.getColumnTypeName(i)));
				sbResult.append("<Length>0</Length>");
				sbResult.append("<Comment></Comment>");
				sbResult.append("<IsNullAble>0</IsNullAble>");
				sbResult.append("<IsKey>0</IsKey>");
				
				sbResult.append("</DataTable>");
			}
			
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				connection.close();
		}
		
		sbResult.append("</DataSouce>");
		
		return DocumentHelper.parseText(sbResult.toString());
	}
	
	/**取得缓存参数	
	* @param key 缓存键值
	*/
	public SqlField[] getCachedParameters(String key) {
		return null;
	}

	/**缓存参数
	* @param key 缓存键值
	* @param val 缓存值
	 * *
	 */
	public void cacheParameters(String key, SqlField[] val) {
	}

	private Connection connection;
	private Statement statement;

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#openConnection()
	 */
	public void openConnection()
	{
		if (this.getIsConnectionOpen())
			return;
		
		this.setIsConnectionOpen(true);
		
		try {
			connection = SqlHelper.getConnection(this.getConnectionInfo());
			connection.setAutoCommit(true);
			statement = connection.createStatement();
			logger.debug("open connection");
		}
		catch (Exception ex) {
			this.setIsConnectionOpen(false);
			logger.error("open connection", ex);			
		}		
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#closeConnection()
	 */
	public void closeConnection() {
		if (!this.getIsConnectionOpen())
			return;

		try {
			statement.close();
			connection.close();
		}
		catch (Exception ex) {
			logger.error("close connection", ex);
		}
		finally {
			this.setIsConnectionOpen(false);
		}
		logger.debug("close connection");
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#transBegin()
	 */
	public void transBegin()
	{
		if (this.getIsTransing())
			return;
	
		this.setIsTransing(true);
		try {
			connection = SqlHelper.getConnection(this.getConnectionInfo());
			connection.setAutoCommit(false);
			statement = connection.createStatement();
		}
		catch (Exception ex) {
			this.setIsTransing(false);
			try {
				connection.setAutoCommit(true);
			}
			catch (Exception e) {
			}
			logger.error("transbegin", ex);			
		}
		logger.debug("transBegin");
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#transCommit()
	 */
	public void transCommit() throws Exception {
		if (!this.getIsTransing())
			throw new Exception("没有可用的事务!");

		try {
			connection.commit();
		}
		finally {
			this.setIsTransing(false);
			connection.setAutoCommit(true);
			statement.close();
			connection.close();
		}
		logger.debug("transCommint");
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#transRollback()
	 */
	public void transRollback()
	{
		if (!this.getIsTransing())
			return;

		try {
			connection.rollback();
		}
		catch (Exception ex) {
			logger.error("transrollback", ex);
		}
		finally {
			this.setIsTransing(false);
			try {
				connection.setAutoCommit(true);
				statement.close();
				connection.close();
			}
			catch (Exception ex) {
				logger.error("transrollback", ex);
			}
		}
		logger.debug("transRollback");
	}

	protected String getRecordCountCommandString(String keyId, String tableName, String strWhere) {
		String countCommand = "";
		if (!StringUtil.isNullOrEmpty(keyId) && keyId.toLowerCase().indexOf("distinct") >= 0)
		{
			if (keyId.indexOf(",") > 0)
			{
				countCommand = String.format(
						"SELECT Count(0) FROM (SELECT %s FROM %s %s) A", 
						keyId, 
						tableName, 
						strWhere);
			}
			else 
			{
				countCommand = String.format("SELECT Count(%s) FROM %s %s", 
						keyId, 
						tableName, 
						strWhere);
			}
		}
		else
		{
			countCommand = String.format("SELECT Count(0) FROM %s %s", tableName, strWhere);
		}
		return countCommand;
	}
	
	protected <T extends IValueSet> List<T> load(Class<T> type,String sql,int startIndex,int endIndex) throws SQLException
	{		
		int index = 0;
		Class<T> t = type;
		List<T> items = new ArrayList<T>();
		ResultSet ret = null;
		Connection innerConnection = null;
		
		if (this.getIsConnectionOpen())
		{
			ret = this.executeReader(sql);
		}
		else 
		{
			logger.debug(sql);
			innerConnection = OracleHelper.getConnection(this.getConnectionInfo());			
			ret = innerConnection.createStatement().executeQuery(sql);
		}
		
		try 
		{
			ResultSetMetaData rsmd = ret.getMetaData();
			T item = null;
			
			while (ret.next()) 
			{
				if (index >= startIndex) 
				{
					try 
					{
						item = t.newInstance();
					}
					catch (Exception ex) {
					}
					
					for (int i = 1; i <= rsmd.getColumnCount(); i++) 
					{
						String sTempValue = null;
						if (rsmd.getColumnType(i) == Types.TIMESTAMP) 
						{
							Timestamp timeDate = ret.getTimestamp(i);
							if (timeDate != null) {
								DateFormat df = new SimpleDateFormat(DATE_FORMAT);
								sTempValue = df.format(timeDate);
							}
						}
						else 
						{
							try {
								sTempValue = ret.getString(i);
							}
							catch (Exception textnull) {
							}
						}

						try {
							item.setValue(rsmd.getColumnName(i), sTempValue);
						}
						catch (Exception textnull) 
						{
						}
					}
					items.add(item);
				}
				
				index++;
				if (endIndex>0 && index >= endIndex) 
				{
					break;
				}
			}
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				innerConnection.close();
		}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends IValueSet> List<T> load(Class<T> type, PageInfo pInfo, String keyID, String fields, String fromTable, String order, SqlCondition... conditions) throws SQLException {
		if (pInfo == null)
			pInfo = new PageInfo();
		String strWhere = this.sqlParser.parseSqlCondition(conditions);
		String strOrder = order == null ? "" : order;
		String strFileds = StringUtil.isNullOrEmpty(fields) ? "*" : fields;
		String cmd = null;

		List<T> items = new ArrayList<T>();
		
		int recordCount = Integer.parseInt(
				this.executeScalar(
				this.getRecordCountCommandString(keyID, fromTable, strWhere)).toString());
		pInfo.setRowCount(recordCount);
		
		if ((recordCount % pInfo.getPageSize()) > 0)
		{
			pInfo.setPageCount(recordCount / pInfo.getPageSize() + 1);
		}
		else
		{
			pInfo.setPageCount(recordCount / pInfo.getPageSize());
		}

		int intStartIndex = pInfo.getCurrentPage() <= 1 ? 0 : (pInfo.getCurrentPage() - 1) * pInfo.getPageSize();
		int intEndIndex = intStartIndex + pInfo.getPageSize();
		intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;

		if (recordCount == 0 || intStartIndex > intEndIndex) 
		{
			return items;
		}

		/**1.没有分页的主键，取所有记录行
		 * 2.有主键，如果行数大于1000取前面的指定行数，否则取所有
		 *   用主键ID作条件加载指定记录行
		 * */
		if (StringUtil.isNullOrEmpty(keyID) || keyID.indexOf(",") > 0 || keyID.indexOf(" ") > 0 || pInfo.getCurrentPage() <= 1) {
			cmd = this.getSqlParser().getSelectCommandString(
					fromTable, 
					intEndIndex >= recordCount ? 0 : intEndIndex, 
					false, 
					fields, 
					null, 
					strOrder, 
					conditions);
		}
		else {		
			
			String[] recordPage = getPageRecordIds(intStartIndex,intEndIndex,recordCount,
					fromTable,keyID,strOrder,pInfo,conditions);
			String strIds = recordPage[1];
						
			if (strIds.length() > 0) 
			{
				cmd = String.format(
						"SELECT %s FROM %s WHERE %s %s ", 
						strFileds, 
						fromTable, 
						this.getSqlParser().parseSqlCondition(
								null, 
								false, 
								new SqlCondition(keyID, strIds, SqlLogicType.And, SqlRelationType.In, "1".equals(recordPage[0]) ? SqlParamType.String : SqlParamType.Numeric)), strOrder);
			}
			else 
			{
				return items;
			}
			
			//所有记录行都是
			intStartIndex = 0;
		}

		return load(type,cmd,intStartIndex,intEndIndex);
	}
	
	protected Document fill(String sql, String rootName, String itemName,int startIndex,int recordSize) throws SQLException {
		if (this.getIsConnectionOpen())
		{
			return SqlHelper.executeXmlDocument(
					statement, 
					sql, rootName, itemName, startIndex, recordSize);
		}
		else
		{
			return SqlHelper.executeXmlDocument(
					this.getConnectionInfo(), 
					sql, rootName, itemName, startIndex, recordSize);
		}
	}
	
	protected String load(String sql, String itemName,int startIndex,int recordSize) throws SQLException {
		if (this.getIsConnectionOpen())
		{
			return SqlHelper.executeJsonString(
					statement, 
					sql, 					
					itemName, 
					startIndex, 
					recordSize);
		}
		else
		{
			return SqlHelper.executeJsonString(
					this.getConnectionInfo(), 
					sql,
					itemName, 
					startIndex, 
					recordSize);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String[] getPageRecordIds(int intStartIndex,int intEndIndex,int recordCount,
			String fromTable,String keyID,String strOrder,PageInfo pInfo,SqlCondition... conditions) throws SQLException
	{
		String cmd = this.getSqlParser().getSelectCommandString(fromTable, intEndIndex >= recordCount ? 0 : intEndIndex, false, keyID, null, strOrder, conditions);
		Document dsResult = this.fill(cmd, "DataSource", "Table",intStartIndex,pInfo.getPageSize());
				
		List nodes = dsResult.getDocument().selectNodes("DataSource/Table");
		String strKey = keyID.indexOf('.') > 0 ? keyID.substring(keyID.lastIndexOf(".") + 1) : keyID;
		StringBuilder sbIds = new StringBuilder(500);			
		boolean bIsString = false;
		
		if (nodes.size() > 0) 
		{
			String keyType = ((Element) nodes.get(0)).selectSingleNode(strKey).valueOf("@type");
			bIsString = "VARCHAR".equalsIgnoreCase(keyType) 
				|| "NVARCHAR".equalsIgnoreCase(keyType) 
				|| "CHAR".equalsIgnoreCase(keyType);
		}
		
		for (int i = 0; i < nodes.size(); i++) 
		{
			String strValue = ((Node) nodes.get(i)).selectSingleNode(strKey).getText();
			sbIds.append(strValue);
			sbIds.append(",");
		}
		
		if (sbIds.length() > 0) 
		{
			sbIds = sbIds.deleteCharAt(sbIds.length() - 1);
		}
		
		String strIds = sbIds.toString();
		
		return new String[]{bIsString?"1":"0",strIds};
	}
}