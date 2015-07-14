/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 表达式解析前接口
 * @author 李小敏
 */
public interface IConditionOperationBefore {
	void operationBefore(SqlCondition p) throws Exception;
}