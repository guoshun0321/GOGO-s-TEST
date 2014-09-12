package jetsennet.jsmp.nav.util;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.media.db.DataSourceManager;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;

public class ObjectInsertUtil
{

	public static void main(String[] args)
	{
		SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
		Session session = factory.openSession();

		ColumnEntity column = new ColumnEntity();
		column.setColumnId(1);
		column.setColumnName("我是名称");
		session.insert(column, false);
	}

}
