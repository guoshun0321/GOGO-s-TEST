/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author Li
 */
@Table(name = "BMP_DUTYLOG")
public class DutyLogEntity
{
    @Id
    @Column(name = "DUTYLOG_ID")
    private int dutyLogId;
    @Column(name = "DUTYLOG_TITLE")
    private String dutyLogTitle;
    @Column(name = "DUTYLOG_DESC")
    private String dutyLogDesc;
    @Column(name = "CREATE_USERID")
    private int createUserId;
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 构造函数
     */
    public DutyLogEntity()
    {
    }

    /**
     * @return the dutyLogId
     */
    public int getDutyLogId()
    {
        return dutyLogId;
    }

    /**
     * @param dutyLogId the dutyLogId to set
     */
    public void setDutyLogId(int dutyLogId)
    {
        this.dutyLogId = dutyLogId;
    }

    /**
     * @return the dutyLogTitle
     */
    public String getDutyLogTitle()
    {
        return dutyLogTitle;
    }

    /**
     * @param dutyLogTitle the dutyLogTitle to set
     */
    public void setDutyLogTitle(String dutyLogTitle)
    {
        this.dutyLogTitle = dutyLogTitle;
    }

    /**
     * @return the dutyLogDesc
     */
    public String getDutyLogDesc()
    {
        return dutyLogDesc;
    }

    /**
     * @param dutyLogDesc the dutyLogDesc to set
     */
    public void setDutyLogDesc(String dutyLogDesc)
    {
        this.dutyLogDesc = dutyLogDesc;
    }

    /**
     * @return the createUserId
     */
    public int getCreateUserId()
    {
        return createUserId;
    }

    /**
     * @param createUserId the createUserId to set
     */
    public void setCreateUserId(int createUserId)
    {
        this.createUserId = createUserId;
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
