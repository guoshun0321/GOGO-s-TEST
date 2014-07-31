package jetsennet.jsmp.nav.syn.db;

import jetsennet.orm.session.SqlSessionFactory;

public abstract class AbsDataSynDb
{

	protected static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

}
