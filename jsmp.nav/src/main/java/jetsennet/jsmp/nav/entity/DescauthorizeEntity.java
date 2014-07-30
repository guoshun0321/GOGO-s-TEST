package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_DESCAUTHORIZE")
public class DescauthorizeEntity implements Serializable
{
	/**
	 * 主键
	 */
	@Id
	@Column("ID")
	private String id;
	/**
	 * 所属节目ID
	 */
	@Column("PGM_ID")
	private int pgmId;
	/**
	 * 授权使用者
	 */
	@Column("AUTHOR_USER")
	private String authorUser;
	/**
	 * 授权使用方式
	 */
	@Column("AUTHOR_USAGE")
	private String authorUsage;
	/**
	 * 授权开始日期
	 */
	@Column("START_DATE")
	private String startDate;
	/**
	 * 授权使用期限
	 */
	@Column("AUTHOR_DEADLINE")
	private String authorDeadline;
	/**
	 * 授权使用地域
	 */
	@Column("AUTHOR_GEOGRAAREA")
	private String authorGeograarea;
	/**
	 * 授权使用次数
	 */
	@Column("TIMES_USAGE")
	private int timesUsage;
	/**
	 * 其他信息
	 */
	@Column("DESCRIPTION")
	private String description;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	private long updateTime;
	/**
	 * 
	 */
	@Column("AUTHOR_NAME")
	private String authorName;
	/**
	 * 
	 */
	@Column("AUTHOR_TYPE")
	private String authorType;
	/**
	 * 
	 */
	@Column("AUTHOR_CONTENTTYPE")
	private String authorContenttype;
	/**
	 * 
	 */
	@Column("AUTHOR_NOTE")
	private String authorNote;
	/**
	 * 
	 */
	@Column("DOC_NUMBER")
	private String docNumber;
	/**
	 * 
	 */
	@Column("AUTHOR_UNIT")
	private String authorUnit;
	/**
	 * 
	 */
	@Column("IMPORT_DOCNUM")
	private String importDocnum;
	/**
	 * 
	 */
	@Column("SERIAL_NUMBER")
	private String serialNumber;
	/**
	 * 
	 */
	@Column("BOOK_NUMBER")
	private String bookNumber;
	/**
	 * 
	 */
	@Column("LICENSE_NUMBER")
	private String licenseNumber;
	/**
	 * 
	 */
	@Column("PUBLISH_DATE")
	private String publishDate;
	/**
	 * 
	 */
	@Column("TURN_CPUSER")
	private String turnCpuser;
	/**
	 * 
	 */
	@Column("REAUTHORIZATION")
	private String reauthorization;
	/**
	 * 
	 */
	@Column("ARTICLE_NUMBER")
	private String articleNumber;
	/**
	 * 
	 */
	@Column("TYPE_CLASS")
	private String typeClass;
	/**
	 * 
	 */
	@Column("FIRST_CATEGORY")
	private String firstCategory;

	private static final long serialVersionUID = 1L;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
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

	public String getAuthorUser()
	{
		return authorUser;
	}

	public void setAuthorUser(String authorUser)
	{
		this.authorUser = authorUser;
	}

	public String getAuthorUsage()
	{
		return authorUsage;
	}

	public void setAuthorUsage(String authorUsage)
	{
		this.authorUsage = authorUsage;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getAuthorDeadline()
	{
		return authorDeadline;
	}

	public void setAuthorDeadline(String authorDeadline)
	{
		this.authorDeadline = authorDeadline;
	}

	public String getAuthorGeograarea()
	{
		return authorGeograarea;
	}

	public void setAuthorGeograarea(String authorGeograarea)
	{
		this.authorGeograarea = authorGeograarea;
	}

	public int getTimesUsage()
	{
		return timesUsage;
	}

	public void setTimesUsage(int timesUsage)
	{
		this.timesUsage = timesUsage;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getAuthorName()
	{
		return authorName;
	}

	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}

	public String getAuthorType()
	{
		return authorType;
	}

	public void setAuthorType(String authorType)
	{
		this.authorType = authorType;
	}

	public String getAuthorContenttype()
	{
		return authorContenttype;
	}

	public void setAuthorContenttype(String authorContenttype)
	{
		this.authorContenttype = authorContenttype;
	}

	public String getAuthorNote()
	{
		return authorNote;
	}

	public void setAuthorNote(String authorNote)
	{
		this.authorNote = authorNote;
	}

	public String getDocNumber()
	{
		return docNumber;
	}

	public void setDocNumber(String docNumber)
	{
		this.docNumber = docNumber;
	}

	public String getAuthorUnit()
	{
		return authorUnit;
	}

	public void setAuthorUnit(String authorUnit)
	{
		this.authorUnit = authorUnit;
	}

	public String getImportDocnum()
	{
		return importDocnum;
	}

	public void setImportDocnum(String importDocnum)
	{
		this.importDocnum = importDocnum;
	}

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public String getBookNumber()
	{
		return bookNumber;
	}

	public void setBookNumber(String bookNumber)
	{
		this.bookNumber = bookNumber;
	}

	public String getLicenseNumber()
	{
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber)
	{
		this.licenseNumber = licenseNumber;
	}

	public String getPublishDate()
	{
		return publishDate;
	}

	public void setPublishDate(String publishDate)
	{
		this.publishDate = publishDate;
	}

	public String getTurnCpuser()
	{
		return turnCpuser;
	}

	public void setTurnCpuser(String turnCpuser)
	{
		this.turnCpuser = turnCpuser;
	}

	public String getReauthorization()
	{
		return reauthorization;
	}

	public void setReauthorization(String reauthorization)
	{
		this.reauthorization = reauthorization;
	}

	public String getArticleNumber()
	{
		return articleNumber;
	}

	public void setArticleNumber(String articleNumber)
	{
		this.articleNumber = articleNumber;
	}

	public String getTypeClass()
	{
		return typeClass;
	}

	public void setTypeClass(String typeClass)
	{
		this.typeClass = typeClass;
	}

	public String getFirstCategory()
	{
		return firstCategory;
	}

	public void setFirstCategory(String firstCategory)
	{
		this.firstCategory = firstCategory;
	}

}
