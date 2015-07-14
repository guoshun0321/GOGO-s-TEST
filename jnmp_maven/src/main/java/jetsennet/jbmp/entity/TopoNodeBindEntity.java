package jetsennet.jbmp.entity;

/**
 * 拓扑图的节点绑定的对象与对象组
 * @author YL
 */
public class TopoNodeBindEntity
{

    /**
     * 节点ID
     */
    private String nodeId = "";
    /**
     * 绑定的对象ID
     */
    private String objId = "";
    /**
     * 绑定的对象组的ID
     */
    private String groupId = "";

    /**
     * 构造函数
     */
    public TopoNodeBindEntity()
    {

    }

    /**
     * 构造函数
     * @param nodeId 节点ID
     * @param objId 对象id
     * @param groupId 对象组
     */
    public TopoNodeBindEntity(String nodeId, String objId, String groupId)
    {
        super();
        this.nodeId = nodeId;
        this.objId = objId;
        this.groupId = groupId;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<data ");
        sb.append("nodeId = \"");
        sb.append(this.getNodeId());
        sb.append("\" objId = \"");
        sb.append(this.getObjId());
        sb.append("\" groupId = \"");
        sb.append(this.getGroupId());
        sb.append("\" />");
        return sb.toString();
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getObjId()
    {
        return objId;
    }

    public void setObjId(String objId)
    {
        this.objId = objId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

}
