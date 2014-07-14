/************************************************************************
日 期: 2011-12-29
作 者: 郭祥
版 本: v1.3
描 述: 使用SNMP协议取出来的值转换成可以使用的值
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp.datahandle;

import java.io.Serializable;

import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * 使用SNMP协议取出来的值转换成可以使用的值
 * @author 郭祥
 */
public abstract class AbsSnmpValueTrans
{

    /**
     * 把使用SNMP协议取出来的值转换可以使用的值
     * @param vb 参数
     * @param coding 参数
     * @param length 参数
     * @return 结果
     */
    public abstract String handle(VariableBinding vb, String coding, int length);

    /**
     * 把使用SNMP协议取出来的值转换可以使用的值
     * @param var 参数
     * @param coding 参数
     * @param length 参数
     * @return 结果
     */
    public abstract String handle(Variable var, String coding, int length);
}
