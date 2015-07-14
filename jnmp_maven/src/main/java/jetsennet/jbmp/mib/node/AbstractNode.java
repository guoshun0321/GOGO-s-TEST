/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

/**
 * @author Guo
 */
public class AbstractNode implements Cloneable
{

    /**
     * 名称
     */
    private String name;
    /**
     * 对标量，是节点OID，不包含后面的".0" 对表格，是行的OID 对Trap，v1标识enterprise，v2c标识TRAP OID 对类型定义，设置为null
     */
    private String oid;
    /**
     * 类型
     */
    private int type;
    /**
     * 描述
     */
    private String desc;
    public static final int NODE_TYPE_SCALAR = 0;
    public static final int NODE_TYPE_ROW = 0;
    public static final int NODE_TYPE_TRAP = 0;
    public static final int NODE_TYPE_TEX = 0;

    /**
     * @param name 名称
     * @param oid 参数
     */
    public AbstractNode(String name, String oid)
    {
        this.name = name;
        this.oid = oid;
    }

    @Override
    public Object clone()
    {
        return new AbstractNode(this.name, this.oid);
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
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

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }

    /**
     * @return the desc
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    // </editor-fold>
}
