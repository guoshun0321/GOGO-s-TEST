package jetsennet.jsmp.nav.media.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.config.SysConfig;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;

public class NavBusinessDal
{

	private static final DataCacheOp cache = DataCacheOp.getInstance();

	/** 
	 * 获取顶级栏目
	 * 
	 * @return
	 */
	public static final List<ColumnEntity> getTopColumns()
	{
		// 获取顶级字段的键
		List<String> topKeys = cache.getT(ColumnCache.topColumn());
		return getColumnsByAssetId(topKeys);
	}

	private static final List<ColumnEntity> getColumnsByAssetId(List<String> assetIds)
	{
		if (assetIds == null || assetIds.isEmpty())
		{
			return new ArrayList<>(0);
		}
		List<ColumnEntity> retval = null;

		// 所有字段
		List<String> keyStrs = new ArrayList<>(assetIds.size());
		for (String assetId : assetIds)
		{
			keyStrs.add(ColumnCache.columnKey(assetId));
		}
		Map<String, Object> tempMap = cache.gets(keyStrs);

		Set<String> keys = tempMap.keySet();
		retval = new ArrayList<ColumnEntity>(keys.size());
		for (String key : keyStrs)
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
	 * 获取所有的子栏目
	 * 
	 * @param column
	 * @return
	 */
	public static final List<ColumnEntity> subColumns(ColumnEntity column)
	{
		String key = ColumnCache.subColumn(column.getAssetId(), column.getRegionCode());
		List<String> keys = cache.getT(key);
		return getColumnsByAssetId(keys);
	}

	/**
	 * 获取栏目对应的图片
	 * 
	 * @param column
	 * @return
	 */
	public static final List<PictureEntity> columnPicturs(ColumnEntity column)
	{
		String key = ColumnCache.columnPicKey(column.getAssetId());
		return cache.getT(key);
	}

	/**
	 * 根据assetId获取栏目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public static final ColumnEntity getColumnByAssetId(String assetId)
	{
		return cache.getT(ColumnCache.columnKey(assetId));
	}

	/**
	 * 获取栏目下的节目AssetId
	 * 
	 * @param columnId
	 * @return
	 */
	public static final List<String> columnProgramIds(String assetId)
	{
		return cache.getT(ProgramCache.columnPgm(assetId));
	}

	/**
	 * 根据assetId获取节目信息
	 * 
	 * @param assetId
	 * @return
	 */
	public static final ProgramEntity getProgramByAssetId(String assetId)
	{
		return cache.getT(ProgramCache.programAsset(assetId));
	}

	/**
	 * 获取所有节目信息
	 * 
	 * @param pgmAssetIds
	 * @return
	 */
	public static final List<ProgramEntity> getPrograms(List<String> pgmAssetIds)
	{
		if (pgmAssetIds == null || pgmAssetIds.isEmpty())
		{
			return null;
		}
		List<String> keys = new ArrayList<>(pgmAssetIds.size());
		for (String pgmAssetId : pgmAssetIds)
		{
			keys.add(ProgramCache.programAsset(pgmAssetId));
		}
		Map<String, Object> pgmMap = cache.gets(keys);

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
	public static final List<ProgramEntity> getSubPrograms(String pAssetId)
	{
		List<ProgramEntity> retval = null;

		List<String> subAssetIds = cache.getT(ProgramCache.subPgm(pAssetId));
		if (subAssetIds != null && !subAssetIds.isEmpty())
		{
			List<String> subKeys = new ArrayList<>(subAssetIds.size());
			for (String subAssetId : subAssetIds)
			{
				subKeys.add(ProgramCache.programAsset(subAssetId));
			}

			if (subKeys != null)
			{
				Map<String, Object> tempMap = cache.gets(subKeys);
				Set<String> keys = tempMap.keySet();
				retval = new ArrayList<ProgramEntity>(keys.size());
				for (String key : keys)
				{
					Object obj = tempMap.get(key);
					if (obj != null && obj instanceof ProgramEntity)
					{
						retval.add((ProgramEntity) obj);
					}
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
	public static final Object getPgmBase(int pgmId)
	{
		return cache.getT(ProgramCache.pgmBaseKey(pgmId));
	}

	/**
	 * 获取节目的图片
	 * @param pgmId
	 * @return
	 */
	public static final List<FileItemEntity> getPgmPictures(int pgmId)
	{
		List<FileItemEntity> retval = cache.getT(ProgramCache.pgmPictureKey(pgmId));
		return retval == null ? new ArrayList<FileItemEntity>(0) : retval;
	}

	/**
	 * 获取节目的文件
	 * 
	 * @param pgmId
	 * @return
	 */
	public static final List<FileItemEntity> getPgmItems(int pgmId)
	{
		List<FileItemEntity> retval = cache.getT(ProgramCache.pgmFileItemKey(pgmId));
		return retval == null ? new ArrayList<FileItemEntity>(0) : retval;
	}

	/**
	 * 获取创作者
	 * 
	 * @param pgmId
	 * @return
	 */
	public static final List<CreatorEntity> getCreators(int pgmId)
	{
		return cache.getT(ProgramCache.pgmCreatorKey(pgmId));
	}

	/**
	 * 根据assetId获取FileItem
	 * 
	 * @param assetId
	 * @return
	 */
	public static final FileItemEntity getFileItemByAssetId(String assetId)
	{
		return cache.getT(ProgramCache.pgmFileItemAsset(assetId));
	}

	/**
	 * 根据assetId获取PlayBillItem
	 * 
	 * @param assetId
	 * @return
	 */
	public static final PlaybillItemEntity getPlayBillItemByAssetId(String assetId)
	{
		return cache.getT(PlaybillCache.playbillItemKey(assetId));
	}

	/**
	 * 添加用于和SM系统通讯的TOKEN
	 * 
	 * @param playUrl
	 */
	public static final String addSMKey(String playUrl)
	{
		String token = UUID.randomUUID().toString();
		String key = SysConfig.selectionStartKey(token);
		cache.putTimeout(key, playUrl, Config.SM_TIMEOUT);
		return token;
	}

	/**
	 * 获取指定区域和语言的ChannelId
	 * 
	 * @param region
	 * @param lang
	 * @return
	 */
	public static final List<String> getChannelIds(String region, String lang)
	{
		String key = ChannelCache.channelIndex(region, lang);
		List<String> retval = cache.getT(key);
		return retval == null ? new ArrayList<String>(0) : retval;
	}

	/**
	 * 获取频道列表
	 * 
	 * @param chIds
	 * @return
	 */
	public static final List<ChannelEntity> getChannels(List<String> chAssetIds)
	{
		List<ChannelEntity> retval = null;
		if (chAssetIds != null)
		{
			List<String> chKeys = new ArrayList<String>(chAssetIds.size());
			for (String chAssetId : chAssetIds)
			{
				chKeys.add(ChannelCache.channelKey(chAssetId));
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
		return retval == null ? new ArrayList<ChannelEntity>(0) : retval;
	}

	/**
	 * 获取物理频道
	 * 
	 * @param chlId
	 * @return
	 */
	public static final List<PhysicalChannelEntity> getPhysicalChannels(String chlAssetId)
	{
		List<PhysicalChannelEntity> retval = cache.getT(ChannelCache.physicalChannelKey(chlAssetId));
		return retval == null ? new ArrayList<PhysicalChannelEntity>(0) : retval;
	}

	/**
	 * 获取PlayBillItem列表
	 * 
	 * @param chlAssetId 频道ID
	 * @param day 日期
	 * @return
	 */
	public static final List<String> getPlayBillItemIds(String chlAssetId, long day)
	{
		List<String> retval = null;
		String pbAssetId = cache.get(PlaybillCache.channelPlaybill(chlAssetId, day), true);
		if (pbAssetId != null)
		{
			PlaybillEntity pb = cache.getT(PlaybillCache.playbillKey(pbAssetId));
			if (pb != null)
			{
				retval = cache.getT(PlaybillCache.playbillItemList(pb.getAssetId()));
			}
		}
		return retval == null ? new ArrayList<String>(0) : retval;
	}

}
