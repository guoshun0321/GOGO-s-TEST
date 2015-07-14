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
 * @author guoshiping
 */
@Table(name = "BMP_DUTYLOGATTACHMENT")
public class DutyLogAttachmentEntity
{

    @Id
    @Column(name = "ATTACHMENT_ID")
    private int attachmentId;
    @Column(name = "DUTYLOG_ID")
    private int dutyLogId;
    @Column(name = "ATTACHMENT_PATH")
    private String attachmentPath;
    @Column(name = "ATTACHMENT_NAME")
    private String attachmentName;
    @Column(name = "CREATE_USERID")
    private int createUserId;
    @Column(name = "CREATE_TIME")
    private Date createTime;

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public DutyLogAttachmentEntity()
    {
    }

    public int getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public int getDutyLogId()
    {
        return dutyLogId;
    }

    public void setDutyLogId(int dutyLogId)
    {
        this.dutyLogId = dutyLogId;
    }

    public String getAttachmentPath()
    {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath)
    {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName)
    {
        this.attachmentName = attachmentName;
    }

    public int getCreateUserId()
    {
        return createUserId;
    }

    public void setCreateUserId(int createUserId)
    {
        this.createUserId = createUserId;
    }

}
