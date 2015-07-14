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
 * SQL查询
 * @author 李小敏
 */
public class SqlQuery implements ISerializer{

	public SqlQuery()
	{	
	}
	
	/**
	 * 查询对象
	 * @param pInfo
	 * @param keyId
	 * @param tableName
	 * @param resultFields
	 * @param orderString
	 * @param conditions
	 */
	public SqlQuery(PageInfo pInfo,String keyId,String tableName, String resultFields, String orderString,SqlCondition... conditions)
    {
        this.pageInfo = pInfo;
        this.isPageResult = pInfo == null ? false : true;
        this.resultFields = resultFields; 
        this.orderString = orderString;
        this.keyId = keyId;
        this.conditions = conditions;
        
        if (!StringUtil.isNullOrEmpty(tableName))
        {
            this.queryTable = new QueryTable(tableName, null);
        }
    }
	
	/**
    *主键
    */
    public String keyId = "";
    /**
    *排序
    */
    public String orderString = "";
    /**
    *记录集根名称
    */
    public String recordSetName = "RecordSet";
    /**
    *记录项名称
    */
    public String recordName = "Record";
    /**
    *是否分页
    */
    public boolean isPageResult = true;
    /**
    *分页信息
    */
    public PageInfo pageInfo;
    /**
    *查询条件
    */
    public SqlCondition[] conditions;
    /**
    *分组字段
    */
    public String groupFields = "";
    /**
    *是否排除重复
    */
    public boolean isDistinct = false;
    /**
    *取记录数
    */
    public int topRows = 0;
    /**
    *结果字段
    */
    public String resultFields = "";
    /**
    *查询表
    */
    public QueryTable queryTable;
    /**
    *合并查询
    */
    public UnionQuery unionQuery;
    
    /**
    *反序列化
    */   
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
        
        Node subNode = node.selectSingleNode("KeyId");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.keyId = subNode.getText().replace("'", "").replace(";", "");

        subNode = node.selectSingleNode("OrderString");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.orderString = subNode.getText().replace("'", "").replace(";", "");

        subNode = node.selectSingleNode("RecordSetName");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.recordSetName = subNode.getText();

        subNode = node.selectSingleNode("RecordName");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.recordName = subNode.getText();

        subNode = node.selectSingleNode("IsPageResult");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.isPageResult = (subNode.getText().equals("1") || subNode.getText().equals("true"))?true:false;

        subNode = node.selectSingleNode("IsDistinct");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.isDistinct = (subNode.getText().equals("1") || subNode.getText().equals("true")) ? true : false;

        subNode = node.selectSingleNode("PageInfo");
        if (subNode != null)
            this.pageInfo = SerializerUtil.deserialize(PageInfo.class,subNode.asXML());

        subNode = node.selectSingleNode("Conditions");
        if (subNode != null)
            this.conditions = SqlCondition.loadXml(subNode.asXML());

        subNode = node.selectSingleNode("ResultFields");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.resultFields = subNode.getText().replace(";", "");

        subNode = node.selectSingleNode("GroupFields");
        if (subNode != null && !StringUtil.isNullOrEmpty(subNode.getText()))
            this.groupFields = subNode.getText().replace(";", "");

        subNode = node.selectSingleNode("TopRows");
        if (subNode != null)
            this.topRows = StringUtil.isNullOrEmpty(subNode.getText()) ? 0 : Integer.parseInt(subNode.getText());

        subNode = node.selectSingleNode("QueryTable");
        if (subNode != null)
            this.queryTable = SerializerUtil.deserialize(QueryTable.class,subNode.asXML());
        
        subNode = node.selectSingleNode("UnionQuery");
        if (subNode != null)
            this.unionQuery = SerializerUtil.deserialize(UnionQuery.class,subNode.asXML());
    }
    
    /**
    *序列化
    */           
    public String serialize(String rootName)
    {
    	rootName = StringUtil.isNullOrEmpty(rootName) ? "SqlQuery" : rootName;

        StringBuilder sbText = new StringBuilder();
        sbText.append("<"+rootName+">");
        sbText.append(StringUtil.format("<KeyId>%s</KeyId>",this.keyId));
        sbText.append(StringUtil.format("<OrderString>%s</OrderString>", this.orderString));
        sbText.append(StringUtil.format("<RecordSetName>%s</RecordSetName>", this.recordSetName));
        sbText.append(StringUtil.format("<RecordName>%s</RecordName>", this.recordName));
        sbText.append(StringUtil.format("<IsPageResult>%s</IsPageResult>", this.isPageResult?1:0));
        sbText.append(StringUtil.format("<IsDistinct>%s</IsDistinct>", this.isDistinct?1:0));
        sbText.append(StringUtil.format("<ResultFields>%s</ResultFields>", this.resultFields));
        sbText.append(StringUtil.format("<GroupFields>%s</GroupFields>", XmlUtil.escapeXml(this.groupFields)));
        sbText.append(StringUtil.format("<TopRows>%s</TopRows>", this.topRows));
        
        if(this.pageInfo!=null)
        {
            sbText.append(this.pageInfo.serialize("PageInfo"));
        }
        
        if (this.conditions!=null)
        {
            sbText.append("<Conditions>");
            for(SqlCondition c : this.conditions)
            {
                sbText.append(c.serialize("SC"));
            }
            sbText.append("</Conditions>");
        }  
       
        if(this.queryTable != null)
        {
            sbText.append(this.queryTable.serialize("QueryTable"));
        }
        
        if(this.unionQuery != null)
        {
            sbText.append(this.unionQuery.serialize("UnionQuery"));
        }
        
        sbText.append("</" + rootName + ">");

        return sbText.toString();
    }
}