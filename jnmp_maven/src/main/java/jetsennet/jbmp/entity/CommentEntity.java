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
@Table(name = "BMP_KNOWLEDGECOMMENT")
public class CommentEntity
{
    @Id
    @Column(name = "COMMENT_ID")
    private int commentId;
    @Column(name = "KNOWLEDGE_ID")
    private int knowledgeId;
    @Column(name = "COMMENT_CONTENT")
    private String commentContent;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "CREATE_USERID")
    private int createUserId;

    /**
     * 构造方法
     */
    public CommentEntity()
    {
    }

    /**
     * @return the commentId
     */
    public int getCommentId()
    {
        return commentId;
    }

    /**
     * @param commentId the commentId to set
     */
    public void setCommentId(int commentId)
    {
        this.commentId = commentId;
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
     * @return the commentContent
     */
    public String getCommentContent()
    {
        return commentContent;
    }

    /**
     * @param commentContent the commentContent to set
     */
    public void setCommentContent(String commentContent)
    {
        this.commentContent = commentContent;
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
}
