package jetsennet.sqlclient;

/**
 * 采用NET_SEQUENCE生成主键
 * @author 李小敏
 */
public class SequenceKeyGenerator implements ITableKeyGenerator
{
	public String generateKey(ISqlExecutor sqlExecutor,String tableName) throws Exception
	{
		String keyId = "1";
		String cmd_select = "SELECT SERIAL_NUMBER FROM NET_SEQUENCE WHERE TABLE_NAME=%s";
		String cmd_update = "UPDATE NET_SEQUENCE SET SERIAL_NUMBER = SERIAL_NUMBER+1 WHERE TABLE_NAME=%s";
		String cmd_insert = "INSERT INTO NET_SEQUENCE (TABLE_NAME,SERIAL_NUMBER) VALUES(%s,1)";
		SqlValue tableNamelValue = new SqlValue(tableName);
		Object newKey;
		try {
			
			sqlExecutor.executeNonQuery(cmd_update,tableNamelValue);
			newKey = sqlExecutor.executeScalar(cmd_select,tableNamelValue);

			if (newKey != null) {
				keyId = newKey.toString();
			}
			else {
				sqlExecutor.executeNonQuery(cmd_insert,tableNamelValue);
			}
		}
		catch (Exception ex) {
			throw new Exception("生成主键错误," + ex.getMessage());
		}
		return keyId;
	}
	
	public void updateKey(ISqlExecutor sqlExecutor,String tableName,String keyValue) throws Exception
	{
		SqlValue tableNamelValue = new SqlValue(tableName);
		SqlValue keyIdValue = new SqlValue(keyValue,SqlParamType.Numeric);
		
		int flag = sqlExecutor.executeNonQuery("UPDATE NET_SEQUENCE SET SERIAL_NUMBER=%s WHERE TABLE_NAME=%s", 
				keyIdValue, tableNamelValue);
				
		if (flag == 0) {
			sqlExecutor.executeNonQuery("INSERT INTO NET_SEQUENCE (TABLE_NAME,SERIAL_NUMBER) VALUES('%s',%s)", 
					tableNamelValue, keyIdValue);
		}
	}
}