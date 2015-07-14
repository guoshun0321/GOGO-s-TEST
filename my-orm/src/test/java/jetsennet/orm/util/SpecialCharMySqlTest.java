package jetsennet.orm.util;

import junit.framework.TestCase;

public class SpecialCharMySqlTest extends TestCase
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
        assertEquals("abc", SpecialCharMySql.trans("abc"));
        assertEquals("ab''c", SpecialCharMySql.trans("ab'c"));
        assertEquals("ab\\\\c", SpecialCharMySql.trans("ab\\c"));
        assertEquals("ab\\\\c\\\\", SpecialCharMySql.trans("ab\\c\\"));

        assertEquals("abc", SpecialCharMySql.transLikeParam("abc").first);
        assertEquals("ab''c", SpecialCharMySql.transLikeParam("ab'c").first);
        assertEquals("ab\\\\c", SpecialCharMySql.transLikeParam("ab\\c").first);
        assertEquals("ab\\\\c/%", SpecialCharMySql.transLikeParam("ab\\c%").first);
        assertEquals("ab\\\\c/_", SpecialCharMySql.transLikeParam("ab\\c_").first);
    }
}
