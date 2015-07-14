package jetsennet.jbmp.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.util.FormatUtil;

/**
 * 解析由xml转换成String类型的字符串，获取字符串中id的值 2013-05-18
 * @author liwei
 */
public class ResolveXml
{
    /**
     * 解析传入的参数objId，获取id的值
     * @param objId 对象id
     * @return id id
     * @throws DocumentException 异常
     */
    public static String getXmlId(String objId) throws DocumentException
    {
        String id = "";
        Document xmlDoc = DocumentHelper.parseText(objId);
        Element rootNode = xmlDoc.getRootElement();
        List<Node> itemNodes = rootNode.selectNodes("Item");
        for (Node itemNode : itemNodes)
        {
            String xmlId = FormatUtil.tryGetItemText(itemNode, "Id", "-1");
            id = id + xmlId + "; ";
        }
        return id;
    }
}
