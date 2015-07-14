/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：     2012-05-23 添加SplitLike
************************************************************************/
package jetsennet.sqlclient;

/**
 * SQL关系操作类型
 * @author 李小敏
 */
public enum SqlRelationType
{    
    Equal,    
    Than,    
    Less,    
    ThanEqual,    
    LessEqual,   
    NotEqual,    
    Like,    
    In,   
    NotIn, 
    Between,    
    Custom,
    NotLike,
    /**
     * ab%c => like ab%c
     */
    CustomLike,
    IsNull,
    IsNotNull,
    Exists,
    NotExists,
    ILike,
    /**
     * abcD => toupper() like %ABCD%
     */
    IEqual,
    Parser,
    /**
     *a,b,c => like %a% or like %b% or like %c%
     */
    InLike,
    /**
    *a,b,c => not like %a% or not like %b% or not like %c%
    */
    NotInLike,
    /**
     * abc => %a%b%c
     */
    SplitLike;
    /**
     * @param val
     * @return SqlRelationType
     */
    public static SqlRelationType valueOf(int val)
    {
    	switch(val)
    	{
	    	case 0:
	    		return Equal;
	    	case 1:
	    		return Than;
	    	case 2:
	    		return Less;
	    	case 3:
	    		return ThanEqual;
	    	case 4:
	    		return LessEqual;
	    	case 5:
	    		return NotEqual;
	    	case 6:
	    		return Like;
	    	case 7:
	    		return In;
	    	case 8:
	    		return NotIn;
	    	case 9:
	    		return Between;
	    	case 10:
	    		return Custom;
	    	case 11:
	    		return NotLike;
	    	case 12:
	    		return CustomLike;
	    	case 13:
	    		return IsNull;
	    	case 14:
	    		return IsNotNull;
	    	case 15:
	    		return Exists;
	    	case 16:
	    		return NotExists;
	    	case 17:
	    		return ILike;
	    	case 18:
	    		return IEqual;
	    	case 19:
	    		return Parser;
	    	case 20:
	    		return InLike;
	    	case 21:
	    		return NotInLike;
	    	case 22:
	    		return SplitLike;
    	}
    	return Equal;
    }
    /**
     * @return SqlRelationType int
     */
    public int toInteger()
    {
        switch(this)
        {
            
            case Equal:
                return 0;
            case Than:
                return 1;
            case Less:
                return 2;   
            case ThanEqual:
                return 3; 
            case LessEqual:
                return 4; 
            case NotEqual:
                return 5;                 
            case Like:
                return 6; 
            case In:
                return 7;
            case NotIn:
                return 8;                
            case Between:
                return 9;
            case Custom:
                return 10;
            case NotLike:
                return 11;
            case CustomLike:
                return 12;
            case IsNull:
                return 13;
            case IsNotNull:
                return 14;
            case Exists:
                return 15;
            case NotExists:
                return 16;
            case ILike:
                return 17;
            case IEqual:
                return 18;
            case Parser:
                return 19;
            case InLike:
                return 20;
            case NotInLike:
                return 21;
            case SplitLike:
                return 22;
        }
        return 0;
    } 
}