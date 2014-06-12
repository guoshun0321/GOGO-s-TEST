package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 本文件由jetsennet.jsmp.nav.util.BDBFileParse生成
 */
@Table("NS_CREATOR")
public class CreatorEntity implements Serializable
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
	 * 名称
	 */
	@Column("NAME")
	private String name;
	/**
	 * 并列名
	 */
	@Column("PARALLEL_NAME")
	private String parallelName;
	/**
	 * 责任方式
	 */
	@Column("ROLE_MODE")
	private String roleMode;
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getParallelName()
	{
		return parallelName;
	}

	public void setParallelName(String parallelName)
	{
		this.parallelName = parallelName;
	}

	public String getRoleMode()
	{
		return roleMode;
	}

	public void setRoleMode(String roleMode)
	{
		this.roleMode = roleMode;
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
