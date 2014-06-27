package jetsennet.jsmp.nav.service.a7;

import static jetsennet.jsmp.nav.syn.CachedKeyUtil.channel2pchannel;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.channelIndex;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.columnAssetKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.columnKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmBaseKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.physicalChannelKeys;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.programKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;
import jetsennet.jsmp.nav.util.UncheckedNavException;

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
	public static final Map<String, Object> getColumns(List<Integer> columnIds)
	{
		if (columnIds != null && !columnIds.isEmpty())
		{
			List<String> tempKeys = columnKey(columnIds);
			return cache.gets(tempKeys);
		}
		return new HashMap<String, Object>(0);
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
	public static final Map<String, Object> getPrograms(List<Integer> pgmIds)
	{
		if (pgmIds == null || pgmIds.isEmpty())
		{
			return null;
		}
		return cache.gets(programKeys(pgmIds));
	}

	/**
	 * 获取子节目
	 * @param pgmId
	 * @return
	 */
	public static final Map<String, Object> getSubPrograms(int pgmId)
	{
		List<Integer> progIds = null;
		Pgm2PgmEntity p2p = cache.get(CachedKeyUtil.pgm2pgmKey(pgmId));
		String p2pDesc = p2p.getRelDesc();
		if (p2pDesc != null && !p2pDesc.isEmpty())
		{
			String[] subs = p2pDesc.split(":");
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
		return cache.gets(CachedKeyUtil.programKeys(progIds));
	}

	/**
	 * 获取节目基本信息
	 * @param pgmId
	 * @return
	 */
	public static final PgmBaseEntity getPgmBase(int pgmId)
	{
		return cache.get(pgmBaseKey(pgmId));
	}

	/**
	 * 获取节目的图片
	 * @param pgmId
	 * @return
	 */
	public static final List<PictureEntity> getPgmPictures(int pgmId)
	{
		return cache.getList(CachedKeyUtil.pgmPictureKey(pgmId));
	}

	/**
	 * 获取节目的文件
	 * 
	 * @param pgmId
	 * @return
	 */
	public static final List<FileItemEntity> getPgmItems(int pgmId)
	{
		return cache.getList(CachedKeyUtil.pgmFileItemKey(pgmId));
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
	 * @return
	 */
	public static final Map<String, Object> getTopColumns()
	{
		List<Integer> topKeys = cache.getListInt(CachedKeyUtil.topColumn());
		if (topKeys == null)
		{
			throw new UncheckedNavException("获取顶级栏目列表失败！");
		}
		List<String> topKeyStrs = CachedKeyUtil.columnKey(topKeys);
		return cache.gets(topKeyStrs);
	}

	/**
	 * 获取指定区域和语言的id
	 * 
	 * @param region
	 * @param lang
	 * @return
	 */
	public static final List<Integer> getChannelIds(String region, String lang)
	{
		return cache.getListInt(channelIndex(region, lang));
	}

	/**
	 * 获取频道列表
	 * 
	 * @param chIds
	 * @return
	 */
	public static final Map<String, Object> getChannels(List<Integer> chIds)
	{
		if (chIds == null)
		{
			return null;
		}
		List<String> chKeys = new ArrayList<String>(chIds.size());
		for (Integer chId : chIds)
		{
			chKeys.add(CachedKeyUtil.channelKey(chId));
		}
		return cache.gets(chKeys);
	}

	/**
	 * 获取物理频道
	 * 
	 * @param chlId
	 * @return
	 */
	public static final Map<String, Object> getPhysicalChannels(int chlId)
	{
		Map<String, Object> retval = null;
		List<Integer> pchlIds = cache.getListInt(channel2pchannel(chlId));
		if (pchlIds != null)
		{
			List<String> pkeys = physicalChannelKeys(pchlIds);
			retval = cache.gets(pkeys);
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
		return retval;
	}

	public static final PlaybillItemEntity getPalyBillItem(int itemId)
	{
		return cache.get(CachedKeyUtil.playbillItemKey(itemId));
	}
}
