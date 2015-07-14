package jetsennet.orm.executor;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.datasource.DataSourceFactory;
import jetsennet.orm.ddl.OracleDdl;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.transaction.ITransactionManager;
import jetsennet.orm.transaction.TransactionManager;
import jetsennet.orm.transform.ITransform2Sql;
import junit.framework.TestCase;

public class SimpleExecutoOracleTest extends TestCase
{

    public void testAll()
    {
        Configuration config = new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration();
        OracleDdl ddl = new OracleDdl(config);
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder(config);
        ITransactionManager trans = new TransactionManager(DataSourceFactory.getDataSource(config), factory);
        ITransform2Sql form = factory.getTransform();
        TableInfo table = factory.getTableInfo(SimpleSqlEntity.class);

        SimpleExecutorTestUtil.rebuild(ddl, table);
        SimpleExecutorTestUtil.testUpdate(trans, form, table);
        SimpleExecutorTestUtil.testUpdateBatch(trans, form, table);
        SimpleExecutorTestUtil.testUpdatePrepared(trans, form, table);
        SimpleExecutorTestUtil.testQuery(trans, form, table);
        SimpleExecutorTestUtil.testQuery1(trans, form, table);
        SimpleExecutorTestUtil.testQuery2(trans, form, table);
    }

}
