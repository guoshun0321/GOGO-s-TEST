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
 * @author 李小敏
 */
public class OracleHelper extends BaseSqlHelper {

	private static jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("jetsennet.sqlclient.OracleHelper");
	
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
	public static int executeNonQuery(Connection connection, String cmdText, SqlValue... sqlValues) throws SQLException {
		logger.debug(cmdText);

		PreparedStatement pstmt = connection.prepareStatement(cmdText);
		prepareCommand(pstmt, sqlValues);
		int rowCount = pstmt.executeUpdate();
		pstmt.close();
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
	public static <T> T executeScalar(Connection connection, String cmdText) throws SQLException {
		logger.debug(cmdText);

		T ret = null;

		PreparedStatement pstmt = connection.prepareStatement(cmdText);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			ret = (T) rs.getObject(1);
		}
		rs.close();
		pstmt.close();

		return ret;
	}

	/**
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
	public static ResultSet executeResultSet(Connection connection, String cmdText) throws SQLException {
		logger.debug(cmdText);

		ResultSet ret = null;
		PreparedStatement pstmt = connection.prepareStatement(cmdText);
		ret = pstmt.executeQuery();
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

	public static Document executeXmlDocument(Connection connection, String cmdText, String xmlRoot, String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		Document retDom = DocumentHelper.createDocument();
		Element rootElement = retDom.addElement(xmlRoot);
		PreparedStatement pstmt = connection.prepareStatement(cmdText);
		ResultSet rs = pstmt.executeQuery();
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
		pstmt.close();

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
	
	public static String executeJsonString(Connection connection, String cmdText, String dataRoot, int startIndex, int recordSize) throws SQLException {
		logger.debug(cmdText);

		PreparedStatement pstmt = connection.prepareStatement(cmdText);
		ResultSet rs = pstmt.executeQuery();
		
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