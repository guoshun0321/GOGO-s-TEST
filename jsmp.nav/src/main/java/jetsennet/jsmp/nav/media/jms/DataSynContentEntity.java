package jetsennet.jsmp.nav.media.jms;

import java.util.ArrayList;
import java.util.List;

public class DataSynContentEntity
{

	/**
	 * 操作标志
	 */
	private int opFlag;
	/**
	 * 节点名称
	 */
	private String eleName;
	/**
	 * 操作对象
	 */
	private List<Object> objs;
	/**
	 * 修改
	 */
	public static final int OP_FLAG_MOD = 0;
	/**
	 * 删除
	 */
	public static final int OP_FLAG_DEL = 1;
	/**
	 * 全量发布
	 */
	public static final int OP_FLAG_ALL = 2;

	public DataSynContentEntity()
	{
		this.objs = new ArrayList<>();
	}

	public DataSynContentEntity(String eleName, List<Object> objs)
	{
		this.eleName = eleName;
		this.objs = objs;
	}

	public void addObj(Object obj)
	{
		this.objs.add(obj);
	}

	public int getOpFlag()
	{
		return opFlag;
	}

	public void setOpFlag(int opFlag)
	{
		this.opFlag = opFlag;
	}

	public String getEleName()
	{
		return eleName;
	}

	public void setEleName(String eleName)
	{
		this.eleName = eleName;
	}

	public List<Object> getObjs()
	{
		return objs;
	}

}
