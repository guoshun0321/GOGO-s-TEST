package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_PRODUCT")
public class ProductEntity implements Serializable
{
	/**
	 * 产品包ID
	 */
	@Id
	@Column("PRODUCT_ID")
	private int productId;
	/**
	 * 产品包名称
	 */
	@Column("PRODUCT_NAME")
	private String productName;
	/**
	 * 产品包代码
	 */
	@Column("PRODUCT_CODE")
	private String productCode;
	/**
	 * 产品包类型
	 */
	@Column("PRODUCT_TYPE")
	private int productType;
	/**
	 * 产品包状态。
	 */
	@Column("PRODUCT_STATE")
	private int productState;
	/**
	 * 有效开始时间，单位：毫秒
	 */
	@Column("LICENSING_START")
	private long licensingStart;
	/**
	 * 有效结束时间，单位：毫秒
	 */
	@Column("LICENSING_END")
	private long licensingEnd;
	/**
	 * 描述
	 */
	@Column("PRODUCT_DESC")
	private String productDesc;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;

	private static final long serialVersionUID = 1L;

	public int getProductId()
	{
		return productId;
	}

	public void setProductId(int productId)
	{
		this.productId = productId;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getProductCode()
	{
		return productCode;
	}

	public void setProductCode(String productCode)
	{
		this.productCode = productCode;
	}

	public int getProductType()
	{
		return productType;
	}

	public void setProductType(int productType)
	{
		this.productType = productType;
	}

	public int getProductState()
	{
		return productState;
	}

	public void setProductState(int productState)
	{
		this.productState = productState;
	}

	public long getLicensingStart()
	{
		return licensingStart;
	}

	public void setLicensingStart(long licensingStart)
	{
		this.licensingStart = licensingStart;
	}

	public long getLicensingEnd()
	{
		return licensingEnd;
	}

	public void setLicensingEnd(long licensingEnd)
	{
		this.licensingEnd = licensingEnd;
	}

	public String getProductDesc()
	{
		return productDesc;
	}

	public void setProductDesc(String productDesc)
	{
		this.productDesc = productDesc;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

}
