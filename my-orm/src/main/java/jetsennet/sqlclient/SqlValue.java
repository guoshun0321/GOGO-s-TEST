/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.sql.Timestamp;
import java.util.*;

import jetsennet.util.DateUtil;
import jetsennet.util.StringUtil;


/**
 * SQL 参数值
 * @author 李小敏
 */
public class SqlValue {

	private String value;
	private SqlParamType sqlType;
	
	public String getValue()
	{
		return value;
	}
	public void setValue(String val)
	{
		value = val;
	}
	
	public SqlParamType getSqlType()
	{
		return sqlType;
	}
	
	public SqlValue(int val)
	{
		this.value = String.valueOf(val);
		this.sqlType = SqlParamType.Numeric;
	}
	
	public SqlValue(double val)
	{
		this.value = String.valueOf(val);
		this.sqlType = SqlParamType.Numeric;
	}
	
	public SqlValue(float val)
	{
		this.value = String.valueOf(val);
		this.sqlType = SqlParamType.Numeric;
	}
	
	public SqlValue(String val)
	{
		this.sqlType = SqlParamType.String;
		
		if(val == null)
			return;
		
		this.value = String.valueOf(val);		
	}
	public SqlValue(Date val)
	{		
		this.value = DateUtil.formatDateString(val,"yyyy-MM-dd HH:mm:ss");
		this.sqlType = SqlParamType.DateTime;
	}
	
	public SqlValue(Object obj,SqlParamType sqlType)
	{
		this.sqlType = sqlType;
		
		if (obj == null)
            return;        
		
        switch (sqlType)
        {
            case DateTime:            	
            	if (obj.getClass()==java.util.Date.class || obj.getClass() == Timestamp.class)
            		this.setValue(DateUtil.formatDateString((java.util.Date)obj,"yyyy-MM-dd HH:mm:ss"));
            	else
            		this.setValue(String.valueOf(obj));
                break;
            case Numeric:
                this.setValue(String.valueOf(obj));
                break;
            case Boolean:
                this.setValue(Boolean.parseBoolean(String.valueOf(obj)) ? "1" : "0");
                break;
            default:
                this.setValue(String.valueOf(obj));
                break;
        }
	}
	
	
	public String toString()
    {
    	return StringUtil.format("%s[%s]",value,sqlType);
    }
}