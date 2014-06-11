package jetsennet.jsmp.nav.syn.db;

import jetsennet.jsmp.nav.dal.DataSourceManager;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;

public class DataSynDb
{

    private static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

    public int insert(Object obj)
    {
        Session session = factory.openSession();
        return session.insert(obj);
    }

    public int update(Object obj)
    {
        Session session = factory.openSession();
        return session.update(obj);
    }

    public int delete(Object obj)
    {
        Session session = factory.openSession();
        return session.deleteByObj(obj);
    }
}
