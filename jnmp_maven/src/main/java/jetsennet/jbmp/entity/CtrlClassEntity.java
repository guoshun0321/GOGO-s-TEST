package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "BMP_CTRLCLASS")
public class CtrlClassEntity
{

    @Id
    @Column(name = "CLASS_ID")
    private int class_id;
    @Column(name = "PARENT_ID")
    private int parent_id;
    @Column(name = "CLASS_TYPE")
    private int class_type;
    @Column(name = "CLASS_NAME")
    private String class_name;
    @Column(name = "LAYER")
    private String layer;
    @Column(name = "CLASS_DESC")
    private String class_desc;
    @Column(name = "VIEW_NAME")
    private String view_name;

    /**
     * 构造方法
     */
    public CtrlClassEntity()
    {
        // TODO Auto-generated constructor stub
    }

    public int getClass_id()
    {
        return class_id;
    }

    public void setClass_id(int class_id)
    {
        this.class_id = class_id;
    }

    public int getParent_id()
    {
        return parent_id;
    }

    public void setParent_id(int parent_id)
    {
        this.parent_id = parent_id;
    }

    public int getClass_type()
    {
        return class_type;
    }

    public void setClass_type(int class_type)
    {
        this.class_type = class_type;
    }

    public String getClass_name()
    {
        return class_name;
    }

    public void setClass_name(String class_name)
    {
        this.class_name = class_name;
    }

    public String getLayer()
    {
        return layer;
    }

    public void setLayer(String layer)
    {
        this.layer = layer;
    }

    public String getClass_desc()
    {
        return class_desc;
    }

    public void setClass_desc(String class_desc)
    {
        this.class_desc = class_desc;
    }

    public String getView_name()
    {
        return view_name;
    }

    public void setView_name(String view_name)
    {
        this.view_name = view_name;
    }
}
