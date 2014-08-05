package jetsennet.jsmp.nav.media.db;

import java.util.List;

import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbHelper
{

	private static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	private static final Logger logger = LoggerFactory.getLogger(DbHelper.class);

	/**
	 * 新建或修改操作
	 * 
	 * @param obj
	 * @return 结果为null时，表示未进行任何操作
	 */
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
				// 表不存在更新时间时
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
				Object temp = checkObj(obj);

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
			throw new UncheckedNavException(ex);
		}
		return retval;
	}

	/**
	 * 删除操作
	 * 
	 * @param obj
	 * @return
	 */
	public static DataSynDbResult delete(Object obj)
	{
		DataSynDbResult retval = null;
		if (obj != null)
		{
			Object temp = checkObj(obj);
			int num = 0;
			if (temp != null)
			{
				num = deleteObj(obj);
				retval = new DataSynDbResult(DataSynDbResult.TYPE_DELETE, temp, num);
			}
		}
		return retval;
	}

	public static Object checkObj(Object obj)
	{
		TableInfo info = factory.getTableInfo(obj.getClass());
		ISql sql = Sql.select("*").from(info.getTableName()).where(info.genFilterFromObject(obj));
		Session session = factory.openSession();
		List<?> objs = session.query(sql, RowsResultSetExtractor.gen(info.getCls(), info));
		return (objs != null && !objs.isEmpty()) ? objs.get(0) : null;
	}

	public static int deleteObj(Object obj)
	{
		Session session = factory.openSession();
		return session.deleteByObj(obj);
	}

	/**
	 * 返回实体的更新时间，如果无更新时间，返回null
	 * 
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

}
