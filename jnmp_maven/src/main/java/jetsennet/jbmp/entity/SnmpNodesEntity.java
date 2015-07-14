package jetsennet.jbmp.entity;

import java.io.Serializable;
import java.util.ArrayList;

import net.percederberg.mibble.MibValueSymbol;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.jbmp.mib.parse.SnmpEnumEntity;

/**
 * @author ？
 */
@Table(name = "BMP_SNMPNODES")
public class SnmpNodesEntity implements Serializable
{

    // <editor-fold defaultstate="collapsed" desc="数据库映射字段">
    /**
     * 编号
     */
    @Id
    @Column(name = "NODE_ID")
    private int nodeId;
    /**
     * 父编号，无父对象的为0.
     */
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 设备类型
     */
    @Column(name = "MIB_ID")
    private int mibId;
    /**
     * 名称
     */
    @Column(name = "NODE_NAME")
    private String nodeName;
    /**
     * OID
     */
    @Column(name = "NODE_OID")
    private String nodeOid;
    /**
     * 类型。0，不能取值的OID；1，标量；2，表；3，行；4，列；5，Trap；6，类型定义；7，表索引；100，未知。
     */
    @Column(name = "NODE_TYPE")
    private int nodeType;
    /**
     * 索引，仅对行类型有效
     */
    @Column(name = "NODE_INDEX")
    private String nodeIndex;
    /**
     * 值类型
     */
    @Column(name = "VALUE_TYPE")
    private String valueType;
    /**
     * 取值处理
     */
    @Column(name = "HANDLE")
    private int handle;
    /**
     * 枚举ID
     */
    @Column(name = "VALUE_ID")
    private int valueId;
    /**
     * 文件
     */
    @Column(name = "MIB_FILE")
    private String mibFile;
    /**
     * 数据来源
     */
    @Column(name = "SOURCE_TYPE")
    private int sourceType;
    /**
     * 描述
     */
    @Column(name = "NODE_DESC")
    private String nodeDesc;
    /**
     * 中文描述
     */
    @Column(name = "NODE_EXPLAIN")
    private String nodeExplain;
    // </editor-fold>
    /**
     * 父节点
     */
    private transient SnmpNodesEntity parent;
    /**
     * 子节点
     */
    private transient ArrayList<SnmpNodesEntity> child = new ArrayList<SnmpNodesEntity>();

    /**
     * 由mibble解析出来的节点
     */
    private transient MibValueSymbol symbol;
    /**
     * 当OID的值为枚举节点时，保存对应的枚举值
     */
    private transient SnmpEnumEntity enumE;
    // <editor-fold defaultstate="collapsed" desc="类型定义">
    public static final int MIBTYPE_OID = 0;
    public static final int MIBTYPE_SCALAR = 1;
    public static final int MIBTYPE_TABLE = 2;
    public static final int MIBTYPE_ROW = 3;
    public static final int MIBTYPE_COLUMN = 4;
    public static final int MIBTYPE_TRAP = 5;
    public static final int MIBTYPE_TYPEDEF = 6;
    public static final int MIBTYPE_INDEX = 7;
    public static final int MIBTYPE_UNKNOWN = 100;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="数据来源">
    public static final int SOURCE_TYPE_AUTO = 0;
    public static final int SOURCE_TYPE_MANU = 1;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    // </editor-fold>

    /**
     * 构造函数
     */
    public SnmpNodesEntity()
    {
        this.sourceType = SOURCE_TYPE_AUTO;
        this.nodeDesc = " ";
    }

    /**
     * 构造函数
     * @param nodeName 节点名
     * @param nodeOid 节点id
     */
    public SnmpNodesEntity(String nodeName, String nodeOid)
    {
        this();
        this.nodeName = nodeName;
        this.nodeOid = nodeOid;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(nodeName);
        sb.append(" : ");
        sb.append(nodeOid);
        return sb.toString();
    }

    /**
     * @return the nodeId
     */
    public int getNodeId()
    {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    /**
     * @return the parentId
     */
    public int getParentId()
    {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public int getMibId()
    {
        return mibId;
    }

    public void setMibId(int mibId)
    {
        this.mibId = mibId;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName()
    {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeOid
     */
    public String getNodeOid()
    {
        return nodeOid;
    }

    /**
     * @param nodeOid the nodeOid to set
     */
    public void setNodeOid(String nodeOid)
    {
        this.nodeOid = nodeOid;
    }

    /**
     * @return the nodeType
     */
    public int getNodeType()
    {
        return nodeType;
    }

    /**
     * @param nodeType the nodeType to set
     */
    public void setNodeType(int nodeType)
    {
        this.nodeType = nodeType;
    }

    /**
     * @return the nodeIndex
     */
    public String getNodeIndex()
    {
        return nodeIndex;
    }

    /**
     * @param nodeIndex the nodeIndex to set
     */
    public void setNodeIndex(String nodeIndex)
    {
        this.nodeIndex = nodeIndex;
    }

    /**
     * @return the valueType
     */
    public String getValueType()
    {
        return valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }

    /**
     * @return the handle
     */
    public int getHandle()
    {
        return handle;
    }

    /**
     * @param handle the handle to set
     */
    public void setHandle(int handle)
    {
        this.handle = handle;
    }

    /**
     * @return the valueId
     */
    public int getValueId()
    {
        return valueId;
    }

    /**
     * @param valueId the valueId to set
     */
    public void setValueId(int valueId)
    {
        this.valueId = valueId;
    }

    /**
     * @return the mibFile
     */
    public String getMibFile()
    {
        return mibFile;
    }

    /**
     * @param mibFile the mibFile to set
     */
    public void setMibFile(String mibFile)
    {
        this.mibFile = mibFile;
    }

    /**
     * @return the nodeDesc
     */
    public String getNodeDesc()
    {
        return nodeDesc;
    }

    /**
     * @param nodeDesc the nodeDesc to set
     */
    public void setNodeDesc(String nodeDesc)
    {
        if (nodeDesc != null && nodeDesc.length() > 1800)
        {
            nodeDesc = nodeDesc.substring(0, 1800);
        }
        this.nodeDesc = nodeDesc;
    }

    /**
     * @return the nodeExplain
     */
    public String getNodeExplain()
    {
        return nodeExplain;
    }

    /**
     * @param nodeExplain the nodeExplain to set
     */
    public void setNodeExplain(String nodeExplain)
    {
        this.nodeExplain = nodeExplain;
    }

    /**
     * @return the parent
     */
    public SnmpNodesEntity getParent()
    {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(SnmpNodesEntity parent)
    {
        this.parent = parent;
    }

    /**
     * @return the symbol
     */
    public MibValueSymbol getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(MibValueSymbol symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the enumE
     */
    public SnmpEnumEntity getEnumE()
    {
        return enumE;
    }

    /**
     * @param enumE the enumE to set
     */
    public void setEnumE(SnmpEnumEntity enumE)
    {
        this.enumE = enumE;
    }

    public int getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(int sourceType)
    {
        this.sourceType = sourceType;
    }

    public ArrayList<SnmpNodesEntity> getChild()
    {
        return child;
    }

    public void setChild(ArrayList<SnmpNodesEntity> child)
    {
        this.child = child;
    }

    public void add(SnmpNodesEntity e)
    {
        getChild().add(e);
    }

}
