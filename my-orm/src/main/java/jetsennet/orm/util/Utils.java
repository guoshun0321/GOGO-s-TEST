package jetsennet.orm.util;

import java.util.HashSet;
import java.util.Set;

public class Utils
{

    private static final Set<Class<?>> basicTypeMap = new HashSet<Class<?>>();

    static
    {
        basicTypeMap.add(byte.class);
        basicTypeMap.add(Byte.class);
        basicTypeMap.add(char.class);
        basicTypeMap.add(Character.class);
        basicTypeMap.add(short.class);
        basicTypeMap.add(Short.class);
        basicTypeMap.add(int.class);
        basicTypeMap.add(Integer.class);
        basicTypeMap.add(long.class);
        basicTypeMap.add(Long.class);
        basicTypeMap.add(double.class);
        basicTypeMap.add(Double.class);
    }

    public static final boolean isBasicType(Class<?> cls)
    {
        boolean retval = false;
        if (cls != null)
        {
            retval = basicTypeMap.contains(cls);
        }
        return retval;
    }

}
