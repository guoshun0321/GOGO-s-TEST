/************************************************************************
日  期：	   2013-02-30
作  者:	   李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.HashMap;

/**
 * 数据表
 * @author 李小敏
 */
public class DataMap extends HashMap<String,String> implements IValueSet
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 设置值
	 */
	public void setValue(String fieldName,Object fieldValue)
	{
		if(fieldValue != null)
		{
			this.put(fieldName,String.valueOf(fieldValue));
		}
	}
}