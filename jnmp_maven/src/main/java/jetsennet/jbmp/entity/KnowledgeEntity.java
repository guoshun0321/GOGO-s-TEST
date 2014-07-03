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
@Table(name = "BMP_KNOWLEDGE")
public class KnowledgeEntity
{
    @Id
    @Column(name = "KNOWLEDGE_ID")
    private int knowledgeId;
    @Column(name = "KNOWLEDGE_TITLE")
    private String knowledgeTitle;
    @Column(name = "KNOWLEDGE_SUMMARY")
    private String knowledgeSummary;
    @Column(name = "KNOWLEDGE_CONTENT")
    private String knowledgeContent;
    @Column(name = "CREATE_USERID")
    private int createUserId;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "CLICK_COUNT")
    private int clickCount;
    @Column(name = "COMMENT_COUNT")
    private int commentCount;
    @Column(name = "CLASS_ID")
    private int classId;

    public int getClassId()
    {
        return classId;
    }

    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public int getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }

    @Column(name = "ALARM_ID")
    private int alarmId;

    public KnowledgeEntity()
    {

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
     * @return the knowledgeTitle
     */
    public String getKnowledgeTitle()
    {
        return knowledgeTitle;
    }

    /**
     * @param knowledgeTitle the knowledgeTitle to set
     */
    public void setKnowledgeTitle(String knowledgeTitle)
    {
        this.knowledgeTitle = knowledgeTitle;
    }

    /**
     * @return the knowledgeSummary
     */
    public String getKnowledgeSummary()
    {
        return knowledgeSummary;
    }

    /**
     * @param knowledgeSummary the knowledgeSummary to set
     */
    public void setKnowledgeSummary(String knowledgeSummary)
    {
        this.knowledgeSummary = knowledgeSummary;
    }

    /**
     * @return the knowledgeContent
     */
    public String getKnowledgeContent()
    {
        return knowledgeContent;
    }

    /**
     * @param knowledgeContent the knowledgeContent to set
     */
    public void setKnowledgeContent(String knowledgeContent)
    {
        this.knowledgeContent = knowledgeContent;
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

    /**
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    /**
     * @return the clickCount
     */
    public int getClickCount()
    {
        return clickCount;
    }

    /**
     * @param clickCount the clickCount to set
     */
    public void setClickCount(int clickCount)
    {
        this.clickCount = clickCount;
    }

    /**
     * @return the commentCount
     */
    public int getCommentCount()
    {
        return commentCount;
    }

    /**
     * @param commentCount the commentCount to set
     */
    public void setCommentCount(int commentCount)
    {
        this.commentCount = commentCount;
    }
}
