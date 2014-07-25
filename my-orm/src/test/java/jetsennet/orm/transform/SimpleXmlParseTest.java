package jetsennet.orm.transform;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class SimpleXmlParseTest extends TestCase
{

    private static String xml = "<Records><Record><ID>0</ID><NAME>name</NAME></Record><Record><ID>1</ID><NAME>name1</NAME></Record></Records>";

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
        List<Map<String, Object>> map = SimpleXmlParse.parse(xml);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("0", map.get(0).get("ID"));
        assertEquals("name", map.get(0).get("NAME"));
        assertEquals("1", map.get(1).get("ID"));
        assertEquals("name1", map.get(1).get("NAME"));
    }

}
