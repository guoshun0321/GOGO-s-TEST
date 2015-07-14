package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "BMP_CLASS2CLASS")
public class Class2ClassEntity
{

    @Column(name = "CLASS_ID")
    private int classId;
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 关系类型。0，父子关系；1，从属关系。
     */
    @Column(name = "USE_TYPE")
    private int useType;
    /**
     * 父子关系
     */
    public static final int USE_TYPE_PARENT = 0;
    /**
     * 从属关系
     */
    public static final int USE_TYPE_CONTAIN = 1;

    /**
     * 构造函数
     */
    public Class2ClassEntity()
    {
    }

    /**
     * @param classId 参数
     * @param parentId 参数
     */
    public Class2ClassEntity(int classId, int parentId)
    {
        this.classId = classId;
        this.parentId = parentId;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(classId);
        sb.append(",");
        sb.append(parentId);
        sb.append(">");
        return sb.toString();
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
     * @return the parentId
     */
    public int getParentId()
    {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    /**
     * @return the useType
     */
    public int getUseType()
    {
        return useType;
    }

    /**
     * @param useType the useType to set
     */
    public void setUseType(int useType)
    {
        this.useType = useType;
    }
}
