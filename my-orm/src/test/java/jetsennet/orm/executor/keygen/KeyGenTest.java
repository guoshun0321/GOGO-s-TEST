package jetsennet.orm.executor.keygen;

import java.util.List;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.ddl.ConnectionUtil;
import jetsennet.orm.ddl.Ddl;
import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import junit.framework.TestCase;

public class KeyGenTest extends TestCase
{

    public void testKeyGenMySql()
    {
        System.out.println("MYSQL");
        IDdl ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.mysql.properties").genConfiguration());
        KeyGenUtil.genTableDb(ddl, KeyGenMySqlEntity.class);
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder("/dbconfig.mysql.properties");
        KeyGenUtil.testGenKey(factory, KeyGenMySqlEntity.class);
        KeyGenUtil.testGenKeyArray(factory, KeyGenMySqlEntity.class);
    }

    public void testKeyGenSqlServer()
    {
        System.out.println("SQL SERVER");
        IDdl ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.properties").genConfiguration());
        KeyGenUtil.genTableDb(ddl, KeyGenSqlServerEntity.class);
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder("/dbconfig.properties");
        KeyGenUtil.testGenKey(factory, KeyGenSqlServerEntity.class);
        KeyGenUtil.testGenKeyArray(factory, KeyGenSqlServerEntity.class);
    }

    public void testKeyGenOracle()
    {
        System.out.println("ORACLE");
        IDdl ddl = Ddl.getDdl(new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration());
        KeyGenUtil.genTableDb(ddl, KeyGenOracleEntity.class);
        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder("/dbconfig.oracle.properties");
        KeyGenUtil.testGenKey(factory, KeyGenOracleEntity.class);
        KeyGenUtil.testGenKeyArray(factory, KeyGenOracleEntity.class);

        seqTest(ddl, factory, KeyGenOracleEntity.class);
    }

    private void seqTest(IDdl dll, SqlSessionFactory factory, Class<?> cls)
    {
        Session session = factory.openSession();
        TableInfo table = session.getTableInfo(cls);

        try
        {
            String dropSql = "drop sequence SEQ_" + table.getTableName();
            ConnectionUtil.execute(new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration(), dropSql);
        }
        catch (Exception ex)
        {
            // ignore
        }
        String createSql = "create sequence SEQ_" + table.getTableName();
        ConnectionUtil.execute(new ConfigurationBuilderProp("/dbconfig.oracle.properties").genConfiguration(), createSql);

        List<FieldInfo> fields = table.getKeyFields();
        Object obj = KeyGen.genKey(table.getTableName(), fields.get(5), session);
        assertEquals(1l, obj);
    }

}
