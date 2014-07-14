package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 盘阵拥有者
 * @author xuyuji
 */
@Table(name = "BMP_DISKARRAYOWNER")
public class DiskArrayOwnerEntity
{
    /**
     * ID
     */
    @Id
    @Column(name = "DISKARRAYOWNER_ID")
    private int diskArrayOwnerId;
    /**
     * 盘阵拥有者名称
     */
    @Column(name = "DISKARRAYOWNER_NAME")
    private String diskArrayOwnerName;
    /**
     * 盘阵拥有者编号
     */
    @Column(name = "DISKARRAYOWNER_NO")
    private String diskArrayOwnerNo;
    /**
     * 盘阵拥有者分配容量大小
     */
    @Column(name = "DISKARRAYOWNER_SIZE")
    private String diskArrayOwnerSize;
    /**
     * 盘阵拥有者使用容量大小
     */
    @Column(name = "DISKARRAYOWNER_USE")
    private String diskArrayOwnerUse;
    /**
     * 盘阵拥有者描述
     */
    @Column(name = "DISKARRAYOWNER_DESC")
    private String diskArrayOwnerDesc;
    /**
     * 盘阵ID
     */
    @Column(name = "DISKARRAY_ID")
    private int diskArrayId;

    public String getDiskArrayOwnerSize()
    {
        return diskArrayOwnerSize;
    }

    public void setDiskArrayOwnerSize(String diskArrayOwnerSize)
    {
        this.diskArrayOwnerSize = diskArrayOwnerSize;
    }

    public String getDiskArrayOwnerUse()
    {
        return diskArrayOwnerUse;
    }

    public void setDiskArrayOwnerUse(String diskArrayOwnerUse)
    {
        this.diskArrayOwnerUse = diskArrayOwnerUse;
    }

    public String getDiskArrayOwnerDesc()
    {
        return diskArrayOwnerDesc;
    }

    public void setDiskArrayOwnerDesc(String diskArrayOwnerDesc)
    {
        this.diskArrayOwnerDesc = diskArrayOwnerDesc;
    }

    public int getDiskArrayOwnerId()
    {
        return diskArrayOwnerId;
    }

    public void setDiskArrayOwnerId(int diskArrayOwnerId)
    {
        this.diskArrayOwnerId = diskArrayOwnerId;
    }

    public String getDiskArrayOwnerName()
    {
        return diskArrayOwnerName;
    }

    public void setDiskArrayOwnerName(String diskArrayOwnerName)
    {
        this.diskArrayOwnerName = diskArrayOwnerName;
    }

    public int getDiskArrayId()
    {
        return diskArrayId;
    }

    public void setDiskArrayId(int diskArrayId)
    {
        this.diskArrayId = diskArrayId;
    }

    public String getDiskArrayOwnerNo()
    {
        return diskArrayOwnerNo;
    }

    public void setDiskArrayOwnerNo(String diskArrayOwnerNo)
    {
        this.diskArrayOwnerNo = diskArrayOwnerNo;
    }
}
