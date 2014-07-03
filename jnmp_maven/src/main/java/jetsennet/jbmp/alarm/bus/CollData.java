package jetsennet.jbmp.alarm.bus;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 采集数据的定义。
 * @author lianghongjie
 */
public class CollData implements Serializable
{

    /** 对象ID */
    public int objID;
    /** 对象属性ID */
    public int objAttrID;
    /** 属性ID */
    public int attrID;
    /**
     * 数据类型 0:整形 2:字符串 30:Trap数据 31:Syslog数据 32:告警数据等
     */
    public int dataType;
    public static final int DATATYPE_EMPTY = -10000; // 空数据，用于唤醒线程
    public static final int DATATYPE_PERF = 0;
    public static final int DATATYPE_TRAP = 30;
    public static final int DATATYPE_SYSLOG = 31;
    public static final int DATATYPE_ALARM = 32;
    /**
     * 数据值 性能数据:实时数据 Trap数据:TrapOID Syslog数据:BGP|TELNET|CLI... ...
     */
    public String value;
    /** 数据来源IP */
    public String srcIP;
    /** 数据产生的时间 */
    public Date time;
    /** 参数列表 */
    public Map<String, Object> params;
    /**
     * 附带的数据
     */
    public static final String PARAMS_DATA = "data";
    /**
     * 附带的消息
     */
    public static final String COLL_INFO = "info";

    /**
     * @param obj 对象
     */
    public void addParam(Object obj)
    {
        if (params == null)
        {
            params = new LinkedHashMap<String, Object>(10);
        }
        params.put("" + params.size(), obj);
    }

    /**
     * @param key 键
     * @param value 值
     */
    public void put(String key, Object value)
    {
        if (params == null)
        {
            params = new LinkedHashMap<String, Object>(10);
        }
        params.put(key, value);
    }
}
