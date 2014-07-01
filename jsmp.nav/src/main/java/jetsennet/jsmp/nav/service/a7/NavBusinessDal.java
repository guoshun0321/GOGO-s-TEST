package jetsennet.jsmp.nav.service.a7;

import static jetsennet.jsmp.nav.syn.CachedKeyUtil.channel2pchannel;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.channelIndex;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.columnAssetKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.columnKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmBaseKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.physicalChannelKeys;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.programKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class NavBusinessDal
{

	private static final DataCacheOp cache = DataCacheOp.getInstance();

	/**
	 * 获取子栏目ID
	 * 
	 * @param column
	 * @return
	 */
	public static final List<Integer> subColumnIds(ColumnEntity column)
	{
		return cache.getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
	}

	/**
	 * 根据assetId获取栏目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public static final ColumnEntity getColumnByAssetId(String assetId)
	{
		return cache.get(columnKey(cache.getInt(columnAssetKey(assetId))));
	}

	/**
	 * 批量获取栏目信息
	 * @param columnIds
	 * @return
	 */
	public static final List<ColumnEntity> getColumns(List<Integer> columnIds)
	{
		List<ColumnEntity> retval = null;
		if (columnIds != null && !columnIds.isEmpty())
		{
			List<String> tempKeys = columnKey(columnIds);
			Map<String, Object> colMap = cache.gets(tempKeys);
			retval = new ArrayList<ColumnEntity>(colMap.size());
			for (String tempKey : tempKeys)
			{
				Object obj = colMap.get(tempKey);
				if (obj != null && obj instanceof ColumnEntity)
				{
					retval.add((ColumnEntity) obj);
				}
			}
		}
		return retval != null ? retval : new ArrayList<ColumnEntity>(0);
	}

	/**
	 * 获取栏目下的节目ID
	 * 
	 * @param columnId
	 * @return
	 */
	public static final List<Integer> columnProgramIds(int columnId)
	{
		return cache.getListInt(CachedKeyUtil.columnPgm(columnId));
	}

	/**
	 * 获取所有节目信息
	 * 
	 * @param pgmIds
	 * @return
	 */
	public static final List<ProgramEntity> getPrograms(List<Integer> pgmIds)
	{
		if (pgmIds == null || pgmIds.isEmpty())
		{
			return null;
		}
		List<String> keys = programKeys(pgmIds);
		Map<String, Object> pgmMap = cache.gets(programKeys(pgmIds));
		List<ProgramEntity> retval = new ArrayList<ProgramEntity>(pgmMap.size());
		for (String key : keys)
		{
			Object obj = pgmMap.get(key);
			if (obj != null && obj instanceof ProgramEntity)
			{
				retval.add((ProgramEntity) obj);
			}
		}
		return retval;
	}

	/**
	 * 获取子节目
	 * @param pgmId
	 * @return
	 */
	public static final List<ProgramEntity> getSubPrograms(int pgmId)
	{
		List<ProgramEntity> retval = null;
		List<Integer> progIds = null;
		Pgm2PgmEntity p2p = cache.get(CachedKeyUtil.pgm2pgmKey(pgmId), true);
		if (p2p != null)
		{
			String p2pDesc = p2p.getRelDesc();
			if (p2pDesc != null && !p2pDesc.isEmpty())
			{
				String[] subs = p2pDesc.split(";");
				if (subs != null && subs.length > 0)
				{
					progIds = new ArrayList<Integer>(subs.length);
					for (String sub : subs)
					{
						String[] temp = sub.split(",");
						if (temp.length == 2)
						{
							progIds.add(Integer.valueOf(temp[0]));
						}
					}
				}
			}

			Map<String, Object> tempMap = cache.gets(CachedKeyUtil.programKeys(progIds));
			Set<String> keys = tempMap.keySet();
			retval = new ArrayList<ProgramEntity>(keys.size());
			for (String key : keys)
			{
				Object obj = tempMap.get(key);
				if (obj == null && obj instanceof ProgramEntity)
				{
					retval.add((ProgramEntity) obj);
				}
			}
		}
		return retval != null ? retval : new ArrayList<ProgramEntity>(0);
	}

	/**
	 * 获取节目基本信息
	 * @param pgmId
	 * @return
	 */
	public static final PgmBaseEntity getPgmBase(int pgmId)
	{
		return cache.get(pgmBaseKey(pgmId), true);
	}

	/**
	 * 获取节目的图片
	 * @param pgmId
	 * @return
	 */
	public static final List<PictureEntity> getPgmPictures(int pgmId)
	{
		List<PictureEntity> retval = cache.getList(CachedKeyUtil.pgmPictureKey(pgmId));
		if (retval == null)
		{
			retval = new ArrayList<PictureEntity>(0);
		}
		return retval;
	}

	/**
	 * 获取节目的文件
	 * 
	 * @param pgmId
	 * @return
	 */
	public static final List<FileItemEntity> getPgmItems(int pgmId)
	{
		List<FileItemEntity> retval = cache.getList(CachedKeyUtil.pgmFileItemKey(pgmId));
		if (retval == null)
		{
			retval = new ArrayList<FileItemEntity>(0);
		}
		return retval;
	}

	/**
	 * 根据assetId获取节目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public static final ProgramEntity getProgramByAssetId(String assetId)
	{
		ProgramEntity retval = null;
		Integer pgmId = cache.get(CachedKeyUtil.programAsset(assetId), true);
		if (pgmId != null)
		{
			retval = cache.get(CachedKeyUtil.programKey(pgmId));
		}
		return retval;
	}

	/**
	 * 获取创作者
	 * 
	 * @param pgmId
	 * @return
	 */
	public static final List<CreatorEntity> getCreators(int pgmId)
	{
		return cache.getList(CachedKeyUtil.pgmCreatorKey(pgmId));
	}

	/**
	 * 根据assetId获取FileItem
	 * 
	 * @param assetId
	 * @return
	 */
	public static final FileItemEntity getFileItemByAssetId(String assetId)
	{
		return cache.get(CachedKeyUtil.pgmFileItemAsset(assetId));
	}

	/**
	 * 根据assetId获取PlayBillItem
	 * 
	 * @param assetId
	 * @return
	 */
	public static final PlaybillItemEntity getPlayBillItemByAssetId(String assetId)
	{
		int itemId = cache.get(CachedKeyUtil.playbillItemListAsset(assetId));
		return cache.get(CachedKeyUtil.playbillItemKey(itemId));
	}

	/**
	 * 添加用于和SM系统通讯的TOKEN
	 * 
	 * @param playUrl
	 */
	public static final String addSMKey(String playUrl)
	{
		String token = UUID.randomUUID().toString();
		String key = CachedKeyUtil.selectionStartKey(token);
		cache.putTimeout(key, playUrl, Config.SM_TIMEOUT);
		return token;
	}

	/** 
	 * 获取顶级栏目
	 * 
	 * @return
	 */
	public static final List<ColumnEntity> getTopColumns()
	{
		List<ColumnEntity> retval = null;

		List<Integer> topKeys = cache.getListInt(CachedKeyUtil.topColumn());
		List<String> topKeyStrs = CachedKeyUtil.columnKey(topKeys);
		Map<String, Object> tempMap = cache.gets(topKeyStrs);

		Set<String> keys = tempMap.keySet();
		retval = new ArrayList<ColumnEntity>(keys.size());
		for (String key : keys)
		{
			Object obj = tempMap.get(key);
			if (obj != null && obj instanceof ColumnEntity)
			{
				retval.add((ColumnEntity) obj);
			}
		}
		return retval == null ? new ArrayList<ColumnEntity>(0) : retval;
	}

	/**
	 * 获取指定区域和语言的ChannelId
	 * 
	 * @param region
	 * @param lang
	 * @return
	 */
	public static final List<Integer> getChannelIds(String region, String lang)
	{
		List<Integer> retval = cache.getListInt(channelIndex(region, lang));
		return retval == null ? new ArrayList<Integer>(0) : retval;
	}

	/**
	 * 获取频道列表
	 * 
	 * @param chIds
	 * @return
	 */
	public static final List<ChannelEntity> getChannels(List<Integer> chIds)
	{
		List<ChannelEntity> retval = null;
		if (chIds != null)
		{
			List<String> chKeys = new ArrayList<String>(chIds.size());
			for (Integer chId : chIds)
			{
				chKeys.add(CachedKeyUtil.channelKey(chId));
			}
			Map<String, Object> chlMap = cache.gets(chKeys);
			if (chlMap != null)
			{
				retval = new ArrayList<ChannelEntity>(chlMap.size());
				for (String chKey : chKeys)
				{
					Object obj = chlMap.get(chKey);
					if (obj != null && obj instanceof ChannelEntity)
					{
						retval.add((ChannelEntity) obj);
					}
				}
			}
		}
		if (retval == null)
		{
			return new ArrayList<ChannelEntity>(0);
		}
		return retval;
	}

	/**
	 * 获取物理频道
	 * 
	 * @param chlId
	 * @return
	 */
	public static final List<PhysicalChannelEntity> getPhysicalChannels(int chlId)
	{
		List<PhysicalChannelEntity> retval = null;
		Map<String, Object> tempMap = null;
		List<Integer> pchlIds = cache.getListInt(channel2pchannel(chlId));
		if (pchlIds != null)
		{
			List<String> pkeys = physicalChannelKeys(pchlIds);
			tempMap = cache.gets(pkeys);
			if (tempMap != null)
			{
				retval = new ArrayList<PhysicalChannelEntity>(tempMap.size());
				for (String pkey : pkeys)
				{
					Object obj = tempMap.get(pkey);
					if (obj != null && obj instanceof PhysicalChannelEntity)
					{
						retval.add((PhysicalChannelEntity) obj);
					}
				}
			}
		}
		if (retval == null)
		{
			retval = new ArrayList<PhysicalChannelEntity>();
		}
		return retval;
	}

	/**
	 * 获取PlayBillItem列表
	 * 
	 * @param chId
	 * @param day
	 * @return
	 */
	public static final List<Integer> getPlayBillItemIds(int chId, long day)
	{
		List<Integer> retval = null;
		Integer pbId = cache.get(CachedKeyUtil.channelPlaybill(chId, day), true);
		if (pbId != null)
		{
			PlaybillEntity pb = cache.get(CachedKeyUtil.playbillKey(pbId));
			if (pb != null)
			{
				retval = cache.getListInt(CachedKeyUtil.playbillItemList(pb.getPbId()));
			}
		}
		if (retval == null)
		{
			retval = new ArrayList<Integer>(0);
		}
		return retval;
	}

	/**
	 * 获取PlayBillItem
	 * 
	 * @param itemId
	 * @return
	 */
	public static final PlaybillItemEntity getPalyBillItem(int itemId)
	{
		return cache.get(CachedKeyUtil.playbillItemKey(itemId));
	}

}
