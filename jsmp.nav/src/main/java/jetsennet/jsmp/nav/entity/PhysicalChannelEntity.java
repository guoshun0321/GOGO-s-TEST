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
@Table("NS_PHYSICALCHANNEL")
public class PhysicalChannelEntity implements Serializable
{
	/**
	 * 物理频道ID
	 */
	@Id
	@Column("PHYCHL_ID")
	private int phychlId;
	/**
	 * 物理频道名称
	 */
	@Column("PHYCHL_NAME")
	private String phychlName;
	/**
	 * 所属频道ID，关联NS_CHANNEL表
	 */
	@Column("CHL_ID")
	private int chlId;
	/**
	 * 物理频道状态
	 */
	@Column("PHYCHL_STATE")
	private int phychlState;
	/**
	 * 物理频道类型：0：组播； 1：单播；
	 */
	@Column("PHYCHL_TYPE")
	private int phychlType;
	/**
	 * 物理频道标识
	 */
	@Column("PHYCHL_IDENT")
	private String phychlIdent;
	/**
	 * 物理频道号
	 */
	@Column("PHYCHL_NUM")
	private int phychlNum;
	/**
	 * 物理频道描述
	 */
	@Column("PHYCHL_DESC")
	private String phychlDesc;
	/**
	 * serviceId
	 */
	@Column("SERVICE_ID")
	@IdentAnnocation("serviceId")
	private String serviceId;
	/**
	 * tsId
	 */
	@Column("TSID")
	@IdentAnnocation("tsId")
	private String tsid;
	/**
	 * 频率
	 */
	@Column("FREQ")
	@IdentAnnocation("frequency")
	private String freq;
	/**
	 * 调制方式
	 */
	@Column("QAM")
	@IdentAnnocation("qam")
	private String qam;
	/**
	 * 符号率
	 */
	@Column("SYMBOL_RATE")
	@IdentAnnocation("symbolRate")
	private String symbolRate;
	/**
	 * 码率
	 */
	@Column("BIT_RATE")
	@IdentAnnocation("bitRate")
	private String bitRate;
	/**
	 * 组播IP:PORT/单播URL
	 */
	@Column("BROAD_URL")
	private String broadUrl;
	/**
	 * 是否高清频道。0：否；1：是。
	 */
	@Column("IF_HD")
	private int ifHd;
	/**
	 * 通讯协议类型。0：HTTP；
	 */
	@Column("PROTOCOL_TYPE")
	private int protocolType;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 格式
	 */
	@Column("FORMAT")
	private String format;
	/**
	 * 码流类型
	 */
	@Column("STREAM_TYPE")
	private String streamType;
	/**
	 * SDP信息
	 */
	@Column("SDP_INFO")
	private String sdpInfo;
	/**
	 * 用户反问方式
	 */
	@Column("ACCESS_TYPE")
	private String accessType;
	/**
	 * 地区
	 */
	@Column("REGION")
	private String region;

	private static final long serialVersionUID = 1L;

	public int getPhychlId()
	{
		return phychlId;
	}

	public void setPhychlId(int phychlId)
	{
		this.phychlId = phychlId;
	}

	public String getPhychlName()
	{
		return phychlName;
	}

	public void setPhychlName(String phychlName)
	{
		this.phychlName = phychlName;
	}

	public int getChlId()
	{
		return chlId;
	}

	public void setChlId(int chlId)
	{
		this.chlId = chlId;
	}

	public int getPhychlState()
	{
		return phychlState;
	}

	public void setPhychlState(int phychlState)
	{
		this.phychlState = phychlState;
	}

	public int getPhychlType()
	{
		return phychlType;
	}

	public void setPhychlType(int phychlType)
	{
		this.phychlType = phychlType;
	}

	public String getPhychlIdent()
	{
		return phychlIdent;
	}

	public void setPhychlIdent(String phychlIdent)
	{
		this.phychlIdent = phychlIdent;
	}

	public int getPhychlNum()
	{
		return phychlNum;
	}

	public void setPhychlNum(int phychlNum)
	{
		this.phychlNum = phychlNum;
	}

	public String getPhychlDesc()
	{
		return phychlDesc;
	}

	public void setPhychlDesc(String phychlDesc)
	{
		this.phychlDesc = phychlDesc;
	}

	public String getServiceId()
	{
		return serviceId;
	}

	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}

	public String getTsid()
	{
		return tsid;
	}

	public void setTsid(String tsid)
	{
		this.tsid = tsid;
	}

	public String getFreq()
	{
		return freq;
	}

	public void setFreq(String freq)
	{
		this.freq = freq;
	}

	public String getQam()
	{
		return qam;
	}

	public void setQam(String qam)
	{
		this.qam = qam;
	}

	public String getSymbolRate()
	{
		return symbolRate;
	}

	public void setSymbolRate(String symbolRate)
	{
		this.symbolRate = symbolRate;
	}

	public String getBitRate()
	{
		return bitRate;
	}

	public void setBitRate(String bitRate)
	{
		this.bitRate = bitRate;
	}

	public String getBroadUrl()
	{
		return broadUrl;
	}

	public void setBroadUrl(String broadUrl)
	{
		this.broadUrl = broadUrl;
	}

	public int getIfHd()
	{
		return ifHd;
	}

	public void setIfHd(int ifHd)
	{
		this.ifHd = ifHd;
	}

	public int getProtocolType()
	{
		return protocolType;
	}

	public void setProtocolType(int protocolType)
	{
		this.protocolType = protocolType;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getStreamType()
	{
		return streamType;
	}

	public void setStreamType(String streamType)
	{
		this.streamType = streamType;
	}

	public String getSdpInfo()
	{
		return sdpInfo;
	}

	public void setSdpInfo(String sdpInfo)
	{
		this.sdpInfo = sdpInfo;
	}

	public String getAccessType()
	{
		return accessType;
	}

	public void setAccessType(String accessType)
	{
		this.accessType = accessType;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

}
