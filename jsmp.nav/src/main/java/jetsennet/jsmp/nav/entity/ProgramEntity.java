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
@Table("NS_PROGRAM")
public class ProgramEntity implements Serializable
{
	/**
	 * 节目ID
	 */
	@Id
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 媒资唯一ID，频道类型节目与NS_CHANNEL表的ASSET_ID一致。
	 */
	@Column("ASSET_ID")
	@IdentAnnocation("assetld")
	private String assetId;
	/**
	 * 节目名称
	 */
	@Column("PGM_NAME")
	@IdentAnnocation("titleFull")
	private String pgmName;
	/**
	 * 节目类型。0：正片；10：系列/集合；20：预告片；30：片花；40：花絮；100：频道；
	 */
	@Column("PGM_TYPE")
	private int pgmType;
	/**
	 * 内容类型。9：电影；10：电视剧；11：综艺；12：综合；13：纪录片；14：音乐；15：文章；16：频道；
	 */
	@Column("CONTENT_TYPE")
	private int contentType;
	/**
	 * 节目状态。
	 */
	@Column("PGM_STATE")
	private int pgmState;
	/**
	 * 实体类型。1：视频；10：音频；20：组图；30：文章；100：频道；
	 */
	@Column("OBJ_TYPE")
	private int objType;
	/**
	 * 节目时长，格式为：00:00:00
	 */
	@Column("DURATION")
	@IdentAnnocation("runtime")
	private String pgmDuration;
	/**
	 * 搜索字符，一般为名称拼音首字母缩写。
	 */
	@Column("SEARCH_LETTER")
	@IdentAnnocation("initialLetter")
	private String searchLetter;
	/**
	 * 关键词，多个以逗号相连
	 */
	@Column("KEY_WORDS")
	private String keyWords;
	/**
	 * 节目内容描述
	 */
	@Column("CONTENT_INFO")
	@IdentAnnocation("summarMedium")
	private String contentInfo;
	/**
	 * ASSET_TYPE=系列：总集数；ASSET_TYPE=单集：分集数。默认为1。
	 */
	@Column("EPISODES_NUMBER")
	@IdentAnnocation("chapter")
	private String episodesNumber;
	/**
	 * 有效期开始时间，毫秒数。
	 */
	@Column("LICENSING_START")
	@IdentAnnocation(value = "startDateTime", type = "date")
	private long licensingStart;
	/**
	 * 有效期结束时间，毫秒数。
	 */
	@Column("LICENSING_END")
	@IdentAnnocation(value = "endDateTime", type = "date")
	private long licensingEnd;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 看点
	 */
	@Column("WATCH_FOCUS")
	private String watchFocus;
	/**
	 * 分类
	 */
	@Column("CATEGORY")
	private String category;
	/**
	 * 栏目下排序号
	 */
	@Column("SEQ_NO")
	@IdentAnnocation("orderNumber")
	private int seqNo;
	/**
	 * 地域编码
	 */
	@Column("REGION_CODE")
	private String regionCode;
	/**
	 * 点击数
	 */
	@Column("CLICK_NUM")
	@IdentAnnocation("favorRating")
	private int clickNum;
	/**
	 * 内容提供商ID
	 */
	@Column("CP_ID")
	private int cpId;
	/**
	 * 内容提供商标识
	 */
	@Column("CP_CODE")
	private String cpCode;
	/**
	 * 内容提供商名称
	 */
	@Column("CP_NAME")
	private String cpName;
	/**
	 * 关联栏目ID，关联NS_COLUMN表
	 */
	@Column("COLUMN_ID")
	private int columnId;
	/**
	 * 关联栏目的媒资唯一ID，关联NS_COLUMN表
	 */
	@Column("COLUMN_ASSETID")
	@IdentAnnocation("folderAssetId")
	private String columnAssetid;

	@Column("PARENT_ID")
	private int parentId;

	@Column("PARENT_ASSET_ID")
	private String parentAssetId;

