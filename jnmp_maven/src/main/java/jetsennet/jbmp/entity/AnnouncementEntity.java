/*
 * 时间：2012年2月13日
 * 公告栏实体类
 * 对应数据库为NMP_ANNOUNCEMENT表
 * 作者：郭世平
 */

package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author？
 */
@Table(name = "BMP_ANNOUNCEMENT")
public class AnnouncementEntity
{
    @Id
    @Column(name = "ANNOUNCEMENT_ID")
    private int announcementId;
    @Column(name = "ANNOUNCEMENT_TITLE")
    private String announcementTitle;
    @Column(name = "ANNOUNCEMENT_CONTENT")
    private String announcementContent;
    @Column(name = "ANNOUNCEMENT_FILE")
    private String announcementFile;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "IS_TOP")
    private int top;

    /**
     * 构造函数
     */
    public AnnouncementEntity()
    {

    }

    /* setters and getters */
    public int getAnnouncementId()
    {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId)
    {
        this.announcementId = announcementId;
    }

    public String getAnnouncementTitle()
    {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle)
    {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementContent()
    {
        return announcementContent;
    }

    public void setAnnouncementContent(String announcementContent)
    {
        this.announcementContent = announcementContent;
    }

    public String getAnnouncementFile()
    {
        return announcementFile;
    }

    public void setAnnouncementFile(String announcementFile)
    {
        this.announcementFile = announcementFile;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public int getTop()
    {
        return top;
    }

    public void setTop(int top)
    {
        this.top = top;
    }

}
