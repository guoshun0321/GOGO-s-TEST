package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_PLAYBILL")
public class PlaybillEntity implements Serializable
{
	/**
	 * 节目单ID
	 */
	@Id
	@Column("PB_ID")
	private int pbId;
	/**
	 * 媒资唯一ID
	 */
	@Column("ASSET_ID")
	private String assetId;
	/**
	 * 所属频道，关联NS_CHANNEL表
	 */
	@Column("CHL_ID")
	private int chlId;
	/**
	 * 播出单类型：0：缺省，播出单，10：播后单

	 */
	@Column("PB_TYPE")
	private int pbType;
	/**
	 * 播出单状态：10：内容审核中（待审） 50：内容审核通过 70: 被打回 20：注入； 23:取消注入；
	 */
	@Column("PB_STATE")
	private int pbState;
	/**
	 * 播出日期
	 */
	@Column("PLAY_DATE")
	private Date playDate;
	/**
	 * 起始时间：秒为单位，开始播出的绝对时间，从0点开始。
	 */
	@Column("START_TIME")
	private int startTime;
	/**
	 * 节目单时长
	 */
	@Column("PLAY_DURATION")
	private int playDuration;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 最终修改时间
	 */
	@Column("MODIFY_TIME")
	private Date modifyTime;

	private static final long serialVersionUID = 1L;

	public int getPbId()
	{
		return pbId;
	}

	public void setPbId(int pbId)
	{
		this.pbId = pbId;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public int getChlId()
	{
		return chlId;
	}

	public void setChlId(int chlId)
	{
		this.chlId = chlId;
	}

	public int getPbType()
	{
		return pbType;
	}

	public void setPbType(int pbType)
	{
		this.pbType = pbType;
	}

	public int getPbState()
	{
		return pbState;
	}

	public void setPbState(int pbState)
	{
		this.pbState = pbState;
	}

	public Date getPlayDate()
	{
		return playDate;
	}

	public void setPlayDate(Date playDate)
	{
		this.playDate = playDate;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}

	public int getPlayDuration()
	{
		return playDuration;
	}

	public void setPlayDuration(int playDuration)
	{
		this.playDuration = playDuration;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(int updateTime)
	{
		this.updateTime = updateTime;
	}

	public Date getModifyTime()
	{
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime)
	{
		this.modifyTime = modifyTime;
	}

}
