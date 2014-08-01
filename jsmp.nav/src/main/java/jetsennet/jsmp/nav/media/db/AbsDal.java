package jetsennet.jsmp.nav.media.db;

import jetsennet.frame.dataaccess.BaseDaoNew;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;

public class AbsDal
{

	public static SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	public static BaseDaoNew dal = new BaseDaoNew(factory.getConfig());;

	public AbsDal()
	{
	}

}
