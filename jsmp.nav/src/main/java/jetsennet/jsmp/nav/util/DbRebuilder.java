package jetsennet.jsmp.nav.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static void rebuild()
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
		List<Class<?>> clss = ClassUtil.getClasses(PACKAGE_PATH);
		for (Class<?> cls : clss)
		{
			if (cls.isAnnotationPresent(Table.class))
			{
				TableInfo info = factory.getTableInfo(cls);
				logger.debug("初始化表：" + info.getTableName());
				//				ddl.delete(info.getTableName());
				ddl.create(info);
			}
		}
	}

	public static void main(String[] args)
	{
		DbRebuilder.rebuild();
	}

}
