/************************************************************************
日 期：2012-3-1
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author yl
 */
@Table(name = "BMP_HOMEPAGE")
public class HomePageEntity
{
    /**
     * 用户ID
     */
    @Id
    @Column(name = "USER_ID")
    private int userId;
    /**
     * 主页保存路径
     */
    @Column(name = "HOME_PATH")
    private String homePath;
    /**
     * 冗余
     */
    @Column(name = "FIELD_1")
    private String field1;

    /**
     * 构造函数
     */
    public HomePageEntity()
    {
    }

    /**
     * @return the userId
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * @return the homePath
     */
    public String getHomePath()
    {
        return homePath;
    }

    /**
     * @param homePath the homePath to set
     */
    public void setHomePath(String homePath)
    {
        this.homePath = homePath;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

}
