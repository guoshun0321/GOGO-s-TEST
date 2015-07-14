package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @version 1.0 date 2011-12-30上午9:38:53
 * @author xli
 */
@Table(name = "NMP_CONTENTVERSION")
public class ContentVersionEntity
{
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "resource_ID")
    private int resourceID;
    @Column(name = "contentName")
    private String contentName;
    @Column(name = "resContent")
    private String content;
    @Column(name = "version")
    private String version;
    @Column(name = "userId")
    private int userId;
    @Column(name = "addTime")
    private Date addTime;

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public Date getAddTime()
    {
        return addTime;
    }

    public void setAddTime(Date addTime)
    {
        this.addTime = addTime;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getResourceID()
    {
        return resourceID;
    }

    public void setResourceID(int resourceID)
    {
        this.resourceID = resourceID;
    }

    public String getContentName()
    {
        return contentName;
    }

    public void setContentName(String contentName)
    {
        this.contentName = contentName;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

}
