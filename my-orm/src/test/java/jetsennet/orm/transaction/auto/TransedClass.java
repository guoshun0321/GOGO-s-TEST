package jetsennet.orm.transaction.auto;

import jetsennet.orm.annotation.Transactional;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactoryBuilder;

public class TransedClass
{

    @Transactional
    public String trans1()
    {
        Session session = SqlSessionFactoryBuilder.builder().openSession();
        return SqlSessionFactoryBuilder.builder().getConfig().connInfo.url;
    }

    @Transactional
    public String trans2()
    {
        Session session = SqlSessionFactoryBuilder.builder().openSession();
        return SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.oracle.properties")).getConfig().connInfo.url;
    }

}
