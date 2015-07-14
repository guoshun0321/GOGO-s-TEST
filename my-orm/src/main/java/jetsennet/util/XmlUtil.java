/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.util;

import org.dom4j.Node;

/**
 * XML相关操作
 * @author 李小敏
 */
public class XmlUtil {
	
	/**
	 * xml编码
	 * @param str
	 * @return
	 */
	public static String escapeXml(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return "";
		return str.replace("&", "&amp;").replace("<", "&lt;").replace(">","&gt;");
	}

	/**
	 * xml反编码
	 * @param str
	 * @return
	 */
	public static String unescapeXml(String str) {
		if (StringUtil.isNullOrEmpty(str))
			return "";
		return str.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;","&");
	}
	

	/**
	 * 获取XmlNode节点值
	 * @param node
	 * @param itemName
	 * @param defaultValue
	 * @return
	 */
	public static String tryGetItemText(Node node, String itemName,String defaultValue) {
		if (node == null)
			return defaultValue;

		Node item = node.selectSingleNode(itemName);
		if (item == null)
			return defaultValue;

		return item.getText();
	}
}