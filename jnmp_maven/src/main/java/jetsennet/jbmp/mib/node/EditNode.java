/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.protocols.snmp.datahandle.AbsSnmpValueTrans;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;

/**
 * @author Guo
 */
public class EditNode implements Serializable
{

    /**
     * 编码
     */
    private String coding;
    /**
     * 值处理函数
     */
    private transient AbsSnmpValueTrans handle;
    /**
     * 该节点的取值
     */
    private VariableBinding value;
    /**
     * 对应的数据库中的数据
     */
    private SnmpNodesEntity symbol;
    /**
     * 填充表格时使用
     */
    private String oid;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(EditNode.class);

    /**
     * 构造函数
     */
    public EditNode()
    {
        this.symbol = null;
    }

    /**
     * 构造函数
     * @param symbol 参数
     * @param handle 值处理函数
     * @param coding 编码
     */
    public EditNode(SnmpNodesEntity symbol, AbsSnmpValueTrans handle, String coding)
    {
        this();
        this.symbol = symbol;
        if (symbol != null)
        {
            this.oid = symbol.getNodeOid();
        }
        this.handle = handle;
        this.coding = coding;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol);
        sb.append(" : ");
        sb.append(oid);
        sb.append(" : ");
        if (value != null)
        {
            sb.append(value.toString());
        }
        else
        {
            sb.append("null");
        }
        return sb.toString();
    }

    @Override
    public Object clone()
    {
        EditNode node = new EditNode();
        node.setValue(this.value);
        node.setCoding(this.coding);
        node.setHandle(this.handle);
        node.setSymbol(this.symbol);
        node.setOid(this.oid);
        return node;
    }

    /**
     * 获取解析后的值
     * @return 结果
     */
    public String getEditValue()
    {
        if (coding == null)
        {
            coding = MibConstants.CODING_DEFAULT;
        }
        return SnmpValueTranser.getInstance().trans(value, coding, 0);
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the coding
     */
    public String getCoding()
    {
        return coding;
    }

    /**
     * @param coding the coding to set
     */
    public void setCoding(String coding)
    {
        this.coding = coding;
    }

    /**
     * @return the handle
     */
    public AbsSnmpValueTrans getHandle()
    {
        return handle;
    }

    /**
     * @param handle the handle to set
     */
    public void setHandle(AbsSnmpValueTrans handle)
    {
        this.handle = handle;
    }

    /**
     * @return the value
     */
    public VariableBinding getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(VariableBinding value)
    {
        this.value = value;
    }

    /**
     * @return the symbol
     */
    public SnmpNodesEntity getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(SnmpNodesEntity symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the oid
     */
    public String getOid()
    {
        return oid;
    }

    /**
     * @param oid the oid to set
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }
    // </editor-fold>
}
