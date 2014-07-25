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
import org.dom4j.Node;

import jetsennet.util.*;

/**
 * 连接表
 * @author 李小敏
 */
public class JoinTable  implements ISerializer{
	
	public JoinTable()
	{
	
	}

    /**连接表
     * @param table 表信息
     * @param joinCondition 连接条件
     * @param joinType 连接类型
     */
    public JoinTable(QueryTable table, String joinCondition, TableJoinType joinType)
    {
        this.queryTable = table;
        this.joinCondition = joinCondition;
        this.joinType = joinType;
    }
    
    /**
     * 表信息
     */
    public QueryTable queryTable;
   
    /**
     * 连接条件
     */
    public String joinCondition = "";
   
    /**
     * 连接类型
     */
    public TableJoinType joinType = TableJoinType.Inner;

   
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
        Node node = xml.getRootElement();

        Node subNode = node.selectSingleNode("JoinCondition");
        if (subNode != null)
            this.joinCondition = subNode.getText();

        subNode = node.selectSingleNode("JoinType");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.joinType = TableJoinType.valueOf(Integer.parseInt(subNode.getText()));

        subNode = node.selectSingleNode("QueryTable");
        if (subNode != null)
            this.queryTable = SerializerUtil.deserialize(QueryTable.class,subNode.asXML());
    }

    public String serialize(String rootName)
    {
    	rootName = StringUtil.isNullOrEmpty(rootName) ? "JoinTable" : rootName;

        StringBuilder sbText = new StringBuilder();
        sbText.append("<" + rootName + ">");

        sbText.append(StringUtil.format("<JoinCondition>%s</JoinCondition>", XmlUtil.escapeXml(this.joinCondition)));
        sbText.append(String.format("<JoinType>%s</JoinType>", this.joinType.toInteger()));
        if(this.queryTable!=null)
            sbText.append(this.queryTable.serialize("QueryTable"));

        sbText.append("</" + rootName + ">");
        return sbText.toString();
    }
}