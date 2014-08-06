package jetsennet.jsmp.nav.media.db;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.Column2RelateruleEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.Pgm2ProductEntity;
import jetsennet.jsmp.nav.entity.PgmBase10Entity;
import jetsennet.jsmp.nav.entity.PgmBase11Entity;
import jetsennet.jsmp.nav.entity.PgmBase12Entity;
import jetsennet.jsmp.nav.entity.PgmBase13Entity;
import jetsennet.jsmp.nav.entity.PgmBase14Entity;
import jetsennet.jsmp.nav.entity.PgmBase15Entity;
import jetsennet.jsmp.nav.entity.PgmBase16Entity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProductEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.entity.RelateBlackEntity;
import jetsennet.jsmp.nav.entity.RelateColumnEntity;
import jetsennet.orm.configuration.ConfigurationBuilderProp;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceManager
{

	public static final SqlSessionFactory MEDIA_FACTORY;

	public static final SqlSessionFactory NAV_FACTORY;

	private static final Logger logger = LoggerFactory.getLogger(DataSourceManager.class);

	static
	{
		MEDIA_FACTORY = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.mysql.media.properties").genConfiguration());
		NAV_FACTORY = SqlSessionFactoryBuilder.builder(new ConfigurationBuilderProp("/dbconfig.mysql.nav.properties").genConfiguration());
		// 注册CLASS信息
		MEDIA_FACTORY.getTableInfo(ChannelEntity.class);
		MEDIA_FACTORY.getTableInfo(Column2RelateruleEntity.class);
		MEDIA_FACTORY.getTableInfo(ColumnEntity.class);
		MEDIA_FACTORY.getTableInfo(CreatorEntity.class);
		MEDIA_FACTORY.getTableInfo(DescauthorizeEntity.class);
		MEDIA_FACTORY.getTableInfo(FileItemEntity.class);
		MEDIA_FACTORY.getTableInfo(Pgm2PgmEntity.class);
		MEDIA_FACTORY.getTableInfo(Pgm2ProductEntity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase9Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase10Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase11Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase12Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase13Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase14Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase15Entity.class);
		MEDIA_FACTORY.getTableInfo(PgmBase16Entity.class);
		MEDIA_FACTORY.getTableInfo(PhysicalChannelEntity.class);
		MEDIA_FACTORY.getTableInfo(PictureEntity.class);
		MEDIA_FACTORY.getTableInfo(PlaybillEntity.class);
		MEDIA_FACTORY.getTableInfo(PlaybillItemEntity.class);
		MEDIA_FACTORY.getTableInfo(ProductEntity.class);
		MEDIA_FACTORY.getTableInfo(ProgramEntity.class);
		MEDIA_FACTORY.getTableInfo(RelateBlackEntity.class);
		MEDIA_FACTORY.getTableInfo(RelateColumnEntity.class);
		if (Config.ISDEBUG)
		{
			logger.debug("nav_url : " + NAV_FACTORY.getConfig().configFile);
		}
	}

}
