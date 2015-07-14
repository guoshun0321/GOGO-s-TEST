package jetsennet.jsmp.nav.util;

import java.lang.reflect.Field;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectUtil
{

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

	public static boolean compare(Class<?> clz, Object old, Object nw)
	{
		if (old == null || nw == null)
		{
			logger.error("compared objects are null");
		}
		boolean retval = true;
		try
		{
			if (old.getClass() == clz && nw.getClass() == clz)
			{
				Field[] fields = clz.getDeclaredFields();
				for (Field field : fields)
				{
					field.setAccessible(true);
					Object oValue = field.get(old);
					Object nValue = field.get(nw);
					if (oValue == null && nValue == null)
					{
						retval = true;
					}
					else if (oValue != null && nValue != null)
					{
						if (isBase(field.getType()) || (field.getType() == String.class) || field.getType() == Date.class)
						{
							if (!oValue.toString().equals(nValue.toString()))
							{
								retval = false;
								logger.debug(String.format("不匹配的值：%s, %s, %s", field.getName(), oValue, nValue));
								break;
							}
						}
						else
						{
							compare(field.getClass(), oValue, nValue);
						}
					}
					else
					{
						retval = false;
					}
				}
			}
			else
			{
				retval = false;
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			retval = false;
		}
		return retval;
	}

	public static boolean isBase(Class<?> clz)
	{
		if (clz == byte.class
			|| clz == Byte.class
			|| clz == char.class
			|| clz == Character.class
			|| clz == short.class
			|| clz == Short.class
			|| clz == int.class
			|| clz == Integer.class
			|| clz == long.class
			|| clz == Long.class
			|| clz == double.class
			|| clz == Double.class)
		{
			return true;
		}
		return false;
	}

}
