/************************************************************************
日 期：2012-1-4
作 者: 郭祥
版 本: v1.3
描 述: 报警分类
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 报警类型
 * @author 郭祥
 */
@Table(name = "BMP_ALARMTYPE")
public class AlarmTypeEntity
{

    /**
     * 分类ID
     */
    @Id
    @Column(name = "TYPE_ID")
    private int typeId;
    /**
     * 父ID，无父ID时，为-1
     */
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 分类名称
     */
    @Column(name = "TYPE_NAME")
    private String typeName;
    /**
     * 分类描述
     */
    @Column(name = "TYPE_DESC")
    private String typeDesc;
    /**
     * 创建用户
     */
    @Column(name = "CREATE_USER")
    private String createUser;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * @return the typeId
     */
    public int getTypeId()
    {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
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

    /**
     * @return the typeName
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    /**
     * @return the typeDesc
     */
    public String getTypeDesc()
    {
        return typeDesc;
    }

    /**
     * @param typeDesc the typeDesc to set
     */
    public void setTypeDesc(String typeDesc)
    {
        this.typeDesc = typeDesc;
    }

    /**
     * @return the createUser
     */
    public String getCreateUser()
    {
        return createUser;
    }

    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
