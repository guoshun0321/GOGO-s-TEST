package jetsennet.jsmp.nav.service.a7.entity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.util.SafeDateFormater;

public class RequestEntityUtil
{

	private static ConcurrentMap<Class<?>, RequestEntityInfo> infoMap = new ConcurrentHashMap<Class<?>, RequestEntityUtil.RequestEntityInfo>();
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(RequestEntityUtil.class);

	/**
	 * 将Map转换为实体
	 * 
	 * @param cls
	 * @param map
	 * @return
	 */
	public static <T> T map2Obj(Class<T> cls, Map<String, String> map)
	{
		T retval = null;
		try
		{
			RequestEntityInfo info = ensureEntityInfo(cls);
			retval = cls.newInstance();
			Set<String> keys = info.fieldMap.keySet();
			for (String key : keys)
			{
				Field field = info.fieldMap.get(key);
				String def = info.defValueMap.get(key);
				Object value = ensureFieldValue(key, field, def, map);
				field.set(retval, value);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
		return retval;
	}

	/**
	 * 目前支持int/String/boolean/Date类型
	 * 
	 * @param key
	 * @param f
	 * @param def
	 * @param map
	 * @return
	 */
	private static Object ensureFieldValue(String key, Field f, String def, Map<String, String> map)
	{
		Object retval = null;
		String valueS = map.get(key);
		valueS = valueS == null ? def : valueS;
		if (f.getType() == int.class)
		{
			retval = Integer.valueOf(valueS);
		}
		else if (f.getType() == String.class)
		{
			retval = valueS;
		}
		else if (f.getType() == boolean.class)
		{
			if (valueS.equalsIgnoreCase("Y") || valueS.equalsIgnoreCase("YES"))
			{
				retval = true;
			}
			else
			{
				retval = false;
			}
		}
		else if (f.getType() == Date.class)
		{
			retval = SafeDateFormater.parse(valueS, A7Constants.DATE_FORMATE);
		}
		else
		{
			throw new UncheckedNavException("不支持数据类型：" + f.getType());
		}
		return retval;
	}

	/**
	 * a7.entity实体解析信息
	 * 
	 * @param cls
	 * @return
	 */
	private static RequestEntityInfo ensureEntityInfo(Class<?> cls)
	{
		RequestEntityInfo retval = null;
		if (infoMap.containsKey(cls))
		{
			retval = infoMap.get(cls);
		}
		else
		{
			retval = new RequestEntityInfo();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(IdentAnnocation.class))
				{
					field.setAccessible(true);
					IdentAnnocation id = field.getAnnotation(IdentAnnocation.class);
					retval.fieldMap.put(id.value(), field);
					retval.defValueMap.put(id.value(), id.def());
				}
			}
			RequestEntityInfo temp = infoMap.putIfAbsent(cls, retval);
			if (temp != null)
			{
				retval = temp;
			}
		}
		return retval;
	}

	private static class RequestEntityInfo
	{
		public Map<String, Field> fieldMap = new HashMap<String, Field>();

		public Map<String, String> defValueMap = new HashMap<String, String>();
	}
	
	public static void main(String[] args)
	{
		ChannelEntity cl = new ChannelEntity();
		Class clz = CreatorEntity.class;
		System.out.println(clz);
		System.out.println(clz.getFields().length);
	}

}
