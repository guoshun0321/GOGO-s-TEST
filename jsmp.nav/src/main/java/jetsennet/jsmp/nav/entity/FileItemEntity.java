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
@Table("NS_FILEITEM")
public class FileItemEntity implements Serializable
{
	/**
	 * 文件条目ID
	 */
	@Id
	@Column("FILE_ID")
	private String fileId;
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
	 * 文件类型。小于100为音视频， 0：VIDEO+ADVIO；1：VIDEO；2：ADVIO；10：字幕工程文件；201~299为图片类型，关联受控词的图片类型。
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
	private long fileSize;
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
	 * 文件宽度，如图片宽度。
	 */
	@Column("FILE_WIDTH")
	private int fileWidth;
	/**
	 * 文件高度，如图片高度
	 */
	@Column("FILE_HEIGHT")
	private int fileHeight;
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
	 * 分辨率宽
	 */
	@Column("BROWSE_WIDTH")
	private int browseWidth;
	/**
	 * 分辨率高
	 */
	@Column("BROWSE_HEIGHT")
	private int browseHeight;
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
	 * 播放地址
	 */
	@Column("PLAY_URL")
	private String playUrl;
	/**
	 * 视频流GOP长度，对MPEG系列格式有效
	 */
	@Column("GOP_SIZE")
	private int gopSize;
	/**
	 * 图像显示位深，单位：bit
	 */
	@Column("DEPTH")
	private int depth;
	/**
	 * 视频流比例
	 */
	@Column("SCALE")
	private int scale;
	/**
	 * 视频码率控制模式。CBR或VBR。
	 */
	@Column("VIDEO_BITRATE_MODE")
	private String videoBitrateMode;
	/**
	 * 视频色差格式。主要有4:2:0、4:1:1、4:2:2、4:4:4。
	 */
	@Column("CHROMA_FMT")
	private String chromaFmt;
	/**
	 * 视频扫描方式。主要有interlaced-topFirst、interlaced-bottomFirst、progress
	 */
	@Column("SCAN_TYPE")
	private String scanType;
	/**
	 * 对MXF有效
	 */
	@Column("AFD")
	private int afd;
	/**
	 * 对MPEG2,H264有效
	 */
	@Column("PROFILE")
	private String profile;
	/**
	 * 对MPEG2,H264有效
	 */
	@Column("LEVEL")
	private String level;
	/**
	 * 音频采样率。单位：HZ
	 */
	@Column("FREQ")
	private int freq;
	/**
	 * 音频采样总数
	 */
	@Column("SAMPLES")
	private int samples;
	/**
	 * 音频采样精度。单位：bit
	 */
	@Column("BIE_PER_SAMPLE")
	private int biePerSample;
	/**
	 * 音频码率控制模式。CBR或VBR。
	 */
	@Column("AUDIO_BITRATE_MODE")
	private String audioBitrateMode;
	/**
	 * 是否3D。0：2D；1：3D；2：伪3D；
	 */
	@Column("IF_3D")
	private int if3d;
	/**
	 * 语言代码
	 */
	@Column("LANGUAGE")
	private String language;

	private static final long serialVersionUID = 1L;

	public boolean isPictrue()
	{
		return this.fileType >= 100;
	}

	public String getFileId()
	{
		return fileId;
	}

	public void setFileId(String fileId)
	{
		this.fileId = fileId;
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

	public long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(long fileSize)
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

	public int getFileWidth()
	{
		return fileWidth;
	}

	public void setFileWidth(int fileWidth)
	{
		this.fileWidth = fileWidth;
	}

	public int getFileHeight()
	{
		return fileHeight;
	}

	public void setFileHeight(int fileHeight)
	{
		this.fileHeight = fileHeight;
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

	public int getBrowseWidth()
	{
		return browseWidth;
	}

	public void setBrowseWidth(int browseWidth)
	{
		this.browseWidth = browseWidth;
	}

	public int getBrowseHeight()
	{
		return browseHeight;
	}

	public void setBrowseHeight(int browseHeight)
	{
		this.browseHeight = browseHeight;
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

	public int getGopSize()
	{
		return gopSize;
	}

	public void setGopSize(int gopSize)
	{
		this.gopSize = gopSize;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	public int getScale()
	{
		return scale;
	}

	public void setScale(int scale)
	{
		this.scale = scale;
	}

	public String getVideoBitrateMode()
	{
		return videoBitrateMode;
	}

	public void setVideoBitrateMode(String videoBitrateMode)
	{
		this.videoBitrateMode = videoBitrateMode;
	}

	public String getChromaFmt()
	{
		return chromaFmt;
	}

	public void setChromaFmt(String chromaFmt)
	{
		this.chromaFmt = chromaFmt;
	}

	public String getScanType()
	{
		return scanType;
	}

	public void setScanType(String scanType)
	{
		this.scanType = scanType;
	}

	public int getAfd()
	{
		return afd;
	}

	public void setAfd(int afd)
	{
		this.afd = afd;
	}

	public String getProfile()
	{
		return profile;
	}

	public void setProfile(String profile)
	{
		this.profile = profile;
	}

	public String getLevel()
	{
		return level;
	}

	public void setLevel(String level)
	{
		this.level = level;
	}

	public int getFreq()
	{
		return freq;
	}

	public void setFreq(int freq)
	{
		this.freq = freq;
	}

	public int getSamples()
	{
		return samples;
	}

	public void setSamples(int samples)
	{
		this.samples = samples;
	}

	public int getBiePerSample()
	{
		return biePerSample;
	}

	public void setBiePerSample(int biePerSample)
	{
		this.biePerSample = biePerSample;
	}

	public String getAudioBitrateMode()
	{
		return audioBitrateMode;
	}

	public void setAudioBitrateMode(String audioBitrateMode)
	{
		this.audioBitrateMode = audioBitrateMode;
	}

	public int getIf3d()
	{
		return if3d;
	}

	public void setIf3d(int if3d)
	{
		this.if3d = if3d;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

}
