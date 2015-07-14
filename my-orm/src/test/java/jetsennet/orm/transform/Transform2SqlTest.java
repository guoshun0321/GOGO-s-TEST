package jetsennet.orm.transform;

import static jetsennet.orm.sql.FilterUtil.bt;
import static jetsennet.orm.sql.FilterUtil.eq;
import static jetsennet.orm.sql.FilterUtil.ex;
import static jetsennet.orm.sql.FilterUtil.ilk;
import static jetsennet.orm.sql.FilterUtil.in;
import static jetsennet.orm.sql.FilterUtil.lk;
import static jetsennet.orm.sql.FilterUtil.ls;
import static jetsennet.orm.sql.FilterUtil.lseq;
import static jetsennet.orm.sql.FilterUtil.noeq;
import static jetsennet.orm.sql.FilterUtil.noex;
import static jetsennet.orm.sql.FilterUtil.noin;
import static jetsennet.orm.sql.FilterUtil.nolk;
import static jetsennet.orm.sql.FilterUtil.nonu;
import static jetsennet.orm.sql.FilterUtil.nu;
import static jetsennet.orm.sql.FilterUtil.or;
import static jetsennet.orm.sql.FilterUtil.th;
import static jetsennet.orm.sql.FilterUtil.theq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.InsertEntity;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.sql.UpdateEntity;
import jetsennet.orm.test.util.AllTypeEntity;
import jetsennet.orm.test.util.StringUtil;
import jetsennet.util.SafeDateFormater;
import jetsennet.util.TwoTuple;
import junit.framework.TestCase;

public class Transform2SqlTest extends TestCase
{

    private Configuration config = new ConfigurationBuilderProp(Configuration.DEFAULT_CONFIG).genConfiguration();

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testTransISql()
    {
        Transform2Sql trans = new Transform2SqlSqlServer(config);
        // insert
        InsertEntity insert =
            Sql.insert("ALL_TYPE")
                .columns("id", "num1", "value1", "value2", "value3", "date1", "date2")
                .values(1,
                    2,
                    "value1",
                    "value2",
                    "value3",
                    SafeDateFormater.parse("1927-12-11 11:22:33"),
                    SafeDateFormater.parse("1927-12-11 11:22:33"));
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(1,2,'value1','value2','value3','1927-12-11 11:22:33','1927-12-11 11:22:33')",
            trans.trans(insert));

        // update
        UpdateEntity update = Sql.update("ALL_TYPE").columns("num1", "value1", "value2", "value3", "date1", "date2");
        update.values(2, "value1", "value2", "value3", SafeDateFormater.parse("1927-12-11 11:22:33"), SafeDateFormater.parse("1927-12-11 11:22:33"));
        update.where(eq("id", 1));
        assertEquals("UPDATE ALL_TYPE SET num1=2,value1='value1',value2='value2',value3='value3',date1='1927-12-11 11:22:33',date2='1927-12-11 11:22:33' WHERE id = 1",
            trans.trans(update));

        // delete
        DeleteEntity delete = Sql.delete("ALL_TYPE").where(eq("id", 1));
        assertEquals("DELETE FROM ALL_TYPE WHERE id = 1", trans.trans(delete));

        // selete
        SelectEntity select = Sql.select("*").from("ALL_TYPE");
        assertEquals("SELECT * FROM ALL_TYPE", trans.trans(select));
        select = Sql.select("id,num1,date1").from("ALL_TYPE").where(eq("id", 1));
        assertEquals("SELECT id,num1,date1 FROM ALL_TYPE WHERE id = 1", trans.trans(select));
        select = Sql.select("*").from(Sql.select("id, num1").from("ALL_TYPE"), "a").where(eq("a.id", 1));
        assertEquals("SELECT * FROM (SELECT id, num1 FROM ALL_TYPE) a WHERE a.id = 1", trans.trans(select));
        select =
            Sql.select("count(0)")
                .from(Sql.select("id, num1").from("ALL_TYPE"), "a")
                .where(eq("a.id", 1))
                .group("a.id")
                .having("count(0) > 0")
                .order("a.id");

