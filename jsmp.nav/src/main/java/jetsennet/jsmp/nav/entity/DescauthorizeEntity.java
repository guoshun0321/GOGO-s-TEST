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

}
