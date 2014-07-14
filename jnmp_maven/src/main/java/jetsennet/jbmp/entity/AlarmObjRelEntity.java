package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

@Table(name = "BMP_ALARMOBJREL")
public class AlarmObjRelEntity
{

    /**
     * 编号
     */
    @Id
    @Column(name = "REL_ID")
    private int relId;
    /**
     * 父ID
     */
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 采集器ID
     */
    @Column(name = "COLL_ID")
    private int collId;

    public AlarmObjRelEntity()
    {
        this.parentId = 0;
    }

    public int getRelId()
    {
        return relId;
    }

    public void setRelId(int relId)
    {
        this.relId = relId;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getCollId()
    {
        return collId;
    }

    public void setCollId(int collId)
    {
        this.collId = collId;
    }

}
