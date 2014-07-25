package jetsennet.orm.util;

import junit.framework.TestCase;

public class SpecialCharOracleTest extends TestCase
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
        assertEquals("abc", SpecialCharOracle.trans("abc"));
        assertEquals("ab''c", SpecialCharOracle.trans("ab'c"));
        assertEquals("ab\\c", SpecialCharOracle.trans("ab\\c"));
        assertEquals("ab\\c\\", SpecialCharOracle.trans("ab\\c\\"));
        assertEquals("ab'||chr(38)||'c", SpecialCharOracle.trans("ab&c"));
        assertEquals("ab'||chr(38)||'c'||chr(38)||'", SpecialCharOracle.trans("ab&c&"));

        assertEquals("abc", SpecialCharOracle.transLikeParam("abc").first);
        assertEquals("ab''c", SpecialCharOracle.transLikeParam("ab'c").first);
        assertEquals("ab'||chr(38)||'c", SpecialCharOracle.transLikeParam("ab&c").first);
        assertEquals("ab'||chr(38)||'c/%", SpecialCharOracle.transLikeParam("ab&c%").first);
        assertEquals("ab'||chr(38)||'c/_", SpecialCharOracle.transLikeParam("ab&c_").first);
    }
}
