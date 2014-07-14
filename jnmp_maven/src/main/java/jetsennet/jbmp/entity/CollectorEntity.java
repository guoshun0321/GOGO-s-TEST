package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 数据采集器
 * @author 郭祥
 */
@Table(name = "BMP_COLLECTOR")
public class CollectorEntity
{

    /**
     * 采集器ID
     */
    @Id
    @Column(name = "COLL_ID")
    private int collId;
    /**
     * 采集器名称
     */
    @Column(name = "COLL_NAME")
    private String collName;
    /**
     * 采集器类型
     */
    @Column(name = "COLL_TYPE")
    private String collType;
    /**
     * 采集对象IP
     */
    @Column(name = "IP_ADDR")
    private String ipAddr;
    /**
     * 采集器创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 构造函数
     */
    public CollectorEntity()
    {
    }

    /**
     * @return the collId
     */
    public int getCollId()
    {
        return collId;
    }

    /**
     * @param collId the collId to set
     */
    public void setCollId(int collId)
    {
        this.collId = collId;
    }

    /**
     * @return the collName
     */
    public String getCollName()
    {
        return collName;
    }

    /**
     * @param collName the collName to set
     */
    public void setCollName(String collName)
    {
        this.collName = collName;
    }

    /**
     * @return the collType
     */
    public String getCollType()
    {
        return collType;
    }

    /**
     * @param collType the collType to set
     */
    public void setCollType(String collType)
    {
        this.collType = collType;
    }

    /**
     * @return the ipAddr
     */
    public String getIpAddr()
    {
        return ipAddr;
    }

    /**
     * @param ipAddr the ipAddr to set
     */
    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
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

    @Override
    public String toString()
    {
        return collName;
    }
}
