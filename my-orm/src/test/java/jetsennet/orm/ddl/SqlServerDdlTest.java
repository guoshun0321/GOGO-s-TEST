package jetsennet.orm.ddl;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import junit.framework.TestCase;

public class SqlServerDdlTest extends TestCase
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
        SqlServerDdl ddl = new SqlServerDdl(new ConfigurationBuilderProp("/dbconfig.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);
    }

}
