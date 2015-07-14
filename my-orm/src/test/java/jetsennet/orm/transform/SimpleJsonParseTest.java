package jetsennet.orm.transform;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class SimpleJsonParseTest extends TestCase
{

    private static String str =
        "[{\"id\" : \"0\",\"name\" : \"名称\",\"时间\" : \"19290102 11:22:33\"},{\"id\" : \"1\",\"name\" : \"名称1\",\"时间\" : \"19290103 11:22:33\"}]";

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testSimpleJson2Map()
    {
        List<Map<String, Object>> map = SimpleJsonParse.parse(str);
        assertEquals(2, map.size());
        assertEquals("0", map.get(0).get("id"));
        assertEquals("名称", map.get(0).get("name"));
        assertEquals("19290102 11:22:33", map.get(0).get("时间"));
        assertEquals("1", map.get(1).get("id"));
        assertEquals("名称1", map.get(1).get("name"));
        assertEquals("19290103 11:22:33", map.get(1).get("时间"));
    }

}
