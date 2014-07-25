package jetsennet.frame.dataaccess;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import junit.framework.TestCase;

public class BaseDaoTest extends TestCase
{

    public void testMySql() throws Exception
    {
        Configuration config = new ConfigurationBuilderProp("/dbconfig.mysql.properties").genConfiguration();
        testAll(config);
    }

    public void testSqlServer() throws Exception
    {
        Configuration config = new ConfigurationBuilderProp("/dbconfig.properties").genConfiguration();
        testAll(config);
    }
    
    public void testOracle() throws Exception
    {
        Configuration config = new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration();
        testAll(config);
    }

    private void testAll(Configuration config) throws Exception
    {
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder(config);
        BaseDaoNewTestUtil util = new BaseDaoNewTestUtil(config, factory);
        util.rebuild();
        util.fillTest();
        util.fillJsonTest();
        util.queryBusinessObjByPkTest();
        util.queryTest();
        util.queryByPagedQueryTest();
        util.saveTest();
        util.deleteTest();
        util.executeTest();
        util.getTest();
        util.updateTest();
    }

}
