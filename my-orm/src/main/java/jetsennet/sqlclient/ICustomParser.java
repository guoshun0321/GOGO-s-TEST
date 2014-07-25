/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 自定义解析接口
 * @author 李小敏
 */
public interface  ICustomParser
{
	/**
	 * 自定义解析的接口
	 * @param c
	 * @return
	 */
	String parseCondition(SqlCondition c);

}