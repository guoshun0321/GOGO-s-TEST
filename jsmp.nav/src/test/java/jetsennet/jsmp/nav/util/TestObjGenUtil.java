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

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBase10Entity;
import jetsennet.jsmp.nav.entity.PgmBase11Entity;
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

public class TestObjGenUtil
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

	private static final Logger logger = LoggerFactory.getLogger(TestObjGenUtil.class);

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
			// clean
			cleanAll();

			// 顶级栏目
			List<ColumnEntity> level1Chs = new ArrayList<ColumnEntity>(baseColumn);
			Map<String, Object> special = new HashMap<String, Object>();
			clearMap(special);
			special.put("PARENT_ID", 0);
			special.put("PARENT_ASSETID", "");
			level1Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, baseColumn, special, columnId));
			multiInsert(level1Chs);

			// 二级栏目
			List<ColumnEntity> level2Chs = new ArrayList<ColumnEntity>(baseColumn * 10);
			clearMap(special);
			for (ColumnEntity level1Ch : level1Chs)
			{
				special.put("PARENT_ID", level1Ch.getColumnId());
				special.put("PARENT_ASSETID", level1Ch.getAssetId());
				level2Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 10, special, columnId));
			}
			multiInsert(level2Chs);

			// 三级栏目
			List<ColumnEntity> level3Chs = new ArrayList<ColumnEntity>(baseColumn * 20);
			clearMap(special);
			for (ColumnEntity level2Ch : level2Chs)
			{
				special.put("PARENT_ID", level2Ch.getColumnId());
				special.put("PARENT_ASSETID", level2Ch.getAssetId());
				level3Chs.addAll(TestObjGenUtil.genObj(ColumnEntity.class, 2, special, columnId));
			}
			multiInsert(level3Chs);

			// 栏目图片
			List<PictureEntity> pics = new ArrayList<PictureEntity>(100);
			for (ColumnEntity column : level1Chs)
			{
				clearMap(special);
				special.put("OBJ_ID", Integer.toString(column.getColumnId()));
				special.put("OBJ_ASSETID", column.getAssetId());
				pics.addAll(TestObjGenUtil.genObj(PictureEntity.class, 3, special, picId));
			}
			for (ColumnEntity column : level2Chs)
			{
				clearMap(special);
				special.put("OBJ_ID", Integer.toString(column.getColumnId()));
				special.put("OBJ_ASSETID", column.getAssetId());
				pics.addAll(TestObjGenUtil.genObj(PictureEntity.class, 3, special, picId));
			}
			multiInsert(pics);

			// 节目
			List<ProgramEntity> progs9 = new ArrayList<ProgramEntity>(baseColumn * 500);
			List<ProgramEntity> progs10 = new ArrayList<ProgramEntity>(baseColumn * 500);
			List<ProgramEntity> progs11 = new ArrayList<ProgramEntity>(baseColumn * 500);
			List<ProgramEntity> prog4Chls = new ArrayList<ProgramEntity>(baseColumn * 500);
			clearMap(special);
			for (ColumnEntity level3Ch : level3Chs)
			{
				special.put("CONTENT_TYPE", 9); // 电影
				special.put("COLUMN_ID", level3Ch.getColumnId());
				special.put("COLUMN_ASSETID", level3Ch.getAssetId());
				progs9.addAll(TestObjGenUtil.genObj(ProgramEntity.class, 2, special, pgmId));

				special.put("CONTENT_TYPE", 10); // 电视剧
				special.put("COLUMN_ID", level3Ch.getColumnId());
				special.put("COLUMN_ASSETID", level3Ch.getAssetId());
				progs10.addAll(TestObjGenUtil.genObj(ProgramEntity.class, 2, special, pgmId));

				special.put("CONTENT_TYPE", 11); // 综艺
				special.put("COLUMN_ID", level3Ch.getColumnId());
				special.put("COLUMN_ASSETID", level3Ch.getAssetId());
				progs11.addAll(TestObjGenUtil.genObj(ProgramEntity.class, 2, special, pgmId));

				special.put("CONTENT_TYPE", 16); // 频道
				prog4Chls.addAll(TestObjGenUtil.genObj(ProgramEntity.class, 1, special, pgmId));
			}
			multiInsert(progs9);
			multiInsert(progs10);
			multiInsert(progs11);
			multiInsert(prog4Chls);

			// 子节目
			List<ProgramEntity> prog2prog = new ArrayList<ProgramEntity>(baseColumn * 250);
			//			List<Pgm2PgmEntity> pgm2pgm = new ArrayList<Pgm2PgmEntity>(baseColumn * 50);
			clearMap(special);
			for (ProgramEntity prog : progs10)
			{
				special.put("CONTENT_TYPE", 10); // 电视剧
				special.put("COLUMN_ID", 0);
				special.put("COLUMN_ASSETID", prog.getAssetId());
				List<ProgramEntity> tempProgs = TestObjGenUtil.genObj(ProgramEntity.class, 2, special, pgmId);
				prog2prog.addAll(tempProgs);

				StringBuilder sb = new StringBuilder();
				int rel = 0;
				for (ProgramEntity tempProg : tempProgs)
				{
					sb.append(tempProg.getPgmId()).append(",").append(rel).append(";");
					rel++;
					if (rel == 4)
					{
						rel = 10;
					}
				}
				sb.deleteCharAt(sb.length() - 1);
				//				pgm2pgm.add(new Pgm2PgmEntity(prog.getPgmId(), sb.toString()));
			}
			multiInsert(prog2prog);
			//			multiInsert(pgm2pgm);

			// 节目详细信息
			List<ProgramEntity> allProgs = new ArrayList<ProgramEntity>(baseColumn * 350);
			allProgs.addAll(progs9);
			allProgs.addAll(progs10);
			allProgs.addAll(progs11);
			//			allProgs.addAll(prog4Chls);
			allProgs.addAll(prog2prog);

			int progSize = allProgs.size();
			List<PgmBase9Entity> pgmBases9 = new ArrayList<PgmBase9Entity>(progSize);
			List<PgmBase10Entity> pgmBases10 = new ArrayList<PgmBase10Entity>(progSize);
			List<PgmBase11Entity> pgmBases11 = new ArrayList<PgmBase11Entity>(progSize);
			List<DescauthorizeEntity> authors = new ArrayList<DescauthorizeEntity>(progSize);
			List<CreatorEntity> creators = new ArrayList<CreatorEntity>(progSize * 8);

			List<FileItemEntity> files = new ArrayList<FileItemEntity>(progSize * 4);
			for (ProgramEntity prog : allProgs)
			{
				if (prog.getContentType() == 9)
				{
					clearMap(special);
					special.put("PGM_ID", prog.getPgmId());
					special.put("PGM_ASSETID", prog.getAssetId());
					pgmBases9.addAll(TestObjGenUtil.genObj(PgmBase9Entity.class, 1, special, pgmBaseId));
				}
				else if (prog.getContentType() == 10)
				{
					clearMap(special);
					special.put("PGM_ID", prog.getPgmId());
					special.put("PGM_ASSETID", prog.getAssetId());
					pgmBases10.addAll(TestObjGenUtil.genObj(PgmBase10Entity.class, 1, special, pgmBaseId));
				}
				else if (prog.getContentType() == 11)
				{
					clearMap(special);
					special.put("PGM_ID", prog.getPgmId());
					special.put("PGM_ASSETID", prog.getAssetId());
					pgmBases11.addAll(TestObjGenUtil.genObj(PgmBase11Entity.class, 1, special, pgmBaseId));
				}

				clearMap(special);
				special.put("ROLE_MODE", "43");
				special.put("PGM_ID", prog.getPgmId());
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "44");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "45");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "46");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 1, special, creatorId));
				special.put("ROLE_MODE", "42");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 2, special, creatorId));
				special.put("ROLE_MODE", "48");
				creators.addAll(TestObjGenUtil.genObj(CreatorEntity.class, 2, special, creatorId));

				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				authors.addAll(TestObjGenUtil.genObj(DescauthorizeEntity.class, 1, special, authId));

				// 文件
				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("VIDEO_QUALITY", 2);
				special.put("FILE_TYPE", 1);
				files.addAll(TestObjGenUtil.genObj(FileItemEntity.class, 2, special, fileId));

				// 图片
				clearMap(special);
				special.put("PGM_ID", prog.getPgmId());
				special.put("PGM_ASSETID", prog.getAssetId());
				special.put("VIDEO_QUALITY", 2);
				special.put("FILE_TYPE", 201);
				files.addAll(TestObjGenUtil.genObj(FileItemEntity.class, 2, special, fileId));

			}
			multiInsert(pgmBases9);
			multiInsert(pgmBases10);
			multiInsert(pgmBases11);
			multiInsert(creators);
			multiInsert(authors);
			multiInsert(files);

			// 频道
			List<ChannelEntity> chls = new ArrayList<ChannelEntity>(prog4Chls.size());
			List<PhysicalChannelEntity> phys = new ArrayList<PhysicalChannelEntity>(prog4Chls.size() * 15);
			for (ProgramEntity prog : prog4Chls)
			{
				clearMap(special);
				special.put("ASSET_ID", prog.getAssetId());
				ChannelEntity ch = TestObjGenUtil.genObj(ChannelEntity.class, 1, special, channelId).get(0);
				chls.add(ch);

				clearMap(special);
				special.put("CHL_ID", ch.getChlId());
				special.put("CHL_ASSETID", ch.getAssetId());
				phys.addAll(TestObjGenUtil.genObj(PhysicalChannelEntity.class, 2, special, pchannelId));
			}
			multiInsert(chls);
			multiInsert(phys);

			// 节目单
			Date now = new Date();
			List<PlaybillEntity> bills = new ArrayList<PlaybillEntity>(chls.size() * 2);
			for (ChannelEntity channel : chls)
			{
				clearMap(special);
				special.put("CHL_ID", channel.getChlId());
				special.put("CHL_ASSETID", channel.getAssetId());
				special.put("PLAY_DATE", now);
				bills.addAll(TestObjGenUtil.genObj(PlaybillEntity.class, 1, special, pbId));

				clearMap(special);
				special.put("CHL_ID", channel.getChlId());
				special.put("CHL_ASSETID", channel.getAssetId());
				special.put("PLAY_DATE", DateUtil.preDate(now, 1));
				bills.addAll(TestObjGenUtil.genObj(PlaybillEntity.class, 1, special, pbId));
			}
			multiInsert(bills);

			List<PlaybillItemEntity> items = new ArrayList<PlaybillItemEntity>(bills.size() * 105);
			for (PlaybillEntity bill : bills)
			{
				special = new HashMap<String, Object>();
				special.put("PB_ID", bill.getPbId());
				special.put("ASSET_ID", "UUID");
				special.put("LANGUAGE_CODE", "zh_CN");
				special.put("REGION_CODE", "");
				items.addAll(TestObjGenUtil.genObj(PlaybillItemEntity.class, 25, special, pbiId));
			}
			multiInsert(items);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		DataCacheOp.getInstance().shutdown();
	}

	private static final void insertPgm()
	{

	}

	private static final void clearMap(Map<String, Object> special)
	{
		special.clear();
		special.put("ASSET_ID", "UUID");
		special.put("LANGUAGE_CODE", "zh_CN");
		special.put("REGION_CODE", "");
	}

	private static int count = 1;

	public static void multiInsert(List objs)
	{
		SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
		Session session = factory.openSession();
		for (Object obj : objs)
		{
			session.insert(obj, false);
		}
		//		for (Object obj : objs)
		//		{
		//			DataSyn4Cache.getHandle(obj).insert(obj);
		//			if ((count++ % 100) == 0)
		//			{
		//				logger.info("count : " + count);
		//			}
		//		}
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
		session.deleteAll("NS_PGMBASE9");
		session.deleteAll("NS_PGMBASE10");
		session.deleteAll("NS_PGMBASE11");
		session.deleteAll("NS_PGMBASE12");
		session.deleteAll("NS_PGMBASE13");
		session.deleteAll("NS_PGMBASE14");
		session.deleteAll("NS_PGMBASE15");
		session.deleteAll("NS_PGMBASE16");
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
		TestObjGenUtil.builderCache(1);
	}
}
