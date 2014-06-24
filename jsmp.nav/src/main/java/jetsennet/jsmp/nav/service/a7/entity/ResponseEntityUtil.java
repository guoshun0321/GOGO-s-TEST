package jetsennet.jsmp.nav.service.a7.entity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.util.SafeDateFormater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseEntityUtil
{

	private static ConcurrentMap<Class<?>, ResponseObjInfo> infoMap = new ConcurrentHashMap<Class<?>, ResponseObjInfo>();
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResponseEntityUtil.class);

	public static ResponseEntity obj2Resp(Object obj, String name, ResponseEntity resp)
	{
		if (resp == null)
		{
			resp = new ResponseEntity(name);
		}
		try
		{
			ResponseObjInfo info = ensureEntityInfo(obj.getClass());
			Set<String> keys = info.fieldMap.keySet();
			for (String key : keys)
			{
				Field field = info.fieldMap.get(key);
				Object oValue = field.get(obj);
				resp.addAttr(key, info.getStringValue(field, oValue));
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
		return resp;
	}

	private static ResponseObjInfo ensureEntityInfo(Class<?> cls)
	{
		ResponseObjInfo retval = null;
		if (infoMap.containsKey(cls))
		{
			retval = infoMap.get(cls);
		}
		else
		{
			retval = new ResponseObjInfo();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields)
			{
				IdentAnnocation id = field.getAnnotation(IdentAnnocation.class);
				if (id != null)
				{
					field.setAccessible(true);
					retval.fieldMap.put(id.value(), field);

					// 值转换处理
					// 枚举值
					String enumValueStr = id.enumValue().trim();
					if (!enumValueStr.isEmpty())
					{
						String[] entries = enumValueStr.split(",");
						Map<String, String> tempMap = new HashMap<>();
						for (String entry : entries)
						{
							String[] enumValueTuple = entry.split(":");
							if (enumValueTuple != null && enumValueTuple.length == 2)
							{
								tempMap.put(enumValueTuple[0], enumValueTuple[1]);
							}
						}
						retval.enumMap.put(field, tempMap);
					}
				}
			}
			ResponseObjInfo temp = infoMap.putIfAbsent(cls, retval);
			if (temp != null)
			{
				retval = temp;
			}
		}
		return retval;
	}

	private static class ResponseObjInfo
	{
		public Map<String, Field> fieldMap = new HashMap<String, Field>();

		public Map<Field, Map<String, String>> enumMap = new HashMap<Field, Map<String, String>>();

		public String getStringValue(Field field, Object oValue)
		{
			String type = field.getAnnotation(IdentAnnocation.class).type();
			String enumValue = field.getAnnotation(IdentAnnocation.class).enumValue();

			// 处理null
			if (oValue == null)
			{
				return "";
			}

			String value = oValue.toString();
			// 处理枚举类型
			if (enumValue != null && !enumValue.isEmpty())
			{
				Map<String, String> enumValueMap = enumMap.get(field);
				if (enumValueMap.containsKey(value))
				{
					return enumValueMap.get(value);
				}
				else
				{
					throw new UncheckedNavException("找不到枚举对应的值：" + value);
				}
			}

			// 处理时间类型
			if (type.equalsIgnoreCase("date"))
			{
				Date tempDate = null;
				if (field.getType() == long.class)
				{
					tempDate = new Date(Long.valueOf(value));
				}
				else if (field.getType() == Date.class)
				{
					tempDate = (Date) oValue;
				}
				else
				{
					throw new UncheckedNavException("不正确的数据类型：" + field.getType());
				}
				return dateFormat(tempDate);
			}

			return value;
		}
	}

	public static String dateFormat(Date date)
	{
		return SafeDateFormater.format(date, "YYYYMMDDHHmmss");
	}

	public static String dateFormat(long dateL)
	{
		return SafeDateFormater.format(new Date(dateL), "YYYYMMDDHHmmss");
	}

}
