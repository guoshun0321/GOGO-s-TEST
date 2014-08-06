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
@Table("NS_PICTURE")
public class PictureEntity implements Serializable
{
	/**
	 * 图片ID
	 */
	@Id
	@Column("PIC_ID")
	private String picId;
	/**
	 * 图片名称
	 */
	@Column("PIC_NAME")
	private String picName;
	/**
	 * 图片类型。1：缩略图，2：剧照，3：图标，4：标题图，5：广告图，6：大海报，7：小海报，8：背景图，9：其他
	 */
	@Column("PIC_TYPE")
	private int picType;
	/**
	 * 所属对象类型。1：站点；2：栏目；3：频道
	 */
	@Column("OBJ_TYPE")
	private int objType;
	/**
	 * 所属对象ID。
	 */
	@Column("OBJ_ID")
	private String objId;
	/**
	 * 所属对象的assetId
	 */
	@Column("OBJ_ASSETID")
	private String objAssetId;
	/**
	 * 宽
	 */
	@Column("PIC_WIDTH")
	private int picWidth;
	/**
	 * 高
	 */
	@Column("PIC_HEIGHT")
	private int picHeight;
	/**
	 * 文件大小，单位k
	 */
	@Column("FILE_SIZE")
	private int fileSize;
	/**
	 * 文件MD5值
	 */
	@Column("FILE_MD5")
	private String fileMd5;
	/**
	 * 文件名称
	 */
	@Column("FILE_NAME")
	private String fileName;
	/**
	 * 文件格式
	 */
	@Column("FILE_FORMAT")
	private String fileFormat;
	/**
	 * 文件物理路径
	 */
	@Column("FILE_PATH")
	private String filePath;
	/**
	 * 图片访问地址
	 */
	@IdentAnnocation("posterUrl")
	@Column("FILE_URL")
	private String fileUrl;
	/**
	 * 图片状态。
	 */
	@Column("PIC_STATUS")
	private int picStatus;
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
	 * 图片的媒资唯一ID
	 */
	@Column("ASSET_ID")
	private String assetId;
	/**
	 * 图片排序。默认为1。
	 */
	@IdentAnnocation("rank")
	@Column("PIC_RANK")
	private int picRank;

	private static final long serialVersionUID = 1L;

	public String getPicId()
	{
		return picId;
	}

	public void setPicId(String picId)
	{
		this.picId = picId;
	}

	public String getPicName()
	{
		return picName;
	}

	public void setPicName(String picName)
	{
		this.picName = picName;
	}

	public int getPicType()
	{
		return picType;
	}

	public void setPicType(int picType)
	{
		this.picType = picType;
	}

	public int getObjType()
	{
		return objType;
	}

	public void setObjType(int objType)
	{
		this.objType = objType;
	}

	public String getObjId()
	{
		return objId;
	}

	public void setObjId(String objId)
	{
		this.objId = objId;
	}

	public int getPicWidth()
	{
		return picWidth;
	}

	public void setPicWidth(int picWidth)
	{
		this.picWidth = picWidth;
	}

	public int getPicHeight()
	{
		return picHeight;
	}

	public void setPicHeight(int picHeight)
	{
		this.picHeight = picHeight;
	}

	public int getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(int fileSize)
	{
		this.fileSize = fileSize;
	}

	public String getFileMd5()
	{
		return fileMd5;
	}

	public void setFileMd5(String fileMd5)
	{
		this.fileMd5 = fileMd5;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileFormat()
	{
		return fileFormat;
	}

	public void setFileFormat(String fileFormat)
	{
		this.fileFormat = fileFormat;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public String getFileUrl()
	{
		return fileUrl;
	}

	public void setFileUrl(String fileUrl)
	{
		this.fileUrl = fileUrl;
	}

	public int getPicStatus()
	{
		return picStatus;
	}

	public void setPicStatus(int picStatus)
	{
		this.picStatus = picStatus;
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

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public int getPicRank()
	{
		return picRank;
	}

	public void setPicRank(int picRank)
	{
		this.picRank = picRank;
	}

	public String getObjAssetId()
	{
		return objAssetId;
	}

	public void setObjAssetId(String objAssetId)
	{
		this.objAssetId = objAssetId;
	}

}
