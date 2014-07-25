/************************************************************************
日  期：		2013-07-10
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.dom4j.Element;

/**
 * @author 李小敏
 */
public class BaseSqlHelper
{
	protected static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static Connection getConnection(ConnectionInfo cInfo) throws SQLException {
		/** try {
		//    		
		// Class.forName(DBConfig.getProperty("driver"));
		// } catch (ClassNotFoundException e) {
		// }
		// Connection conn =
		// DriverManager.getConnection(DBConfig.getProperty("dburl"),
		// DBConfig.getProperty("dbuser"),
		// DBConfig.getProperty("dbpwd"));
		// return conn;
		//return DbConfig.getDataSource(cInfo.getDbDriver(), cInfo.getDbUrl(), cInfo.getDbUser(), cInfo.getDbPwd()).getConnection();
		 * 
		 */
		return DbConfig.getConnection(cInfo);
	}
	
	
	protected static void executeXmlDocument(ResultSet rs,Element rootElement,String dataRoot, int startIndex, int recordSize) throws SQLException
	{		
		ResultSetMetaData metaData = rs.getMetaData();
		
		int index = 0;
		int rowIndex = -1;
		int fieldCount = 0;
		String sTempName = null;
		String sTempValue = null;
		Element cellElement = null;
		Element rowElement = null;
		
		while (rs.next()) {
			
			rowIndex++;
			if (rowIndex < startIndex)
				continue;
			
			rowElement = rootElement.addElement(dataRoot);
			fieldCount = metaData.getColumnCount();
			
			for (int i = 1; i <= fieldCount; i++) {
				
				sTempName = metaData.getColumnName(i);
				sTempValue = null;
				
				if (metaData.getColumnType(i) == Types.TIMESTAMP) {
					Timestamp timeDate = rs.getTimestamp(i);
					if (timeDate != null) {
						DateFormat df = new SimpleDateFormat(DATE_FORMAT);
						sTempValue = df.format(timeDate);
					}
				}
				else 
				{
					try {
						sTempValue = rs.getString(i);
					}
					catch (Exception textnull) {
					}
				}				
						
				if (sTempValue != null) {
					cellElement = rowElement.addElement(sTempName);
					cellElement.addText(sTempValue);
					if (index == 0 && fieldCount<=2) {
						cellElement.addAttribute("type", metaData.getColumnTypeName(i));
					}
				}
							
			}
			
			index++;
			
			if (recordSize > 0 && index >= recordSize) {
				break;
			}
		}
	}
	
	protected static String executeJsonString(ResultSet rs,String dataRoot, int startIndex, int recordSize) throws SQLException
	{		
		ResultSetMetaData metaData = rs.getMetaData();
		
		int index = 0;
		int rowIndex = -1;
		int fieldCount = 0;
		String sTempName = null;
		String sTempValue = null;
		
		StringBuilder result = new StringBuilder(1000);
		result.append("\""+dataRoot+"\":[");	
		
		while (rs.next()) {
			
			rowIndex++;
			if (rowIndex < startIndex)
				continue;
			
			if(index>0)
			{
				result.append(",{");
			}
			else
			{
				result.append("{");
			}
						
			fieldCount = metaData.getColumnCount();
			int fieldIndex = 0;
			
			for (int i = 1; i <= fieldCount; i++) {
				
				sTempName = metaData.getColumnName(i);
				sTempValue = null;
				int columnType = metaData.getColumnType(i);
				if (columnType == Types.TIMESTAMP) {
					Timestamp timeDate = rs.getTimestamp(i);
					if (timeDate != null) {
						DateFormat df = new SimpleDateFormat(DATE_FORMAT);
						sTempValue = df.format(timeDate);
					}
				}
				else 
				{
					try {
						sTempValue = rs.getString(i);
					}
					catch (Exception textnull) {
					}
				}				
						
				if (sTempValue != null) {	
					
					if(fieldIndex>0)
					{
						result.append(",");
					}
					
					fieldIndex++;
					
					if(!(columnType == Types.TIMESTAMP || columnType == Types.INTEGER 
							|| columnType == Types.INTEGER || columnType == Types.DATE
							|| columnType == Types.BIGINT || columnType == Types.NUMERIC))
					{
						sTempValue = jetsennet.util.StringUtil.escapeJson(sTempValue);
					}		
					result.append("\""+sTempName+"\":");
					result.append("\""+sTempValue+"\"");					
				}
							
			}
			
			result.append("}");
			
			index++;
			
			if (recordSize > 0 && index >= recordSize) {
				break;
			}
		}
		
		if(index == 0)
		{
			return "";
		}
		
		result.append("]");
		
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	protected static void prepareCommand(PreparedStatement pstmt, SqlValue... param) throws SQLException {
		if (param != null) {
			for (int i = 0; i < param.length; i++) {
				SqlValue obj = param[i];
				if (null == obj) {
					pstmt.setString(i + 1, "");
				}
				else if (obj.getSqlType() == SqlParamType.String) {
					pstmt.setString(i + 1, obj.getValue());
				}
				else if (obj.getSqlType() == SqlParamType.DateTime) {
					pstmt.setTimestamp(i + 1, Timestamp.valueOf(obj.getValue()));
				}
				else if (obj.getSqlType() == SqlParamType.Numeric) {
					pstmt.setInt(i + 1, Integer.parseInt(obj.getValue()));
				}
				else if (obj.getSqlType() == SqlParamType.Text) {
					pstmt.setString(i + 1, obj.getValue());
				}
			}
		}
	}
}
