package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;

/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_PLAYBILLITEM")
public class PlaybillItemEntity implements Serializable
{
	/**
	 * 条目ID
	 */
	@Id
	@Column("PBI_ID")
	@IdentAnnocation("programId")
	private int pbiId;
	/**
	 * 媒资唯一ID
	 */
	@Column("ASSET_ID")
	@IdentAnnocation("assetId")
	private String assetId;
	/**
	 * 节目单ID，关联NS_PLAYBILL表
	 */
	@Column("PB_ID")
	private int pbId;
	/**
	 * 节目名称
	 */
	@Column("PGM_NAME")
	@IdentAnnocation("programName")
	private String pgmName;
	/**
	 * 节目状态：0:可用；1：不可用；
	 */
	@IdentAnnocation("status")
	@Column("PGM_STATE")
	private int pgmState;
	/**
	 * 节目类型。默认0
	 */
	@Column("PGM_TYPE")
	private int pgmType;
	/**
	 * 节目时长，秒为单位
	 */
	@Column("DURATION")
	private int duration;
	/**
	 * 开始时间，毫秒为单位，播出开始绝对时间，从0点开始。
	 */
	@Column("START_TIME")
	private long startTime;
	/**
	 * 节目描述
	 */
	@Column("PGM_DESC")
	private String pgmDesc;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 播放路径
	 */
	@Column("PLAY_URL")
	private String playUrl;

	private static final long serialVersionUID = 1L;

	public int getPbiId()
	{
		return pbiId;
	}

	public void setPbiId(int pbiId)
	{
		this.pbiId = pbiId;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public int getPbId()
	{
		return pbId;
	}

	public void setPbId(int pbId)
	{
		this.pbId = pbId;
	}

	public String getPgmName()
	{
		return pgmName;
	}

	public void setPgmName(String pgmName)
	{
		this.pgmName = pgmName;
	}

	public int getPgmState()
	{
		return pgmState;
	}

	public void setPgmState(int pgmState)
	{
		this.pgmState = pgmState;
	}

	public int getPgmType()
	{
		return pgmType;
	}

	public void setPgmType(int pgmType)
	{
		this.pgmType = pgmType;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public String getPgmDesc()
	{
		return pgmDesc;
	}

	public void setPgmDesc(String pgmDesc)
	{
		this.pgmDesc = pgmDesc;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getPlayUrl()
	{
		return playUrl;
	}

	public void setPlayUrl(String playUrl)
	{
		this.playUrl = playUrl;
	}

}
