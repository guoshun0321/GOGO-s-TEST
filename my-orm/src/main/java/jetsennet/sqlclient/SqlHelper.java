/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.sql.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * SQL执行辅助类
 * @author 李小敏
 */
public class SqlHelper extends BaseSqlHelper {
	
	private static jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("jetsennet.sqlclient.SqlHelper");

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

	@SuppressWarnings("unchecked")
	public static int executeNonQuery(ConnectionInfo cInfo, String cmdText, SqlValue... sqlValues) throws SQLException {

		logger.debug(cmdText);

		int rowCount = 0;
		Connection conn = null;
		try {
			conn = getConnection(cInfo);
			PreparedStatement pstmt = conn.prepareStatement(cmdText);
			prepareCommand(pstmt, sqlValues);
			rowCount = pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return rowCount;
	}

	@SuppressWarnings("unchecked")
	public static int executeNonQuery(Statement pstmt, String cmdText) throws SQLException {
		logger.debug(cmdText);

		int rowCount = pstmt.executeUpdate(cmdText);
		return rowCount;
	}

	public static int[] executeBatch(ConnectionInfo cInfo, String[] sqls) throws SQLException {

		int[] rowCounts;
		Connection conn = null;
		try {
			conn = getConnection(cInfo);
			Statement stmt = conn.createStatement();
			for (int i = 0; i < sqls.length; i++) {
				stmt.addBatch(sqls[i]);
			}
			rowCounts = stmt.executeBatch();
			stmt.close();
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return rowCounts;
	}

	@SuppressWarnings("unchecked")
	public static <T> T executeScalar(ConnectionInfo cInfo, String cmdText) throws SQLException {

		logger.debug(cmdText);

		T ret = null;
		Connection conn = null;
		try {
			conn = getConnection(cInfo);
			PreparedStatement pstmt = conn.prepareStatement(cmdText);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				ret = (T) rs.getObject(1);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> T executeScalar(Statement pstmt, String cmdText) throws SQLException {
		logger.debug(cmdText);

		T ret = null;

		ResultSet rs = pstmt.executeQuery(cmdText);
		if (rs.next()) {
			ret = (T) rs.getObject(1);
		}
		
		rs.close();
		
		return ret;
	}

	/*
	 * @SuppressWarnings("unchecked") public static ResultSet
	 * executeResultSet(ConnectionInfo cInfo,String cmdText) throws SQLException {
	 * logger.debug(cmdText);
	 * 
	 * ResultSet ret = null; Connection conn = null; try { conn =
	 * getConnection(cInfo); PreparedStatement pstmt =
	 * conn.prepareStatement(cmdText); ret = pstmt.executeQuery(); } catch
	 * (SQLException ex) { throw ex; } finally { if(conn!=null){ conn.close(); } }
	 * return ret; }
	 */

	@SuppressWarnings("unchecked")
	public static ResultSet executeResultSet(Statement pstmt, String cmdText) throws SQLException {
		logger.debug(cmdText);

		ResultSet ret = null;
		ret = pstmt.executeQuery(cmdText);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Document executeXmlDocument(ConnectionInfo cInfo, String cmdText, String xmlRoot, String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		Connection conn = null;
		try {
			Document retDom = DocumentHelper.createDocument();
			Element rootElement = retDom.addElement(xmlRoot);
			
			conn = getConnection(cInfo);
			
			PreparedStatement pstmt = conn.prepareStatement(cmdText);
			ResultSet rs = pstmt.executeQuery();
			executeXmlDocument(rs,rootElement,dataRoot,startIndex,recordSize);
			
			rs.close();
			pstmt.close();
			
			return retDom;
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static Document executeXmlDocument(Statement pstmt, String cmdText, String xmlRoot, String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		Document retDom = DocumentHelper.createDocument();
		Element rootElement = retDom.addElement(xmlRoot);
		ResultSet rs = pstmt.executeQuery(cmdText);
		int resultSetIndex = 0;
		
		do {
			if (resultSetIndex > 0)
				rs = pstmt.getResultSet();
			
			executeXmlDocument(rs,rootElement,
					resultSetIndex > 0 ? dataRoot + String.valueOf(resultSetIndex) : dataRoot
					,startIndex,recordSize);
			
			resultSetIndex++;
		}
		while (pstmt.getMoreResults());
		
		rs.close();

		return retDom;
	}
	
	@SuppressWarnings("unchecked")
	public static String executeJsonString(ConnectionInfo cInfo, String cmdText,String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		Connection conn = null;
		try {
			
			StringBuilder result = new StringBuilder(1000);
			//result.append("{");
			
			conn = getConnection(cInfo);
			
			PreparedStatement pstmt = conn.prepareStatement(cmdText);
			ResultSet rs = pstmt.executeQuery();
			result.append(executeJsonString(rs,dataRoot,startIndex,recordSize));
			
			rs.close();
			pstmt.close();
			
			//result.append("}");
			return result.toString();
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public static String executeJsonString(Statement pstmt, String cmdText, String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		ResultSet rs = pstmt.executeQuery(cmdText);
		StringBuilder result = new StringBuilder();
		//result.append("{");
		
		int resultSetIndex = 0;
		
		do {
			if (resultSetIndex > 0)
				rs = pstmt.getResultSet();
			
			result.append(executeJsonString(rs,
					resultSetIndex > 0 ? dataRoot + String.valueOf(resultSetIndex) : dataRoot
					,startIndex,recordSize));
			
			resultSetIndex++;
		}
		while (pstmt.getMoreResults());
		
		rs.close();

		//result.append("}");
		return result.toString();
	}
	
}