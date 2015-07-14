package jetsennet.jbmp.syslog;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Syslog解析实体
 * @author 郭祥
 */
public class SyslogParseEntity
{

    /**
     * PRI
     */
    public String pri;
    /**
     * 时间
     */
    public String time;
    /**
     * 主机名称
     */
    public String hostName;
    /**
     * TAG
     */
    public String tag;
    /**
     * MSG
     */
    public String msg;
    /**
     * MSG的解析
     */
    public Map<String, String> values;

    /**
     * @param key 键
     * @param value 值
     */
    public void put(String key, String value)
    {
        if (values == null)
        {
            values = new LinkedHashMap<String, String>();
        }
        values.put(key, value);
    }

    /**
     * @param key 键
     * @return 结果
     */
    public String get(String key)
    {
        if (values != null)
        {
            return values.get(key);
        }
        return null;
    }

}
