package jetsennet.jsmp.nav.service.a7.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

public class RequestEntityUtil
{

    private static ConcurrentMap<Class<?>, RequestEntityInfo> infoMap = new ConcurrentHashMap<Class<?>, RequestEntityUtil.RequestEntityInfo>();
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(RequestEntityUtil.class);

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
        if (f.getType() == boolean.class)
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
        else
        {
            throw new UncheckedNavException("不支持数据类型：" + f.getType());
        }
        return retval;
    }

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
            Field[] fields = cls.getFields();
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

}
