package jetsennet.jsmp.nav.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.media.db.DataSourceManager;
import jetsennet.orm.annotation.Column;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.util.SafeDateFormater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestObjGenUtil1
{

	private static int globalId = 1;

	private static int activeT = -1;

	private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 100, 1, TimeUnit.SECONDS, queue);

	private static final SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	private static final Session session = factory.openSession();

	private static AtomicInteger columnId = new AtomicInteger(1);

	private static AtomicInteger pgmId = new AtomicInteger(1);

	private static AtomicInteger channelId = new AtomicInteger(1);

	private static AtomicInteger pchannelId = new AtomicInteger(1);

	private static AtomicInteger pbId = new AtomicInteger(1);

	private static AtomicInteger pbiId = new AtomicInteger(1);

	private static AtomicInteger pgmBaseId = new AtomicInteger(1);

	private static AtomicInteger creatorId = new AtomicInteger(1);

	private static AtomicInteger authId = new AtomicInteger(1);

	private static AtomicInteger picId = new AtomicInteger(1);

	private static AtomicInteger fileId = new AtomicInteger(1);

	private static AtomicInteger prodId = new AtomicInteger(1);

	private static final Logger logger = LoggerFactory.getLogger(TestObjGenUtil1.class);

	public static <T> List<T> genObj(Class<T> clz, int num, Map<String, Object> special, AtomicInteger index) throws Exception
	{
		List<T> retval = new ArrayList<T>(num);
		Field[] fs = clz.getDeclaredFields();
		for (int i = 0; i < num; i++)
		{
			int seq = index.getAndIncrement();
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
							//							f.set(t, UUID.randomUUID().toString());
							f.set(t, clz.getName() + "_" + f.getName() + "_UUID_" + seq);
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
							f.set(t, seq);
						}
						else if (fType == String.class)
						{
							f.set(t, columnValue + seq);
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

	public static void builderCache(int baseColumn) throws Exception
	{

		try
		{
			// top column
			List<ColumnEntity> level1Chs = new ArrayList<ColumnEntity>(baseColumn);
			Map<String, Object> special = new HashMap<String, Object>();
			clearMap(special);
			special.put("PARENT_ID", 0);
			special.put("PARENT_ASSETID", "");
			level1Chs.addAll(TestObjGenUtil1.genObj(ColumnEntity.class, baseColumn, special, columnId));
			multiInsert(level1Chs);

			List<ColumnEntity> level2Chs = new ArrayList<ColumnEntity>(baseColumn * 10);
			clearMap(special);
			for (ColumnEntity level1Ch : level1Chs)
			{
				special.put("PARENT_ID", level1Ch.getColumnId());
				special.put("PARENT_ASSETID", level1Ch.getAssetId());
				level2Chs.addAll(TestObjGenUtil1.genObj(ColumnEntity.class, 3, special, columnId));
			}
			multiInsert(level2Chs);

			List<ColumnEntity> level3Chs = new ArrayList<ColumnEntity>(baseColumn * 30);
			clearMap(special);
			for (ColumnEntity level2Ch : level2Chs)
			{
				special.put("PARENT_ID", level2Ch.getColumnId());
				special.put("PARENT_ASSETID", level2Ch.getAssetId());
				level3Chs.addAll(TestObjGenUtil1.genObj(ColumnEntity.class, 3, special, columnId));
			}
			multiInsert(level3Chs);

			// PictureEntity
			List<ColumnEntity> columns = new ArrayList<>(20);
			columns.addAll(level1Chs);
			columns.addAll(level2Chs);
			columns.addAll(level3Chs);
			List<PictureEntity> pics = new ArrayList<>(40);
			for (ColumnEntity column : columns)
			{
				clearMap(special);
				special.put("OBJ_ID", Integer.toString(column.getColumnId()));
				special.put("OBJ_ASSETID", column.getAssetId());
				pics.addAll(TestObjGenUtil1.genObj(PictureEntity.class, 2, special, picId));
			}
			multiInsert(pics);

			List<ProgramEntity> progs = new ArrayList<ProgramEntity>(baseColumn * 500);
			List<ProgramEntity> prog4Chls = new ArrayList<ProgramEntity>(baseColumn * 500);
			clearMap(special);
			for (ColumnEntity level3Ch : level3Chs)
			{
				special.put("CONTENT_TYPE", 9); // 综艺
				special.put("COLUMN_ID", level3Ch.getColumnId());
				special.put("COLUMN_ASSETID", level3Ch.getAssetId());
				progs.addAll(TestObjGenUtil1.genObj(ProgramEntity.class, 2, special, pgmId));

				special.put("CONTENT_TYPE", 16); // 频道
				prog4Chls.addAll(TestObjGenUtil1.genObj(ProgramEntity.class, 1, special, pgmId));
			}
			multiInsert(progs);
			multiInsert(prog4Chls);

			List<ProgramEntity> prog2prog = new ArrayList<ProgramEntity>(baseColumn * 250);
			clearMap(special);
			for (ProgramEntity prog : progs)
			{
				special.put("CONTENT_TYPE", 9); // 综艺
				special.put("PARENT_ID", prog.getPgmId());
				special.put("COLUMN_ID", prog.getColumnId());
				special.put("COLUMN_ASSETID", prog.getAssetId());
				List<ProgramEntity> tempProgs = TestObjGenUtil1.genObj(ProgramEntity.class, 2, special, pgmId);
				prog2prog.addAll(tempProgs);
			}
			multiInsert(prog2prog);

			List<ProgramEntity> allProgs = new ArrayList<ProgramEntity>(baseColumn * 350);
			allProgs.addAll(progs);
			allProgs.addAll(prog4Chls);
			allProgs.addAll(prog2prog);

			int progSize = allProgs.size();
			List<PgmBase9Entity> pgmBases = new ArrayList<PgmBase9Entity>(progSize);
			List<DescauthorizeEntity> authors = new ArrayList<DescauthorizeEntity>(progSize);
			List<CreatorEntity> creators = new ArrayList<CreatorEntity>(progSize * 8);
			List<FileItemEntity> files = new ArrayList<FileItemEntity>(progSize * 4);
			for (ProgramEntity prog : allProgs)
			{
				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				pgmBases.addAll(TestObjGenUtil1.genObj(PgmBase9Entity.class, 1, special, pgmBaseId));

				clearMap(special);
				special.put("ROLE_MODE", "43");
				special.put("PGM_ID", prog.getPgmId());
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "44");
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "45");
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "46");
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "42");
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 2, special, creatorId));
				special.put("ROLE_MODE", "48");
				creators.addAll(TestObjGenUtil1.genObj(CreatorEntity.class, 2, special, creatorId));

				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				authors.addAll(TestObjGenUtil1.genObj(DescauthorizeEntity.class, 1, special, authId));

				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("VIDEO_QUALITY", 2);
				special.put("FILE_TYPE", 50);
				files.addAll(TestObjGenUtil1.genObj(FileItemEntity.class, 2, special, fileId));

				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("VIDEO_QUALITY", 2);
				special.put("FILE_TYPE", 201);
				files.addAll(TestObjGenUtil1.genObj(FileItemEntity.class, 2, special, fileId));

			}
			multiInsert(pgmBases);
			multiInsert(creators);
			multiInsert(authors);
			multiInsert(files);

			List<ChannelEntity> chls = new ArrayList<ChannelEntity>(prog4Chls.size());
			List<PhysicalChannelEntity> phys = new ArrayList<PhysicalChannelEntity>(prog4Chls.size() * 15);
			for (ProgramEntity prog : prog4Chls)
			{
				clearMap(special);
				special.put("ASSET_ID", prog.getAssetId());
				ChannelEntity ch = TestObjGenUtil1.genObj(ChannelEntity.class, 1, special, channelId).get(0);
				chls.add(ch);

				clearMap(special);
				special.put("CHL_ID", ch.getChlId());
				phys.addAll(TestObjGenUtil1.genObj(PhysicalChannelEntity.class, 15, special, pchannelId));
			}
			multiInsert(chls);
			multiInsert(phys);

			// playbill
			Date now = new Date();
			List<PlaybillEntity> bills = new ArrayList<PlaybillEntity>(chls.size() * 2);
			for (ChannelEntity channel : chls)
			{
				clearMap(special);
				special.put("CHL_ID", channel.getChlId());
				special.put("CHL_ASSETID", channel.getAssetId());
				special.put("PLAY_DATE", now);
				bills.addAll(TestObjGenUtil1.genObj(PlaybillEntity.class, 1, special, pbId));

				clearMap(special);
				special.put("CHL_ID", channel.getChlId());
				special.put("CHL_ASSETID", channel.getAssetId());
				special.put("PLAY_DATE", DateUtil.preDate(now, 1));
				bills.addAll(TestObjGenUtil1.genObj(PlaybillEntity.class, 1, special, pbId));
			}
			multiInsert(bills);

			// playbillitem
			List<PlaybillItemEntity> items = new ArrayList<PlaybillItemEntity>(bills.size() * 12);
			for (PlaybillEntity bill : bills)
			{
				special = new HashMap<String, Object>();
				special.put("PB_ID", bill.getPbId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				items.addAll(TestObjGenUtil1.genObj(PlaybillItemEntity.class, 12, special, pbiId));
			}
			multiInsert(items);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	private static final void clearMap(Map<String, Object> special)
	{
		special.clear();
		special.put("ASSET_ID", "UUID");
		special.put("LANGUAGE_CODE", "zh_CN");
		special.put("REGION_CODE", "");
	}

	public static void multiInsert(List<?> objs)
	{
		Session session = DataSourceManager.MEDIA_FACTORY.openSession();
		for (Object obj : objs)
		{
			session.insert(obj, false);
		}
	}

	/**
	 * 清理所有数据
	 */
	public static void cleanAll()
	{
		Session session = DataSourceManager.MEDIA_FACTORY.openSession();
		session.openConnection();
		session.deleteAll("NS_CHANNEL");
		session.deleteAll("NS_COLUMN");
		session.deleteAll("NS_COLUMN2RELATERULE");
		session.deleteAll("NS_CREATOR");
		session.deleteAll("NS_CTRLWORD");
		session.deleteAll("NS_DESCAUTHORIZE");
		session.deleteAll("NS_FILEITEM");
		session.deleteAll("NS_PGM2PRODUCT");
		session.deleteAll("NS_PGMBASE_10");
		session.deleteAll("NS_PGMBASE_11");
		session.deleteAll("NS_PGMBASE_12");
		session.deleteAll("NS_PGMBASE_13");
		session.deleteAll("NS_PGMBASE_14");
		session.deleteAll("NS_PGMBASE_15");
		session.deleteAll("NS_PGMBASE_16");
		session.deleteAll("NS_PGMBASE_9");
		session.deleteAll("NS_PHYSICALCHANNEL");
		session.deleteAll("NS_PICTURE");
		session.deleteAll("NS_PLAYBILL");
		session.deleteAll("NS_PLAYBILLITEM");
		session.deleteAll("NS_PRODUCT");
		session.deleteAll("NS_PROGRAM");
		session.deleteAll("NS_RELATEBLACK");
		session.deleteAll("NS_RELATECOLUMN");
		session.closeConnection();
	}

	public static void main(String[] args) throws Exception
	{
		TestObjGenUtil1.cleanAll();
		TestObjGenUtil1.builderCache(2);
	}
}
