package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;

/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_COLUMN2RELATERULE")
public class Column2RelateruleEntity implements Serializable
{
    /**
     * 
     */
    @Id
    @Column("REL_ID")
    private int id;
    /**
     * 关联栏目
     */
    @Column("REL_COLUMN_ID")
    private int relColumnId;
    /**
     * 节目字段
     */
    @Column("REL_FIELD")
    private String relField;
    /**
     * 字段名称
     */
    @Column("REL_FIELD_NAME")
    private String relFieldName;
    /**
     * 优先级，数字越小，优先级越高
     */
    @Column("REL_LEVEL")
    private int level;

    private static final long serialVersionUID = 1L;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getRelColumnId()
    {
        return relColumnId;
    }

    public void setRelColumnId(int relColumnId)
    {
        this.relColumnId = relColumnId;
    }

    public String getRelField()
    {
        return relField;
    }

    public void setRelField(String relField)
    {
        this.relField = relField;
    }

    public String getRelFieldName()
    {
        return relFieldName;
    }

    public void setRelFieldName(String relFieldName)
    {
        this.relFieldName = relFieldName;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

}