        assertEquals("SELECT count(0) FROM (SELECT id, num1 FROM ALL_TYPE) a WHERE a.id = 1 GROUP BY a.id HAVING count(0) > 0 ORDER BY a.id",
            trans.trans(select));
        select = Sql.select("id").from("ALL_TYPE").where(th("id", 1)).union(Sql.select("id").from("ALL_TYPE").where(ls("id", 5)));
        assertEquals("SELECT id FROM ALL_TYPE WHERE id > 1 UNION SELECT id FROM ALL_TYPE WHERE id < 5", trans.trans(select));
    }

    public void testPageSelect()
    {
        // 在子类中测试
    }

    public void testPrepareInsertMap()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", "1");
        map.put("num1", "2");
        map.put("value1", "value1");
        map.put("value2", "value2");
        map.put("value3", "value3");
        map.put("date1", "1927-12-11 11:22:33");
        map.put("date2", "1927-12-11 11:22:33");

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("id", "2");
        map1.put("num1", "3");
        map1.put("value1", "value11");
        map1.put("value2", "value21");
        map1.put("value3", "value31");
        map1.put("date1", "1927-12-12 11:22:33");
        map1.put("date2", "1927-12-12 11:22:33");

        SqlSessionFactory facotry = SqlSessionFactoryBuilder.builder();
        List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
        types.add(map);
        types.add(map1);
        Transform2Sql trans = new Transform2SqlSqlServer(config);
        List<Map<String, Object>> retval = trans.prepareInsertMap(facotry.getTableInfo(AllTypeEntity.class), types);
        assertEquals(2, retval.size());
        Map<String, Object> temp = retval.get(0);
        List<Object> objs = new ArrayList<Object>(temp.size());
        for (Iterator<String> it = temp.keySet().iterator(); it.hasNext();)
        {
            String key = it.next();
            Object value = temp.get(key);
            objs.add(value);
        }
        assertEquals("[1, 2, value1, value2, value3, 1927-12-11 11:22:33, 1927-12-11 11:22:33]", StringUtil.printArray(objs.toArray()));
        temp = retval.get(1);
        objs = new ArrayList<Object>(temp.size());
        for (Iterator<String> it = temp.keySet().iterator(); it.hasNext();)
        {
            String key = it.next();
            Object value = temp.get(key);
            objs.add(value);
        }
        assertEquals("[2, 3, value11, value21, value31, 1927-12-12 11:22:33, 1927-12-12 11:22:33]", StringUtil.printArray(objs.toArray()));
    }

    public void testPrepareInsertObj()
    {
        AllTypeEntity all =
            new AllTypeEntity(1,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        AllTypeEntity all1 =
            new AllTypeEntity(2,
                3,
                "value11",
                "value21",
                "value31",
                SafeDateFormater.parse("1927-12-11 12:22:33"),
                SafeDateFormater.parse("1927-12-11 12:22:33"));
        List<Object> alls = new ArrayList<Object>();
        alls.add(all);
        alls.add(all1);
        Transform2Sql trans = new Transform2SqlSqlServer(config);
        SqlSessionFactory facotry = SqlSessionFactoryBuilder.builder();
        List<Map<String, Object>> retval = trans.prepareInsertObj(facotry.getTableInfo(AllTypeEntity.class), alls);
        assertEquals(2, retval.size());
        Map<String, Object> map = retval.get(0);
        List<Object> objs = new ArrayList<Object>(map.size());
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext();)
        {
            String key = it.next();
            Object value = map.get(key);
            objs.add(value);
        }
        assertEquals("[1, 2, value1, value2, value3, 1927-12-11 11:22:33, 1927-12-11 11:22:33]", StringUtil.printArray(objs.toArray()));
        map = retval.get(1);
        objs = new ArrayList<Object>(map.size());
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext();)
        {
            String key = it.next();
            Object value = map.get(key);
            objs.add(value);
        }
        assertEquals("[2, 3, value11, value21, value31, 1927-12-11 12:22:33, 1927-12-11 12:22:33]", StringUtil.printArray(objs.toArray()));
    }

    public void testTransJson()
    {
        SqlSessionFactory facotry = SqlSessionFactoryBuilder.builder();
        String str =
            "[{id:'1',num1:'2',value1:'value1',value2:'value2',value3:'value3',date1:'1927-12-11 11:22:33',date2:'1927-12-11 11:22:33'},{id:'2',num1:'3',value1:'value1',value2:'value2',value3:'value3',date1:'1927-12-12 11:22:33',date2:'1927-12-12 11:22:33'}]";
        Transform2Sql trans = new Transform2SqlSqlServer(config);
        List<String> sqls = trans.transJson(facotry.getTableInfo(AllTypeEntity.class), str, SqlTypeEnum.INSERT);
        assertEquals(2, sqls.size());
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(1,2,'value1','value2','value3','1927-12-11 11:22:33','1927-12-11 11:22:33')",
            sqls.get(0));
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(2,3,'value1','value2','value3','1927-12-12 11:22:33','1927-12-12 11:22:33')",
            sqls.get(1));
        sqls = trans.transJson(facotry.getTableInfo(AllTypeEntity.class), str, SqlTypeEnum.UPDATE);
        assertEquals(2, sqls.size());
        assertEquals("UPDATE ALL_TYPE SET num1=2,value1='value1',value2='value2',value3='value3',date1='1927-12-11 11:22:33',date2='1927-12-11 11:22:33' WHERE id = 1",
            sqls.get(0));
        assertEquals("UPDATE ALL_TYPE SET num1=3,value1='value1',value2='value2',value3='value3',date1='1927-12-12 11:22:33',date2='1927-12-12 11:22:33' WHERE id = 2",
            sqls.get(1));
        sqls = trans.transJson(facotry.getTableInfo(AllTypeEntity.class), str, SqlTypeEnum.DELETE);
        assertEquals(2, sqls.size());
        assertEquals("DELETE FROM ALL_TYPE WHERE id = 1", sqls.get(0));
        assertEquals("DELETE FROM ALL_TYPE WHERE id = 2", sqls.get(1));
    }

    public void testTransXml()
    {
        SqlSessionFactory facotry = SqlSessionFactoryBuilder.builder();
        String xml =
            "<Records><Record><id>1</id><num1>2</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record><Record><id>2</id><num1>3</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-12 11:22:33</date1><date2>1927-12-12 11:22:33</date2></Record></Records>";
        Transform2Sql trans = new Transform2SqlSqlServer(config);
        List<String> sqls = trans.transXml(facotry.getTableInfo(AllTypeEntity.class), xml, SqlTypeEnum.INSERT);
        assertEquals(2, sqls.size());
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(1,2,'value1','value2','value3','1927-12-11 11:22:33','1927-12-11 11:22:33')",
            sqls.get(0));
        assertEquals("INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(2,3,'value1','value2','value3','1927-12-12 11:22:33','1927-12-12 11:22:33')",
            sqls.get(1));
        sqls = trans.transXml(facotry.getTableInfo(AllTypeEntity.class), xml, SqlTypeEnum.UPDATE);
        assertEquals(2, sqls.size());
        assertEquals("UPDATE ALL_TYPE SET num1=2,value1='value1',value2='value2',value3='value3',date1='1927-12-11 11:22:33',date2='1927-12-11 11:22:33' WHERE id = 1",
            sqls.get(0));
        assertEquals("UPDATE ALL_TYPE SET num1=3,value1='value1',value2='value2',value3='value3',date1='1927-12-12 11:22:33',date2='1927-12-12 11:22:33' WHERE id = 2",
            sqls.get(1));
        sqls = trans.transXml(facotry.getTableInfo(AllTypeEntity.class), xml, SqlTypeEnum.DELETE);
        assertEquals(2, sqls.size());
        assertEquals("DELETE FROM ALL_TYPE WHERE id = 1", sqls.get(0));
        assertEquals("DELETE FROM ALL_TYPE WHERE id = 2", sqls.get(1));
    }

    public void testCondition1()
    {
        Transform2Sql trans = new Transform2SqlSqlServer(config);

        Date date = SafeDateFormater.parse("1987-03-11 11:24:45");

        assertEquals("id = 1", trans.condition(eq("id", 1), null).toString());
        assertEquals("id = 'test'", trans.condition(eq("id", "test"), null).toString());
        assertEquals("id = '1987-03-11 11:24:45'", trans.condition(eq("id", date), null).toString());

        assertEquals("id <> 1", trans.condition(noeq("id", 1), null).toString());
        assertEquals("id <> 'test'", trans.condition(noeq("id", "test"), null).toString());
        assertEquals("id <> '1987-03-11 11:24:45'", trans.condition(noeq("id", date), null).toString());

        assertEquals("id IS NULL", trans.condition(nu("id"), null).toString());
        assertEquals("id IS NOT NULL", trans.condition(nonu("id"), null).toString());

        assertEquals("id > 1", trans.condition(th("id", 1), null).toString());
        assertEquals("id > 'test'", trans.condition(th("id", "test"), null).toString());
        assertEquals("id > '1987-03-11 11:24:45'", trans.condition(th("id", date), null).toString());

        assertEquals("id < 1", trans.condition(ls("id", 1), null).toString());
        assertEquals("id < 'test'", trans.condition(ls("id", "test"), null).toString());
        assertEquals("id < '1987-03-11 11:24:45'", trans.condition(ls("id", date), null).toString());

        assertEquals("id >= 1", trans.condition(theq("id", 1), null).toString());
        assertEquals("id >= 'test'", trans.condition(theq("id", "test"), null).toString());
        assertEquals("id >= '1987-03-11 11:24:45'", trans.condition(theq("id", date), null).toString());

        assertEquals("id <= 1", trans.condition(lseq("id", 1), null).toString());
        assertEquals("id <= 'test'", trans.condition(lseq("id", "test"), null).toString());
        assertEquals("id <= '1987-03-11 11:24:45'", trans.condition(lseq("id", date), null).toString());

        assertEquals("id LIKE '%test%'", trans.condition(lk("id", "test"), null).toString());
        assertEquals("id LIKE '%te/%st%' ESCAPE '/'", trans.condition(lk("id", "te%st"), null).toString());
        assertEquals("id LIKE '%te/_st%' ESCAPE '/'", trans.condition(lk("id", "te_st"), null).toString());

        assertEquals("id NOT LIKE '%test%'", trans.condition(nolk("id", "test"), null).toString());
        assertEquals("id NOT LIKE '%te/%st%' ESCAPE '/'", trans.condition(nolk("id", "te%st"), null).toString());
        assertEquals("id NOT LIKE '%te/_st%' ESCAPE '/'", trans.condition(nolk("id", "te_st"), null).toString());

        assertEquals("UPPER(id) LIKE UPPER('%test%')", trans.condition(ilk("id", "test"), null).toString());
        assertEquals("UPPER(id) LIKE UPPER('%te/%st%') ESCAPE '/'", trans.condition(ilk("id", "te%st"), null).toString());
        assertEquals("UPPER(id) LIKE UPPER('%te/_st%') ESCAPE '/'", trans.condition(ilk("id", "te_st"), null).toString());

        assertEquals("id IN ('id1', 'id2', 'id3')", trans.condition(in("id", "id1", "id2", "id3"), null).toString());
        assertEquals("id NOT IN ('id1', 'id2', 'id3')", trans.condition(noin("id", "id1", "id2", "id3"), null).toString());
        assertEquals("id BETWEEN ('id1' AND 'id2')", trans.condition(bt("id", "id1", "id2"), null).toString());
        assertEquals("EXISTS (select * from all_type)", trans.condition(ex("select * from all_type"), null).toString());
        assertEquals("NOT EXISTS (select * from all_type)", trans.condition(noex("select * from all_type"), null).toString());

        FilterNode node = eq("id", 1).and(ls("id", 10).and(th("id", 1)).and(or(nu("value1"), nonu("value2"))));
        String str = trans.condition(node, null).toString();
        System.out.println(str);
        assertEquals("(id = 1 AND ((id < 10 AND id > 1) AND (value1 IS NULL OR value2 IS NOT NULL)))", str);
    }

}
