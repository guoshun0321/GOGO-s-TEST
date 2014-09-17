package jetsennet.jsmp.nav.media.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;

public class ChannelCache extends AbsCache
{

	public static void insert(ChannelEntity channel)
	{
		cache.put(channelKey(channel.getAssetId()), channel);
	}

	public static void update(ChannelEntity channel)
	{
		String assetId = channel.getAssetId();
		cache.put(channelKey(assetId), channel);
		String key = channelIndex(channel.getRegionCode(), channel.getLanguageCode());
		List<String> lst = cache.get(key);
		if (lst == null)
		{
			lst = new ArrayList<>();
		}
		if (!lst.contains(assetId))
		{
			lst.add(assetId);
		}
		cache.put(key, lst);
	}

	/**
	 * 插入物理频道
	 * 
	 * @param channel
	 */
	public static void insertPhsical(PhysicalChannelEntity phy)
	{
		String key = physicalChannelKey(phy.getChlAssetId());
		List<PhysicalChannelEntity> physicals = cache.get(key);
		if (physicals == null)
		{
			physicals = new ArrayList<>();
		}
		int size = physicals.size();
		boolean isUpdate = false;
		for (int i = 0; i < size; i++)
		{
			PhysicalChannelEntity temp = physicals.get(i);
			if (temp.getPhychlId() == phy.getPhychlId())
			{
				// 更新
				physicals.set(i, phy);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate)
		{
			physicals.add(phy);
		}
		cache.put(key, physicals);
	}

	/**
	 * 插入频道列表
	 * 
	 * @param channelMap
	 */
	public static void insertChannelList(Map<String, List<String>> channelMap)
	{
		Set<String> keys = channelMap.keySet();
		for (String key : keys)
		{
			cache.put(key, channelMap.get(key));
		}
	}

	/**
	 * 更新所有物理频道信息
	 * 
	 * @param pchlMap
	 */
	public static void insertPhysicalChannelMap(Map<String, List<PhysicalChannelEntity>> pchlMap)
	{
		Set<String> keys = pchlMap.keySet();
		for (String key : keys)
		{
			cache.put(key, pchlMap.get(key));
		}
	}

	public static final String channelKey(String assetId)
	{
		return "CHL$" + assetId;
	}

	/**
	 * 按地区和语言对频道进行分类，返回频道assetId集合
	 * 
	 * @param region
	 * @param lang
	 * @return
	 */
	public static final String channelIndex(String region, String lang)
	{
		return "CHLLIST$" + region + "$" + lang;
	}

	public static final String physicalChannelKey(String assetId)
	{
		return "CHL_PCHL$" + assetId;
	}

}
