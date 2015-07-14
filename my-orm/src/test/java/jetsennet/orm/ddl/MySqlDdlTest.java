package jetsennet.orm.ddl;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import junit.framework.TestCase;

public class MySqlDdlTest extends TestCase
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
        MySqlDdl ddl = new MySqlDdl(new ConfigurationBuilderProp("/dbconfig.mysql.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);
    }

}
