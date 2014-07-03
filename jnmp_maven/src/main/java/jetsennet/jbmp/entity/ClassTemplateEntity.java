/************************************************************************
日 期：2012-1-13
作 者: 余灵
版 本：v1.3
描 述: 分类模板实体
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author yl
 */
@Table(name = "BMP_TOPOIMAGE")
public class ClassTemplateEntity
{
    /**
     * 模板ID
     */
    @Id
    @Column(name = "TEMP_ID")
    private int tempId;
    /**
     * 模板名称
     */
    @Column(name = "TEMP_NAME")
    private String tempName;
    /**
     * 模板保存路径
     */
    @Column(name = "TEMP_INFO")
    private String tempInfo;
    /**
     * 模板状态
     */
    @Column(name = "TEMP_STATE")
    private int tempState;
    /**
     * 模板关联的分类
     */
    @Column(name = "CLASS_ID")
    private String classId;
    /**
     * 创建用户ID
     */
    @Column(name = "CREATE_USERID")
    private String createUserId;
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
     * 构造函数
     */
    public ClassTemplateEntity()
    {
    }

    /**
     * @return the tempId
     */
    public int getTempId()
    {
        return tempId;
    }

    /**
     * @param tempId the tempId to set
     */
    public void setTempId(int tempId)
    {
        this.tempId = tempId;
    }

    /**
     * @return the tempName
     */
    public String getTempName()
    {
        return tempName;
    }

    /**
     * @param tempName the tempName to set
     */
    public void setTempName(String tempName)
    {
        this.tempName = tempName;
    }

    /**
     * @return the tempInfo
     */
    public String getTempInfo()
    {
        return tempInfo;
    }

    /**
     * @param tempInfo the tempInfo to set
     */
    public void setTempInfo(String tempInfo)
    {
        this.tempInfo = tempInfo;
    }

    /**
     * @return the tempState
     */
    public int getTempState()
    {
        return tempState;
    }

    /**
     * @param tempState the tempState to set
     */
    public void setTempState(int tempState)
    {
        this.tempState = tempState;
    }

    /**
     * @return the classId
     */
    public String getClassId()
    {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(String classId)
    {
        this.classId = classId;
    }

    /**
     * @return the createUserId
     */
    public String getCreateUserId()
    {
        return createUserId;
    }

    /**
     * @param createUserId the createUserId to set
     */
    public void setCreateUserId(String createUserId)
    {
        this.createUserId = createUserId;
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
