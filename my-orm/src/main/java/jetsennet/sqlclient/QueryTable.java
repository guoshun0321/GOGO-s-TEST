/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
 ************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import jetsennet.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 查询表
 * @author 李小敏
 */
public class QueryTable implements ISerializer
{
	public QueryTable()
	{
	}

	/**
	 * 查询表
	 * @param tableName 表名
	 * @param aliasName 别名
	 */
	public QueryTable(String tableName, String aliasName)
	{
		this.tableName = tableName;
		this.aliasName = aliasName;
	}

	public String tableName = "";

	public String aliasName = "";

	public List<JoinTable> joinTables;

	/**
	 * 添加连接表
	 * @param tableName
	 * @param aliasTableName
	 * @param joinContion
	 * @param joinType
	 */
	public void AddJoinTable(String tableName, String aliasTableName, String joinContion, TableJoinType joinType)
	{
		if (joinTables == null)
			joinTables = new ArrayList<JoinTable>();
		joinTables.add(new JoinTable(new QueryTable(tableName, aliasTableName), joinContion, joinType));
	}

	@SuppressWarnings("unchecked")
	public void deserialize(String serializedXml, String rootName)
	{
		if (StringUtil.isNullOrEmpty(serializedXml))
			return;

		Document xml;

		try
		{
			xml = DocumentHelper.parseText(serializedXml);
		}
		catch (Exception ex)
		{
			return;
		}
		Node node = xml.getRootElement();

		Node subNode = node.selectSingleNode("TableName");
		if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
			this.tableName = subNode.getText().replace("'", "").replace(";", "");

		subNode = node.selectSingleNode("AliasName");
		if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
			this.aliasName = subNode.getText().replace("'", "").replace(";", "");

		subNode = node.selectSingleNode("JoinTables");
		if (subNode != null)
		{
			List<Node> nodeList = ((Element) subNode).elements();
			this.joinTables = new ArrayList<JoinTable>();
			for (int i = 0; i < nodeList.size(); i++)
			{
				JoinTable temp = SerializerUtil.deserialize(JoinTable.class, nodeList.get(i).asXML());
				if (temp != null)
					this.joinTables.add(temp);
			}
		}
	}

	public String serialize(String rootName)
	{
		rootName = StringUtil.isNullOrEmpty(rootName) ? "QueryTable" : rootName;

		StringBuilder sbText = new StringBuilder();
		sbText.append("<" + rootName + ">");

		sbText.append(StringUtil.format("<TableName>%s</TableName>", XmlUtil.escapeXml(this.tableName)));
		sbText.append(StringUtil.format("<AliasName>%s</AliasName>", this.aliasName));
		if (this.joinTables != null)
		{
			sbText.append("<JoinTables>");
			for (JoinTable j : this.joinTables)
			{
				sbText.append(j.serialize("JoinTable"));
			}
			sbText.append("</JoinTables>");
		}

		sbText.append("</" + rootName + ">");
		return sbText.toString();
	}
}