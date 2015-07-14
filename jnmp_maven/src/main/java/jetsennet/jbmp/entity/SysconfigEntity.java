package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NET_SYSCONFIG")
public class SysconfigEntity
{
    public static final String TSCH_SCAN_UPDATE_NAME = "TschScanUpdate";
    public static final int TSCH_SCAN_UPDATE_YES = 0;
    public static final int TSCH_SCAN_UPDATE_NO = 1;
    @Id
    @Column(name = "NAME")
    private String name;
    @Column(name = "DATA")
    private String data;
    @Column(name = "TYPE")
    private int type;
    @Column(name = "VIEW_POS")
    private int viewPos;
    @Column(name = "DESCRIPTION")
    private String description;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
