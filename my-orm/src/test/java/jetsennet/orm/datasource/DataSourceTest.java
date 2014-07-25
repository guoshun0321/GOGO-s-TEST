package jetsennet.orm.datasource;

import javax.sql.DataSource;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilder;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.configuration.IConfigurationBuilder;
import junit.framework.TestCase;

public class DataSourceTest extends TestCase
{

    public void testGenConnPool()
    {
        Configuration config = new ConfigurationBuilderProp(null).genConfiguration();
        DataSource ds = DataSourceFactory.getDataSource(config);
        assertNotNull(ds);

        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST";
        String user = "sa";
        String pwd = "jetsen";
        IConfigurationBuilder builder = new ConfigurationBuilder(driver, url, user, pwd);
        config = builder.genConfiguration();
        ds = DataSourceFactory.getDataSource(config);
        assertNotNull(ds);
    }

}
