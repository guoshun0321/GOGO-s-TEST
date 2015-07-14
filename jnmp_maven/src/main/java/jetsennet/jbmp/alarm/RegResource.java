/************************************************************************
日 期：2011-3-9
作 者: 郭祥
版 本：v1.3
描 述: 资源注册
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源注册
 * @author 郭祥
 */
public class RegResource
{

    public static final Map<String, Object> str2obj = new HashMap<String, Object>();

    public static final String RESOURCE_CLUSTER_NAME = "CLUSTER";

    /**
     * @param str 参数
     * @param obj 对象
     */
    public static synchronized void set(String str, Object obj)
    {
        str2obj.put(str, obj);
    }

    /**
     * @param str 参数
     * @return 结果
     */
    public static synchronized Object get(String str)
    {
        return str2obj.get(str);
    }
}
