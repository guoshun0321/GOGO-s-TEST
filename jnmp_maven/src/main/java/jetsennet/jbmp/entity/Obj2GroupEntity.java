/**
 * 
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author lianghongjie
 */
@Table(name = "BMP_OBJ2GROUP")
public class Obj2GroupEntity
{

    @Column(name = "OBJ_ID")
    private int objId;
    @Column(name = "GROUP_ID")
    private int groupId;
    @Column(name = "USE_TYPE")
    private int useType;

    /**
     * 默认关系
     */
    public static final int USE_TYPE_DEF = 0;
    /**
     * 从属关系
     */
    public static final int USE_TYPE_AFFILIATION = 1;
    /**
     * 对象业务关系
     */
    public static final int USE_TYPE_BUS = 2;
    /**
     * 拓扑图绑定关系
     */
    public static final int USE_TYPE_TOPO = 3;

    /**
     * 构造函数
     */
    public Obj2GroupEntity()
    {
    }

    /**
     * @param objId 对象id
     * @param groupId 对象组id
     * @param useType 类型
     */
    public Obj2GroupEntity(int objId, int groupId, int useType)
    {
        this.objId = objId;
        this.groupId = groupId;
        this.useType = useType;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public int getUseType()
    {
        return useType;
    }

    public void setUseType(int useType)
    {
        this.useType = useType;
    }
}
