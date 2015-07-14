package jetsennet.jbmp.dataaccess.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ？
 */
public class TableInfoMgr
{
    private static Map<Class, TableInfo> infoMap = new HashMap<Class, TableInfo>();

    public static TableInfo getTableInfo(Class c)
    {
        TableInfo result = infoMap.get(c);
        if (result == null)
        {
            result = loadTableInfo(c);
        }
        return result;
    }

    private synchronized static TableInfo loadTableInfo(Class c)
    {
        TableInfo info = infoMap.get(c);
        if (info != null)
        {
            return info;
        }
        info = new TableInfo(c);
        infoMap.put(c, info);
        return info;
    }
}
