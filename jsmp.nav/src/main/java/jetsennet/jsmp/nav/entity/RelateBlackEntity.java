package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成，最好不要手动修改！
 */
@Table("NS_RELATEBLACK")
public class RelateBlackEntity implements Serializable
{
	/**
	 * 源栏目ID，关联COP_COLUMN表
	 */
	@Id
	@Column("SRC_COLUMN_ID")
	private int srcColumnId;
	/**
	 * 过滤内容类型，逗号连接
	 */
	@Column("BLACK_CONTENTTYPE")
	private String blackContenttype;
	/**
	 * 过滤内容提供商，逗号连接
	 */
	@Column("BLACK_CP")
	private String blackCp;
	/**
	 * 过滤关键词，逗号连接
	 */
	@Column("BLACK_KEYWORDS")
	private String blackKeywords;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private int updateTime;

	private static final long serialVersionUID = 1L;

	public int getSrcColumnId()
	{
		return srcColumnId;
	}

	public void setSrcColumnId(int srcColumnId)
	{
		this.srcColumnId = srcColumnId;
	}

	public String getBlackContenttype()
	{
		return blackContenttype;
	}

	public void setBlackContenttype(String blackContenttype)
	{
		this.blackContenttype = blackContenttype;
	}

	public String getBlackCp()
	{
		return blackCp;
	}

	public void setBlackCp(String blackCp)
	{
		this.blackCp = blackCp;
	}

	public String getBlackKeywords()
	{
		return blackKeywords;
	}

	public void setBlackKeywords(String blackKeywords)
	{
		this.blackKeywords = blackKeywords;
	}

	public int getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(int updateTime)
	{
		this.updateTime = updateTime;
	}

}
