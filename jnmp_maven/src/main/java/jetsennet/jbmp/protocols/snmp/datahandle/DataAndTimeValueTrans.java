/************************************************************************
日 期: 2011-12-29
作 者: 郭祥
版 本: v1.3
描 述: DataAndTime类型的数据转换
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp.datahandle;

import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * DataAndTime类型的数据转换
 * @author 郭祥
 */
public class DataAndTimeValueTrans extends AbsSnmpValueTrans
{

    @Override
    public String handle(VariableBinding vb, String coding, int length)
    {
        String result = null;
        if (vb != null)
        {
            Variable var = vb.getVariable();
            if (var instanceof OctetString)
            {
                OctetString oct = (OctetString) var;
                byte[] bytes = oct.getValue();
                if (bytes.length == 8)
                {
                    int year = ((bytes[0] & 255) << 8) | (bytes[1] & 255);
                    int month = bytes[2] & 255;
                    int day = bytes[3] & 255;
                    int hour = bytes[4] & 255;
                    int min = bytes[5] & 255;
                    int sec = bytes[6] & 255;
                    int msec = bytes[7] & 255;
                    result = String.format("%1$d-%2$d-%3$d %4$d:%5$d:%6$d.%7$d", year, month, day, hour, min, sec, msec);
                }
                else
                {
                    throw new SnmpValueTransException(vb.toString() + "的值不是DataAndTime类型");
                }
            }
            else
            {
                throw new SnmpValueTransException(vb.toString() + "的值不是DataAndTime类型");
            }
        }
        return result;
    }

    @Override
    public String handle(Variable var, String coding, int length)
    {
        if (var == null)
        {
            return null;
        }
        String result = null;
        if (var instanceof OctetString)
        {
            OctetString oct = (OctetString) var;
            byte[] bytes = oct.getValue();
            if (bytes.length == 8)
            {
                int year = ((bytes[0] & 255) << 8) | (bytes[1] & 255);
                int month = bytes[2] & 255;
                int day = bytes[3] & 255;
                int hour = bytes[4] & 255;
                int min = bytes[5] & 255;
                int sec = bytes[6] & 255;
                int msec = bytes[7] & 255;
                result = String.format("%1$d-%2$d-%3$d %4$d:%5$d:%6$d.%7$d", year, month, day, hour, min, sec, msec);
            }
            else
            {
                throw new SnmpValueTransException(var.toString() + "的值不是DataAndTime类型");
            }
        }
        else
        {
            throw new SnmpValueTransException(var.toString() + "的值不是DataAndTime类型");
        }
        return result;
    }
}
