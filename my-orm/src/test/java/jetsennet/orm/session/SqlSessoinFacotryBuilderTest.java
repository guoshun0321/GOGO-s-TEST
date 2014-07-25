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

        SqlSessionFactory factory1 = SqlSessionFactoryBuilder.builder();
        assertEquals(true, factory.equals(factory1));
    }

}
