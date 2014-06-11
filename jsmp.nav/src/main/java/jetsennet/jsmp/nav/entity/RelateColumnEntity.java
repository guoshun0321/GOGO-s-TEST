package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成，最好不要手动修改！
 */
@Table("NS_RELATECOLUMN")
public class RelateColumnEntity implements Serializable
{
	/**
	 * 关联栏目ID，关联COP_COLUMN表
	 */
	@Id
	@Column("REL_COLUMN_ID")
	private int relColumnId;
	/**
	 * 源栏目ID，关联COP_COLUMN表
	 */
	@Id
	@Column("SRC_COLUMN_ID")
	private int srcColumnId;
	/**
	 * 关联规则
	 */
	@Column("RELATE_RULE")
	private String relateRule;

	private static final long serialVersionUID = 1L;

	public int getRelColumnId()
	{
		return relColumnId;
	}

	public void setRelColumnId(int relColumnId)
	{
		this.relColumnId = relColumnId;
	}

	public int getSrcColumnId()
	{
		return srcColumnId;
	}

	public void setSrcColumnId(int srcColumnId)
	{
		this.srcColumnId = srcColumnId;
	}

	public String getRelateRule()
	{
		return relateRule;
	}

	public void setRelateRule(String relateRule)
	{
		this.relateRule = relateRule;
	}

}
