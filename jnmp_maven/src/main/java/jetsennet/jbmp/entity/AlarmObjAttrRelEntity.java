package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

@Table(name = "BMP_ALARMOBJATTRREL")
public class AlarmObjAttrRelEntity
{

    /**
     * 编号
     */
    @Id
    @Column(name = "REL_ID")
    private int relId;
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int ObjId;
    /**
     * 对象属性ID
     */
    @Column(name = "OBJATTR_ID")
    private int objAttrId;
    /**
     * 父对象ID
     */
    @Column(name = "OBJ_PID")
    private int ObjPid;
    /**
     * 父对象属性ID
     */
    @Column(name = "OBJATTR_PID")
    private int objAttrPid;

    public int getRelId()
    {
        return relId;
    }

    public void setRelId(int relId)
    {
        this.relId = relId;
    }

    public int getObjId()
    {
        return ObjId;
    }

    public void setObjId(int objId)
    {
        ObjId = objId;
    }

    public int getObjAttrId()
    {
        return objAttrId;
    }

    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    public int getObjPid()
    {
        return ObjPid;
    }

    public void setObjPid(int objPid)
    {
        ObjPid = objPid;
    }

    public int getObjAttrPid()
    {
        return objAttrPid;
    }

    public void setObjAttrPid(int objAttrPid)
    {
        this.objAttrPid = objAttrPid;
    }

}
