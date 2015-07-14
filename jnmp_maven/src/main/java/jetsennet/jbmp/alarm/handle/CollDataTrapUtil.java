/************************************************************************
日 期: 2011-12-30
作 者: 郭祥
版 本: v1.3
描 述: trap处理工具类
历 史:
 ************************************************************************/
package jetsennet.jbmp.alarm.handle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.snmp4j.smi.Variable;

import jetsennet.jbmp.alarm.AlarmUtil;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTrans;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;

/**
 * 处理从采集层传入的Trap数据
 * @author 郭祥
 */
public class CollDataTrapUtil
{

    /**
     * snmpTrapOID.0
     */
    public static final String SNMP_TRAP_OID = "1.3.6.1.6.3.1.1.4.1.0";
    /**
     * SysUpTime.0
     */
    public static final String SYS_UP_TIME = "1.3.6.1.2.1.1.3.0";
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(CollDataTrapUtil.class);

    /**
     * 获取Trap类型
     * @param data 参数
     * @return 结果
     */
    public static String getType(CollData data)
    {
        if (data != null && data.params != null)
        {
            Object obj = data.params.get(SNMP_TRAP_OID);
            if (obj != null)
            {
                return obj.toString();
            }
        }
        return null;
    }

    /**
     * 时间
     * @param data 参数
     * @return 结果
     */
    public static long getSysUpTime(CollData data)
    {
        if (data != null && data.params != null)
        {
            Object obj = data.params.get(SYS_UP_TIME);
            if (obj != null && obj instanceof String)
            {
                try
                {
                    return Long.valueOf((String) obj);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return -1;
    }

    /**
     * Trap的内容
     * @param data 数据
     * @param coding 编码
     * @return 结果
     */
    public static String getContent(CollData data, String coding)
    {
        StringBuilder sb = new StringBuilder();
        if (data != null && data.params != null)
        {
            Set<String> oids = data.params.keySet();
            for (String oid : oids)
            {
                sb.append(oid);
                sb.append(":");
                Object obj = data.params.get(oid);
                if (obj != null)
                {
                    if (obj instanceof Variable)
                    {
                        sb.append(SnmpValueTranser.getInstance().trans((Variable) obj, coding, 0));
                    }
                    else
                    {
                        sb.append(obj);
                    }
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }

    /**
     * 获取给定OID对应的值
     * @param oid 参数
     * @param data 数据
     * @param coding 参数
     * @return 结果
     */
    public static String getByOid(String oid, CollData data, String coding)
    {
        String retval = null;
        SnmpValueTrans oidHandle = new SnmpValueTrans();
        if (data != null && data.params != null)
        {
            Set<String> oids = data.params.keySet();
            for (String soid : oids)
            {
                if (oid.equals(soid))
                {
                    retval = SnmpValueTranser.getInstance().trans((Variable) data.params.get(oid), coding, 0);
                }
            }
        }
        return retval;
    }

    /**
     * 根据oidMap中需要的OID从data中获取相应的值
     * @param data 采集数据
     * @param oidMap OID和相应名称的对应
     * @param coding 参数
     * @return 结果
     */
    public static HashMap<String, String> CollData2Map(CollData data, Map<String, String> oidMap, String coding)
    {
        Map<String, String> oid2str = AlarmUtil.getCollDataParams(data, coding);
        HashMap<String, String> alarmValue = new HashMap<String, String>();
        for (String key : oid2str.keySet())
        {
            if (oidMap.containsKey(key))
            {
                alarmValue.put(oidMap.get(key), oid2str.get(key));
            }
        }
        return alarmValue;
    }
}
