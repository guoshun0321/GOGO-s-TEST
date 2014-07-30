package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;

/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_PGM2PGM")
public class Pgm2PgmEntity implements Serializable
{
	/**
	 * 父节目ID
	 */
	@Id
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 子节目以及关联关系描述。格式为【子节目ID,关联类型;子节目ID,关联类型;】。关联类型。0：系列-正片；1：系列-预告片；2：系列-花絮；3：系列-片花；10：正片-预告片；11：正片-花絮；12：正片-片花；
	 */
	@Column("REL_DESC")
	private String relDesc;

	private static final long serialVersionUID = 1L;

	public Pgm2PgmEntity()
	{
	}

	public Pgm2PgmEntity(int pgmId, String relDesc)
	{
		this.pgmId = pgmId;
		this.relDesc = relDesc;
	}

	public int getPgmId()
	{
		return pgmId;
	}

	public void setPgmId(int pgmId)
	{
		this.pgmId = pgmId;
	}

	public String getRelDesc()
	{
		return relDesc;
	}

	public void setRelDesc(String relDesc)
	{
		this.relDesc = relDesc;
	}

}
