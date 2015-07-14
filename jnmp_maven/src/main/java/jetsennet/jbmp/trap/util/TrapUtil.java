package jetsennet.jbmp.trap.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

/**
 * @author liwei 参数
 */
public class TrapUtil
{

    private static final Logger logger = Logger.getLogger(TrapUtil.class);

    /**
     * 把采集到的数据解析成<String, String>的形式
     * @param respEvnt 参数
     * @return OID和其对应的值。为空时，返回size为0的map。
     */
    public static HashMap<String, String> pdu2Map(CommandResponderEvent respEvnt)
    {
        PDU pdu = respEvnt.getPDU();
        VariableBinding[] binds = pdu.toArray();
        HashMap<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < binds.length; i++)
        {
            String rs = null;
            VariableBinding bind = binds[i];
            if (bind.getVariable() instanceof OctetString)
            {
                byte[] bytes = ((OctetString) bind.getVariable()).getValue();
                try
                {
                    rs = new String(bytes, "GB2312");
                }
                catch (UnsupportedEncodingException e)
                {
                    logger.error(e);
                }
            }
            else
            {
                rs = bind.getVariable().toString();
            }
            result.put(bind.getOid().toString(), rs);
        }
        return result;
    }

    /**
     * @param alarmValue 参数
     * @return 结果
     */
    public static String printOidMap(HashMap<String, String> alarmValue)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("TRAP数据解析开始");
        Set<String> keys = alarmValue.keySet();
        for (String key : keys)
        {
            sb.append("\r\n");
            sb.append(key);
            sb.append(" -> ");
            sb.append(alarmValue.get(key));
        }
        sb.append("\r\n");
        sb.append("TRAP数据解析结束");
        return sb.toString();
    }
}
