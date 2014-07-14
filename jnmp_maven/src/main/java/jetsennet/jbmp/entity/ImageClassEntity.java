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
@Table(name = "BMP_IMAGECLASS")
public class ImageClassEntity
{
    @Id
    @Column(name = "CLASS_ID")
    private int classId;
    @Column(name = "PARENT_ID")
    private int parentId;
    @Column(name = "CLASS_NAME")
    private String className;
    @Column(name = "CLASS_DESC")
    private String classDesc;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "FIELD_1")
    private String field1;

    public int getClassId()
    {
        return classId;
    }

    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassDesc()
    {
        return classDesc;
    }

    public void setClassDesc(String classDesc)
    {
        this.classDesc = classDesc;
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
