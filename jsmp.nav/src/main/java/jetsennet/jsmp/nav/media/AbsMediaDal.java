package jetsennet.jsmp.nav.media;

import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.session.SqlSessionFactory;

public abstract class AbsMediaDal
{

	protected static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

}
