package jetsennet.orm.util;

import junit.framework.TestCase;

public class SpecialCharSqlServerTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testMysqlTran()
    {
        assertEquals("abc", SpecialCharSqlServer.trans("abc"));
        assertEquals("ab''c", SpecialCharSqlServer.trans("ab'c"));
        assertEquals("ab\\c", SpecialCharSqlServer.trans("ab\\c"));
        assertEquals("ab\\c\\", SpecialCharSqlServer.trans("ab\\c\\"));

        assertEquals("abc", SpecialCharSqlServer.transLikeParam("abc").first);
        assertEquals("ab''c", SpecialCharSqlServer.transLikeParam("ab'c").first);
        assertEquals("ab\\c", SpecialCharSqlServer.transLikeParam("ab\\c").first);
        assertEquals("ab\\c/%", SpecialCharSqlServer.transLikeParam("ab\\c%").first);
        assertEquals("ab\\c/_", SpecialCharSqlServer.transLikeParam("ab\\c_").first);
        assertEquals("ab\\c/[", SpecialCharSqlServer.transLikeParam("ab\\c[").first);
    }
}
