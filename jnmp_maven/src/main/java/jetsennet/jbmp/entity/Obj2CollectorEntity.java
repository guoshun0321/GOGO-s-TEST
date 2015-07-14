package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NMP_OBJ2COLLECTOR")
public class Obj2CollectorEntity
{

    @Column(name = "OBJ_ID")
    private int objId;
    @Column(name = "COLL_ID")
    private int collId;

    /**
     * 构造函数
     */
    public Obj2CollectorEntity()
    {
    }

    /**
     * 构造函数
     * @param objId 对象id
     * @param collId 采集id
     */
    public Obj2CollectorEntity(int objId, int collId)
    {
        this.objId = objId;
        this.collId = collId;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    /**
     * @return the collId
     */
    public int getCollId()
    {
        return collId;
    }

    /**
     * @param collId the collId to set
     */
    public void setCollId(int collId)
    {
        this.collId = collId;
    }
}
