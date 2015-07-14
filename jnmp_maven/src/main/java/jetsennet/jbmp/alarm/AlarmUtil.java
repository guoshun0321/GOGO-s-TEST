/************************************************************************
日 期：2011-3-9
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.snmp4j.smi.Variable;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.util.BMPConstants;

/**
 * @author 郭祥
 */
public class AlarmUtil
{

    /**
     * 采集数据转换成字符串键值对，Trap专用
     * @param data 参数
     * @param coding 参数
     * @return 结果
     */
    public static Map<String, String> getCollDataParams(CollData data, String coding)
    {
        Map<String, Object> params = data.params;
        if (data == null || params == null)
        {
            return null;
        }
        Map<String, String> result = new LinkedHashMap<String, String>();
        Set<String> keys = params.keySet();
        for (String key : keys)
        {
            Object obj = params.get(key);
            String temp = null;
            if (obj != null && obj instanceof Variable)
            {
                temp = SnmpValueTranser.getInstance().trans((Variable) obj, coding, 0);
            }
            result.put(key, temp);
        }
        return result;
    }

    /**
     * 确定编码
     * @param coding 编码
     * @return 结果
     */
    public static String ensureCoding(String coding)
    {
        return (coding == null || coding.trim().isEmpty()) ? BMPConstants.DEFAULT_SNMP_CODING : coding;
    }
}
