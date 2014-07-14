package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "BMP_REPORT")
public class ReportEntity
{

    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "PARENT_ID")
    private int parentId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PARAM")
    private String param;
    @Column(name = "STATE")
    private int state;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TYPE")
    private int type;
    @Column(name = "VIEW_POS")
    private int viewPos;

    /**
     * 构造函数
     */
    public ReportEntity()
    {
        // TODO Auto-generated constructor stub
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getParentId()
    {
        return parentId;
    }

    public void setParentId(int parentId)
    {
        this.parentId = parentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getViewPos()
    {
        return viewPos;
    }

    public void setViewPos(int viewPos)
    {
        this.viewPos = viewPos;
    }
}
