package jetsennet.jsmp.nav.media.syn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.media.cache.ChannelCache;
import jetsennet.jsmp.nav.media.cache.ColumnCache;
import jetsennet.jsmp.nav.media.cache.ProgramCache;
import jetsennet.jsmp.nav.media.db.AbsDal;
import jetsennet.jsmp.nav.media.db.ProgramDal;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;

public class SynFromDb
{

	private SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;

	private Session session = null;

	private static final Logger logger = LoggerFactory.getLogger(SynFromDb.class);

	public SynFromDb()
	{
		// TODO Auto-generated constructor stub
	}

	public void syn()
	{
		session = getSession();

		try
		{
			this.synColumn();
			this.synProgram();
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		finally
		{
			factory.closeSession();
		}
	}

	private Session getSession()
	{
		return factory.openSession();
	}

	private void synColumn()
	{
		String sql = "SELECT * FROM NS_COLUMN";
		List<ColumnEntity> columns = session.query(sql, ColumnEntity.class);
		// 顶级栏目ID
		List<String> tops = new ArrayList<>(20);
		// 栏目之间的关系
		Map<String, List<String>> subMap = new LinkedHashMap<>();
		for (ColumnEntity column : columns)
		{
			// 更新栏目数据
			ColumnCache.insert(column);
			int parentId = column.getParentId();
			String pAssetId = column.getParentAssetid();
			if (parentId == 0)
			{
				tops.add(column.getAssetId());
			}
			else
			{
				String key = ColumnCache.subColumn(pAssetId, column.getRegionCode());
				List<String> subs = subMap.get(key);
				if (subs == null)
				{
					subs = new ArrayList<>();
					subMap.put(key, subs);
				}
				subs.add(column.getAssetId());
			}
		}
		// 更新顶级栏目
		ColumnCache.insertTopColumn(tops);
		// 更新栏目之间的关系
		Set<String> keys = subMap.keySet();
		for (String key : keys)
		{
			ColumnCache.insertSub(key, subMap.get(key));
		}

		// 栏目图片
		sql = "SELECT * FROM NS_PICTURE";
		List<PictureEntity> pics = session.query(sql, PictureEntity.class);
		Map<String, List<String>> picMap = new LinkedHashMap<>();
		for (PictureEntity pic : pics)
		{
			ColumnCache.insertPic(pic);
			String key = ColumnCache.picColumn(pic.getObjId());
			List<String> subPics = picMap.get(key);
			if (subPics == null)
			{
				subPics = new ArrayList<>();
			}
			subPics.add(pic.getAssetId());
		}

		// 栏目和图片的关系
		keys = picMap.keySet();
		for (String key : keys)
		{
			ColumnCache.insertPic2Column(key, picMap.get(key));
		}

	}

	private void synProgram() throws Exception
	{
		ProgramDal dal = new ProgramDal();
		List<Integer> pgmIds = dal.getPgmIds();
		if (pgmIds == null)
		{
			return;
		}
		for (Integer pgmId : pgmIds)
		{
			List<Object> objs = dal.getProgram(pgmId);
			if (objs != null && objs.size() > 0 && objs.get(0) != null && objs.get(0) instanceof ProgramEntity)
			{
				ProgramCache.delete((ProgramEntity) objs.get(0));
				ProgramCache.insert(objs);
			}
		}

		List<Pgm2PgmEntity> pgm2pgms = AbsDal.dal.queryAllBusinessObjs(Pgm2PgmEntity.class);
		for (Pgm2PgmEntity pgm2pgm : pgm2pgms)
		{
			ProgramCache.insertPgm2Pgm(pgm2pgm);
		}
	}

	private void synChannel() throws Exception
	{
		List<ChannelEntity> channels = AbsDal.dal.queryAllBusinessObjs(ChannelEntity.class);
		Map<String, List<String>> channelMap = new LinkedHashMap<>();
		for (ChannelEntity channel : channels)
		{
			ChannelCache.insert(channel);
			String key = ChannelCache.channelIndex(channel.getRegionCode(), channel.getLanguageCode());
			List<String> lst = channelMap.get(key);
			if (lst == null)
			{
				lst = new ArrayList<>();
				channelMap.put(key, lst);
			}
			lst.add(channel.getAssetId());
		}
		ChannelCache.insertChannelList(channelMap);
	}

}
