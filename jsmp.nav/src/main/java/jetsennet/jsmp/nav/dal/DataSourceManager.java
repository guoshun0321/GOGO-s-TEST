package jetsennet.jsmp.nav.dal;

import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;

public class DataSourceManager
{

    public static final SqlSessionFactory MEDIA_FACTORY;

    public static final SqlSessionFactory NAV_FACTORY;

    static
    {
        MEDIA_FACTORY = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.mysql.media.properties").genConfiguration());
        NAV_FACTORY = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.mysql.nav.properties").genConfiguration());
    }

}
