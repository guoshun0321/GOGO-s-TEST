package jetsennet.orm.executor;

import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.executor.keygen.EfficientPkEntity;
import jetsennet.orm.executor.keygen.IdentifierEntity;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoParseClz;
import jetsennet.orm.tableinfo.convert.SqlTypeConvert;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

public class SimpleExecutorTestUtil extends TestCase
{
    public static void rebuild(IDdl ddl, TableInfo table)
    {
        if (ddl.isExist(table.getTableName()))
        {
            ddl.delete(table.getTableName());
        }
        ddl.rebuild(table);
        ddl.rebuild(TableInfoParseClz.parse(IdentifierEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(EfficientPkEntity.class));
    }

    public static void testUpdate(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("update");

        SimpleExecutor exec = new SimpleExecutor();

        SimpleSqlEntity entity = SimpleSqlEntity.instance(1, trans.getFactory().getConfig().isOracle());
        String sql = form.trans(table.obj2Sql(entity, SqlTypeEnum.INSERT));
        int val = exec.update(trans, sql);
        assertEquals(1, val);

        entity.setField1(10);
        sql = form.trans(table.obj2Sql(entity, SqlTypeEnum.UPDATE));
        val = exec.update(trans, sql);
        assertEquals(1, val);

        sql = form.trans(table.obj2Sql(entity, SqlTypeEnum.DELETE));
        val = exec.update(trans, sql);
        assertEquals(1, val);
    }

    public static void testUpdateBatch(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("update batch");
        SimpleExecutor exec = new SimpleExecutor();

        SimpleSqlEntity entity = SimpleSqlEntity.instance(1, trans.getFactory().getConfig().isOracle());
        String sqls[] = new String[3];
        sqls[0] = form.trans(table.obj2Sql(entity, SqlTypeEnum.INSERT));
        sqls[1] = form.trans(table.obj2Sql(entity, SqlTypeEnum.UPDATE));
        sqls[2] = form.trans(table.obj2Sql(entity, SqlTypeEnum.DELETE));

        int vals[] = exec.update(trans, sqls);

        assertEquals(3, vals.length);
        assertEquals(1, vals[0]);
        assertEquals(1, vals[1]);
        assertEquals(1, vals[2]);

    }

    public static void testUpdatePrepared(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("update prepared");
        List<Map<String, Object>> objValMaps = new ArrayList<Map<String, Object>>(10);
        for (int i = 0; i < 10; i++)
        {
            SimpleSqlEntity entity = SimpleSqlEntity.instance(i, false);
            objValMaps.add(table.obj2map(entity));
        }

        SimpleExecutor exec = new SimpleExecutor();
        String sql = table.preparedInsert();

        int[] vals = exec.update(trans, sql, table, objValMaps);
        assertEquals(10, vals.length);
        for (int i = 0; i < 10; i++)
        {
            if (trans.getFactory().getConfig().isOracle())
            {
                assertEquals(Statement.SUCCESS_NO_INFO, vals[i]);
            }
            else
            {
                assertEquals(1, vals[i]);
            }
        }
    }

    public static void testQuery(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("query");
        String sql = "select * from TEST_SIMPLE where id1 = 0";
        SimpleExecutor exec = new SimpleExecutor();
        SimpleSqlEntity val = exec.query(trans, sql, RowsResultSetExtractor.gen(SimpleSqlEntity.class, table)).get(0);
        assertEquals(0, val.getId1());
        System.out.println(val);
    }

    public static void testQuery1(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("query");
        String sql = "select * from TEST_SIMPLE where id1 = ? and FIELD3 = ? and field6 = ?";
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("ID1", 0);
        map.put("FIELD3", 0.78d);
        map.put("FIELD6", SafeDateFormater.parse("1977-09-08 11:22:33"));

        SimpleExecutor exec = new SimpleExecutor();
        SimpleSqlEntity val = exec.query(trans, sql, table, map, RowsResultSetExtractor.gen(SimpleSqlEntity.class, table)).get(0);
        assertEquals(0, val.getId1());
        System.out.println(val);
    }

    public static void testQuery2(ITransactionManager trans, ITransform2Sql form, TableInfo table)
    {
        System.out.println("query");
        String sql = "select * from TEST_SIMPLE where id1 = ? and FIELD3 = ? and field6 = ?";
        Object objs[] = new Object[3];
        objs[0] = 0;
        objs[1] = 0.78;
        objs[2] = SqlTypeConvert.convert(SafeDateFormater.parse("1977-09-08 11:22:33"), Types.TIMESTAMP);

        SimpleExecutor exec = new SimpleExecutor();
        SimpleSqlEntity val = exec.query(trans, sql, objs, RowsResultSetExtractor.gen(SimpleSqlEntity.class, table)).get(0);
        assertEquals(0, val.getId1());
        System.out.println(val);
    }
}
