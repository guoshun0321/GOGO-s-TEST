package jetsennet.orm.configuration;

import junit.framework.TestCase;

public class DefaultConfigurationBuilderTest extends TestCase
{

    public void testGenConfiguration()
    {
        ConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.genConfiguration(null);
        assertNotNull(config);
        assertNotNull(config.connInfo);
        assertEquals(config.configFile, Configuration.DEFAULT_CONFIG);
        assertEquals(config.connInfo.driver, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertEquals(config.connInfo.url, "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=JNMP20");
        assertEquals(config.connInfo.user, "sa");
        assertEquals(config.connInfo.pwd, "jetsen");
    }

}
