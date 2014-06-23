package jetsennet.jsmp.nav.syn.db;

import jetsennet.orm.executor.resultset.ResultSetHandleFactory;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSynDb
{

	private static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	private static final Logger logger = LoggerFactory.getLogger(DataSynDb.class);

	public static DataSynDbResult insert(Object obj)
	{
		return insertOrUpdate(obj);
	}

	public static DataSynDbResult update(Object obj)
	{
		return insertOrUpdate(obj);
	}

	public static DataSynDbResult insertOrUpdate(Object obj)
	{
		DataSynDbResult retval = null;
		Long updateTime = getUpdateTime(obj);
		Session session = factory.openSession();
		boolean isSelf = session.transBegin();
		try
		{
			if (updateTime == null)
			{
				// 表不存在更新时间
				int isUpdate = session.deleteByObj(obj);
				int temp = session.insert(obj, false);
				if (isUpdate > 0)
				{
					retval = new DataSynDbResult(DataSynDbResult.TYPE_UPDATE, obj, temp);
				}
				else
				{
					retval = new DataSynDbResult(DataSynDbResult.TYPE_INSERT, obj, temp);
				}
			}
			else
			{
				// 表存在更新时间
				TableInfo table = factory.getTableInfo(obj.getClass());
				ISql sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(obj));
				Object temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(obj.getClass(), table));
				if (temp == null)
				{
					// 不存在旧数据时，新增数据
					int num = session.insert(obj, false);
					retval = new DataSynDbResult(DataSynDbResult.TYPE_INSERT, obj, num);
				}
				else
				{
					// 存在旧数据时，比较更新时间
					Long dbUpdateTime = getUpdateTime(temp);
					if (updateTime > dbUpdateTime)
					{
						int num = session.update(obj);
						retval = new DataSynDbResult(DataSynDbResult.TYPE_UPDATE, obj, num);
					}
				}
			}
			session.transCommit(isSelf);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			session.transRollback(isSelf);
		}
		return retval;
	}

	/**
	 * 返回实体的更新时间，如果无更新时间，返回null
	 * @param obj
	 * @return
	 */
	private static Long getUpdateTime(Object obj)
	{
		Long retval = null;
		try
		{
			TableInfo info = factory.getTableInfo(obj.getClass());
			FieldInfo field = info.getFieldInfo("UPDATE_TIME");
			if (field != null)
			{
				retval = (Long) field.getField().get(obj);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public static DataSynDbResult delete(Object obj)
	{
		Session session = factory.openSession();
		int num = session.deleteByObj(obj);
		return new DataSynDbResult(DataSynDbResult.TYPE_DELETE, obj, num);
	}
}
