/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import jetsennet.util.*;

/**
 * SQL字段
 * @author 李小敏
 */
public class SqlField  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fieldName;
	private String fieldValue;
	private SqlParamType sqlParamType = SqlParamType.String;
	private boolean isSecurity;
	
	public String getFieldName()
	{
		return this.fieldName;
	}
	public void setFieldName(String value)
	{
		this.fieldName = value;
	}
			
	public String getFieldValue()
	{
		return this.fieldValue;
	}
	public void setFieldValue(String value)
	{
		this.fieldValue = value;
	}
	
	public SqlParamType getSqlParamType()
	{
		return this.sqlParamType;
	}
	public void setSqlParamType(SqlParamType value)
	{
		this.sqlParamType = value;
	}
	
	public boolean getIsSecurity()
	{
		return this.isSecurity;
	}
	public void setIsSecurity(boolean value)
	{
		this.isSecurity = value;
	}
	public SqlField(String fieldName,int fieldValue)
	{
		this.fieldName = fieldName;
		this.sqlParamType = SqlParamType.Numeric;
		setValue(fieldValue);
	}
	public SqlField(String fieldName,Date fieldValue)
	{
		this.fieldName = fieldName;
		this.sqlParamType = SqlParamType.DateTime;
		setValue(fieldValue);
	}
	public SqlField(String fieldName,String fieldValue)
	{
		this.fieldName = fieldName;
		this.sqlParamType = SqlParamType.String;
		setValue(fieldValue);
	}
	
	public SqlField(String fieldName,Object fieldValue,SqlParamType sqlParamType)
	{
		this.fieldName = fieldName;
		this.sqlParamType = sqlParamType;
		setValue(fieldValue);		
	}
	
	public SqlField(String fieldName,Object fieldValue,SqlParamType sqlParamType,boolean isSecurity)
	{
		this.fieldName = fieldName;
		this.sqlParamType = sqlParamType;
		this.isSecurity = isSecurity;
		setValue(fieldValue);		
	}
	
	public void setValue(Object obj)
	{
		if (obj == null)
            return;
        
        switch (this.sqlParamType)
        {
            case DateTime:            	
            	if (obj.getClass()==java.util.Date.class || obj.getClass() == Timestamp.class)
            		this.setFieldValue(DateUtil.formatDateString((java.util.Date)obj,"yyyy-MM-dd HH:mm:ss"));
            	else
            		this.setFieldValue(String.valueOf(obj));
                break;
            case Numeric:
                this.setFieldValue(String.valueOf(obj));
                break;
            case Boolean:
                this.setFieldValue(Boolean.parseBoolean(String.valueOf(obj)) ? "1" : "0");
                break;
            default:
                this.setFieldValue(String.valueOf(obj));
                break;
        }
	}		
    /**
     * 尝试创建SqlField,可能返回null
     * @param fieldName
     * @param fieldValue
     * @param paramType
     * @return SqlField or null
     */
    public static SqlField tryCreate(String fieldName, String fieldValue, SqlParamType paramType)
    {
        if (StringUtil.isNullOrEmpty(fieldName) || fieldValue == null)
            return null;

        return new SqlField(fieldName, fieldValue, paramType);
    }   
    /**
     * 尝试创建SqlField,可能返回null
     * @param fieldName
     * @param fieldValue
     * @return SqlField or null
     */
    public static SqlField tryCreate(String fieldName, String fieldValue)
    {
        if (StringUtil.isNullOrEmpty(fieldName) || fieldValue == null)
            return null;

        return new SqlField(fieldName, fieldValue);
    }
    
    public String toString()
    {
    	return StringUtil.format("%s:%s",fieldName,fieldValue);
    }
}