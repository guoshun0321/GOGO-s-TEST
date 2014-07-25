/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 设置值接口
 * @author 李小敏
 */
public interface IValueSet {
	
	/**
	 * 设置值
	 * @param fieldName
	 * @param fieldValue
	 */
	void setValue(String fieldName, Object fieldValue);
}