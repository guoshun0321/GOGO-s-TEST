package jetsennet.jsmp.nav.syn;

import java.util.List;

import jetsennet.jsmp.nav.syn.cache.DataSyn4Cache;
import jetsennet.jsmp.nav.syn.cache.IDataSynCache;
import jetsennet.jsmp.nav.syn.db.DataSynDb;
import jetsennet.jsmp.nav.syn.db.DataSynDbResult;

public class DataHandleUtil
{

	public static final void handleData(DataSynEntity entity, String xml)
	{
		List<DataSynContentEntity> contents = entity.getContents();
		for (DataSynContentEntity content : contents)
		{
			Object obj = content.getObj();
			int opFlag = content.getOpFlag();

			// 更新数据库
			// 如果是删除操作，那么会从数据库中取出改数据后，再做删除操作
			DataSynDbResult dbResult = null;
			if (opFlag == DataSynContentEntity.OP_FLAG_MOD)
			{
				dbResult = DataSynDb.insertOrUpdate(obj);
			}
			else if (opFlag == DataSynContentEntity.OP_FLAG_DEL)
			{
				dbResult = DataSynDb.delete(obj);
			}
			else
			{
				throw new DataSynException("暂时不支持操作：" + opFlag);
			}

			// 更新缓存
			IDataSynCache cache = DataSyn4Cache.getHandle(obj);
			if (cache != null)
			{
				if (dbResult != null)
				{
					updateCache(dbResult.obj, dbResult.type, dbResult.num);
				}
			}
			else
			{
				throw new DataSynException("找不到XML对于的Cache处理器：" + obj.getClass());
			}
		}
	}

	public static void updateCache(Object obj, int type, int num)
	{
		// 更新缓存
		IDataSynCache cache = DataSyn4Cache.getHandle(obj);
		if (cache != null)
		{
			if (num > 0)
			{
				if (type == DataSynDbResult.TYPE_INSERT)
				{
					cache.insert(obj);
				}
				else if (type == DataSynDbResult.TYPE_UPDATE)
				{
					cache.update(obj);
				}
				else if (type == DataSynDbResult.TYPE_DELETE)
				{
					cache.delete(obj);
				}
				else
				{
					throw new DataSynException("无效操作类型：" + type);
				}
			}
		}
	}

}
