/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.io.Serializable;
import java.lang.reflect.*;
import java.sql.Timestamp;
import java.util.HashMap;

import jetsennet.util.DateUtil;

/**
 * 数据模型的基类
 * @author 李小敏
 */
public class ModelBase implements Serializable,IValueSet 
{
	private static final long serialVersionUID = 1L;	
	protected static HashMap<String,String> fieldMap = new HashMap<String,String>();

	/**
	 * 设置字段值
	 */
	public void setValue(String fieldName, Object fieldValue) 
	{		
		if (fieldName != null && fieldName.length() > 0)
		{
			initialFieldMap();
						
			String fName = fieldMap.get(fieldName.toLowerCase());
			fName = fName == null ? fieldName:fName;
			
			try {
				Field f = this.getClass().getDeclaredField(fName);
				if(f != null)
				{
					f.setAccessible(true);
					Object obj = fieldValue;
					
					if(f.getType() == int.class)
					{
						obj = Integer.parseInt(String.valueOf(fieldValue));
					}
					else if (f.getType()==java.util.Date.class || f.getType() == Timestamp.class)
					{
						obj = DateUtil.parseDate(fieldValue);
					}
					else if(f.getType() == boolean.class)
					{
						obj = Boolean.parseBoolean(String.valueOf(fieldValue));
					}
					
					f.set(this, obj);
				}
			} catch (Exception ex) {
				//System.out.println(ex);
			}
		}
	}

	/**
	 * 获取字段值
	 * @param fieldName
	 * @return
	 */
	public Object getValue(String fieldName) 
	{
		if (fieldName == null || fieldName.length() == 0)
			return null;
		
		initialFieldMap();
		
		String fName = fieldMap.get(fieldName.toLowerCase());
		fName = fName == null ? fieldName:fName;
		
		try 
		{
			Field f = this.getClass().getDeclaredField(fName);
			if(f!=null)
			{
				f.setAccessible(true);
				return f.get(this);
			}
		} 
		catch (Exception ex) 
		{
		}
		
		return null;
	}

	protected synchronized void initialFieldMap()
	{
		if(fieldMap == null || fieldMap.isEmpty())
		{
			Field[] fields = this.getClass().getDeclaredFields();
			for(Field field : fields)
			{		
				fieldMap.put(field.getName().toLowerCase(),field.getName());
				
				FieldName fieldNameAnno = field.getAnnotation(FieldName.class);
				if(fieldNameAnno != null)
				{
					fieldMap.put(fieldNameAnno.name().toLowerCase(),field.getName());
				}				
			}
		}		
	}
}