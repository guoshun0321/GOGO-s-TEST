/************************************************************************
日 期：2012-1-5
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.jbmp.util.XmlUtil;

/**
 * @author 郭祥
 */
@Table(name = "BMP_TRAPTABLE")
public class TrapTableEntity
{

    /**
     * 主键
     */
    @Id
    @Column(name = "TRAP_ID")
    private int trapId;
    /**
     * 父ID，未父时，为-1
     */
    @Column(name = "PARENT_ID")
    private int parentId;
    /**
     * 所属类型
     */
    @Column(name = "MIB_ID")
    private int mibId;
    /**
     * Trap名称
     */
    @Column(name = "TRAP_NAME")
    private String trapName;
    /**
     * OID
     */
    @Column(name = "TRAP_OID")
    private String trapOid;
    /**
     * 描述
     */
    @Column(name = "TRAP_DESC")
    private String trapDesc;
    /**
     * 版本
     */
    @Column(name = "TRAP_VERSION")
    private String trapVersion;
    /**
     * 中文名称
     */
    @Column(name = "NAME_CN")
    private String nameCn;
    /**
     * 中文描述
     */
    @Column(name = "DESC_CN")
    private String descCn;
    /**
     * mib文件名
     */
    @Column(name = "MIB_FILE")
    private String mibFile;

    public String getMibFile()
    {
        return mibFile;
    }

    public void setMibFile(String mibFile)
    {
        this.mibFile = mibFile;
    }

    private List<TrapTableEntity> subs;
    /**
     * 子对象
     */
    private ArrayList<TrapTableEntity> childs = new ArrayList<TrapTableEntity>();

    /**
     * 默认的父对象ID
     */
    public static final int PARENT_ID_DEF = -1;

    /**
     * @param sub 参数
     */
    public void addSub(TrapTableEntity sub)
    {
        if (subs == null)
        {
            subs = new ArrayList<TrapTableEntity>();
        }
        subs.add(sub);
    }

    /**
     * 获取描述性名称
     * @return 结果
     */
    public String getDescName()
    {
        if (nameCn != null && !"".equals(nameCn.trim()))
        {
            return nameCn;
        }
        return trapName;
    }

    /**
     * 获取描述
     * @return 结果
     */
    public String getDescTxt()
    {
        if (descCn != null && !"".equals(descCn.trim()))
        {
            return descCn;
        }
        return trapDesc;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        XmlUtil.appendXmlBegin(sb, "TRAP");
        XmlUtil.appendTextNode(sb, "NAME", trapName);
        XmlUtil.appendTextNode(sb, "OID", trapOid);
        XmlUtil.appendTextNode(sb, "VERSION", trapVersion);
        XmlUtil.appendTextNode(sb, "DESC", trapDesc);
        XmlUtil.appendXmlBegin(sb, "BINDS");
        if (subs != null && !subs.isEmpty())
        {
            for (TrapTableEntity sub : subs)
            {
                XmlUtil.appendXmlBegin(sb, "BIND");
                XmlUtil.appendTextNode(sb, "NAME", sub.trapName);
                XmlUtil.appendTextNode(sb, "OID", sub.trapOid);
                XmlUtil.appendTextNode(sb, "DESC", sub.trapDesc);
                XmlUtil.appendXmlEnd(sb, "BIND");
            }
        }
        XmlUtil.appendXmlEnd(sb, "BINDS");
        XmlUtil.appendXmlEnd(sb, "TRAP");
        return sb.toString();
    }

    /**
     * @return the trapId
     */
    public int getTrapId()
    {
        return trapId;
    }

    /**
     * @param trapId the trapId to set
     */
    public void setTrapId(int trapId)
    {
        this.trapId = trapId;
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

    public int getMibId()
    {
        return mibId;
    }

    public void setMibId(int mibId)
    {
        this.mibId = mibId;
    }

    /**
     * @return the trapName
     */
    public String getTrapName()
    {
        return trapName;
    }

    /**
     * @param trapName the trapName to set
     */
    public void setTrapName(String trapName)
    {
        this.trapName = trapName;
    }

    /**
     * @return the trapOid
     */
    public String getTrapOid()
    {
        return trapOid;
    }

    /**
     * @param trapOid the trapOid to set
     */
    public void setTrapOid(String trapOid)
    {
        this.trapOid = trapOid;
    }

    /**
     * @return the trapDesc
     */
    public String getTrapDesc()
    {
        return trapDesc;
    }

    /**
     * @param trapDesc the trapDesc to set
     */
    public void setTrapDesc(String trapDesc)
    {
        if (trapDesc != null && trapDesc.length() > 1800)
        {
            trapDesc = trapDesc.substring(0, 1800);
        }
        this.trapDesc = trapDesc;
    }

    /**
     * @return the trapVersion
     */
    public String getTrapVersion()
    {
        return trapVersion;
    }

    /**
     * @param trapVersion the trapVersion to set
     */
    public void setTrapVersion(String trapVersion)
    {
        this.trapVersion = trapVersion;
    }

    /**
     * @return the nameCn
     */
    public String getNameCn()
    {
        return nameCn;
    }

    /**
     * @param nameCn the nameCn to set
     */
    public void setNameCn(String nameCn)
    {
        this.nameCn = nameCn;
    }

    /**
     * @return the descCn
     */
    public String getDescCn()
    {
        return descCn;
    }

    /**
     * @param descCn the descCn to set
     */
    public void setDescCn(String descCn)
    {
        if (descCn != null && descCn.length() > 1800)
        {
            descCn = descCn.substring(0, 1800);
        }
        this.descCn = descCn;
    }

    /**
     * @return the subs
     */
    public List<TrapTableEntity> getSubs()
    {
        return subs;
    }

    /**
     * @param subs the subs to set
     */
    public void setSubs(List<TrapTableEntity> subs)
    {
        this.subs = subs;
    }

    public ArrayList<TrapTableEntity> getChilds()
    {
        return childs;
    }

    public void setChilds(ArrayList<TrapTableEntity> childs)
    {
        this.childs = childs;
    }

    /**
     * 增加子节点
     * @param e 实体
     */
    public void add(TrapTableEntity e)
    {
        getChilds().add(e);
    }

}
