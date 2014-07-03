/************************************************************************
日 期: 2011-12-29
作 者: 郭祥
版 本: v1.3
描 述: 默认的值转换方法
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp.datahandle;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import jetsennet.jbmp.util.ConvertUtil;

import org.apache.log4j.Logger;

import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * 默认的值转换方法
 * @author 郭祥
 */
public class SnmpValueTrans extends AbsSnmpValueTrans implements Serializable
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpValueTrans.class);
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1l;

    @Override
    public String handle(VariableBinding vb, String coding, int length)
    {
        if (vb == null)
        {
            return null;
        }
        return this.handle(vb.getVariable(), coding, length);
    }

    @Override
    public String handle(Variable var, String coding, int length)
    {
        if (var == null)
        {
            return null;
        }
        if (coding == null)
        {
            coding = "GBK";
        }
        String result = null;
        if (var instanceof OctetString)
        {
            OctetString oct = (OctetString) var;
            try
            {
                if ("HEX".equals(coding))
                {
                    result = oct.toString();
                }
                else
                {
                    result = new String(oct.getValue(), coding);
                }
            }
            catch (UnsupportedEncodingException ex)
            {
                result = oct.toHexString();
            }
        }
        else
        {
            result = var.toString();
        }
        if (length > 0)
        {
            result = result.length() <= length ? result : result.substring(0, length);
        }
        result = ConvertUtil.chopWhitespace(result);
        return result;
    }
}
