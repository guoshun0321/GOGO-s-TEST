/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: 字符型性能数据结果
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 字符型性能数据结果
 * @author 郭祥
 */
@Table(name = "BMP_OBJATTRIBVALUE")
public class ObjAttribValueEntity
{
    /**
     * 对象属性ID
     */
    @Id
    @Column(name = "OBJATTR_ID")
    private int objAttrId;
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 采集时间
     */
    @Column(name = "COLL_TIME")
    private long collTime;
    /**
     * 属性值
     */
    @Column(name = "STR_VALUE")
    private String strValue;

    /**
     * 构造函数
     */
    public ObjAttribValueEntity()
    {
    }

    /**
     * @return the objAttrId
     */
    public int getObjAttrId()
    {
        return objAttrId;
    }

    /**
     * @param objAttrId the objAttrId to set
     */
    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public long getCollTime()
    {
        return collTime;
    }

    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    public String getStrValue()
    {
        return strValue;
    }

    public void setStrValue(String strValue)
    {
        this.strValue = strValue;
    }
}
