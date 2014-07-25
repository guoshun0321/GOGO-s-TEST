/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import jetsennet.util.XmlUtil;
import jetsennet.util.ISerializer;
import jetsennet.util.StringUtil;

/**
 * SQL条件
 * @author 李小敏
 */
public class SqlCondition  implements ISerializer
{
    public SqlCondition() { }
    
    /**
    * SQL 搜索条件
    * @param paramName 参数名称
    * @param paramValue 参数值
    * @param logicType 参数关系类型(各参数间的关系)
    * @param relationType 参数条件类型
    * @param paramType 参数数据类型
    */
    public SqlCondition(String paramName, String paramValue, SqlLogicType logicType, SqlRelationType relationType, SqlParamType paramType)
    {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.sqlLogicType = logicType;
        this.sqlRelationType = relationType;
        this.sqlParamType = paramType;
        this.isSecurity = false;
    }
    /**
    * SQL 搜索条件
    * @param paramName 参数名称
    * @param paramValue 参数值
    * @param logicType 参数关系类型(各参数间的关系)
    * @param relationType 参数条件类型
    * @param paramType 参数数据类型
    * @param isSecurity 是否安全
    */
    public SqlCondition(String paramName, String paramValue, SqlLogicType logicType, SqlRelationType relationType, SqlParamType paramType, boolean isSecurity)
    {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.sqlLogicType = logicType;
        this.sqlRelationType = relationType;
        this.sqlParamType = paramType;
        this.isSecurity = isSecurity;
    }

    private String paramName = "";
    private String paramValue = "";
    private SqlLogicType sqlLogicType = SqlLogicType.And;
    private SqlRelationType sqlRelationType = SqlRelationType.Equal;
    private SqlParamType sqlParamType = SqlParamType.String;
    private boolean isSecurity;
    /**
    * 参数名称
    */
    public String getParamName()
    {
        return paramName;
    }
    public void setParamName(String value)
    {
    	this.paramName = value;
    }
    /**
    * 参数值
    */
    public String getParamValue()
    {
        return paramValue; 
    }
    public void setParamValue(String value)
    {
    	this.paramValue = value;
    }
    /**
    * 参数关系类型(各参数间的关系)
    */    
    public SqlLogicType getSqlLogicType()
    {
        return sqlLogicType;
    }
    public void setSqlLogicType(SqlLogicType value)
    {
    	this.sqlLogicType = value;
    }
    /**
    * 参数条件类型
    */
    public SqlRelationType getSqlRelationType()
    {
        return sqlRelationType;
    }
    public void setSqlRelationType(SqlRelationType value)
    {
    	this.sqlRelationType = value;
    }
    /**
    * 参数数据类型
    */
    public SqlParamType getSqlParamType()
    {
        return sqlParamType;
    }
    public void setSqlParamType(SqlParamType value)
    {
    	this.sqlParamType = value;
    }
    
    private List<SqlCondition> sqlConditions;
    /**
    * 子条件
    */
    public List<SqlCondition> getSqlConditions()
    {        
            if (sqlConditions == null)
                sqlConditions = new ArrayList<SqlCondition>();
            return sqlConditions;        
    }
    /**
    * 是否安全
    */
    public boolean getIsSecurity()
	{
		return this.isSecurity;
	}
	public void setIsSecurity(boolean value)
	{
		this.isSecurity = value;
	}
	
