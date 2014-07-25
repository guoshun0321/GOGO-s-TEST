package jetsennet.orm.sql;

import jetsennet.orm.transform.ITransform2Sql;
import jetsennet.orm.transform.Transform2SqlMySql;
import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

import static jetsennet.orm.sql.FilterUtil.*;

public class SqlMySqlTest extends TestCase
{

    private ITransform2Sql transform = new Transform2SqlMySql(null);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testInsert()
    {
        // INSERT INTO all_type VALUES(2,2,'value21','value22','value23','1937-12-11 11:22:33','1937-12-11 12:22:33')
        ISql sql =
            Sql.insert("all_type").values(2,
                2,
                "value21",
                "value22",
                "value23",
                SafeDateFormater.parse("1937-12-11 11:22:33"),
                SafeDateFormater.parse("1937-12-11 12:22:33"));
        String str = transform.trans(sql);
        assertEquals("INSERT INTO all_type VALUES(2,2,'value21','value22','value23','1937-12-11 11:22:33','1937-12-11 12:22:33')", str);
        // INSERT INTO all_type(id,num1,value1,value2,value3,date1,date2) VALUES(2,2,'value21','value22','value23','1937-12-11 11:22:33','1937-12-11 12:22:33')
        sql =
            Sql.insert("all_type")
                .columns("id", "num1", "value1", "value2", "value3", "date1", "date2")
                .values(2,
                    2,
                    "value21",
                    "value22",
                    "value23",
                    SafeDateFormater.parse("1937-12-11 11:22:33"),
                    SafeDateFormater.parse("1937-12-11 12:22:33"));
        str = transform.trans(sql);
        assertEquals("INSERT INTO all_type(id,num1,value1,value2,value3,date1,date2) VALUES(2,2,'value21','value22','value23','1937-12-11 11:22:33','1937-12-11 12:22:33')",
            str);
    }

    public void testUpdate()
    {
        // UPDATE all_type SET id=2,num1=2,value1='value21',value2='value22',value3='value23',date1='1937-12-11 11:22:33',date2='1937-12-11 12:22:33'
        ISql sql =
            Sql.update("all_type")
                .columns("num1", "value1", "value2", "value3", "date1", "date2")
                .values(2,
                    "value21",
                    "value22",
                    "value23",
                    SafeDateFormater.parse("1937-12-11 11:22:33"),
                    SafeDateFormater.parse("1937-12-11 12:22:33"))
                .where(eq("id", 1));
        String str = transform.trans(sql);
        assertEquals("UPDATE all_type SET num1=2,value1='value21',value2='value22',value3='value23',date1='1937-12-11 11:22:33',date2='1937-12-11 12:22:33' WHERE id = 1",
            str);

        sql =
            Sql.update("all_type")
                .set("num1",
                    2,
                    "value1",
                    "value21",
                    "value2",
                    "value22",
                    "value3",
                    "value23",
                    "date1",
                    SafeDateFormater.parse("1937-12-11 11:22:33"),
                    "date2",
                    SafeDateFormater.parse("1937-12-11 12:22:33"))
                .where(eq("id", 1));
        str = transform.trans(sql);
        assertEquals("UPDATE all_type SET num1=2,value1='value21',value2='value22',value3='value23',date1='1937-12-11 11:22:33',date2='1937-12-11 12:22:33' WHERE id = 1",
            str);
    }

    public void testDelete()
    {
        ISql sql = Sql.delete("all_type").where(eq("id", 1));
        String str = transform.trans(sql);
        assertEquals("DELETE FROM all_type WHERE id = 1", str);
    }

    public void testQuery()
    {
        ISql sql = Sql.select("id,num1").from("all_type");
        String str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("id,num1").distinct().from("all_type");
        str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("id,num1").distinct().from(Sql.select("*").from("all_type"), "a");
        str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("id,num1").distinct().from(Sql.select("*").from("all_type"), "a").where(eq("id", 1));
        str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("id,num1").distinct().from(Sql.select("*").from("all_type"), "a").where(eq("id", 1)).order("id asc");
        str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("count(id) as cn").from("all_type").where(eq("id", 1)).group("id").order("id asc");
        str = transform.trans(sql);
        System.out.println(str);

        // SELECT id,num1 FROM all_type WHERE id = 1 UNION ALL SELECT id,num1 FROM all_type WHERE id = 1 ORDER BY id asc
        sql =
            Sql.select("id,num1")
                .from("all_type")
                .where(eq("id", 1))
                .order("id asc")
                .unionAll(Sql.select("id,num1").from("all_type").where(eq("id", 1)));
        and(eq("id", 1), or(eq("id", 2), eq("id", 3)));
        eq("id", 1).and(eq("id", 2));
        str = transform.trans(sql);
        System.out.println(str);

        sql = Sql.select("*").from("BMP_ALARM").where(eq("id", 1).and(eq("source_id", 2)));
        str = transform.trans(sql);
        System.out.println(str);
    }
}
