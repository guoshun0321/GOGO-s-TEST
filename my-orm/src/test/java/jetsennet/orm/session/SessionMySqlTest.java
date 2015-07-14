package jetsennet.orm.session;

import static jetsennet.orm.sql.FilterUtil.eq;
import static jetsennet.orm.sql.FilterUtil.ls;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.ddl.Ddl;
import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.executor.keygen.EfficientPkEntity;
import jetsennet.orm.executor.keygen.IdentifierEntity;
import jetsennet.orm.executor.resultset.AbsResultSetHandle;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.InsertEntity;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import jetsennet.orm.tableinfo.TableInfoParseClz;
import jetsennet.orm.test.util.AllTypeEntity;
import jetsennet.orm.test.util.MySqlDataInfo;
import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionMySqlTest extends TestCase
{

    private SqlSessionFactory factory;

    private static final Logger logger = LoggerFactory.getLogger(SessionMySqlTest.class);

    @Override
    protected void setUp() throws Exception
    {
        String file = "/dbconfig.mysql.properties";
        IDdl ddl = Ddl.getDdl(new ConfigurationBuilderProp(file).genConfiguration());
        ddl.rebuild(TableInfoParseClz.parse(AllTypeEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(IdentifierEntity.class));
        ddl.rebuild(TableInfoParseClz.parse(EfficientPkEntity.class));
        factory = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp(file));
    }

    @Override
    protected void tearDown() throws Exception
    {
    }

    public void testInsert0()
    {
        Session session = factory.openSession();
        // sql语句
        String sql =
            "INSERT INTO ALL_TYPE(id,num1,value1,value2,value3,date1,date2) VALUES(1,2,'value1','value2','value3','1927-12-11 11:22:33','1927-12-11 11:22:33')";
        session.insert(sql);
        // ISql语句
        InsertEntity insert =
            Sql.insert("ALL_TYPE")
                .columns("id", "num1", "value1", "value2", "value3", "date1", "date2")
                .values(2,
                    2,
                    "value21",
                    "value22",
                    "value23",
                    SafeDateFormater.parse("1937-12-11 11:22:33"),
                    SafeDateFormater.parse("1937-12-11 12:22:33"));
        session.insert(insert);
        assertEquals(2, this.getNum("ALL_TYPE"));
    }

    public void testInsert1()
    {
        Session session = factory.openSession();
        // 自动管理id
        // 对象
        AllTypeEntity all =
            new AllTypeEntity(1,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        session.insert(all, false);
        // map
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id", "2");
        map.put("num1", "2");
        map.put("value1", "value1");
        map.put("value2", "value2");
        map.put("value3", "value3");
        map.put("date1", "1927-12-11 11:22:33");
        map.put("date2", "1927-12-11 11:22:33");
        session.insert(map, AllTypeEntity.class);
        // json
        String json =
            "[{id:'3',num1:'2',value1:'value1',value2:'value2',value3:'value3',date1:'1927-12-11 11:22:33',date2:'1927-12-11 11:22:33'},{id:'4',num1:'3',value1:'value1',value2:'value2',value3:'value3',date1:'1927-12-12 11:22:33',date2:'1927-12-12 11:22:33'}]";
        session.insertJson(json, AllTypeEntity.class);
        // xml
        String xml =
            "<Records><Record><id>5</id><num1>2</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record><Record><id>6</id><num1>3</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-12 11:22:33</date1><date2>1927-12-12 11:22:33</date2></Record></Records>";
        session.insertXml(xml, AllTypeEntity.class);
        assertEquals(6, this.getNum("ALL_TYPE"));
        // 多个对象
        AllTypeEntity all1 =
            new AllTypeEntity(7,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        AllTypeEntity all2 =
            new AllTypeEntity(8,
                2,
                "value1",
                "value2",
                "value3",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        List<Object> alls = new ArrayList<Object>();
        alls.add(all1);
        alls.add(all2);
        session.insertObjList(alls, AllTypeEntity.class, false);
        assertEquals(8, this.getNum("ALL_TYPE"));
    }

    public void testUpdate0()
    {
        Session session = factory.openSession();
        testInsert1();
        String sql = "update all_type set value1 = 'value1update'";
        int i = session.update(sql);
        assertEquals(8, i);
        ISql update = Sql.update("ALL_TYPE").set("value1", "value2update").where(eq("id", "6"));
        i = session.update(update);
        assertEquals(1, i);
        // 对象
        AllTypeEntity all =
            new AllTypeEntity(3,
                2,
                "value1update",
                "value2update",
                "value3update",
                SafeDateFormater.parse("1927-12-11 11:22:33"),
                SafeDateFormater.parse("1927-12-11 11:22:33"));
        i = session.update(all);
        assertEquals(1, i);
        // json
        String json =
            "[{id:'1',num1:'2',value1:'update11',value2:'value2',value3:'value3',date1:'1927-12-11 11:22:33',date2:'1927-12-11 11:22:33'},{id:'2',num1:'3',value1:'udpate12',value2:'value2',value3:'value3',date1:'1927-12-12 11:22:33',date2:'1927-12-12 11:22:33'}]";
        session.updateJson(json, AllTypeEntity.class);
        // xml
        String xml =
            "<Records><Record><id>3</id><num1>2</num1><value1>value1</value1><value2>update21</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record><Record><id>4</id><num1>3</num1><value1>value1</value1><value2>value2</value2><value3>update31</value3><date1>1927-12-12 11:22:33</date1><date2>1927-12-12 11:22:33</date2></Record></Records>";
        session.updateXml(xml, AllTypeEntity.class);
    }

    public void testDelete0()
    {
        Session session = factory.openSession();
        testInsert1();
        String sql = "delete from all_type";
        session.delete(sql);
        assertEquals(0, this.getNum("ALL_TYPE"));

        testInsert1();
        ISql delete = Sql.delete("all_type");
        session.delete(delete);
        assertEquals(0, this.getNum("ALL_TYPE"));

        testInsert1();
        session.deleteById(AllTypeEntity.class, 1);
        assertEquals(7, this.getNum("ALL_TYPE"));
        session.deleteAll("ALL_TYPE");
        assertEquals(0, this.getNum("ALL_TYPE"));
    }

    public void testQuery0()
    {
        Session session = factory.openSession();
        testInsert1();

        ISql select = Sql.select("*").from("all_type").where(ls("id", 3));
        List<AllTypeEntity> list = session.query(select, RowsResultSetExtractor.gen(AllTypeEntity.class, session.getTableInfo(AllTypeEntity.class)));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getId());

        String sql = "select * from all_type where id < 3";
        list = session.query(sql, AllTypeEntity.class);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getId());

        String str = session.queryJson(sql);
        System.out.println(str);
        assertEquals("[{\"id\":1,\"num1\":2,\"value1\":\"value1\",\"value2\":\"value2\",\"value3\":\"value3\",\"date1\":\"1927-12-11 11:22:33\",\"date2\":\"1927-12-11 11:22:33\"},{\"id\":2,\"num1\":2,\"value1\":\"value1\",\"value2\":\"value2\",\"value3\":\"value3\",\"date1\":\"1927-12-11 11:22:33\",\"date2\":\"1927-12-11 11:22:33\"}]",
            str);

        str = session.queryXml(sql);
        System.out.println(str);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Records><Record><id>1</id><num1>2</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record><Record><id>2</id><num1>2</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record></Records>",
            str);
    }

    public void testPage0()
    {
        Session session = factory.openSession();
        testInsert1();

        ISql select = Sql.select("*").from("all_type").order("id");

        String str = session.pageJson(select, 2, 2);
        System.out.println(str);
        assertEquals("{\"Info\":{\"Count\":4,\"Cur\":2},\"Records\":[{\"date1\":\"1927-12-12 11:22:33\",\"date2\":\"1927-12-12 11:22:33\",\"id\":\"4\",\"num1\":\"3\",\"value1\":\"value1\",\"value2\":\"value2\",\"value3\":\"value3\"},{\"date1\":\"1927-12-11 11:22:33\",\"date2\":\"1927-12-11 11:22:33\",\"id\":\"5\",\"num1\":\"2\",\"value1\":\"value1\",\"value2\":\"value2\",\"value3\":\"value3\"}]}",
            str);

        str = session.pageXml(select, 2, 2);
        System.out.println(str);
        assertEquals("<Records><Record><id>4</id><num1>3</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-12 11:22:33</date1><date2>1927-12-12 11:22:33</date2></Record><Record><id>5</id><num1>2</num1><value1>value1</value1><value2>value2</value2><value3>value3</value3><date1>1927-12-11 11:22:33</date1><date2>1927-12-11 11:22:33</date2></Record><Info><Count>4</Count><Cur>2</Cur></Info></Records>",
            str);
    }

    public void testBatchOp()
    {
        //        TableInfoMgr.registerTableInfo("FirstTable", FirstTableEntity.xml);
        //        TableInfoMgr.registerTableInfo("SecondTable", SecondTableEntity.xml);
        //        TableInfoMgr.registerTableInfo("ThirdTable", ThirdTableEntity.xml);
        //
        //        Session session = factory.openSession();
        //
        //        ISql del = Sql.delete("FirstTable");
        //        session.delete(del);
        //        del = Sql.delete("SecondTable");
        //        session.delete(del);
        //        del = Sql.delete("ThirdTable");
        //        session.delete(del);
        //        del = Sql.delete("net_sequence").where(FilterUtil.in("TABLE_NAME", "FirstTable", "SecondTable", "ThirdTable"));
        //        session.delete(del);
        //
        //        String insertXml =
        //            "<FirstTable action=\"insert\"><FF1>3</FF1><FF2>test</FF2><SecondTable action=\"insert\"><SF1>$FirstTable.FF0</SF1><SF2>test.second</SF2><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable></SecondTable><SecondTable action=\"insert\"><SF1>$FirstTable.FF0</SF1><SF2>test.second</SF2><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable></SecondTable></FirstTable>";
        //        session.batch(Xml2CascadeSqlEntity.parse(insertXml));
        //        assertEquals(1, this.getNum("FirstTable"));
        //        assertEquals(2, this.getNum("SecondTable"));
        //        assertEquals(4, this.getNum("ThirdTable"));
        //        session.batch(Xml2CascadeSqlEntity.parse(insertXml));
        //        assertEquals(2, this.getNum("FirstTable"));
        //        assertEquals(4, this.getNum("SecondTable"));
        //        assertEquals(8, this.getNum("ThirdTable"));
        //
        //        String updateXml =
        //            "<FirstTable action=\"update\"><FF0>1</FF0><FF1>3</FF1><FF2>test</FF2><SecondTable action=\"insert\"><SF1>$FirstTable.FF0</SF1><SF2>test.second</SF2><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable></SecondTable><SecondTable action=\"insert\"><SF1>$FirstTable.FF0</SF1><SF2>test.second</SF2><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable><ThirdTable action=\"insert\"><TF1>$SecondTable.SF0</TF1><TF2>test.third</TF2></ThirdTable></SecondTable></FirstTable>";
        //        session.batch(Xml2CascadeSqlEntity.parse(updateXml));
        //        assertEquals(2, this.getNum("FirstTable"));
        //        assertEquals(6, this.getNum("SecondTable"));
        //        assertEquals(12, this.getNum("ThirdTable"));
        //
        //        String deleteXml =
        //            "<FirstTable action=\"delete\" filter=\"FF0\" affected=\"false\"><FF0>1</FF0><SecondTable action=\"delete\" filter=\"SF1\" affected=\"true\"><SF1>$FirstTable.FF0</SF1><ThirdTable action=\"delete\" filter=\"TF1\"><TF1>$SecondTable.INFO#AFFECTED</TF1></ThirdTable></SecondTable></FirstTable>";
        //        session.batch(deleteXml);
        //        assertEquals(1, this.getNum("FirstTable"));
        //        assertEquals(2, this.getNum("SecondTable"));
        //        assertEquals(4, this.getNum("ThirdTable"));

    }

    private int getNum(String table)
    {
        Session session = factory.openSession();
        SelectEntity select = Sql.select("count(0) as count_num").from(table);
        int retval = -1;
        try
        {
            retval = session.query(session.getTransform().trans(select), new AbsResultSetHandle<Integer>()
            {
                public Integer handle(ResultSet rs) throws Exception
                {
                    int retval = -1;
                    if (rs.next())
                    {
                        retval = rs.getInt("count_num");
                    }
                    return retval;
                }
            });
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
