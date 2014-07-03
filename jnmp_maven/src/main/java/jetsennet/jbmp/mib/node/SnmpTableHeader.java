/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.io.Serializable;

import jetsennet.jbmp.protocols.snmp.datahandle.AbsSnmpValueTrans;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTrans;

/**
 * @author Guo
 */
public class SnmpTableHeader extends AbstractNode implements Serializable
{

    /**
     * 编码
     */
    private String coding;
    /**
     * 值处理
     */
    private AbsSnmpValueTrans handle;

    /**
     * 构造函数
     */
    public SnmpTableHeader()
    {
        this(null, null);
    }

    /**
     * @param oid 参数
     */
    public SnmpTableHeader(String oid)
    {
        this(null, oid);
    }

    /**
     * @param name 名称
     * @param oid 参数
     */
    public SnmpTableHeader(String name, String oid)
    {
        this(name, oid, null);
    }

    /**
     * @param name 名称
     * @param oid 参数
     * @param coding 参数
     */
    public SnmpTableHeader(String name, String oid, String coding)
    {
        this(name, oid, coding, new SnmpValueTrans());
    }

    /**
     * @param name 名称
     * @param oid 参数
     * @param coding 参数
     * @param handle 参数
     */
    public SnmpTableHeader(String name, String oid, String coding, AbsSnmpValueTrans handle)
    {
        super(name, oid);
        this.coding = coding;
        this.handle = handle;
    }

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

    @Override
    public Object clone()
    {
        SnmpTableHeader copy = new SnmpTableHeader();
        copy.setName(this.getName());
        copy.setOid(this.getOid());
        copy.setCoding(this.coding);
        copy.setHandle(this.handle);
        return copy;
    }
}
