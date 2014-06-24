package jetsennet.jsmp.nav.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.syn.DataSynchronizedFromDb;
import jetsennet.jsmp.nav.syn.cache.DataSyn4Cache;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.annotation.Column;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.util.SafeDateFormater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestObjGenUtil
{

	private static int globalId = 1;

	private static int activeT = -1;

	private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 100, 1, TimeUnit.SECONDS, queue);

	private static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	private static final Session session = factory.openSession();

	private static final Logger logger = LoggerFactory.getLogger(TestObjGenUtil.class);

	public static <T> List<T> genObj(Class<T> clz, int num, Map<String, Object> special) throws Exception
	{
		List<T> retval = new ArrayList<T>(num);
		Field[] fs = clz.getDeclaredFields();
		for (int i = 0; i < num; i++)
		{
			T t = clz.newInstance();
			for (Field f : fs)
			{
				Column id = f.getAnnotation(Column.class);
				f.setAccessible(true);
				if (id != null)
				{
					String columnValue = id.value();
					if (special != null && special.containsKey(columnValue))
					{
						if (special.get(columnValue).equals("UUID"))
						{
							f.set(t, UUID.randomUUID().toString());
						}
						else
						{
							f.set(t, special.get(columnValue));
						}
					}
					else
					{
						Class fType = f.getType();
						if (fType == int.class || fType == long.class)
						{
							f.set(t, globalId++);
						}
						else if (fType == String.class)
						{
							f.set(t, columnValue + globalId++);
						}
						else if (fType == Date.class)
						{
							f.set(t, SafeDateFormater.parse("1987-01-02 11:12:23"));
						}
						else
						{
							logger.error("unsupported type : " + fType + ", class : " + clz);
						}
					}
				}
			}
			retval.add(t);
		}

		return retval;
	}

	public static void builderCache() throws Exception
	{

		Session session = null;
		try
		{
			// clean
			DataCacheOp.getInstance().deleteAll();

			// column
			List<ColumnEntity> level1Chs = new ArrayList<ColumnEntity>(10);
			Map<String, Object> special = new HashMap<String, Object>();
			special.put("PARENT_ID", 0);
			special.put("PARENT_ASSETID", "");
			special.put("ASSET_ID", "UUID");
			special.put("LANGUAGE_CODE", "zh_CN");
			special.put("REGION_CODE", "");
			level1Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));
			multiInsert(level1Chs);

			List<ColumnEntity> level2Chs = new ArrayList<ColumnEntity>(10 * 10);
			for (ColumnEntity level1Ch : level1Chs)
			{
				special = new HashMap<String, Object>();
				special.put("PARENT_ID", level1Ch.getColumnId());
				special.put("PARENT_ASSETID", level1Ch.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				level2Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));
			}
			multiInsert(level2Chs);

			List<ColumnEntity> level3Chs = new ArrayList<ColumnEntity>(10 * 10 * 10);
			for (ColumnEntity level2Ch : level2Chs)
			{
				special = new HashMap<String, Object>();
				special.put("PARENT_ID", level2Ch.getColumnId());
				special.put("PARENT_ASSETID", level2Ch.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				level3Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special));
			}
			multiInsert(level3Chs);

			List<ProgramEntity> progs = new ArrayList<ProgramEntity>(10 * 10 * 10 * 10);
			for (ColumnEntity level2Ch : level2Chs)
			{
				special = new HashMap<String, Object>();
				special.put("COLUMN_ID", level2Ch.getColumnId());
				special.put("COLUMN_ASSETID", level2Ch.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				progs.addAll(TestObjGenUtil.genObj(ProgramEntity.class, 10, special));
			}
			multiInsert(progs);

			List<PgmBaseEntity> pgmBases = new ArrayList<PgmBaseEntity>(10000);
			List<CreatorEntity> creators = new ArrayList<CreatorEntity>(10000);
			List<DescauthorizeEntity> authors = new ArrayList<DescauthorizeEntity>(10000);
			List<PictureEntity> pics = new ArrayList<PictureEntity>(50000);
			List<FileItemEntity> files = new ArrayList<FileItemEntity>(50000);
			List<ChannelEntity> chls = new ArrayList<ChannelEntity>(10000);
			List<PhysicalChannelEntity> phys = new ArrayList<PhysicalChannelEntity>(10000);
			for (ProgramEntity prog : progs)
			{
				special = new HashMap<String, Object>();
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				pgmBases.addAll(TestObjGenUtil.genObj(PgmBaseEntity.class, 1, special));

				special = new HashMap<String, Object>();
				special.put("PGM_ID", prog.getPgmId());
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 1, special));

				special = new HashMap<String, Object>();
				special.put("PGM_ID", prog.getPgmId());
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				authors.addAll(TestObjGenUtil.genObj(DescauthorizeEntity.class, 1, special));

				special = new HashMap<String, Object>();
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				pics.addAll(TestObjGenUtil.genObj(PictureEntity.class, 5, special));

				special = new HashMap<String, Object>();
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				special.put("VIDEO_QUALITY", 2);
				files.addAll(TestObjGenUtil.genObj(FileItemEntity.class, 5, special));

				special = new HashMap<String, Object>();
				special.put("ASSET_ID", prog.getAssetId());
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				ChannelEntity ch = TestObjGenUtil.genObj(ChannelEntity.class, 1, special).get(0);
				chls.add(ch);

				special = new HashMap<String, Object>();
				special.put("CHL_ID", ch.getChlId());
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				phys.addAll(TestObjGenUtil.genObj(PhysicalChannelEntity.class, 1, special));
			}
			multiInsert(pgmBases);
			multiInsert(creators);
			multiInsert(authors);
			multiInsert(pics);
			multiInsert(files);
			multiInsert(chls);
			multiInsert(phys);

			// playbill
			List<PlaybillEntity> bills = new ArrayList<PlaybillEntity>(10);
			special = new HashMap<String, Object>();
			special.put("ASSET_ID", "UUID");
			special.put("LANGUAGE_CODE", "zh_CN");
			special.put("REGION_CODE", "");
			bills.addAll(TestObjGenUtil.genObj(PlaybillEntity.class, 10, special));
			multiInsert(bills);

			List<PlaybillItemEntity> items = new ArrayList<PlaybillItemEntity>(1000);
			for (PlaybillEntity bill : bills)
			{
				special = new HashMap<String, Object>();
				special.put("PB_ID", bill.getPbId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				items.addAll(TestObjGenUtil.genObj(PlaybillItemEntity.class, 100, special));
			}
			multiInsert(items);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		DataCacheOp.getInstance().shutdown();
	}

	private static int count = 1;

	public static void multiInsert(List objs)
	{
		for (Object obj : objs)
		{
			DataSyn4Cache.getHandle(obj).insert(obj);
			if ((count++ % 100) == 0)
			{
				logger.info("count : " + count);
			}
		}
	}

	/**
	 * 清理所有数据
	 */
	public static void cleanAll()
	{
		SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
		Session session = factory.openSession();
		session.openConnection();
		session.deleteAll("NS_CHANNEL");
		session.deleteAll("NS_COLUMN");
		session.deleteAll("NS_COLUMN2RELATERULE");
		session.deleteAll("NS_CREATOR");
		session.deleteAll("NS_CTRLWORD");
		session.deleteAll("NS_DESCAUTHORIZE");
		session.deleteAll("NS_FILEITEM");
		session.deleteAll("NS_PGM2PGM");
		session.deleteAll("NS_PGM2PRODUCT");
		session.deleteAll("NS_PGMBASE");
		session.deleteAll("NS_PHYSICALCHANNEL");
		session.deleteAll("NS_PICTURE");
		session.deleteAll("NS_PLAYBILL");
		session.deleteAll("NS_PLAYBILLITEM");
		session.deleteAll("NS_PRODUCT");
		session.deleteAll("NS_PROGRAM");
		session.deleteAll("NS_RELATEBLACK");
		session.deleteAll("NS_RELATECOLUMN");
		session.closeConnection();
		DataCacheOp.getInstance().deleteAll();
		DataCacheOp.getInstance().shutdown();
	}

	public static void main(String[] args) throws Exception
	{
		TestObjGenUtil.builderCache();
	}
}
