package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 
 */
@Table("NS_CHANNEL")
public class ChannelEntity implements Serializable
{
	/**
	 * 频道ID
	 */
	@Id
	@Column("CHL_ID")
	@IdentAnnocation("channelID")
	private int chlId;
	/**
	 * 频道名称
	 */
	@Column("CHL_NAME")
	@IdentAnnocation("channelName")
	private String chlName;
	/**
	 * 唯一资产ID
	 */
	@Column("ASSET_ID")
	private String assetId;
	/**
	 * 频道状态：0(默认)，不可用；1：可用
	 */
	@Column("CHL_STATE")
	private int chlState;
	/**
	 * 频道类型。0：直播频道；1：自营频道；
	 */
	@Column("CHL_TYPE")
	private int chlType;
	/**
	 * 频道描述
	 */
	@Column("CHL_DESC")
	private String chlDesc;
	/**
	 * 频道标识
	 */
	@Column("CHL_IDENT")
	private String chlIdent;
	/**
	 * 建议频道号
	 */
	@Column("CHL_NUM")
	@IdentAnnocation("channelNumber")
	private int chlNum;
	/**
	 * 频道台标
	 */
	@Column("CHL_LOGO")
	@IdentAnnocation("logo")
	private String chlLogo;
	/**
	 * 是否支持回看：0：不可回看；1:可回看；
	 */
	@Column("IS_STARTOVER")
	@IdentAnnocation("isStartOver")
	private int isStartover;
	/**
	 * 是否支持NPVR：0：不支持；1:支持；
	 */
	@Column("IS_NPVR")
	@IdentAnnocation("isNPVR")
	private int isNpvr;
	/**
	 * 是否标准频道：0：不是；1:是；
	 */
	@Column("IS_STANDARD")
	@IdentAnnocation("isStandardChannel")
	private int isStandard;
	/**
	 * 是否支持时移：0：不支持；1:支持；
	 */
	@Column("IS_TVANYTIME")
	@IdentAnnocation("isTVAnyTime")
	private int isTvanytime;
	/**
	 * 语言代码。中文简体(默认)：zh-CN，英文：en，其他参见语言代码表
	 */
	@Column("LANGUAGE_CODE")
	private String languageCode;
	/**
	 * 地域编码
	 */
	@Column("REGION_CODE")
	private String regionCode;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;

	private static final long serialVersionUID = 1L;

	public int getChlId()
	{
		return chlId;
	}

	public void setChlId(int chlId)
	{
		this.chlId = chlId;
	}

	public String getChlName()
	{
		return chlName;
	}

	public void setChlName(String chlName)
	{
		this.chlName = chlName;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public int getChlState()
	{
		return chlState;
	}

	public void setChlState(int chlState)
	{
		this.chlState = chlState;
	}

	public int getChlType()
	{
		return chlType;
	}

	public void setChlType(int chlType)
	{
		this.chlType = chlType;
	}

	public String getChlDesc()
	{
		return chlDesc;
	}

	public void setChlDesc(String chlDesc)
	{
		this.chlDesc = chlDesc;
	}

	public String getChlIdent()
	{
		return chlIdent;
	}

	public void setChlIdent(String chlIdent)
	{
		this.chlIdent = chlIdent;
	}

	public int getChlNum()
	{
		return chlNum;
	}

	public void setChlNum(int chlNum)
	{
		this.chlNum = chlNum;
	}

	public String getChlLogo()
	{
		return chlLogo;
	}

	public void setChlLogo(String chlLogo)
	{
		this.chlLogo = chlLogo;
	}

	public int getIsStartover()
	{
		return isStartover;
	}

	public void setIsStartover(int isStartover)
	{
		this.isStartover = isStartover;
	}

	public int getIsNpvr()
	{
		return isNpvr;
	}

	public void setIsNpvr(int isNpvr)
	{
		this.isNpvr = isNpvr;
	}

	public int getIsStandard()
	{
		return isStandard;
	}

	public void setIsStandard(int isStandard)
	{
		this.isStandard = isStandard;
	}

	public int getIsTvanytime()
	{
		return isTvanytime;
	}

	public void setIsTvanytime(int isTvanytime)
	{
		this.isTvanytime = isTvanytime;
	}

	public String getLanguageCode()
	{
		return languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	public String getRegionCode()
	{
		return regionCode;
	}

	public void setRegionCode(String regionCode)
	{
		this.regionCode = regionCode;
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
