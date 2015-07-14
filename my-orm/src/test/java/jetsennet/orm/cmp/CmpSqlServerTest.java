package jetsennet.orm.cmp;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilder;
import jetsennet.orm.configuration.IConfigurationBuilder;
import jetsennet.orm.session.CscQueryResult;
import junit.framework.TestCase;

public class CmpSqlServerTest extends TestCase
{

    private String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
    private String user = "sa";
    private String pwd = "jetsen";
    private IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
    private Configuration config = builder.genConfiguration();

    private String objName = "test";

    public void testSqlOp() throws Exception
    {
        CmpTestUtil.build();
        Cmp cmp = CmpMgr.ensureCmp(config);
        cmp.unregisterCmpObject(objName);
        cmp.registerCmpObject(CmpTestUtil.genTestObject());

        System.out.println("insert");
        String id1 = cmp.modify(objName, CmpTestUtil.genInsert1());
        assertNotNull(id1);
        CscQueryResult cscRet = cmp.cscQuery(objName, id1);
        String[] excludes = { "FIELD2", "FIELD1" };
        String cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        String expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"10\",\"TABLE1_FIELD1\":\"1\"},\"subs\":[{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        System.out.println("update insert");
        String id2 = cmp.modify(objName, CmpTestUtil.genUpdateInsert(id1));
        assertNotNull(id2);
        cscRet = cmp.cscQuery(objName, id1);
        cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"20\",\"TABLE1_FIELD1\":\"2\"},\"subs\":[{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        System.out.println("update delete");
        String id3 = cmp.modify(objName, CmpTestUtil.genUpdateDelete(id1));
        System.out.println(id3);
        cscRet = cmp.cscQuery(objName, id1);
        cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"30\",\"TABLE1_FIELD1\":\"3\"},\"subs\":[{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        cmp.delete(objName, id1);
        cscRet = cmp.cscQuery(objName, id1);
        assertNull(cscRet);
    }

    public void testSqlOpXml() throws Exception
    {
        CmpTestUtil.build();
        Cmp cmp = CmpMgr.ensureCmp(config);
        cmp.unregisterCmpObject(objName);
        cmp.registerCmpObject(CmpTestUtil.genTestObject());

        System.out.println("insert");
        String xml = CmpTestUtil.genInsert1().toXml(null);
        String id1 = cmp.modify(objName, xml);
        assertNotNull(id1);
        CscQueryResult cscRet = cmp.cscQuery(objName, id1);
        String[] excludes = { "FIELD2", "FIELD1" };
        String cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        String expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"10\",\"TABLE1_FIELD1\":\"1\"},\"subs\":[{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        System.out.println("update insert");
        xml = CmpTestUtil.genUpdateInsert(id1).toXml(null);
        String id2 = cmp.modify(objName, xml);
        assertNotNull(id2);
        cscRet = cmp.cscQuery(objName, id1);
        cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"20\",\"TABLE1_FIELD1\":\"2\"},\"subs\":[{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE11\",\"values\":{\"TABLE11_FIELD2\":\"110\",\"TABLE11_FIELD1\":\"11\"},\"subs\":[{\"name\":\"TABLE111\",\"values\":{\"TABLE111_FIELD2\":\"1110\",\"TABLE111_FIELD1\":\"111\"}},{\"name\":\"TABLE112\",\"values\":{\"TABLE112_FIELD2\":\"1120\",\"TABLE112_FIELD1\":\"112\"}}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        System.out.println("update delete");
        CmpOpEntity cmpOp1 = CmpTestUtil.genUpdateDelete(id1);
        xml = cmpOp1.toXml(null);
        System.out.println(xml);
        String id3 = cmp.modify(objName, xml);
        System.out.println(id3);
        cscRet = cmp.cscQuery(objName, id1);
        cscStr = cscRet.getDesc(null, excludes);
        System.out.println(cscStr);
        expect =
            "{\"name\":\"TABLE1\",\"values\":{\"TABLE1_FIELD2\":\"30\",\"TABLE1_FIELD1\":\"3\"},\"subs\":[{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]},{\"name\":\"TABLE12\",\"values\":{\"TABLE12_FIELD2\":\"null\",\"TABLE12_FIELD1\":\"null\"},\"subs\":[{\"name\":\"TABLE121\",\"values\":{\"TABLE121_FIELD2\":\"1210\",\"TABLE121_FIELD1\":\"121\"}},{\"name\":\"TABLE122\",\"values\":{\"TABLE122_FIELD2\":\"1220\",\"TABLE122_FIELD1\":\"122\"},\"subs\":[{\"name\":\"TABLE1221\",\"values\":{\"TABLE1221_FIELD2\":\"1220\",\"TABLE1221_FIELD1\":\"122\"}}]}]}]}";
        assertEquals(expect, cscStr);

        cmp.delete(objName, id1);
        cscRet = cmp.cscQuery(objName, id1);
        assertNull(cscRet);
    }
}
