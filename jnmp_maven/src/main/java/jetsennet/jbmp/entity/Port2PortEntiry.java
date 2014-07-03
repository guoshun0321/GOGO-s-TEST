package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 端口连接
 * @author xuyuji
 */
@Table(name = "BMP_PORT2PORT")
public class Port2PortEntiry
{
    /**
     * 编号
     */
    @Id
    @Column(name = "ID")
    private int id;
    /**
     * 设备A
     */
    @Column(name = "PORTA_ID")
    private int portAID;
    /**
     * 设备B
     */
    @Column(name = "PORTB_ID")
    private int portBID;
    /**
     * 所属网段ID
     */
    @Column(name = "GROUP_ID")
    private int groupId;
    /**
     * 连接关系类型
     */
    @Column(name = "REL_TYPE")
    private int relType;
    /**
     * 自动生成
     */
    public static final int REL_TYPE_AUTO = 1;
    /**
     * 自动生成（不确定）
     */
    public static final int REL_TYPE_NOT_SURE = 2;
    /**
     * 手动生成
     */
    public static final int REL_TYPE_MANU = 3;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getPortAID()
    {
        return portAID;
    }

    public void setPortAID(int portAID)
    {
        this.portAID = portAID;
    }

    public int getPortBID()
    {
        return portBID;
    }

    public void setPortBID(int portBID)
    {
        this.portBID = portBID;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public int getRelType()
    {
        return relType;
    }

    public void setRelType(int relType)
    {
        this.relType = relType;
    }

}
