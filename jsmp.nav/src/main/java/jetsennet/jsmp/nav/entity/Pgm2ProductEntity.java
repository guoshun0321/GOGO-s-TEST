package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成，最好不要手动修改！
 */
@Table("NS_PGM2PRODUCT")
public class Pgm2ProductEntity implements Serializable
{
	/**
	 * 
	 */
	@Id
	@Column("ID")
	private int id;
	/**
	 * 节目ID，关联NS_PROGRAM表
	 */
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 节目内容的媒资唯一ID
	 */
	@Column("CONTENT_ASSETID")
	private String contentAssetid;
	/**
	 * 产品包ID，关联COP_PRODUCT表
	 */
	@Column("PRODUCT_ID")
	private int productId;
	/**
	 * 产品包代码
	 */
	@Column("PRODUCT_CODE")
	private String productCode;

	private static final long serialVersionUID = 1L;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getPgmId()
	{
		return pgmId;
	}

	public void setPgmId(int pgmId)
	{
		this.pgmId = pgmId;
	}

	public String getContentAssetid()
	{
		return contentAssetid;
	}

	public void setContentAssetid(String contentAssetid)
	{
		this.contentAssetid = contentAssetid;
	}

	public int getProductId()
	{
		return productId;
	}

	public void setProductId(int productId)
	{
		this.productId = productId;
	}

	public String getProductCode()
	{
		return productCode;
	}

	public void setProductCode(String productCode)
	{
		this.productCode = productCode;
	}

}