	public static final int CONTENT_TYPE_MOVIE = 9;
	public static final int CONTENT_TYPE_TV = 10;
	public static final int CONTENT_TYPE_VARITY = 11;
	public static final int CONTENT_TYPE_COM = 12;
	public static final int CONTENT_TYPE_DOC = 13;
	public static final int CONTENT_TYPE_MUSIC = 14;
	public static final int CONTENT_TYPE_TXT = 15;
	public static final int CONTENT_TYPE_CHL = 16;

	private static final long serialVersionUID = 1L;

	public int getPgmId()
	{
		return pgmId;
	}

	public void setPgmId(int pgmId)
	{
		this.pgmId = pgmId;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public String getPgmName()
	{
		return pgmName;
	}

	public void setPgmName(String pgmName)
	{
		this.pgmName = pgmName;
	}

	public int getPgmType()
	{
		return pgmType;
	}

	public void setPgmType(int pgmType)
	{
		this.pgmType = pgmType;
	}

	public int getContentType()
	{
		return contentType;
	}

	public void setContentType(int contentType)
	{
		this.contentType = contentType;
	}

	public int getPgmState()
	{
		return pgmState;
	}

	public void setPgmState(int pgmState)
	{
		this.pgmState = pgmState;
	}

	public int getObjType()
	{
		return objType;
	}

	public void setObjType(int objType)
	{
		this.objType = objType;
	}

	public String getSearchLetter()
	{
		return searchLetter;
	}

	public void setSearchLetter(String searchLetter)
	{
		this.searchLetter = searchLetter;
	}

	public String getKeyWords()
	{
		return keyWords;
	}

	public void setKeyWords(String keyWords)
	{
		this.keyWords = keyWords;
	}

	public String getContentInfo()
	{
		return contentInfo;
	}

	public void setContentInfo(String contentInfo)
	{
		this.contentInfo = contentInfo;
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

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public int getSeqNo()
	{
		return seqNo;
	}

	public void setSeqNo(int seqNo)
	{
		this.seqNo = seqNo;
	}

	public String getRegionCode()
	{
		return regionCode;
	}

	public void setRegionCode(String regionCode)
	{
		this.regionCode = regionCode;
	}

	public int getClickNum()
	{
		return clickNum;
	}

	public void setClickNum(int clickNum)
	{
		this.clickNum = clickNum;
	}

	public int getCpId()
	{
		return cpId;
	}

	public void setCpId(int cpId)
	{
		this.cpId = cpId;
	}

	public String getCpCode()
	{
		return cpCode;
	}

	public void setCpCode(String cpCode)
	{
		this.cpCode = cpCode;
	}

	public String getCpName()
	{
		return cpName;
	}

	public void setCpName(String cpName)
	{
		this.cpName = cpName;
	}

	public int getColumnId()
	{
		return columnId;
	}

	public void setColumnId(int columnId)
	{
		this.columnId = columnId;
	}

	public String getColumnAssetid()
	{
		return columnAssetid;
	}

	public void setColumnAssetid(String columnAssetid)
	{
		this.columnAssetid = columnAssetid;
	}

	public String getPgmDuration()
	{
		return pgmDuration;
	}

	public void setPgmDuration(String pgmDuration)
	{
		this.pgmDuration = pgmDuration;
	}

	public String getEpisodesNumber()
	{
		return episodesNumber;
	}

	public void setEpisodesNumber(String episodesNumber)
	{
		this.episodesNumber = episodesNumber;
	}

	public String getWatchFocus()
	{
		return watchFocus;
	}

	public void setWatchFocus(String watchFocus)
	{
		this.watchFocus = watchFocus;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public int getParentId()
	{
		return parentId;
	}

	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}

	public String getParentAssetId()
	{
		return parentAssetId;
	}

	public void setParentAssetId(String parentAssetId)
	{
		this.parentAssetId = parentAssetId;
	}

}
