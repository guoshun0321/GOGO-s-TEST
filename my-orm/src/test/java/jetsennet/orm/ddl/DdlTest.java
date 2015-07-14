package jetsennet.orm.ddl;

import jetsennet.orm.configuration.ConfigurationBuilder;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.configuration.IConfigurationBuilder;
import junit.framework.TestCase;

public class DdlTest extends TestCase
{

    public void testDdl()
    {
        IDdl ddl = Ddl.getDdl(new ConfigurationBuilderProp(null).genConfiguration());
        DdlTestUtil.ddlTest(ddl);

        ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.mysql.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);

        ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);

        ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration());
        DdlTestUtil.ddlTest(ddl);

        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
        String user = "sa";
        String pwd = "jetsen";
        IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
        ddl = Ddl.getDdl(builder.genConfiguration());
        DdlTestUtil.ddlTest(ddl);
    }

}
