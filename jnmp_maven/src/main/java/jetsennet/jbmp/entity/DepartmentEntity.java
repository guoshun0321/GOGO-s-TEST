package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 科室表
 * @author xuyuji
 */
@Table(name = "BMP_DEPARTMENT")
public class DepartmentEntity
{
    /**
     * ID
     */
    @Id
    @Column(name = "DEPARTMENT_ID")
    private int departmentId;
    /**
     * 科室名称
     */
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    /**
     * 科室描述
     */
    @Column(name = "DEPARTMENT_DESC")
    private String departmentDesc;

    public String getDepartmentDesc()
    {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc)
    {
        this.departmentDesc = departmentDesc;
    }

    public int getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(int departmentId)
    {
        this.departmentId = departmentId;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }
}
