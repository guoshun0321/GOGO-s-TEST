/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 主键生成接口
 * @author 李小敏
 */
public interface ITableKeyGenerator
{
	/**
	 * 生成主键
	 * @param sqlExecutor
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	String generateKey(ISqlExecutor sqlExecutor,String tableName) throws Exception;
	
	/**
	 * 更新主键
	 * @param sqlExecutor
	 * @param tableName
	 * @param keyValue
	 * @throws Exception
	 */
	void updateKey(ISqlExecutor sqlExecutor,String tableName,String keyValue) throws Exception;
}