package jetsennet.orm.session;

import junit.framework.TestCase;

public class SqlSessoinFacotryBuilderTest extends TestCase
{

    public void testBuilder()
    {
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder();
        assertNotNull(factory);
        assertNotNull(factory.getConfig());
        assertNotNull(factory.getDataSource());
    }

}
