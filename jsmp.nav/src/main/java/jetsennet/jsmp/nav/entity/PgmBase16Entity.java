package jetsennet.jsmp.nav.entity;

import java.io.Serializable;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Id;
import jetsennet.orm.annotation.Table;

/**
 */
@Table("NS_PGMBASE_16")
public class PgmBase16Entity implements Serializable
{
	/**
	 * ID
	 */
	@Id
	@Column("ID")
	private int id;
	/**
	 * 所属节目ID
	 */
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 所属节目的媒资唯一ID
	 */
	@Column("PGM_ASSETID")
	private String pgmAssetid;
	/**
	 * 英文名称
	 */
	@Column("ENGLISH_NAME")
	private String englishName;
	/**
	 * 别名
	 */
	@Column("ALIAS")
	private String alias;
	/**
	 * 国家地区
	 */
	@Column("COUNTRY")
	private String country;
	/**
	 * 看点
	 */
	@Column("WATCH_FOCUS")
	private String watchFocus;
	/**
	 * 出品公司
	 */
	@Column("PRODUCT_COMPANY")
	private String productCompany;
	/**
	 * 限制类别
	 */
	@Column("RESTRICT_CATEGORY")
	private String restrictCategory;
	/**
	 * 代理公司
	 */
	@Column("AGENCY")
	private String agency;
	/**
	 * 内容详细介绍
	 */
	@Column("DETAIL_INFO")
	private String detailInfo;
	/**
	 * 推荐星级
	 */
	@Column("RECOMMEND_STAR")
	private String recommendStar;
	/**
	 * 标签
	 */
	@Column("LABEL")
	private String label;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 语言
	 */
	@Column("LANGUAGE")
	private String language;
	/**
	 * 包含奖项
	 */
	@Column("AWARDS")
	private String awards;
	/**
	 * 发行年份
	 */
	@Column("ISSUE_YEAR")
	@IdentAnnocation("year")
	private String issueYear;
	/**
	 * 发行公司
	 */
	@Column("ISSUE_COMPANY")
	private String issueCompany;
	/**
	 * 内容优先级
	 */
	@Column("CONTENT_PRIORITY")
	private String contentPriority;
	/**
	 * 上映日期
	 */
	@Column("RELEASE_DATE")
	private String releaseDate;
	/**
	 * 票房
	 */
	@Column("BOX")
	private long box;
	/**
	 * 精彩对白
	 */
	@Column("DIALOGUE")
	private String dialogue;
	/**
	 * 影评链接
	 */
	@Column("CRITICS_LINK")
	private String criticsLink;
	/**
	 * 已播集数
	 */
	@Column("BROADCAST_NUM")
	private int broadcastNum;
	/**
	 * 是否已经出版完毕
	 */
	@Column("IS_FINISHED")
	private int isFinished;
	/**
	 * 来源
	 */
	@Column("SOURCE")
	private String source;
	/**
	 * 首播日期
	 */
	@Column("LAUNCH_DATE")
	private String launchDate;
	/**
	 * 播放周期
	 */
	@Column("PLAY_CYCLE")
	private String playCycle;
	
	static {
		System.out.println("tes 16");
	}

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

	public String getPgmAssetid()
	{
		return pgmAssetid;
	}

	public void setPgmAssetid(String pgmAssetid)
	{
		this.pgmAssetid = pgmAssetid;
	}

	public String getEnglishName()
	{
		return englishName;
	}

	public void setEnglishName(String englishName)
	{
		this.englishName = englishName;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getWatchFocus()
	{
		return watchFocus;
	}

	public void setWatchFocus(String watchFocus)
	{
		this.watchFocus = watchFocus;
	}

	public String getProductCompany()
	{
		return productCompany;
	}

	public void setProductCompany(String productCompany)
	{
		this.productCompany = productCompany;
	}

	public String getRestrictCategory()
	{
		return restrictCategory;
	}

	public void setRestrictCategory(String restrictCategory)
	{
		this.restrictCategory = restrictCategory;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setAgency(String agency)
	{
		this.agency = agency;
	}

	public String getDetailInfo()
	{
		return detailInfo;
	}

	public void setDetailInfo(String detailInfo)
	{
		this.detailInfo = detailInfo;
	}

	public String getRecommendStar()
	{
		return recommendStar;
	}

	public void setRecommendStar(String recommendStar)
	{
		this.recommendStar = recommendStar;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getAwards()
	{
		return awards;
	}

	public void setAwards(String awards)
	{
		this.awards = awards;
	}

	public String getIssueYear()
	{
		return issueYear;
	}

	public void setIssueYear(String issueYear)
	{
		this.issueYear = issueYear;
	}

	public String getIssueCompany()
	{
		return issueCompany;
	}

	public void setIssueCompany(String issueCompany)
	{
		this.issueCompany = issueCompany;
	}

	public String getContentPriority()
	{
		return contentPriority;
	}

	public void setContentPriority(String contentPriority)
	{
		this.contentPriority = contentPriority;
	}

	public String getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	public long getBox()
	{
		return box;
	}

	public void setBox(long box)
	{
		this.box = box;
	}

	public String getDialogue()
	{
		return dialogue;
	}

	public void setDialogue(String dialogue)
	{
		this.dialogue = dialogue;
	}

	public String getCriticsLink()
	{
		return criticsLink;
	}

	public void setCriticsLink(String criticsLink)
	{
		this.criticsLink = criticsLink;
	}

	public int getBroadcastNum()
	{
		return broadcastNum;
	}

	public void setBroadcastNum(int broadcastNum)
	{
		this.broadcastNum = broadcastNum;
	}

	public int getIsFinished()
	{
		return isFinished;
	}

	public void setIsFinished(int isFinished)
	{
		this.isFinished = isFinished;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getLaunchDate()
	{
		return launchDate;
	}

	public void setLaunchDate(String launchDate)
	{
		this.launchDate = launchDate;
	}

	public String getPlayCycle()
	{
		return playCycle;
	}

	public void setPlayCycle(String playCycle)
	{
		this.playCycle = playCycle;
	}

}
