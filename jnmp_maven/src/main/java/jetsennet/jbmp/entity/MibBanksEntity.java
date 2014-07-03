package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ?
 */
@Table(name = "BMP_MIBBANKS")
public class MibBanksEntity
{

    @Id
    @Column(name = "MIB_ID")
    private int mibId;
    @Column(name = "MIB_NAME")
    private String mibName;
    @Column(name = "MIB_ALIAS")
    private String mibAlias;
    @Column(name = "MIB_DESC")
    private String mibDesc;
    @Column(name = "MIB_FILE")
    private String mibFile;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;

    public MibBanksEntity()
    {
    }

    public int getMibId()
    {
        return mibId;
    }

    public void setMibId(int mibId)
    {
        this.mibId = mibId;
    }

    public String getMibName()
    {
        return mibName;
    }

    public void setMibName(String mibName)
    {
        this.mibName = mibName;
    }

    public String getMibAlias()
    {
        return mibAlias;
    }

    public void setMibAlias(String mibAlias)
    {
        this.mibAlias = mibAlias;
    }

    public String getMibDesc()
    {
        return mibDesc;
    }

    public void setMibDesc(String mibDesc)
    {
        this.mibDesc = mibDesc;
    }

    public String getMibFile()
    {
        return mibFile;
    }

    public void setMibFile(String mibFile)
    {
        this.mibFile = mibFile;
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

}
