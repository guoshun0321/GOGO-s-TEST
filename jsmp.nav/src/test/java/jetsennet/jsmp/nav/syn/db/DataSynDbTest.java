package jetsennet.jsmp.nav.syn.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.RelateColumnEntity;
import jetsennet.jsmp.nav.syn.DataSynContentEntity;
import jetsennet.jsmp.nav.util.ObjectUtil;
import jetsennet.orm.executor.resultset.ResultSetHandleFactory;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.tableinfo.TableInfo;
import junit.framework.TestCase;

public class DataSynDbTest extends TestCase
{

	private SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
	private Session session = null;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataSynDbTest.class);

	protected void setUp() throws Exception
	{
		try
		{
			session = factory.openSession();
			session.deleteAll("NS_COLUMN");
			session.deleteAll("NS_RELATECOLUMN");
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	protected void tearDown() throws Exception
	{
		factory.closeSession();
	}

	public void testInsertOrUpdate()
	{
		// 带UPDATE_TIME
		ColumnEntity column = new ColumnEntity();
		column.setColumnId(10);
		column.setAssetId("column_assetId");
		column.setColumnName("columnName");
		column.setParentId(1);
		column.setParentAssetid("parent_column_assetId");
		column.setColumnCode("column_code");
		column.setColumnState(1);
		column.setColumnDesc("column_desc");
		column.setColumnSeq(-1);
		column.setColumnPath("column_path");
		column.setColumnType(1);
		column.setUpdateTime(2000);
		column.setSortRule("COLUMN_NAME");
		column.setSortDirection(1);
		column.setRegionCode("zh-CN");
		column.setLanguageCode("zh-CN");

		DataSynDb db = new DataSynDb();

		// insert
		DataSynDbResult ret = db.insertOrUpdate(column);
		assertEquals(ret.type, DataSynDbResult.TYPE_INSERT);
		assertEquals(ret.num, 1);
		TableInfo table = factory.getTableInfo(column.getClass());
		ISql sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(column));
		Object temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(column.getClass(), table));
		assertTrue(ObjectUtil.compare(ColumnEntity.class, column, temp));

		// update
		column.setUpdateTime(3000);
		ret = db.insertOrUpdate(column);
		assertEquals(ret.type, DataSynDbResult.TYPE_UPDATE);
		assertEquals(ret.num, 1);
		table = factory.getTableInfo(column.getClass());
		sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(column));
		temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(column.getClass(), table));
		assertTrue(ObjectUtil.compare(ColumnEntity.class, column, temp));

		// not update
		column.setUpdateTime(1000);
		ret = db.insertOrUpdate(column);
		assertNull(ret);
		table = factory.getTableInfo(column.getClass());
		sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(column));
		temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(column.getClass(), table));
		assertFalse(ObjectUtil.compare(ColumnEntity.class, column, temp));

		// 不带UPDATE_TIME
		RelateColumnEntity rc = new RelateColumnEntity();
		rc.setRelColumnId(1);
		rc.setSrcColumnId(2);
		rc.setRelateRule("rule1");

		// insert
		ret = db.insertOrUpdate(rc);
		assertEquals(ret.type, DataSynDbResult.TYPE_INSERT);
		assertEquals(ret.num, 1);
		table = factory.getTableInfo(RelateColumnEntity.class);
		sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(rc));
		temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(rc.getClass(), table));
		assertTrue(ObjectUtil.compare(RelateColumnEntity.class, rc, temp));

		// update
		rc.setRelateRule("rule2");
		ret = db.insertOrUpdate(rc);
		assertEquals(ret.type, DataSynDbResult.TYPE_UPDATE);
		assertEquals(ret.num, 1);
		table = factory.getTableInfo(RelateColumnEntity.class);
		sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(rc));
		temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(rc.getClass(), table));
		assertTrue(ObjectUtil.compare(RelateColumnEntity.class, rc, temp));
	}

	public void testDelete()
	{
		DataSynDb db = new DataSynDb();
		// 不带UPDATE_TIME
		RelateColumnEntity rc = new RelateColumnEntity();
		rc.setRelColumnId(1);
		rc.setSrcColumnId(2);
		rc.setRelateRule("rule1");

		// insert
		DataSynDbResult ret = db.insertOrUpdate(rc);
		assertEquals(ret.type, DataSynDbResult.TYPE_INSERT);
		assertEquals(ret.num, 1);
		TableInfo table = factory.getTableInfo(RelateColumnEntity.class);
		ISql sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(rc));
		Object temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(rc.getClass(), table));
		assertTrue(ObjectUtil.compare(RelateColumnEntity.class, rc, temp));
		
		ret = db.delete(rc);
		assertEquals(ret.type, DataSynDbResult.TYPE_DELETE);
		assertEquals(ret.num, 1);
		table = factory.getTableInfo(RelateColumnEntity.class);
		sql = Sql.select("*").from(table.getTableName()).where(table.genFilterFromObject(rc));
		temp = session.query(sql, ResultSetHandleFactory.getPojoSingleHandle(rc.getClass(), table));
		assertNull(temp);
	}

}
