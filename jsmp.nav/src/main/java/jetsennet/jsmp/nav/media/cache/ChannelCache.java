package jetsennet.jsmp.nav.media.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jsmp.nav.entity.ChannelEntity;

public class ChannelCache extends AbsCache
{

	public static void insert(ChannelEntity channel)
	{
		cache.put(channelKey(channel.getAssetId()), channel);
	}

	public static void insertChannelList(Map<String, List<String>> channelMap)
	{
		Set<String> keys = channelMap.keySet();
		for (String key : keys)
		{
			cache.put(key, channelMap.get(key));
		}
	}

	public static final String channelKey(String assetId)
	{
		return "CHL$" + assetId;
	}

	public static final String channelIndex(String region, String lang)
	{
		return "CHLLIST$" + region + "$" + lang;
	}

	public static final String physicalChannelKey(String assetId)
	{
		return "PCHL$" + assetId;
	}

}
