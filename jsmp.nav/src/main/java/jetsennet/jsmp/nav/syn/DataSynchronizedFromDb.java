package jetsennet.jsmp.nav.syn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.dal.DataSourceManager;
import jetsennet.jsmp.nav.syn.db.DataSynDbResult;
import jetsennet.orm.session.Session;

public class DataSynchronizedFromDb
{

	private static final String[] tables = {
		"NS_CHANNEL",
		"NS_COLUMN",
		"NS_COLUMN2RELATERULE",
		"NS_CREATOR",
		"NS_CTRLWORD",
		"NS_DESCAUTHORIZE",
		"NS_FILEITEM",
		"NS_PGM2PGM",
		"NS_PGM2PRODUCT",
		"NS_PGMBASE",
		"NS_PHYSICALCHANNEL",
		"NS_PICTURE",
		"NS_PLAYBILL",
		"NS_PLAYBILLITEM",
		"NS_PRODUCT",
		"NS_PROGRAM",
		"NS_RELATEBLACK",
		"NS_RELATECOLUMN" };

	private static final Logger logger = LoggerFactory.getLogger(DataSynchronizedFromDb.class);

	public static void synFromDb()
	{
		Session session = DataSourceManager.MEDIA_FACTORY.openSession();
		for (String table : tables)
		{
			logger.debug("加载表：" + table);
			String sql = "SELECT * FROM " + table;
			Class<?> cls = DataSourceManager.MEDIA_FACTORY.getTableInfo(table).getCls();
			List<?> objs = session.query(sql, cls);
			for (Object obj : objs)
			{
				DataHandleUtil.updateCache(obj, DataSynDbResult.TYPE_INSERT, 1);
			}
		}
	}

}
