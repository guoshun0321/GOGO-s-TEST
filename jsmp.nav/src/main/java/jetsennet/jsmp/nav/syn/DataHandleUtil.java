package jetsennet.jsmp.nav.syn;

import java.util.List;

import jetsennet.jsmp.nav.syn.cache.DataSyn4Cache;
import jetsennet.jsmp.nav.syn.cache.IDataSynCache;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.jsmp.nav.syn.db.DataSynDb;
import jetsennet.jsmp.nav.syn.db.DataSynDbResult;
import jetsennet.orm.executor.Executors;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;

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

	/**
	 * 仅用于测试
	 * 
	 * @param objs
	 */
	public static void batchInsert(List objs)
	{
		DataSynEntity entity = new DataSynEntity();
		SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

		int size = objs.size();
		TableInfo info = factory.getTableInfo(objs.get(0).getClass());
		String[] sqls = new String[size];
		for (int i = 0; i < objs.size(); i++)
		{
			String sql = factory.getTransform().trans(info.obj2Sql(objs.get(i), SqlTypeEnum.INSERT));
			sqls[i] = sql;
		}
		factory.openSession().insert(sqls);

	}

}
