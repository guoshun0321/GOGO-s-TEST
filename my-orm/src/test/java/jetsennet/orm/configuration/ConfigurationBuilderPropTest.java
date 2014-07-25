package jetsennet.orm.configuration;

import junit.framework.TestCase;

public class ConfigurationBuilderPropTest extends TestCase
{

    public void testGenConfiguration()
    {
        IConfigurationBuilder builder = new ConfigurationBuilderProp(null);
        Configuration config = builder.genConfiguration();
        assertNotNull(config);
        assertNotNull(config.connInfo);
        assertEquals(config.configFile, Configuration.DEFAULT_CONFIG);
        assertEquals(config.connInfo.driver, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        assertEquals(config.connInfo.url, "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST");
        assertEquals(config.connInfo.user, "sa");
        assertEquals(config.connInfo.pwd, "jetsen");
        
        builder = new ConfigurationBuilderProp("/dbconfig.mysql.properties");
        config = builder.genConfiguration();
        assertNotNull(config);
        assertNotNull(config.connInfo);
        assertEquals(config.configFile, "/dbconfig.mysql.properties");
        assertEquals(config.connInfo.driver, "com.mysql.jdbc.Driver");
        assertEquals(config.connInfo.url, "jdbc:mysql://192.168.8.171:3307/test");
        assertEquals(config.connInfo.user, "root");
        assertEquals(config.connInfo.pwd, "jetsen");
        
        IConfigurationBuilder builder1 = new ConfigurationBuilderProp("/dbconfig.mysql.properties");
        Configuration config1 = builder1.genConfiguration();
        assertEquals(true, config.equals(config1));
    }

}
