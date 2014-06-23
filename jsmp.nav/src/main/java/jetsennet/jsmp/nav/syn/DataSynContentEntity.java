package jetsennet.jsmp.nav.syn;

import java.lang.reflect.Field;
import java.sql.Date;

import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.util.SafeDateFormater;

public class DataSynContentEntity
{

	private int opFlag;

	private Object obj;
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

	/**
	 * 仅用于测试
	 * 
	 * @param sb
	 * @return
	 */
	public StringBuilder toXml(StringBuilder sb)
	{
		sb = sb == null ? new StringBuilder() : sb;
		TableInfo table = DataSourceManager.MEDIA_FACTORY.getTableInfo(obj.getClass());
		sb.append("<").append(table.getTableName()).append("Table").append(" opFlag='").append(this.opFlag).append("'").append(">");
		sb.append("<").append(table.getTableName()).append(">");
		for (FieldInfo f : table.getFieldInfos())
		{

			String value = null;
			if (f != null)
			{
				Class<?> type = f.getClass();
				Object temp = f.get(this.obj);
				
				value = temp.toString();
				if (type == Date.class)
				{
					value = SafeDateFormater.format((Date) temp);
				}
			}
			sb.append("<").append(f.getName()).append(">").append(value);
			sb.append("</").append(f.getName()).append(">");
		}
		sb.append("</").append(table.getTableName()).append(">");
		sb.append("</").append(table.getTableName()).append("Table").append(">");
		return sb;
	}

	public int getOpFlag()
	{
		return opFlag;
	}

	public void setOpFlag(int opFlag)
	{
		this.opFlag = opFlag;
	}

	public Object getObj()
	{
		return obj;
	}

	public void setObj(Object obj)
	{
		this.obj = obj;
	}

}
