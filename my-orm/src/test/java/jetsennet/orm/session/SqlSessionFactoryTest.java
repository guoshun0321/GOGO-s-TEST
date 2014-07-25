package jetsennet.orm.session;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.transform.Transform2SqlMySql;
import jetsennet.orm.transform.Transform2SqlOracle;
import jetsennet.orm.transform.Transform2SqlSqlServer;
import junit.framework.TestCase;

public class SqlSessionFactoryTest extends TestCase
{

    public void testOpenSession()
    {
        SqlSessionFactory facotry = SqlSessionFactoryBuilder.builder();
        assertEquals(Transform2SqlSqlServer.class, facotry.getTransform().getClass());

        SqlSessionFactory facotry1 = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.properties"));
        assertEquals(facotry1, facotry);

        SqlSessionFactory facotry2 = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.mysql.properties"));
        assertEquals(Transform2SqlMySql.class, facotry2.getTransform().getClass());

        SqlSessionFactory facotry3 = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.oracle.properties"));
        assertEquals(Transform2SqlOracle.class, facotry3.getTransform().getClass());
    }

}
