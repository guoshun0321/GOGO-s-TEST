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
@Table(name = "BMP_KNOWLEDGEATTACHMENT")
public class AttachmentEntity
{
    @Id
    @Column(name = "ATTACHMENT_ID")
    private int attachmentId;
    @Column(name = "KNOWLEDGE_ID")
    private int knowledgeId;
    @Column(name = "ATTACHMENT_NAME")
    private String attachmentName;
    @Column(name = "ATTACHMENT_PATH")
    private String attachmentPath;
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 构造函数
     */
    public AttachmentEntity()
    {

    }

    /**
     * @return the attachmentId
     */
    public int getAttachmentId()
    {
        return attachmentId;
    }

    /**
     * @param attachmentId the attachmentId to set
     */
    public void setAttachmentId(int attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    /**
     * @return the knowledgeId
     */
    public int getKnowledgeId()
    {
        return knowledgeId;
    }

    /**
     * @param knowledgeId the knowledgeId to set
     */
    public void setKnowledgeId(int knowledgeId)
    {
        this.knowledgeId = knowledgeId;
    }

    /**
     * @return the attachmentName
     */
    public String getAttachmentName()
    {
        return attachmentName;
    }

    /**
     * @param attachmentName the attachmentName to set
     */
    public void setAttachmentName(String attachmentName)
    {
        this.attachmentName = attachmentName;
    }

    /**
     * @return the attachmentPath
     */
    public String getAttachmentPath()
    {
        return attachmentPath;
    }

    /**
     * @param attachmentPath the attachmentPath to set
     */
    public void setAttachmentPath(String attachmentPath)
    {
        this.attachmentPath = attachmentPath;
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
