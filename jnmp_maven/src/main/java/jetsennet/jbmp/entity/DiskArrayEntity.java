package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 盘阵
 * @author xuyuji
 */
@Table(name = "BMP_DISKARRAY")
public class DiskArrayEntity
{
    /**
     * ID
     */
    @Id
    @Column(name = "DISKARRAY_ID")
    private int diskArrayId;
    /**
     * 盘阵名称
     */
    @Column(name = "DISKARRAY_NAME")
    private String diskArrayName;
    /**
     * 盘阵容量大小
     */
    @Column(name = "DISKARRAY_SIZE")
    private String diskArraySize;
    /**
     * 盘阵已分配容量大小
     */
    @Column(name = "DISKARRAYOWNER_SIZE")
    private String diskArrayOwnerSize;

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

    /**
     * 盘阵已使用容量大小
     */
    @Column(name = "DISKARRAYOWNER_USE")
    private String diskArrayOwnerUse;
    /**
     * 盘阵描述
     */
    @Column(name = "DISKARRAY_DESC")
    private String diskArrayDesc;

    public String getDiskArraySize()
    {
        return diskArraySize;
    }

    public void setDiskArraySize(String diskArraySize)
    {
        this.diskArraySize = diskArraySize;
    }

    public String getDiskArrayDesc()
    {
        return diskArrayDesc;
    }

    public void setDiskArrayDesc(String diskArrayDesc)
    {
        this.diskArrayDesc = diskArrayDesc;
    }

    /**
     * 盘阵所属科室ID
     */
    @Column(name = "DEPARTMENT_ID")
    private int departmentId;

    public int getDiskArrayId()
    {
        return diskArrayId;
    }

    public void setDiskArrayId(int diskArrayId)
    {
        this.diskArrayId = diskArrayId;
    }

    public String getDiskArrayName()
    {
        return diskArrayName;
    }

    public void setDiskArrayName(String diskArrayName)
    {
        this.diskArrayName = diskArrayName;
    }

    public int getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(int departmentId)
    {
        this.departmentId = departmentId;
    }
}
