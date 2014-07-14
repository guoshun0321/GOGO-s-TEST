package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "BMP_ROLE2GROUP")
public class Role2GroupEntity
{

    @Column(name = "ROLE_ID")
    private int roleId;
    @Column(name = "GROUP_ID")
    private int groupId;

    /**
     * 构造函数
     */
    public Role2GroupEntity()
    {
    }

    /**
     * @param roleId 角色id
     * @param groupId 对象组id
     */
    public Role2GroupEntity(int roleId, int groupId)
    {
        this.roleId = roleId;
        this.groupId = groupId;
    }

    public int getRoleId()
    {
        return roleId;
    }

    public void setRoleId(int roleId)
    {
        this.roleId = roleId;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

}
