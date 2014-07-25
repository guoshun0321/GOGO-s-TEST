package jetsennet.orm.configuration;

import junit.framework.TestCase;

public class ConfigurationBuilderTest extends TestCase
{

    public void testGenConfiguration()
    {

        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
        String user = "sa";
        String pwd = "jetsen";

        IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
        Configuration config = builder.genConfiguration();
        assertNotNull(config);
        assertNotNull(config.connInfo);
        assertEquals(config.configFile, null);
        assertEquals(config.connInfo.driver, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertEquals(config.connInfo.url, "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST");
        assertEquals(config.connInfo.user, "sa");
        assertEquals(config.connInfo.pwd, "jetsen");
    }

}
