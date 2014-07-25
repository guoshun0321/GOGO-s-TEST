package jetsennet.orm.tableinfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.InsertEntity;
import jetsennet.orm.sql.UpdateEntity;
import jetsennet.orm.test.util.AllTypeEntity;
import jetsennet.util.SafeDateFormater;
import jetsennet.util.TwoTuple;
import junit.framework.TestCase;

public class TableInfoTest extends TestCase
{

    SqlSessionFactory factory = SqlSessionFactoryBuilder.builder();

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testTableInfo()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        assertNotNull(info);
        assertEquals(AllTypeEntity.class, info.getCls());
        assertEquals("ALL_TYPE", info.getTableName());
        assertNotNull(info.getKey());
        assertNotNull(info.getFieldInfos());
        assertEquals(7, info.getFieldInfos().size());
    }

    public void testTrans2InsertObject()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        AllTypeEntity all =
            new AllTypeEntity(1,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1987-03-21 11:11:11"),
                SafeDateFormater.parse("1987-03-21 22:22:22"));
        InsertEntity insert = info.obj2Insert(all);
        assertNotNull(insert);
        assertEquals("ALL_TYPE", insert.table);
        assertEquals(7, insert.getValues().length);
        assertEquals(1, Integer.parseInt(insert.getValues()[0].toString()));
        assertEquals(2, Integer.parseInt(insert.getValues()[1].toString()));
        assertEquals("value1", insert.getValues()[2]);
        assertEquals("value2", insert.getValues()[3]);
        assertEquals("value3", insert.getValues()[4]);
        assertEquals(SafeDateFormater.parse("1987-03-21 11:11:11").getTime(), ((Date) insert.getValues()[5]).getTime());
        assertEquals(SafeDateFormater.parse("1987-03-21 22:22:22").getTime(), ((Date) insert.getValues()[6]).getTime());
    }

    public void testTrans2InsertMapOfStringObject()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("num1", 2);
        map.put("value1", "value1");
        map.put("value2", "value2");
        map.put("value3", "value3");
        map.put("date1", "1987-03-21 11:11:11");
        map.put("date2", "1987-03-21 22:22:22");
        InsertEntity insert = info.map2Insert(map);
        assertNotNull(insert);
        assertEquals("ALL_TYPE", insert.table);
        assertEquals(7, insert.getValues().length);
        assertEquals(1, Integer.parseInt(insert.getValues()[0].toString()));
        assertEquals(2, Integer.parseInt(insert.getValues()[1].toString()));
        assertEquals("value1", insert.getValues()[2]);
        assertEquals("value2", insert.getValues()[3]);
        assertEquals("value3", insert.getValues()[4]);
        assertEquals(SafeDateFormater.parse("1987-03-21 11:11:11").getTime(), ((Date) insert.getValues()[5]).getTime());
        assertEquals(SafeDateFormater.parse("1987-03-21 22:22:22").getTime(), ((Date) insert.getValues()[6]).getTime());

        map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("num1", null);
        map.put("value1", "value1");
        map.put("value2", "value2");
        map.put("value3", "value3");
        map.put("date1", "1987-03-21 11:11:11");
        map.put("date2", "1987-03-21");
        insert = info.map2Insert(map);
        assertNotNull(insert);
        assertEquals("ALL_TYPE", insert.table);
        assertEquals(6, insert.getValues().length);
        assertEquals(1, Integer.parseInt(insert.getValues()[0].toString()));
        assertEquals("value1", insert.getValues()[1]);
        assertEquals("value2", insert.getValues()[2]);
        assertEquals("value3", insert.getValues()[3]);
        assertEquals(SafeDateFormater.parse("1987-03-21 11:11:11").getTime(), ((Date) insert.getValues()[4]).getTime());
        assertEquals(SafeDateFormater.parse("1987-03-21 00:00:00").getTime(), ((Date) insert.getValues()[5]).getTime());
    }

    public void testTrans2UpdateObject()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        AllTypeEntity all =
            new AllTypeEntity(1,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1987-03-21 11:11:11"),
                SafeDateFormater.parse("1987-03-21 22:22:22"));
        UpdateEntity update = info.obj2Update(all);
        assertNotNull(update);
        assertEquals("ALL_TYPE", update.table);
        assertEquals(6, update.getValues().length);
        assertEquals(2, Integer.parseInt(update.getValues()[0].toString()));
        assertEquals("value1", update.getValues()[1]);
        assertEquals("value2", update.getValues()[2]);
        assertEquals("value3", update.getValues()[3]);
        assertEquals(SafeDateFormater.parse("1987-03-21 11:11:11").getTime(), ((Date) update.getValues()[4]).getTime());
        assertEquals(SafeDateFormater.parse("1987-03-21 22:22:22").getTime(), ((Date) update.getValues()[5]).getTime());
    }

    public void testTrans2UpdateMapOfStringString()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1");
        map.put("num1", "2");
        map.put("value1", "value1");
        map.put("value2", "value2");
        map.put("value3", "value3");
        map.put("date1", "1987-03-21 11:11:11");
        map.put("date2", "1987-03-21 22:22:22");
        UpdateEntity update = info.map2Update(map);
        assertNotNull(update);
        assertEquals("ALL_TYPE", update.table);
        assertEquals(6, update.getValues().length);
        assertEquals(2, Integer.parseInt(update.getValues()[0].toString()));
        assertEquals("value1", update.getValues()[1]);
        assertEquals("value2", update.getValues()[2]);
        assertEquals("value3", update.getValues()[3]);
        assertEquals(SafeDateFormater.parse("1987-03-21 11:11:11").getTime(), ((Date) update.getValues()[4]).getTime());
        assertEquals(SafeDateFormater.parse("1987-03-21 22:22:22").getTime(), ((Date) update.getValues()[5]).getTime());
    }

    public void testTrans2DeleteObject()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        AllTypeEntity all =
            new AllTypeEntity(1,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1987-03-21 11:11:11"),
                SafeDateFormater.parse("1987-03-21 22:22:22"));
        DeleteEntity delete = info.obj2Delete(all);
        assertNotNull(delete);
        assertEquals("ALL_TYPE", delete.table);
    }

    public void testTrans2DeleteMapOfStringString()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1");
        DeleteEntity delete = info.map2Delete(map);
        assertNotNull(delete);
        assertEquals("ALL_TYPE", delete.table);
    }

    public void testPreparedInsert()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        String prep = info.preparedInsert();
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(?,?,?,?,?,?,?)", prep);
    }

    public void testPreparedUpdate()
    {
        AllTypeEntity allType = new AllTypeEntity();
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        TwoTuple<String, Map<String, Object>> prep = info.preparedUpdateWithObj(false, allType);
        System.out.println(prep.first);
        assertEquals("UPDATE ALL_TYPE SET num1= ?, value1= ?, value2= ?, value3= ?, date1= ?, date2= ? WHERE id = ?", prep.first);
        assertEquals(7, prep.second.size());

        prep = info.preparedUpdateWithObj(true, allType);
        System.out.println(prep.first);
        assertEquals("UPDATE ALL_TYPE SET num1= ? WHERE id = ?", prep.first);
        assertEquals(2, prep.second.size());
    }

    public void testPreparedQueryByPk()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        String prep = info.preparedQueryByPk();
        assertEquals("SELECT * FROM ALL_TYPE WHERE id = ?", prep);
    }

    public void testPreparedDeleteByPk()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        String prep = info.preparedDeleteByPk();
        assertEquals("DELETE FROM ALL_TYPE WHERE id = ?", prep);
    }

    public void testQueryAll()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        String prep = info.sqlQueryAll();
        assertEquals("SELECT * FROM ALL_TYPE", prep);
    }

    public void testDeleteAll()
    {
        TableInfo info = factory.getTableInfo(AllTypeEntity.class);
        String prep = info.sqlDeleteAll();
        assertEquals("DELETE FROM ALL_TYPE", prep);
    }

}
