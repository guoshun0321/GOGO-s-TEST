/**********************************************************************
 * 日 期: 2012-09-24
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: PictureEntity.java
 * 历 史: 2012-09-24 Create
 *********************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author liwei 资源管理中的图标
 */
@Table(name = "BMP_PICTURE")
public class PictureEntity
{
    /**
     * PICTURE_ID
     */
    @Id
    @Column(name = "PICTURE_ID")
    private int pictureId;

    /**
     * PICTURE_NAME
     */
    @Column(name = "PICTURE_NAME")
    private String pictureName;

    /**
     * PICTURE_PATH
     */
    @Column(name = "PICTURE_PATH")
    private String picturePath;

    /**
     * CREATTIME
     */
    @Column(name = "CREATTIME")
    private Date creattime;

    /**
     * FIELD_1
     */
    @Column(name = "FIELD_1")
    private String field1;

    /**
     * FIELD_2
     */
    @Column(name = "FIELD_2")
    private String field2;

    /**
     * FIELD_3
     */
    @Column(name = "FIELD_3")
    private String field3;

    public int getPictureId()
    {
        return pictureId;
    }

    public void setPictureId(int pictureId)
    {
        this.pictureId = pictureId;
    }

    public String getPictureName()
    {
        return pictureName;
    }

    public void setPictureName(String pictureName)
    {
        this.pictureName = pictureName;
    }

    public String getPicturePath()
    {
        return picturePath;
    }

    public void setPicturePath(String picturePath)
    {
        this.picturePath = picturePath;
    }

    public Date getCreattime()
    {
        return creattime;
    }

    public void setCreattime(Date creattime)
    {
        this.creattime = creattime;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    public String getField2()
    {
        return field2;
    }

    public void setField2(String field2)
    {
        this.field2 = field2;
    }

    public String getField3()
    {
        return field3;
    }

    public void setField3(String field3)
    {
        this.field3 = field3;
    }
}
