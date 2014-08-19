package jetsennet.jsmp.nav.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.entity.PgmBase11Entity;
import jetsennet.jsmp.nav.entity.PgmBase12Entity;
import jetsennet.jsmp.nav.entity.PgmBase13Entity;
import jetsennet.jsmp.nav.entity.PgmBase14Entity;
import jetsennet.jsmp.nav.entity.PgmBase15Entity;
import jetsennet.jsmp.nav.entity.PgmBase16Entity;
import jetsennet.orm.annotation.Table;
import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.ddl.Ddl;
import jetsennet.orm.ddl.IDdl;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.tableinfo.TableInfo;

public class DbRebuilder
{

	private static String PACKAGE_PATH = "jetsennet.jsmp.nav.entity";

	private static final Logger logger = LoggerFactory.getLogger(DbRebuilder.class);

	public static void rebuild() throws Exception
	{
		Configuration config = new ConfigurationBuilderProp("/dbconfig.mysql.media.properties").genConfiguration();
		IDdl ddl = Ddl.getDdl(config);
		SqlSessionFactory factory = SqlSessionFactoryBuilder.builder(config);

		List<String> tables = ddl.listTable(null);
		logger.debug("删除数据库表");
		for (String table : tables)
		{
			logger.debug(String.format("删除表：%s", table));
			ddl.delete(table);
		}

		logger.debug(String.format("数据库初始化开始，URL：%s", config.connInfo.url));
		//		List<Class<?>> clss = ClassUtil.getClasses(PACKAGE_PATH);
		//		for (Class<?> cls : clss)
		//		{
		//			if (cls.isAnnotationPresent(Table.class))
		//			{
		//				TableInfo info = factory.getTableInfo(cls);
		//				logger.debug("初始化表：" + info.getTableName());
		//				ddl.create(info);
		//			}
		//		}
		BDBFileParse reader = new BDBFileParse();
		reader.parseFolder("./dbscript/scheme");

		TableInfo info = factory.getTableInfo(PgmBase11Entity.class);
		logger.debug("初始化表：" + info.getTableName());
		info = factory.getTableInfo(PgmBase12Entity.class);
		logger.debug("初始化表：" + info.getTableName());
		info = factory.getTableInfo(PgmBase13Entity.class);
		logger.debug("初始化表：" + info.getTableName());
		info = factory.getTableInfo(PgmBase14Entity.class);
		logger.debug("初始化表：" + info.getTableName());
		info = factory.getTableInfo(PgmBase15Entity.class);
		logger.debug("初始化表：" + info.getTableName());
		info = factory.getTableInfo(PgmBase16Entity.class);
		logger.debug("初始化表：" + info.getTableName());
	}

	public static void main(String[] args) throws Exception
	{
		DbRebuilder.rebuild();
	}

}
