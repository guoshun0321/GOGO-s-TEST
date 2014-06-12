package jetsennet.jsmp.nav.syn.db;

import jetsennet.jsmp.nav.dal.DataSourceManager;
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

	public int insert(Object obj)
	{
		return this.insertOrUpdate(obj);
	}

	public int update(Object obj)
	{
		return this.insertOrUpdate(obj);
	}

	protected int insertOrUpdate(Object obj)
	{
		int retval = -1;
		Long updateTime = this.getUpdateTime(obj);
		Session session = factory.openSession();
		boolean isSelf = session.transBegin();
		try
		{
			if (updateTime == null)
			{
				// 表不存在更新时间
				session.deleteByObj(obj);
				retval = session.insert(obj, false);
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
					retval = session.insert(obj, false);
				}
				else
				{
					// 存在旧数据时，比较更新时间
					Long dbUpdateTime = this.getUpdateTime(temp);
					if (updateTime > dbUpdateTime)
					{
						retval = session.update(obj);
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
	private Long getUpdateTime(Object obj)
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

	public int delete(Object obj)
	{
		Session session = factory.openSession();
		return session.deleteByObj(obj);
	}
}
