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
@Table("NS_COLUMN")
public class ColumnEntity implements Serializable
{
	/**
	 * 栏目ID
	 */
	@Id
	@Column("COLUMN_ID")
	private int columnId;
	/**
	 * 媒资唯一ID
	 */
	@Column("ASSET_ID")
	@IdentAnnocation("assetId")
	private String assetId;
	/**
	 * 栏目名称
	 */
	@Column("COLUMN_NAME")
	@IdentAnnocation("displayName")
	private String columnName;
	/**
	 * 上级栏目
	 */
	@Column("PARENT_ID")
	private int parentId;
	/**
	 * 父栏目的媒资唯一ID
	 */
	@Column("PARENT_ASSETID")
	@IdentAnnocation("parentAssetId")
	private String parentAssetid;
	/**
	 * 栏目标识
	 */
	@Column("COLUMN_CODE")
	private String columnCode;
	/**
	 * 栏目状态
	 */
	@Column("COLUMN_STATE")
	private int columnState;
	/**
	 * 栏目描述
	 */
	@Column("COLUMN_DESC")
	@IdentAnnocation("infoText")
	private String columnDesc;
	/**
	 * 栏目序号。-1：置顶；其他：按序号排序；NULL：不参与排序。
	 */
	@Column("COLUMN_SEQ")
	private int columnSeq;
	/**
	 * 栏目路径
	 */
	@Column("COLUMN_PATH")
	private String columnPath;
	/**
	 * 栏目类型。1：普通栏目；2：最新栏目；3：推荐栏目；4：外部栏目；5：排行栏目；10：频道栏目。
	 */
	@Column("COLUMN_TYPE")
	@IdentAnnocation("folderType")
	private int columnType;
	/**
	 * 更新时间
	 */
	@Column("UPDATE_TIME")
	@IdentAnnocation("modifyDate")
	private long updateTime;
	/**
	 * 排序规则，如：COLUMN_SEQ，根据序号排序
	 */
	@Column("SORT_RULE")
	@IdentAnnocation("childFolderSortby")
	private String sortRule;
	/**
	 * 0：逆序；1：正序；
	 */
	@Column("SORT_DIRECTION")
	@IdentAnnocation("childFolderSortDirection")
	private int sortDirection;
	/**
	 * 地域编码
	 */
	@Column("REGION_CODE")
	private String regionCode;
	/**
	 * 语言代码。中文简体(默认)：zh-CN，英文：en，其他参见语言代码表
	 */
	@Column("LANGUAGE_CODE")
	private String languageCode;

	private static final long serialVersionUID = 1L;

	public int getColumnId()
	{
		return columnId;
	}

	public void setColumnId(int columnId)
	{
		this.columnId = columnId;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public int getParentId()
	{
		return parentId;
	}

	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}

	public String getParentAssetid()
	{
		return parentAssetid;
	}

	public void setParentAssetid(String parentAssetid)
	{
		this.parentAssetid = parentAssetid;
	}

	public String getColumnCode()
	{
		return columnCode;
	}

	public void setColumnCode(String columnCode)
	{
		this.columnCode = columnCode;
	}

	public int getColumnState()
	{
		return columnState;
	}

	public void setColumnState(int columnState)
	{
		this.columnState = columnState;
	}

	public String getColumnDesc()
	{
		return columnDesc;
	}

	public void setColumnDesc(String columnDesc)
	{
		this.columnDesc = columnDesc;
	}

	public int getColumnSeq()
	{
		return columnSeq;
	}

	public void setColumnSeq(int columnSeq)
	{
		this.columnSeq = columnSeq;
	}

	public String getColumnPath()
	{
		return columnPath;
	}

	public void setColumnPath(String columnPath)
	{
		this.columnPath = columnPath;
	}

	public int getColumnType()
	{
		return columnType;
	}

	public void setColumnType(int columnType)
	{
		this.columnType = columnType;
	}

	public long getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(long updateTime)
	{
		this.updateTime = updateTime;
	}

	public String getSortRule()
	{
		return sortRule;
	}

	public void setSortRule(String sortRule)
	{
		this.sortRule = sortRule;
	}

	public int getSortDirection()
	{
		return sortDirection;
	}

	public void setSortDirection(int sortDirection)
	{
		this.sortDirection = sortDirection;
	}

	public String getRegionCode()
	{
		return regionCode;
	}

	public void setRegionCode(String regionCode)
	{
		this.regionCode = regionCode;
	}

	public String getLanguageCode()
	{
		return languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

}
