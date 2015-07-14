/**
 * 
 */
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author lianghongjie
 */
@Table(name = "BMP_TOPOIMAGE")
public class TopoImageEntity
{
    @Id
    @Column(name = "IMG_ID")
    private int imgId;
    @Column(name = "CLASS_ID")
    private int classId;
    @Column(name = "IMG_NAME")
    private String imgName;
    @Column(name = "IMG_PATH")
    private String imgPath;
    @Column(name = "URL_PATH")
    private String urlPath;
    @Column(name = "IMG_WIDTH")
    private int imgWidth;
    @Column(name = "IMG_HEIGHT")
    private int imgHeight;
    @Column(name = "IS_SYSIMG")
    private boolean isSysImg;
    @Column(name = "OBJ_CLASS")
    private String objClass;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "FIELD_1")
    private String field1;

    public int getImgId()
    {
        return imgId;
    }

    public void setImgId(int imgId)
    {
        this.imgId = imgId;
    }

    public int getClassId()
    {
        return classId;
    }

    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public String getImgName()
    {
        return imgName;
    }

    public void setImgName(String imgName)
    {
        this.imgName = imgName;
    }

    public String getImgPath()
    {
        return imgPath;
    }

    public void setImgPath(String imgPath)
    {
        this.imgPath = imgPath;
    }

    public String getUrlPath()
    {
        return urlPath;
    }

    public void setUrlPath(String urlPath)
    {
        this.urlPath = urlPath;
    }

    public int getImgWidth()
    {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth)
    {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight()
    {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight)
    {
        this.imgHeight = imgHeight;
    }

    public boolean getIsSysImg()
    {
        return isSysImg;
    }

    public void setIsSysImg(boolean isSysImg)
    {
        this.isSysImg = isSysImg;
    }

    public String getObjClass()
    {
        return objClass;
    }

    public void setObjClass(String objClass)
    {
        this.objClass = objClass;
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

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }
}
