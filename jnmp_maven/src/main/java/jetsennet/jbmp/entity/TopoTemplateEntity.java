/************************************************************************
日 期：2012-2-14
作 者: 余灵
版 本：v1.3
描 述: 
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
@Table(name = "BMP_TOPOTEMPLATE")
public class TopoTemplateEntity
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
     * 模板类型
     */
    @Column(name = "TEMP_TYPE")
    private int tempType;
    /**
     * 模板图标
     */
    @Column(name = "TEMP_ICON")
    private String tempIcon;
    /**
     * 关联ID
     */
    @Column(name = "RELATE_ID")
    private int relateId;
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
     * 冗余字段
     */
    @Column(name = "FIELD_1")
    private String field1;

    /**
     * 构造函数
     */
    public TopoTemplateEntity()
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
     * @return the tempType
     */
    public int getTempType()
    {
        return tempType;
    }

    /**
     * @param tempType the tempType to set
     */
    public void setTempType(int tempType)
    {
        this.tempType = tempType;
    }

    /**
     * @return the tempIcon
     */
    public String getTempIcon()
    {
        return tempIcon;
    }

    /**
     * @param tempIcon the tempIcon to set
     */
    public void setTempIcon(String tempIcon)
    {
        this.tempIcon = tempIcon;
    }

    /**
     * @return the relateId
     */
    public int getRelateId()
    {
        return relateId;
    }

    /**
     * @param relateId the relateId to set
     */
    public void setRelateId(int relateId)
    {
        this.relateId = relateId;
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

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

}
