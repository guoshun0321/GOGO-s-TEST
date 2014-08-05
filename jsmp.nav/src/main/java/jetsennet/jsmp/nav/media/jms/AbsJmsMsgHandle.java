package jetsennet.jsmp.nav.media.jms;

import java.util.HashMap;
import java.util.Map;

import jetsennet.jsmp.nav.media.db.DataSynDbResult;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

public abstract class AbsJmsMsgHandle implements IJmsMsgHandle
{

	private static final Map<String, IJmsMsgHandle> handleMap = new HashMap<String, IJmsMsgHandle>();

	static
	{
		registerHandle(new JmsMsgHandleColumn());
		registerHandle(new JmsMsgHandleProgram());
		registerHandle(new JmsMsgHandleChannel());
		registerHandle(new JmsMsgHandlePlaybill());
	}

	private static void registerHandle(IJmsMsgHandle handle)
	{
		Class<?> cls = handle.getClass();
		IdentAnnocation id = cls.getAnnotation(IdentAnnocation.class);
		if (id != null)
		{
			String value = id.value();
			String[] eleNames = value.split(",");
			for (String eleName : eleNames)
			{
				handleMap.put(eleName, handle);
			}
		}
	}

	@Override
	public void handleAll(DataSynContentEntity content) throws Exception
	{
		throw new UncheckedNavException("暂时不支持全量操作。");
	}

	public static void handle(DataSynContentEntity content)
	{
		String eleName = content.getEleName();
		IJmsMsgHandle handle = ensureHandle(eleName);
		int flag = content.getOpFlag();
		try
		{
			switch (flag)
			{
			case DataSynContentEntity.OP_FLAG_MOD:
			{
				handle.handleModify(content);
				break;
			}
			case DataSynContentEntity.OP_FLAG_DEL:
			{
				handle.handleDelete(content);
				break;
			}
			case DataSynContentEntity.OP_FLAG_ALL:
			{
				handle.handleAll(content);
				break;
			}
			default:
			{
				throw new UncheckedNavException("未知JMS操作标识：" + flag);
			}
			}
		}
		catch (Exception ex)
		{
			throw new UncheckedNavException(ex);
		}
	}

	public static IJmsMsgHandle ensureHandle(String name)
	{
		return handleMap.get(name);
	}

	/**
	 * 判断本次数据库操作是否有效
	 * 
	 * @param syn
	 * @return
	 */
	public static boolean isValid(DataSynDbResult dbResult)
	{
		boolean retval = false;
		if (dbResult != null && dbResult.num > 0)
		{
			retval = true;
		}
		return retval;
	}

}
