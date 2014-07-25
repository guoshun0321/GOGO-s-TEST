/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.util.*;

/**
 * 联合查询
 * @author 李小敏
 */
public class UnionQuery implements ISerializer{
	
	public QueryUnionType unionType = QueryUnionType.UnionAll;
    public SqlQuery sqlQuery;
   
    public void deserialize(String serializedXml, String rootName)
    {
        if (StringUtil.isNullOrEmpty(serializedXml))
            return;
        
        Document xml;
        
        try{
        	xml = DocumentHelper.parseText(serializedXml);
        }
        catch(Exception ex)
        {
        	return;
        }
        
        Element node = xml.getRootElement();

        Node subNode = node.selectSingleNode("UnionType");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
        {
            this.unionType = QueryUnionType.valueOf(Integer.parseInt(subNode.getText()));
        }

        subNode = node.selectSingleNode("SqlQuery");
        if (subNode != null)
        {
            this.sqlQuery = SerializerUtil.deserialize(SqlQuery.class,subNode.asXML());
        }
    }
   
    public String serialize(String rootName)
    {
        rootName = StringUtil.isNullOrEmpty(rootName) ? "UnionQuery" : rootName;

        StringBuilder sbText = new StringBuilder();
        sbText.append("<" + rootName + ">");

        sbText.append(String.format("<UnionType>%s</UnionType>", this.unionType));
        if (this.sqlQuery != null)
            sbText.append(this.sqlQuery.serialize("SqlQuery"));

        sbText.append("</" + rootName + ">");
        return sbText.toString();
    }
}