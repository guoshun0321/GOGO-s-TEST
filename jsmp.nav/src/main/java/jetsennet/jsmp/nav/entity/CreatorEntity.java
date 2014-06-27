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
	// 导演/男主角/女主角/男配角/女配角
	public static final String MODE_DIRECTOR = "42";
	public static final String MODE_HERO = "43";
	public static final String MODE_HEROINE = "44";
	public static final String MODE_MALE_SUPPORT = "45";
	public static final String MODE_FEMALE_SUPPORT = "46";

	// 出品人/制作人/编剧
	public static final String MODE_PRESENTER = "47";
	public static final String MODE_PRODUCER = "48";
	public static final String MODE_WRITER = "49";

	// 配音演员/摄像/剪辑/监制/录音/化妆
	public static final String MODE_DUBBER = "50";
	public static final String MODE_CAMERA = "51";
	public static final String MODE_CUTTING = "52";
	public static final String MODE_SUPERVISED = "53";
	public static final String MODE_AUDIO = "54";
	public static final String MODE_MAKEUP = "55";

	// 美术指导/武术指导/配乐/片头曲/片尾曲/插曲
	public static final String MODE_ART = "56";
	public static final String MODE_ACTION = "57";
	public static final String MODE_DUB = "58";
	public static final String MODE_OP = "59";
	public static final String MODE_ED = "60";
	public static final String MODE_StringERLUDE = "61";

	// 服装/视觉特效/其他幕后人员
	public static final String MODE_DRESS = "62";
	public static final String MODE_VFX = "63";
	public static final String MODE_BEHIND = "64";

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
