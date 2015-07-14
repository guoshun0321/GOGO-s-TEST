/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 表连接类型
 * @author 李小敏
 */
public enum TableJoinType
{
	Inner,    
	Left,   
	Right,    
	All;
    /**
     * @param val
     * @return TableJoinType
     */
    public static TableJoinType valueOf(int val)
    {
    	switch(val)
    	{
	    	case 0:
	    		return Inner;
	    	case 1:
	    		return Left;
	    	case 2:
	    		return Right;
	    	case 3:
	    		return All;	    	
    	}
    	return Inner;
    }
    /**
     * @return TableJoinType int
     */
    public int toInteger()
    {
        switch(this)
        {
            
            case Inner:
                return 0;
            case Left:
                return 1;
            case Right:
                return 2;   
            case All:
                return 3;   
        }
        return 0;
    } 
}