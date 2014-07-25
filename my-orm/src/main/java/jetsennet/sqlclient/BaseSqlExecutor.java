package jetsennet.sqlclient;

import java.sql.SQLException;
import java.util.List;

import jetsennet.util.StringUtil;

import org.dom4j.Document;

public class BaseSqlExecutor
{
private ConnectionInfo connectionInfo;
	
	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo value) {
		this.connectionInfo = value;
	}

	private boolean isTransing = false;
	
	public boolean getIsTransing() {
		return this.isTransing;
	}

	void setIsTransing(boolean value) {
		this.isTransing = value;
	}

	private boolean isConnectionOpen = false;

	public boolean getIsConnectionOpen() {
		return this.isTransing || this.isConnectionOpen;
	}

	public void setIsConnectionOpen(boolean value) {
		this.isConnectionOpen = value;
	}

	private ISqlParser sqlParser;
	
	public ISqlParser getSqlParser() {
		return this.sqlParser;
	}

	public void setSqlParser(ISqlParser value) {
		this.sqlParser = value;
	}
	
	protected ITableKeyGenerator keyGenerator = new SequenceKeyGenerator();
	
	protected String getRecordCountCommandString(String keyId, String tableName, String strWhere) {
		String countCommand = "";
		if (!StringUtil.isNullOrEmpty(keyId) && keyId.toLowerCase().indexOf("distinct") >= 0) {
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
				countCommand = String.format(
						"SELECT Count(%s) FROM %s %s", 
						keyId, 
						tableName, 
						strWhere);
			}
		}
		else {
			countCommand = String.format("SELECT Count(0) FROM %s %s", tableName, strWhere);
		}
		return countCommand;
	}
}
