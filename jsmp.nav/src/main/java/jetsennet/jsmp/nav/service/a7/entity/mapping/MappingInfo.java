package jetsennet.jsmp.nav.service.a7.entity.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import jetsennet.orm.tableinfo.FieldInfo;

/**
 *  实体到结果的关系映射
 *  
 * @author 郭祥
 */
public class MappingInfo
{

	private String idName;

	private Map<String, FieldInfo> attrMap;

	public MappingInfo(String idName)
	{
		this.idName = idName;
		this.attrMap = new LinkedHashMap<>();
	}

	public void addAttr(String key, FieldInfo field)
	{
		this.attrMap.put(key, field);
	}

	public String getIdName()
	{
		return idName;
	}

	public Map<String, FieldInfo> getAttrMap()
	{
		return attrMap;
	}

}
