package jetsennet.orm.ddl;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import junit.framework.TestCase;

public class OracleDdlTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testTableOp()
    {
        OracleDdl ddl = new OracleDdl(new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);
    }

}
