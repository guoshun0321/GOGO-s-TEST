/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

/**
 * 表联合类型
 * @author 李小敏
 */
public enum QueryUnionType {
	UnionAll,
    Union;
	public static QueryUnionType valueOf(int val)
    {
    	switch(val)
    	{
	    	case 0:
	    		return UnionAll;
	    	case 1:
	    		return Union;	    		
    	}
    	return UnionAll;
    }
}