    /**
    * 反序列化条件数组
    */
	@SuppressWarnings("unchecked")
    public static SqlCondition[] loadXml(String xmlCondition)
    {
        if (StringUtil.isNullOrEmpty(xmlCondition))
            return null;
        
        List<SqlCondition> conArr = new ArrayList<SqlCondition>();
        try
		{
			Document doc = DocumentHelper.parseText(xmlCondition);
			List nodeList = doc.getRootElement().elements();//doc.getDocument().selectNodes("SqlConditions/SqlCondition");
	        for (int i = 0; i < nodeList.size(); i++)
	        {
	            SqlCondition c = _loadXml((Node)nodeList.get(i));
	            if (c != null)
	                conArr.add(c);
	        }			
		}
		catch(Exception ex)
		{
		} 
		if(conArr.size()==0)
			return null;
		SqlCondition[] a = new SqlCondition[conArr.size()];
        return conArr.toArray(a);
    }
	private static SqlCondition _loadXml(Node node)
    {
        if (node == null)
            return null;

        SqlCondition c = new SqlCondition();
        c.deserialize(node.asXML(), node.getName());

        return c;
    }
	
    @SuppressWarnings("unchecked")
    public void deserialize(String serializedXml, String rootName)
    {
    	if (StringUtil.isNullOrEmpty(serializedXml))
            return;

    	Document doc = null;
    	
    	try
    	{
    		doc = DocumentHelper.parseText(serializedXml);
    	}
    	catch(Exception ex){}
    	
        Node node = doc.getRootElement();
              

        Node subNode = node.selectSingleNode("PN");
        if(subNode==null)
        	subNode = node.selectSingleNode("ParamName");
        if (subNode != null)
            this.setParamName(subNode.getText());

        subNode = node.selectSingleNode("PV");
        if(subNode==null)
        	node.selectSingleNode("ParamValue");
        if (subNode != null)
        	this.setParamValue(subNode.getText());

        subNode = node.selectSingleNode("SPT");
        if(subNode==null)
        	subNode = node.selectSingleNode("SqlParamType");
        if (subNode != null)
        	this.setSqlParamType(SqlParamType.valueOf(Integer.parseInt(subNode.getText())));

        subNode = node.selectSingleNode("SRT");
        if(subNode==null)
        	subNode = node.selectSingleNode("SqlRelationType");
        if (subNode != null)
        	this.setSqlRelationType(SqlRelationType.valueOf(Integer.parseInt(subNode.getText())));

        subNode = node.selectSingleNode("SLT");
        if(subNode==null)
        	node.selectSingleNode("SqlLogicType");
        if (subNode != null)
        	this.setSqlLogicType(SqlLogicType.valueOf(Integer.parseInt(subNode.getText())));

        subNode = node.selectSingleNode("SCS");
        if(subNode==null)
        	node.selectSingleNode("SqlConditions");
        if (subNode != null)
        {
        	List<Element> nodeList = ((Element)subNode).elements();//selectNodes("SqlCondition");
            for (int i = 0; i < nodeList.size(); i++)
            {
            	SqlCondition subC = new SqlCondition();
                subC.deserialize(nodeList.get(i).asXML(), nodeList.get(i).getName());                
                if (subC != null)
                	this.getSqlConditions().add(subC);
            }
        }        
    }
    
    
    public String serialize(String rootName)
    {
        rootName = StringUtil.isNullOrEmpty(rootName) ? "SqlCondition" : rootName;

        StringBuilder sbText = new StringBuilder();
        sbText.append("<" + rootName + ">");
        sbText.append(StringUtil.format("<PN>%s</PN>", XmlUtil.escapeXml(this.getParamName())));
        sbText.append(StringUtil.format("<PV>%s</PV>", XmlUtil.escapeXml(this.getParamValue())));
        sbText.append(StringUtil.format("<SPT>%s</SPT>", this.getSqlParamType().toInteger()));
        sbText.append(StringUtil.format("<SRT>%s</SRT>", this.getSqlRelationType().toInteger()));
        sbText.append(StringUtil.format("<SLT>%s</SLT>", this.getSqlLogicType().toInteger()));
        if (this.sqlConditions != null)
        {
            sbText.append("<SCS>");
            for(SqlCondition c : this.sqlConditions)
            {
                sbText.append(c.serialize("SC"));
            }
            sbText.append("</SCS>");
        }            
        sbText.append("</" + rootName + ">");

        return sbText.toString();
    }
}