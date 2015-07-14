/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * SQL 逻辑操作类型
 * @author 李小敏
 */
public enum SqlLogicType
{    
    And,    
    Or,    
    AndAll,    
    OrAll;
    /**
     * @param val
     * @return SqlLogicType
     */
    public static SqlLogicType valueOf(int val)
    {
    	switch(val)
    	{
    	case 0:
    		return And;
    	case 1:
    		return Or;
    	case 2:
    		return AndAll;
    	case 3:
    		return OrAll;    		
    	}
    	return And;
    }
    /**
     * @return SqlLogicType int
     */
    public int toInteger()
    {
        switch(this)
        {
            
            case And:
                return 0;
            case Or:
                return 1;
            case AndAll:
                return 2;   
            case OrAll:
                return 3;               
        }
        return 0;
    }
}