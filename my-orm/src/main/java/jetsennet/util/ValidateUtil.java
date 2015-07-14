/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.util;

/**验证相关
 * @author lixiaomin
 *
 */
public class ValidateUtil {
	
	/**是否数字格式
	 * @param val
	 * @return
	 */
	public static boolean isNumeric(String val)
    {
		if(val==null || val.length()==0)
			return false;
		
		String str = val.replaceAll("[,+-.\\s]","");
		return  str.matches("^[0-9]+$");       
    }
}