/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述:
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp.datahandle;

import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * SNMP值转换
 * @author 郭祥
 */
public final class SnmpValueTranser
{

    /**
     * 默认最多取200个字节
     */
    private static final int DEFAULT_LENGTH = 200;

    // 单例
    private static SnmpValueTranser instance = new SnmpValueTranser();

    private SnmpValueTranser()
    {
    }

    public static SnmpValueTranser getInstance()
    {
        return instance;
    }

    /**
     * 数值转换
     * @param vb 参数
     * @param coding 参数
     * @param length 长度。小于等于0，不限定长度；
     * @return 结果
     */
    public String trans(VariableBinding vb, String coding)
    {
        if (vb == null)
        {
            return null;
        }
        return this.trans(vb.getVariable(), coding, DEFAULT_LENGTH);
    }

    /**
     * 数值转换
     * @param vb 参数
     * @param coding 参数
     * @param length 长度。小于等于0，不限定长度；
     * @return 结果
     */
    public String trans(VariableBinding vb, String coding, int length)
    {
        if (vb == null)
        {
            return null;
        }
        return this.trans(vb.getVariable(), coding, length);
    }

    /**
     * 数值转换
     * @param var 参数
     * @param coding 参数
     * @param length 长度。小于等于0，不限定长度；
     * @return 结果
     */
    public String trans(Variable var, String coding, int length)
    {
        if (var == null)
        {
            return null;
        }
        AbsSnmpValueTrans trans = this.ensureTrans();
        if (trans == null)
        {
            return "";
        }
        return trans.handle(var, coding, length);
    }

    private AbsSnmpValueTrans ensureTrans()
    {
        return new SnmpValueTrans();
    }
}
