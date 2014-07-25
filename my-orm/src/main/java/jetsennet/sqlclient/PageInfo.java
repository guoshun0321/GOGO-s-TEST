/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.io.Serializable;

import jetsennet.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * 分页信息
 * @author 李小敏
 */
public class PageInfo implements Serializable,ISerializer {

	private static final long serialVersionUID = 1L;
	
	private int pageSize=1000;
	private int pageCount=0;
	private int currentPage=1;
	private int rowCount=0;
	private String extendInfo="";
	
	public int getPageSize()
	{
		return this.pageSize;
	}
	public void setPageSize(int value)
	{
		this.pageSize = value;
	}
	
	public int getPageCount()
	{
		return this.pageCount;
	}
	public void setPageCount(int value)
	{
		this.pageCount = value;
	}
	
	public int getCurrentPage()
	{
		return this.currentPage;
	}
	public void setCurrentPage(int value)
	{
		this.currentPage = value;
	}
	
	public int getRowCount()
	{
		return this.rowCount;
	}
	public void setRowCount(int value)
	{
		this.rowCount = value;
	}
	
	public String getExtendInfo()
	{
		return this.extendInfo;
	}
	public void setExtendInfo(String value)
	{
		this.extendInfo = value;
	}
	
	
	private  void loadXml(Node node)
    {		
        if (node == null)
            return;            
        Node subNode = node.selectSingleNode("PageSize");
        if (subNode != null)
            this.setPageSize(StringUtil.isNullOrEmpty(subNode.getText()) ? 0 :  Integer.parseInt(subNode.getText()));
        subNode = node.selectSingleNode("CurrentPage");
        if (subNode != null)
            this.setCurrentPage(StringUtil.isNullOrEmpty(subNode.getText()) ? 0 : Integer.parseInt(subNode.getText()));
        subNode = node.selectSingleNode("RowCount");
        if (subNode != null)
            this.setRowCount(StringUtil.isNullOrEmpty(subNode.getText()) ? 0 : Integer.parseInt(subNode.getText()));
        subNode = node.selectSingleNode("PageCount");
        if (subNode != null)
            this.setPageCount( StringUtil.isNullOrEmpty(subNode.getText()) ? 0 : Integer.parseInt(subNode.getText())); 
        subNode = node.selectSingleNode("ExtendInfo");
        if (subNode != null)
            this.setExtendInfo(subNode.getText().replace("'","").replace(";",""));
    }
	public String serialize(String rootName)
	{
		rootName = StringUtil.isNullOrEmpty(rootName) ? "PageInfo" : rootName;

        StringBuilder sbText = new StringBuilder();
        sbText.append("<" + rootName + ">");
        sbText.append(String.format("<CurrentPage>%s</CurrentPage>", this.currentPage));
        sbText.append(String.format("<PageSize>%s</PageSize>", this.pageSize));
        sbText.append(String.format("<RowCount>%s</RowCount>", this.rowCount));
        sbText.append(String.format("<PageCount>%s</PageCount>", this.pageCount));
        sbText.append(SerializerUtil.serializeNode("ExtendInfo", this.extendInfo)); 
        sbText.append("</" + rootName + ">");
        return sbText.toString();
	}
	public void deserialize(String objXml,String rootName)
	{
		if (StringUtil.isNullOrEmpty(objXml))
            return;
		try
		{
			Document doc = DocumentHelper.parseText(objXml);			
	        loadXml(doc.getRootElement());
		}
		catch(Exception ex)
		{
		}
	}
}