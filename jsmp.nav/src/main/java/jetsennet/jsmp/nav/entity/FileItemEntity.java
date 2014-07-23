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
@Table("NS_FILEITEM")
public class FileItemEntity implements Serializable
{
	/**
	 * 文件条目ID
	 */
	@Id
	@Column("FILE_ID")
	private String id;
	/**
	 * 文件媒资唯一ID
	 */
	@Column("ASSET_ID")
	@IdentAnnocation("fileAssertId")
	private String assetId;
	/**
	 * 所属节目ID，关联NS_PROGRAM表
	 */
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 所属节目的媒资唯一ID，关联NS_PROGRAM表
	 */
	@Column("PGM_ASSETID")
	private String pgmAssetid;
	/**
	 * 文件类型
	 */
	@Column("FILE_TYPE")
	private int fileType;
	/**
	 * 目标路径
	 */
	@Column("DEST_PATH")
	private String destPath;
	/**
	 * 目标文件名称
	 */
	@Column("DEST_FILENAME")
	private String destFilename;
	/**
	 * 文件大小(字节数)
	 */
	@Column("FILE_SIZE")
	private int fileSize;
	/**
	 * 文件验证码
	 */
	@Column("FILE_MD")
	private String fileMd;
	/**
	 * 文件描述
	 */
	@Column("FILE_DESC")
	private String fileDesc;
	/**
	 * 文件时长，单位秒
	 */
	@Column("FILE_DURATION")
	private int fileDuration;
	/**
	 * 是否3D。0：2D；1：3D；2：伪3D；
	 */
	@Column("IF_3D")
	private int if3d;
	/**
	 * 画面质量。1：标清，2：高清，3：超清
	 */
	@Column("VIDEO_QUALITY")
	@IdentAnnocation(value = "format", enumValue = "1:标清,2:高清,3:超清")
	private int videoQuality;
	/**
	 * 画面宽高比
	 */
	@Column("ASPECT_RATIO")
	@IdentAnnocation("screenShape")
	private String aspectRatio;
	/**
	 * 分辨率高
	 */
	@Column("BROWSE_HEIGHT")
	private int browseHeight;
	/**
	 * 分辨率宽
	 */
	@Column("BROWSE_WIDTH")
	private int browseWidth;
	/**
	 * 复合码率
	 */
	@Column("COMPLEX_RATE")
	private int complexRate;
	/**
	 * 帧率
	 */
	@Column("FRAME_RATE")
	private int frameRate;
	/**
	 * 声道数
	 */
	@Column("CHANNEL_NUM")
	private int channelNum;
	/**
	 * 音频编码格式
	 */
	@Column("AUCODING_FORMAT")
	@IdentAnnocation("audioType")
	private String aucodingFormat;
	/**
	 * 视频编码格式
	 */
	@Column("VICODING_FORMAT")
	private String vicodingFormat;
	/**
	 * 音频码率
	 */
	@Column("AUDIO_DATARATE")
	private int audioDatarate;
	/**
	 * 视频码率
	 */
	@Column("VIDEO_BITRATE")
	private int videoBitrate;
	/**
	 * 文件格式
	 */
	@Column("FILE_FORMAT")
	private String fileFormat;
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

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
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

	public int getFileType()
	{
		return fileType;
	}

	public void setFileType(int fileType)
	{
		this.fileType = fileType;
	}

	public String getDestPath()
	{
		return destPath;
	}

	public void setDestPath(String destPath)
	{
		this.destPath = destPath;
	}

	public String getDestFilename()
	{
		return destFilename;
	}

	public void setDestFilename(String destFilename)
	{
		this.destFilename = destFilename;
	}

	public int getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(int fileSize)
	{
		this.fileSize = fileSize;
	}

	public String getFileMd()
	{
		return fileMd;
	}

	public void setFileMd(String fileMd)
	{
		this.fileMd = fileMd;
	}

	public String getFileDesc()
	{
		return fileDesc;
	}

	public void setFileDesc(String fileDesc)
	{
		this.fileDesc = fileDesc;
	}

	public int getFileDuration()
	{
		return fileDuration;
	}

	public void setFileDuration(int fileDuration)
	{
		this.fileDuration = fileDuration;
	}

	public int getIf3d()
	{
		return if3d;
	}

	public void setIf3d(int if3d)
	{
		this.if3d = if3d;
	}

	public int getVideoQuality()
	{
		return videoQuality;
	}

	public void setVideoQuality(int videoQuality)
	{
		this.videoQuality = videoQuality;
	}

	public String getAspectRatio()
	{
		return aspectRatio;
	}

	public void setAspectRatio(String aspectRatio)
	{
		this.aspectRatio = aspectRatio;
	}

	public int getBrowseHeight()
	{
		return browseHeight;
	}

	public void setBrowseHeight(int browseHeight)
	{
		this.browseHeight = browseHeight;
	}

	public int getBrowseWidth()
	{
		return browseWidth;
	}

	public void setBrowseWidth(int browseWidth)
	{
		this.browseWidth = browseWidth;
	}

	public int getComplexRate()
	{
		return complexRate;
	}

	public void setComplexRate(int complexRate)
	{
		this.complexRate = complexRate;
	}

	public int getFrameRate()
	{
		return frameRate;
	}

	public void setFrameRate(int frameRate)
	{
		this.frameRate = frameRate;
	}

	public int getChannelNum()
	{
		return channelNum;
	}

	public void setChannelNum(int channelNum)
	{
		this.channelNum = channelNum;
	}

	public String getAucodingFormat()
	{
		return aucodingFormat;
	}

	public void setAucodingFormat(String aucodingFormat)
	{
		this.aucodingFormat = aucodingFormat;
	}

	public String getVicodingFormat()
	{
		return vicodingFormat;
	}

	public void setVicodingFormat(String vicodingFormat)
	{
		this.vicodingFormat = vicodingFormat;
	}

	public int getAudioDatarate()
	{
		return audioDatarate;
	}

	public void setAudioDatarate(int audioDatarate)
	{
		this.audioDatarate = audioDatarate;
	}

	public int getVideoBitrate()
	{
		return videoBitrate;
	}

	public void setVideoBitrate(int videoBitrate)
	{
		this.videoBitrate = videoBitrate;
	}

	public String getFileFormat()
	{
		return fileFormat;
	}

	public void setFileFormat(String fileFormat)
	{
		this.fileFormat = fileFormat;
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
