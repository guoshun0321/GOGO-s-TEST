/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * SQL 数据类型
 * @author 李小敏
 */
public enum SqlParamType
{
    String,    
    Numeric,   
    DateTime,    
    Boolean,    
    /**
     * value is SqlQuery.toXml()
     */
    SqlSelectCmd,  
    /**
     * 有的时候FIELD_A=FIELD_B，无法判断类型，无法做类型验证
     */
    Field,
    UnKnow,
    Text;
    
    /**
     * @param val
     * @return SqlParamType
     */
    public static SqlParamType valueOf(int val)
    {
    	switch(val)
    	{
	    	case 0:
	    		return String;
	    	case 1:
	    		return Numeric;
	    	case 2:
	    		return DateTime;
	    	case 3:
	    		return Boolean;
	    	case 4:
	    		return SqlSelectCmd;
	    	case 5:
	    		return Field;
	    	case 10:
	    		return UnKnow;
	    	case 50:
	    		return Text;
    	}
    	return UnKnow;
    }
    /**
     * @return SqlParamType int
     */
    public int toInteger()
    {
        switch(this)
        {
            
            case String:
                return 0;
            case Numeric:
                return 1;
            case DateTime:
                return 2;   
            case Boolean:
                return 3;   
            case SqlSelectCmd:
                return 4;   
            case Field:
            	return 5;
            case UnKnow:
                return 10; 
            case Text:
                return 50; 
        }
        return 0;
    } 
}