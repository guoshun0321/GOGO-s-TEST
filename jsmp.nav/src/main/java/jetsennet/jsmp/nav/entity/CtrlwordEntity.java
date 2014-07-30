package jetsennet.jsmp.nav.entity;

import java.io.Serializable;
import java.util.Date;

import jetsennet.orm.annotation.Column;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.annotation.Id;
/**
 * 
 */
@Table("NS_CTRLWORD")
public class CtrlwordEntity implements Serializable
{
	/**
	 * 受控词ID
	 */
	@Id
	@Column("CW_ID")
	private int cwId;
	/**
	 * 受控词名称
	 */
	@Column("CW_NAME")
	private String cwName;
	/**
	 * 受控词类型
1图片
2内容类型
3接口协议
5排序规则
	 */
	@Column("CW_TYPE")
	private int cwType;
	/**
	 * 受控词标识
	 */
	@Column("CW_CODE")
	private String cwCode;
	/**
	 * 受控词描述
	 */
	@Column("CW_DESC")
	private String cwDesc;
	/**
	 * 可操作类型
	 */
	@Column("OPER_TYPE")
	private String operType;

	private static final long serialVersionUID = 1L;

	public int getCwId()
	{
		return cwId;
	}

	public void setCwId(int cwId)
	{
		this.cwId = cwId;
	}

	public String getCwName()
	{
		return cwName;
	}

	public void setCwName(String cwName)
	{
		this.cwName = cwName;
	}

	public int getCwType()
	{
		return cwType;
	}

	public void setCwType(int cwType)
	{
		this.cwType = cwType;
	}

	public String getCwCode()
	{
		return cwCode;
	}

	public void setCwCode(String cwCode)
	{
		this.cwCode = cwCode;
	}

	public String getCwDesc()
	{
		return cwDesc;
	}

	public void setCwDesc(String cwDesc)
	{
		this.cwDesc = cwDesc;
	}

	public String getOperType()
	{
		return operType;
	}

	public void setOperType(String operType)
	{
		this.operType = operType;
	}

}
