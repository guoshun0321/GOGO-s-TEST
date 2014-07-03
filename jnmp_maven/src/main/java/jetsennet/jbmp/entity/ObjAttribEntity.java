/************************************************************************
日 期：2011-12-20
作 者: 郭祥
版 本：v1.3
描 述: 对象属性实体类
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.io.Serializable;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 监控对象属性
 * @author 郭祥
 */
@Table(name = "BMP_OBJATTRIB")
public class ObjAttribEntity implements Cloneable, Serializable
{

    /**
     * 是否可见，不可见
     */
    public static final int VISIBLE_ON =0;
    /**
     * 是否可见，可见；
     */
    public static final int VISIBLE_OFF=1;
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
     * 属性ID
     */
    @Column(name = "ATTRIB_ID")
    private int attribId;
    /**
     * 自定义属性：输入值；配置信息：采集值；Trap：oid；信号：标识值；其他：空
     */
    @Column(name = "ATTRIB_VALUE")
    private String attribValue;
    /**
     * 计算规则 <code>smi格式为:path:cop;wql:wql;</code> added by xli
     */
    @Column(name = "ATTRIB_PARAM")
    private String attribParam;
    /**
     * 属性名称
     */
    @Column(name = "OBJATTR_NAME")
    private String objattrName;
    /**
     * 编码
     */
    @Column(name = "DATA_ENCODING")
    private String dataEncoding;
    /**
     * 属性类型
     */
    @Column(name = "ATTRIB_TYPE")
    private int attribType;
    /**
     * 采集间隔
     */
    @Column(name = "COLL_TIMESPAN")
    private int collTimespan;
    /**
     * 是否可见。0，不可见；1，可见。
     */
    @Column(name = "IS_VISIBLE")
    private int isVisible;
    /**
     * 扩展
     */
    @Column(name = "FIELD_1")
    private String field1;
    // <editor-fold defaultstate="collapsed" desc="采集或实例化化时使用">
    /**
     * 所属属性
     */
    private AttributeEntity attr;
    /**
     * 实例化后的表达式
     */
    private String insParam;
    /**
     * 实例化结果
     */
    private Object insResult;
    /**
     * 从属性来的报警ID
     */
    private int alarmId;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ObjAttribEntity.class);
    /**
     * class版本控制
     */
    static final long serialVersionUID = -1L;

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * @return 复制
     */
    public ObjAttribEntity copy()
    {
        ObjAttribEntity retval = null;
        try
        {
            retval = (ObjAttribEntity) this.clone();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    // <editor-fold defaultstate="collapsed" desc="数据访问">
    /**
     * 构造函数
     */
    public ObjAttribEntity()
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

    /**
     * @return the attribId
     */
    public int getAttribId()
    {
        return attribId;
    }

    /**
     * @param attribId the attribId to set
     */
    public void setAttribId(int attribId)
    {
        this.attribId = attribId;
    }

    /**
     * @return the attribValue
     */
    public String getAttribValue()
    {
        return attribValue;
    }

    /**
     * @param attribValue the attribValue to set
     */
    public void setAttribValue(String attribValue)
    {
        this.attribValue = attribValue;
    }

    /**
     * @return the attribParam
     */
    public String getAttribParam()
    {
        return attribParam;
    }

    /**
     * @param attribParam the attribParam to set
     */
    public void setAttribParam(String attribParam)
    {
        this.attribParam = attribParam;
    }

    /**
     * @return the objattrName
     */
    public String getObjattrName()
    {
        return objattrName;
    }

    /**
     * @param objattrName the objattrName to set
     */
    public void setObjattrName(String objattrName)
    {
        this.objattrName = objattrName;
    }

    /**
     * @return the dataEncoding
     */
    public String getDataEncoding()
    {
        if (dataEncoding != null)
        {
            return dataEncoding;
        }
        else
        {
            return "ASCII";
        }
    }

    /**
     * @param dataEncoding the dataEncoding to set
     */
    public void setDataEncoding(String dataEncoding)
    {
        this.dataEncoding = dataEncoding;
    }

    public int getAttribType()
    {
        return attribType;
    }

    public void setAttribType(int attribType)
    {
        this.attribType = attribType;
    }

    public int getCollTimespan()
    {
        return collTimespan;
    }

    public void setCollTimespan(int collTimespan)
    {
        this.collTimespan = collTimespan;
    }

    /**
     * @return the attr
     */
    public AttributeEntity getAttr()
    {
        return attr;
    }

    /**
     * @param attr the attr to set
     */
    public void setAttr(AttributeEntity attr)
    {
        this.attr = attr;
    }

    /**
     * @return the insParam
     */
    public String getInsParam()
    {
        return insParam;
    }

    /**
     * @param insParam the insParam to set
     */
    public void setInsParam(String insParam)
    {
        this.insParam = insParam;
    }

    /**
     * @return the insResult
     */
    public Object getInsResult()
    {
        return insResult;
    }

    /**
     * @param insResult the insResult to set
     */
    public void setInsResult(Object insResult)
    {
        this.insResult = insResult;
    }

    /**
     * @return the isVisible
     */
    public int getIsVisible()
    {
        return isVisible;
    }

    /**
     * @param isVisible the isVisible to set
     */
    public void setIsVisible(int isVisible)
    {
        this.isVisible = isVisible;
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

    public int getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }
    // </editor-fold>
}
