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
 * Oracle执行类
 * @author 李小敏
 */
public class OracleExecutor extends BaseSqlExecutor implements ISqlExecutor {

	private static jetsennet.logger.ILog logger = LogManager.getLogger("jetsennet.sqlclient.OracleExecutor");
	private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public OracleExecutor(ConnectionInfo cInfo) {
		this.setConnectionInfo(cInfo);
	}

	protected void finalize() 
	{
		if (this.getIsTransing()) {
			this.transRollback();
		}
		else if (this.getIsConnectionOpen()) {
			this.closeConnection();
		}
	}	
	
	
	public <T extends IValueSet> List<T> load(Class<T> type, String sql,SqlValue... sqlValues) throws SQLException 
	{
		return load(type,sql,0,sqlValues);
	}
	
	/**
	 * 加载数据列表
	 * @param sql SQL语句
	 */
	public <T extends IValueSet> List<T> load(
			Class<T> type, String sql,int requestRows,SqlValue... sqlValues) throws SQLException 
	{		
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);		
		return load(type,sqlCommand,0,requestRows);
	}
	
	public <T extends IValueSet> List<T> load(Class<T> type,SqlQuery sqlQuery) throws SQLException
	{		
		if (sqlQuery.isPageResult) {
			
			String fromTable = this.getSqlParser().parseQueryTable(sqlQuery.queryTable);
			
			// 对于需要分组的，应分别处理
			if (sqlQuery.groupFields != null && sqlQuery.groupFields.length() > 0) {
				
				// 分页
				String commandCount = "SELECT COUNT(0) FROM (" + this.getSqlParser().getSelectCommandString(
						fromTable, 0, 
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
				int intStartIndex = sqlQuery.pageInfo.getCurrentPage() <= 1 ? 
						0 : (sqlQuery.pageInfo.getCurrentPage() - 1) * sqlQuery.pageInfo.getPageSize();
				int intEndIndex = intStartIndex + sqlQuery.pageInfo.getPageSize();
				intEndIndex = intEndIndex > recordCount ? recordCount : intEndIndex;
				String cmd = null;

				if (recordCount == 0) 
				{
					return new ArrayList<T>();
				}
				else 
				{
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
			else 
			{
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
			return load(type,this.getSqlParser().getSelectCommandString(sqlQuery),0);
	}
	
	public List<String> load(String sql, boolean ignoreEmpty) throws SQLException {
		List<String> items = new ArrayList<String>();
		ResultSet ret = null;
		Connection innerConnection = null;
		
		if (this.getIsConnectionOpen()) {
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
					try {
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
		finally 
		{
			if (!this.getIsConnectionOpen())
				innerConnection.close();
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
						result.append(OracleHelper.executeJsonString(
								this.connection, 
								cmd, 								
								queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize()));
					else
						result.append(OracleHelper.executeJsonString(
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
				result.append(OracleHelper.executeJsonString(
						this.connection, 
						cmd, itemName, intStartIndex, pInfo.getPageSize()));
			}
			else
			{
				result.append(OracleHelper.executeJsonString(
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
	
	/**
	 * 获取数据的第一条记录
	 */
	public <T extends IValueSet> T find(Class<T> type, String sql ,SqlValue... sqlValues) throws SQLException {
		
		T item = null;
		ResultSet ret = null;
		Connection innerConnection = null;
		String sqlCommand = this.getSqlParser().formatCommand(sql,sqlValues);
		
		if (this.getIsConnectionOpen()) {
			ret = this.executeReader(sqlCommand);
		}
		else {
			logger.debug(sqlCommand);
			innerConnection = OracleHelper.getConnection(this.getConnectionInfo());			
			ret = innerConnection.createStatement().executeQuery(sqlCommand);
		}
		
		try 
		{
			ResultSetMetaData rsmd = ret.getMetaData();
			if (ret.next()) 
			{
				try 
				{
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
					catch (Exception textnull) {
					}
				}
			}
			ret.close();
		}
		finally {
			if (!this.getIsConnectionOpen())
				innerConnection.close();
		}
		return item;
	}

	public String[] find(String sql,SqlValue... sqlValues) throws SQLException {
		
		String[] retArr = null;
		ResultSet ret = null;
		Connection innerConnection = null;
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		
		if (this.getIsConnectionOpen()) 
		{
			ret = this.executeReader(sqlCommand);
		}
		else 
		{
			logger.debug(sqlCommand);
			innerConnection = OracleHelper.getConnection(this.getConnectionInfo());			
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

		
	
	
	
	/**加载数据，返回XML
	 * @param pInfo
	 * @param rootName
	 * @param itemName
	 * @param keyID
	 * @param fields
	 * @param fromTable
	 * @param order
	 * @param p
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Document fill(PageInfo pInfo, String rootName, String itemName, String keyID, String fields, String fromTable, String order, SqlCondition... conditions) throws SQLException {
		if (pInfo == null)
			pInfo = new PageInfo();

		String strWhere = this.getSqlParser().parseSqlCondition(conditions);
		String strOrder = order == null ? "" : order;
		String strFileds = StringUtil.isNullOrEmpty(fields) ? "*" : fields;
		String cmd = null;
		Document dsResult = null;

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
				dsResult = OracleHelper.executeXmlDocument(
						this.connection, 
						cmd, rootName, itemName, intStartIndex, pInfo.getPageSize());
			}
			else
			{
				dsResult = OracleHelper.executeXmlDocument(
						this.getConnectionInfo(), 
						cmd, rootName, itemName, intStartIndex, pInfo.getPageSize());
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

			dsResult = fill(cmd, rootName, itemName);
		}

		dsResult.getRootElement().addAttribute("TotalCount", String.valueOf(recordCount));

		return dsResult;
	}

	public Document fill(String sql) throws SQLException 
	{
		return fill(sql,"DataSource","DataTable");
	}
	
	public Document fill(String sql, String rootName, String itemName,SqlValue... sqlValues) throws SQLException {
		String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
		return fill(sqlCommand, rootName, itemName, -1, -1);
	}
	
	public Document fill(SqlQuery queryInfo) throws SQLException 
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
				Document dsResult = null;

				if (recordCount == 0) 
				{
					dsResult = DocumentHelper.createDocument();
					dsResult.addElement(queryInfo.recordSetName);
				}
				else 
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
						dsResult = OracleHelper.executeXmlDocument(
								this.connection, 
								cmd, 
								queryInfo.recordSetName, 
								queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize());
					else
						dsResult = OracleHelper.executeXmlDocument(
								this.getConnectionInfo(), 
								cmd, 
								queryInfo.recordSetName, 
								queryInfo.recordName, 
								intStartIndex, 
								queryInfo.pageInfo.getPageSize());
				}

				dsResult.getRootElement().addAttribute("TotalCount", String.valueOf(recordCount));
				//Element dataElement = ((Element) dsResult.getDocument().selectSingleNode(queryInfo.recordSetName)).addElement(queryInfo.recordName + "1");
				//dataElement.addElement("TotalCount").addText(String.valueOf(recordCount));

				return dsResult;
			}
			else 
			{
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
			return fill(this.getSqlParser().getSelectCommandString(queryInfo), 
					queryInfo.recordSetName, 
					queryInfo.recordName);
	}	
	

	public int executeNonQuery(String sql, SqlValue... sqlValues) throws SQLException {
		
		boolean hasText = false;

		if(sqlValues != null)
		{
			for (SqlValue sqlV : sqlValues) 
			{
				if (sqlV.getSqlType() == SqlParamType.Text) {
					hasText = true;
					break;
				}
			}
		}
		
		if (hasText) 
		{
			sql = sql.replaceAll("%s", "?");

			if (this.getIsConnectionOpen()) {
				return OracleHelper.executeNonQuery(this.connection, sql, sqlValues);
			}
			else {
				return OracleHelper.executeNonQuery(this.getConnectionInfo(), sql, sqlValues);
			}
		}
		else
		{
			String sqlCommand = this.getSqlParser().formatCommand(sql, sqlValues);
			if (this.getIsConnectionOpen()) {
				return OracleHelper.executeNonQuery(this.connection, sqlCommand);
			}
			else {
				return OracleHelper.executeNonQuery(this.getConnectionInfo(), sqlCommand);
			}
		}
	}

	/**
	 * 执行命令
	 * command的Fields如果有Text类型,并且长度大于1000将使用sql参数处理
	 * (non-Javadoc)
	 * @see jetsennet.sqlclient.ISqlExecutor#executeNonQuery(jetsennet.sqlclient.DbCommand)
	 */
	public int executeNonQuery(DbCommand command) throws SQLException {
		// 
		boolean hasText = false;
		List<SqlValue> sqlValues = null;

		sqlValues = new ArrayList<SqlValue>();
		for (SqlField sqlV : command.getSqlFields()) 
		{
			if (sqlV != null && sqlV.getSqlParamType() == SqlParamType.Text) 
			{
				String val = sqlV.getFieldValue();
				
				if (val == null || val.length() < 1000) 
				{
					sqlV.setSqlParamType(SqlParamType.String);
				}
				else 
				{
					hasText = true;
					sqlValues.add(new SqlValue(val, SqlParamType.Text));
				}
			}
		}

		if (hasText) 
		{
			SqlValue[] arrSqlVs = new SqlValue[sqlValues.size()];
			arrSqlVs = sqlValues.toArray(arrSqlVs);
			
			if (this.getIsConnectionOpen()) 
			{
				return OracleHelper.executeNonQuery(this.connection, command.toString(), arrSqlVs);
			}
			else {
				return OracleHelper.executeNonQuery(this.getConnectionInfo(), command.toString(), arrSqlVs);
			}
		}
		else
			return executeNonQuery(command.toString());
	}

	public Object executeScalar(String sql,SqlValue... sqlValues) throws SQLException 
	{		
		String sqlCommand = this.getSqlParser().formatCommand(sql,sqlValues);
		
		if (this.getIsConnectionOpen()) 
		{
			return OracleHelper.executeScalar(this.connection, sqlCommand);
		}
		else {
			return OracleHelper.executeScalar(this.getConnectionInfo(), sqlCommand);
		}
	}

	public ResultSet executeReader(String sql,SqlValue... sqlValues) throws SQLException 
	{		
		String sqlCommand = this.getSqlParser().formatCommand(sql,sqlValues);
		
		if (this.getIsConnectionOpen())
			return OracleHelper.executeResultSet(this.connection, sqlCommand);
		else
			throw new SQLException("调用executeReader方法出错，请先调用openConnection或transBegin方法");		
	}

	public int getNewId(String tableName) throws Exception 
	{
		return Integer.parseInt(keyGenerator.generateKey(this, tableName));
	}

	public void updateNewId(String tableName, int newId) throws Exception {
		
		keyGenerator.updateKey(this, tableName, newId+"");		
	}
	public Document getSchema() throws Exception
	{
		return this.fill("SELECT TABLE_NAME FROM USER_TABLES");
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
			connection = OracleHelper.getConnection(this.getConnectionInfo());			
			ret = connection.createStatement().executeQuery(sqlCmd);
		}
		
		try {
			ResultSetMetaData rsmd = ret.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				sbResult.append("<DataTable>");
				
				sbResult.append(StringUtil.format("<Name>%s</Name>", rsmd.getColumnName(i)));
				sbResult.append(StringUtil.format("<Type>%s</Type>", rsmd.getColumnTypeName(i)));
				//sbResult.append("<Length>0</Length>");
				//sbResult.append("<Comment></Comment>");
				//sbResult.append("<IsNullAble>0</IsNullAble>");
				//sbResult.append("<IsKey>0</IsKey>");
				
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
	public SqlField[] getCachedParameters(String key) {
		return null;
	}

	public void cacheParameters(String key, SqlField[] val) {
	}

	private Connection connection;

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#openConnection()
	 */
	public void openConnection()// throws Exception
	{
		if (this.getIsConnectionOpen())
			return;
		this.setIsConnectionOpen(true);
		try {
			connection = OracleHelper.getConnection(this.getConnectionInfo());
			connection.setAutoCommit(true);
			// statement = connection.createStatement();
		}
		catch (Exception ex) {
			this.setIsConnectionOpen(false);
			logger.error("open connection", ex);
			return;
			// throw ex;
		}
		logger.debug("open connection");
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#closeConnection()
	 */
	public void closeConnection() {
		if (!this.getIsConnectionOpen())
			return;

		try {
			// statement.close();
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
	public void transBegin()// throws Exception
	{
		if (this.getIsTransing())
			return;
		// throw new Exception("事务正在执行中，无法启用新的事务!");
		this.setIsTransing(true);
		try {
			connection = OracleHelper.getConnection(this.getConnectionInfo());
			connection.setAutoCommit(false);
			// statement = connection.createStatement();
		}
		catch (Exception ex) {
			this.setIsTransing(false);
			try {
				connection.setAutoCommit(true);
			}
			catch (Exception e) {
			}
			logger.error("transbegin", ex);
			// throw ex;
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
			// statement.close();
			connection.close();
		}
		logger.debug("transCommint");
	}

	/**
	 * @see jetsennet.sqlclient.ISqlExecutor#transRollback()
	 */
	public void transRollback()// throws Exception
	{
		if (!this.getIsTransing())
			return;
		// throw new Exception("没有可用的事务!");

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
				// statement.close();
				connection.close();
			}
			catch (Exception ex) {
				logger.error("transrollback", ex);
			}
		}
		logger.debug("transRollback");
	}
		
	protected <T extends IValueSet> List<T> load(
			Class<T> type,String sql,int startIndex,int endIndex) throws SQLException
	{		
		int index = 0;
		Class<T> t = type;
		List<T> items = new ArrayList<T>();
		ResultSet ret = null;
		Connection innerConnection = null;
		
		if (this.getIsConnectionOpen()) {
			ret = this.executeReader(sql);
		}
		else {
			logger.debug(sql);
			innerConnection = OracleHelper.getConnection(this.getConnectionInfo());			
			ret = innerConnection.createStatement().executeQuery(sql);
		}
		
		try {
			ResultSetMetaData rsmd = ret.getMetaData();
			T item = null;
			while (ret.next()) 
			{
				if (index >= startIndex) 
				{
					try {
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
						catch (Exception textnull) {
						}
					}
					items.add(item);
				}
				
				index++;
				if (endIndex>0 && index >= endIndex) {
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
	protected <T extends IValueSet> List<T> load(Class<T> type, 
			PageInfo pInfo, String keyID, String fields, String fromTable, String order, SqlCondition... p) throws SQLException 
	{
		if (pInfo == null)
			pInfo = new PageInfo();
		
		String strWhere = this.getSqlParser().parseSqlCondition(p);
		String strOrder = order == null ? "" : order;
		String strFileds = StringUtil.isNullOrEmpty(fields) ? "*" : fields;
		String cmd = null;

		List<T> items = new ArrayList<T>();
		
		int recordCount = Integer.parseInt(this.executeScalar(
				this.getRecordCountCommandString(
						keyID, 
						fromTable, 
						strWhere)).toString());
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
		if (StringUtil.isNullOrEmpty(keyID) 
				|| keyID.indexOf(",") > 0 
				|| keyID.indexOf(" ") > 0 
				|| pInfo.getCurrentPage() <= 1) 
		{
			cmd = this.getSqlParser().getSelectCommandString(
					fromTable, 
					intEndIndex >= recordCount ? 0 : intEndIndex, 
					false, 
					fields, 
					null, 
					strOrder, 
					p);
		}
		else 
		{			
			boolean isRowNum = intEndIndex>0 && (intEndIndex <= recordCount || intEndIndex>1000);
			cmd = this.getSqlParser().getSelectCommandString(fromTable, isRowNum ? intEndIndex :0, 
					false, keyID, null, strOrder, p);
			
			if(isRowNum)
				cmd += " AND RN > "+intStartIndex;
			
			Document dsResult = this.fill(cmd, "DataSource", "Table",isRowNum?0:intStartIndex,pInfo.getPageSize());
			
			List nodes = dsResult.getDocument().selectNodes("DataSource/Table");
			String strKey = keyID.indexOf('.') > 0 ? keyID.substring(keyID.lastIndexOf(".") + 1) : keyID;
			StringBuilder sbIds = new StringBuilder(500);		
			boolean bIsString = false;
			
			if (nodes.size() > 0) 
			{
				String keyType = ((Element) nodes.get(0)).selectSingleNode(strKey).valueOf("@type");
				bIsString = "VARCHAR".equalsIgnoreCase(keyType) 
							|| "VARCHAR2".equalsIgnoreCase(keyType) 
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
			
			if (strIds.length() > 0) 
			{
				cmd = String.format("SELECT %s FROM %s WHERE %s %s ", 
						strFileds, 
						fromTable, 
						this.getSqlParser().parseSqlCondition(
								null, 
								false, 
								new SqlCondition(keyID, strIds, SqlLogicType.And, SqlRelationType.In, bIsString ? SqlParamType.String : SqlParamType.Numeric)), strOrder);
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
			return OracleHelper.executeXmlDocument(
					connection, 
					sql, 
					rootName, 
					itemName, 
					startIndex, 
					recordSize);
		}
		else
		{
			return OracleHelper.executeXmlDocument(
					this.getConnectionInfo(), 
					sql, rootName, 
					itemName, 
					startIndex, 
					recordSize);
		}
	}
	
	protected String load(String sql, String itemName,int startIndex,int recordSize) throws SQLException {
		if (this.getIsConnectionOpen())
		{
			return OracleHelper.executeJsonString(
					connection, 
					sql, 					
					itemName, 
					startIndex, 
					recordSize);
		}
		else
		{
			return OracleHelper.executeJsonString(
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
		boolean isRowNum = intEndIndex>0 && (intEndIndex <= recordCount || intEndIndex>1000);
		String cmd = this.getSqlParser().getSelectCommandString(fromTable, isRowNum ? intEndIndex :0, 
				false, keyID, null, strOrder, conditions);
		
		if(isRowNum)
		{
			cmd += " AND RN > "+intStartIndex;
		}
		
		Document dsResult = this.fill(cmd, "DataSource", "Table",isRowNum?0:intStartIndex,pInfo.getPageSize());
		
		List nodes = dsResult.getDocument().selectNodes("DataSource/Table");
		String strKey = keyID.indexOf('.') > 0 ? keyID.substring(keyID.lastIndexOf(".") + 1) : keyID;
		StringBuilder sbIds = new StringBuilder(500);			
		boolean bIsString = false;
		
		if (nodes.size() > 0) 
		{
			String keyType = ((Element) nodes.get(0)).selectSingleNode(strKey).valueOf("@type");
			bIsString = "VARCHAR".equalsIgnoreCase(keyType) 
							|| "VARCHAR2".equalsIgnoreCase(keyType) 
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