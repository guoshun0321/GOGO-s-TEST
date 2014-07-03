/**
 * 日 期： 2011-12-6
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  ManufacturersEntity.java
 * 历 史： 2011-12-6 创建
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 厂商对象
 */
@Table(name = "BMP_MANUFACTURERS")
public class ManufacturersEntity
{
    @Id
    @Column(name = "MAN_ID")
    private int manId;
    @Column(name = "MAN_NAME")
    private String manName;
    @Column(name = "MAN_DESC")
    private String manDesc;

    /**
     * @return the manId
     */
    public int getManId()
    {
        return manId;
    }

    /**
     * @param manId the manId to set
     */
    public void setManId(int manId)
    {
        this.manId = manId;
    }

    /**
     * @return the manName
     */
    public String getManName()
    {
        return manName;
    }

    /**
     * @param manName the manName to set
     */
    public void setManName(String manName)
    {
        this.manName = manName;
    }

    /**
     * @return the manDesc
     */
    public String getManDesc()
    {
        return manDesc;
    }

    /**
     * @param manDesc the manDesc to set
     */
    public void setManDesc(String manDesc)
    {
        this.manDesc = manDesc;
    }

}
