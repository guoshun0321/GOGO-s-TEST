package jetsennet.jsmp.nav.media.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.util.DateUtil;

public class PlaybillCache extends AbsCache
{

	public static void insertPlaybill(PlaybillEntity pb)
	{
		cache.put(playbillKey(pb.getAssetId()), pb);
	}

	public static void updatePlaybill(PlaybillEntity pb)
	{
		cache.put(playbillKey(pb.getAssetId()), pb);
		String key = channelPlaybill(pb.getChlAssetId(), pb.getPlayDate());
		cache.put(key, pb.getAssetId());
	}

	public static void deletePlaybill(PlaybillEntity pb)
	{
		cache.del(playbillKey(pb.getAssetId()));
		String key = channelPlaybill(pb.getChlAssetId(), pb.getPlayDate());
		cache.del(key);
	}

	public static void insertPlaybillItem(PlaybillItemEntity pbItem)
	{
		cache.put(playbillItemKey(pbItem.getAssetId()), pbItem);
	}

	public static void updatePlaybillItem(PlaybillItemEntity pbItem)
	{
		insertPlaybillItem(pbItem);
		String assetId = pbItem.getAssetId();
		String key = playbillItemList(pbItem.getPbAssetId());
		List<String> lst = cache.getT(key);
		if (lst == null)
		{
			lst = new ArrayList<>();
		}
		if (!lst.contains(assetId))
		{
			lst.add(pbItem.getAssetId());
		}
		cache.put(key, lst);
	}

	public static void deletePlaybillItem(PlaybillItemEntity pbItem)
	{
		String assetId = pbItem.getAssetId();
		cache.del(playbillItemKey(assetId));
		String key = playbillItemList(pbItem.getPbAssetId());
		List<String> lst = cache.getT(key);
		if (lst != null && !lst.contains(assetId))
		{
			lst.remove(assetId);
		}
		if (lst.isEmpty())
		{
			cache.del(key);
		}
		else
		{
			cache.put(key, lst);
		}
	}

	/**
	 * 频道的节目单
	 * 
	 * @param pbMap
	 */
	public static void insertChlPb(Map<String, String> pbMap)
	{
		Set<String> keys = pbMap.keySet();
		for (String key : keys)
		{
			cache.put(key, pbMap.get(key));
		}
	}

	/**
	 * 插入节目单和节目单元素的对于关系
	 * 
	 * @param pbItemMap
	 */
	public static void insertPbItemMap(Map<String, List<String>> pbItemMap)
	{
		Set<String> keys = pbItemMap.keySet();
		for (String key : keys)
		{
			cache.put(key, pbItemMap.get(key));
		}
	}

	public static PlaybillEntity getPlaybill(String assetId)
	{
		return cache.getT(playbillKey(assetId));
	}

	public static PlaybillItemEntity getPlaybillItem(String assetId)
	{
		return cache.getT(playbillItemKey(assetId));
	}

	public static final String playbillKey(String assetId)
	{
		return "PLAYBILL$" + assetId;
	}

	public static final String playbillItemKey(String assetId)
	{
		return "PLAYBILITEM$" + assetId;
	}

	/**
	 * 频道对应的节目单，按频道assetId和时间进行分类
	 * 
	 * @param chlAssetId
	 * @param time
	 * @return
	 */
	public static final String channelPlaybill(String chlAssetId, Date time)
	{
		return "CHL_PLAYBILL$" + chlAssetId + "$" + DateUtil.getPreTimeOfDay(time, 0);
	}

	public static final String channelPlaybill(String chlAssetId, long time)
	{
		return "CHL_PLAYBILL$" + chlAssetId + "$" + time;
	}

	/**
	 * 返回playbillItem的assetId的集合
	 * 
	 * @param assetId
	 * @return
	 */
	public static final String playbillItemList(String assetId)
	{
		return "PLAYBILITEMLIST$" + assetId;
	}

}
