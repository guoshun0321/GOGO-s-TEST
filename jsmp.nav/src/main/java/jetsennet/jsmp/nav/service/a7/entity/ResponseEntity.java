package jetsennet.jsmp.nav.service.a7.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponseEntity
{

	private String name;

	private String value;

	private Map<String, String> attrMap;

	private List<ResponseEntity> children;

	public ResponseEntity(String name)
	{
		this.name = name;
		this.attrMap = new HashMap<String, String>(30);
		this.children = new ArrayList<ResponseEntity>(15);
	}

	public ResponseEntity(String name, String value)
	{
		this.name = name;
		this.value = value;
		this.attrMap = new HashMap<String, String>(30);
		this.children = new ArrayList<ResponseEntity>(15);
	}

	public void addAttr(String key, String value)
	{
		this.attrMap.put(key, value);
	}

	public void addAttr(String key, int value)
	{
		this.attrMap.put(key, Integer.toString(value));
	}

	public void addChild(ResponseEntity child)
	{
		this.children.add(child);
	}

	public int getChildSize()
	{
		return this.children.size();
	}

	public String getChildSizeS()
	{
		return Integer.toString(this.children.size());
	}

	public StringBuilder toXml(StringBuilder sb)
	{
		if (sb == null)
		{
			sb = new StringBuilder(1000);
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		}
		sb.append("<");
		this.appendLabel(sb, name);
		Set<String> attrKeys = attrMap.keySet();
		for (String attrKey : attrKeys)
		{
			String attrValue = attrMap.get(attrKey);
			sb.append(" ");
			this.appendLabel(sb, attrKey).append("=\"");
			this.appendValue(sb, attrValue).append("\"");
		}
		sb.append(">");
		for (ResponseEntity child : children)
		{
			child.toXml(sb);
		}
		if (this.value != null)
		{
			sb.append(this.value);
		}
		sb.append("</");
		this.appendLabel(sb, name).append(">");
		return sb;
	}

	private StringBuilder appendLabel(StringBuilder sb, String str)
	{
		if (str == null)
		{
			sb.append("");
		}
		else
		{
			int length = str.length();
			for (int i = 0; i < length; i++)
			{
				char c = str.charAt(i);
				if (c == '&')
				{
					sb.append("&amp;");
				}
				else if (c == '<')
				{
					sb.append("&lt;");
				}
				else if (c == '>')
				{
					sb.append("&gt;");
				}
				else
				{
					sb.append(c);
				}
			}
		}
		return sb;
	}

	private StringBuilder appendValue(StringBuilder sb, String str)
	{

		if (str != null)
		{
			int length = str.length();
			for (int i = 0; i < length; i++)
			{
				char c = str.charAt(i);
				if (c == '"')
				{
					sb.append("\"");
				}
				else
				{
					sb.append(c);
				}
			}
		}
		return sb;
	}

}
