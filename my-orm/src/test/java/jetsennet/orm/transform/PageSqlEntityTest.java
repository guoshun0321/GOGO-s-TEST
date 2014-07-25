package jetsennet.orm.transform;

import junit.framework.TestCase;

public class PageSqlEntityTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testQuerySql()
    {
        PageSqlEntity page = new PageSqlEntity("<%BEGIN%>,<%END%>,<%SIZE%>", "", 5, 20);
        
        PageResult pr = page.querySql(211);
        assertEquals(11, pr.count);
        assertEquals(5, pr.cur);
        assertEquals("81,100,20", pr.pageSql);
        
        pr = page.querySql(100);
        assertEquals(5, pr.count);
        assertEquals(5, pr.cur);
        assertEquals("81,100,20", pr.pageSql);
        
        pr = page.querySql(92);
        assertEquals(5, pr.count);
        assertEquals(5, pr.cur);
        assertEquals("81,92,20", pr.pageSql);
        
        pr = page.querySql(72);
        assertEquals(4, pr.count);
        assertEquals(4, pr.cur);
        assertEquals("61,72,20", pr.pageSql);
    }

}
