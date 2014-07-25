package jetsennet.orm.transaction.auto;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import junit.framework.TestCase;

public class ClassWrapperTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testWrap()
    {
        TransedClass trans1 = ClassWrapper.wrap(TransedClass.class);

        SqlSessionFactory factory = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.oracle.properties"));
        TransedClass trans2 = ClassWrapper.wrap(TransedClass.class, factory);

        assertEquals("jdbc:sqlserver://192.168.8.43:1433;DatabaseName=GUOXIANG_TEST", trans1.trans1());
        assertEquals("jdbc:oracle:thin:@192.168.8.35:1521:orcl", trans2.trans2());
    }

    public void testWrapTrans()
    {
    }

}
