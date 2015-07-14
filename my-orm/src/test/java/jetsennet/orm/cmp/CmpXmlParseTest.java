package jetsennet.orm.cmp;

import junit.framework.TestCase;

public class CmpXmlParseTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testParse()
    {
        CmpOpEntity insert = CmpTestUtil.genInsert();
        String xml = insert.toXml(null);
        CmpOpEntity insert1 = CmpXmlParse.parse(xml);
        String xml1 = insert1.toXml(null);
        System.out.println(xml);
        System.out.println(xml1);
        assertEquals(xml, xml1);

        insert = CmpTestUtil.genInsert1();
        xml = insert.toXml(null);
        insert1 = CmpXmlParse.parse(xml);
        xml1 = insert1.toXml(null);
        System.out.println(xml);
        System.out.println(xml1);
        assertEquals(xml, xml1);

        insert = CmpTestUtil.genUpdateInsert("TEST");
        xml = insert.toXml(null);
        insert1 = CmpXmlParse.parse(xml);
        xml1 = insert1.toXml(null);
        System.out.println(xml);
        System.out.println(xml1);
        assertEquals(xml, xml1);

        insert = CmpTestUtil.genUpdateDelete("TEST");
        xml = insert.toXml(null);
        insert1 = CmpXmlParse.parse(xml);
        xml1 = insert1.toXml(null);
        System.out.println(xml);
        System.out.println(xml1);
        assertEquals(xml, xml1);
    }

}
