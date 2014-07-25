/************************************************************************
日  期：		2012-04-23
作  者:		李小敏
版  本：      1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import org.dom4j.Document;

/**
 * 
 * @author 李小敏
 */
public class DB2Executor extends SqlExecutor {

	public DB2Executor(ConnectionInfo cInfo) {
		super(cInfo);
	}
	
	@Override
	public Document getSchema() throws Exception{
		return this.fill("SELECT TABNAME as TABLE_NAME FROM SYSCAT.TABLES WHERE TYPE='T' AND TABSCHEMA='DBO'");
	}
}