package jetsennet.jsmp.nav.media.jms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.media.db.DataSourceManager;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步协议XML解析
 * 
 * @author 郭祥
 */
public class DataSynXmlParse
{

	/**
	 * DataSynEntity类字段信息
	 */
	private static final Map<String, Field> fieldMap = DataSynEntity.getEntityInfo();
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataSynXmlParse.class);

	static
	{
		try
		{
			Field[] fields = DataSynEntity.class.getDeclaredFields();
			for (Field field : fields)
			{
				IdentAnnocation id = field.getAnnotation(IdentAnnocation.class);
				if (id != null)
				{
					field.setAccessible(true);
					fieldMap.put(id.value(), field);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	public static DataSynEntity parseXml(String xml)
	{
		DataSynEntity retval = new DataSynEntity();
		InputStream in = null;
		try
		{
			SAXBuilder builder = new SAXBuilder(false);
			in = new ByteArrayInputStream(xml.getBytes());
			Document doc = builder.build(in);

			Element root = doc.getRootElement();
			Element headerEle = root.getChild("header");
			parseHeaderEle(headerEle, retval);

			Element bodyEle = root.getChild("body");
			parseBodyEles(bodyEle, retval);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new DataSynException(ex);
		}
		return retval;
	}

	/**
	 * 解析xml头部
	 * 
	 * @param headerEle
	 * @param entity
	 * @return
	 */
	private static DataSynEntity parseHeaderEle(Element headerEle, DataSynEntity entity)
	{
		try
		{
			List<Element> children = headerEle.getChildren();
			for (Element child : children)
			{
				String key = child.getName().trim();
				String value = child.getText().trim();
				Field f = fieldMap.get(key);
				if (f != null)
				{
					Class<?> type = f.getType();
					if (type == String.class)
					{
						f.set(entity, value);
					}
					else if (type == long.class)
					{
						f.set(entity, Long.valueOf(value));
					}
					else if (type == DataSynMsgTypeEnum.class)
					{
						f.set(entity, DataSynMsgTypeEnum.valueOf(value));
					}
					else if (type == DataSynOpCodeEnum.class)
					{
						f.set(entity, DataSynOpCodeEnum.valueOf(value));
					}
					else
					{
						throw new DataSynException("不支持的数据类型：" + type);
					}
				}
				else
				{
					logger.debug("无效字段：" + key);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new DataSynException(ex);
		}
		return entity;
	}

	/**
	 * 解析body部分信息
	 * 
	 * @param bodyEle
	 * @param entity
	 * @return
	 */
	private static DataSynEntity parseBodyEles(Element bodyEle, DataSynEntity entity)
	{
		try
		{
			List<Element> children = bodyEle.getChildren();
			for (Element child : children)
			{
				int opFlag = getAttrInt(child, "opFlag");
				String eleName = child.getName();

				List<Object> objs = new ArrayList<>();
				List<Element> contentChildren = child.getChildren();
				if (contentChildren != null && !contentChildren.isEmpty())
				{
					for (Element contentChild : contentChildren)
					{
						objs.add(content2Entity(contentChild));
					}
				}
				DataSynContentEntity content = new DataSynContentEntity(eleName, objs);
				content.setOpFlag(opFlag);
				entity.addContent(content);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new DataSynException(ex);
		}
		return entity;
	}

	private static Object content2Entity(Element ele)
	{
		String name = ele.getName();
		TableInfo table = DataSourceManager.MEDIA_FACTORY.getTableInfo(name);
		if (table == null)
		{
			throw new DataSynException("未知表结构：" + name);
		}
		Object retval = null;
		try
		{
			retval = table.getCls().newInstance();
		}
		catch (Exception ex)
		{
			throw new DataSynException(ex);
		}
		if (Config.ISDEBUG)
		{
			logger.debug("表名：" + name);
		}
		List<Element> children = ele.getChildren();
		for (Element child : children)
		{
			String key = child.getName().trim();
			String value = child.getText().trim();
			if (Config.ISDEBUG)
			{
				logger.debug(String.format("处理参数：%s, %s", key, value));
			}
			FieldInfo fieldInfo = table.getFieldInfo(key);
			if (fieldInfo != null)
			{
				setValue(fieldInfo, retval, value);
			}
			else
			{
				if (Config.ISDEBUG)
				{
					logger.debug("不处理参数：" + key);
				}
			}
		}
		return retval;
	}

	private static void setValue(FieldInfo field, Object obj, String value)
	{
		Class<?> fieldClass = field.getCls();
		if (value.equalsIgnoreCase("null"))
		{
			if (fieldClass == int.class
				|| fieldClass == long.class
				|| fieldClass == double.class
				|| fieldClass == short.class
				|| fieldClass == char.class)
			{
				field.set(obj, 0);
			}
			else if (fieldClass == String.class)
			{
				field.set(obj, "");
			}
			else
			{
				field.set(obj, value);
			}
		}
		else
		{
			field.set(obj, value);
		}
	}

	private static int getAttrInt(Element ele, String name)
	{
		String temp = ele.getAttributeValue(name);
		return Integer.valueOf(temp);
	}
}
