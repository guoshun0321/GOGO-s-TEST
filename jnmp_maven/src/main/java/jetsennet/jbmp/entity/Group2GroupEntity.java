/**
 * 
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author lianghongjie
 */
@Table(name = "BMP_GROUP2GROUP")
public class Group2GroupEntity
{
    @Column(name = "GROUP_ID")
    private int groupId;
    @Column(name = "PARENT_ID")
    private int parentId;
    @Column(name = "USE_TYPE")
    private int useType;
    public static final int USE_TYPE_COMMON = 0;
    public static final int USE_TYPE_CHILD = 1;
    /**
     * 拓扑图父子关系
     */
    public static final int USE_TYPE_SERVICE = 2;
    public static final int USE_TYPE_BIND = 3;

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
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
