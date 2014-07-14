package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "BMP_ATTRIB2CLASS")
public class Attrib2ClassEntity
{

    @Column(name = "CLASS_ID")
    private int classId;
    @Column(name = "ATTRIB_ID")
    private int attribId;

    /**
     * 构造函数
     */
    public Attrib2ClassEntity()
    {
    }

    /**
     * @param classId 参数
     * @param attribId 参数
     */
    public Attrib2ClassEntity(int classId, int attribId)
    {
        this.classId = classId;
        this.attribId = attribId;
    }

    /**
     * @return the classId
     */
    public int getClassId()
    {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(int classId)
    {
        this.classId = classId;
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
}
