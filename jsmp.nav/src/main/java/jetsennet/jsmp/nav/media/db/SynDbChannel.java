package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.media.cache.ChannelCache;

public class SynDbChannel implements ISynDb
{

	@Override
	public void syn() throws Exception
	{
		List<ChannelEntity> channels = AbsDal.dal.queryAllBusinessObjs(ChannelEntity.class);
		// 将所有频道按地区和语言进行分类
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

		// 处理物理频道
		List<PhysicalChannelEntity> pchannels = AbsDal.dal.queryAllBusinessObjs(PhysicalChannelEntity.class);
		Map<String, List<PhysicalChannelEntity>> pchlMap = new LinkedHashMap<>();
		for (PhysicalChannelEntity pchannel : pchannels)
		{
			String chlAssetId = pchannel.getChlAssetId();
			String key = ChannelCache.physicalChannelKey(chlAssetId);
			List<PhysicalChannelEntity> lst = pchlMap.get(key);
			if (lst == null)
			{
				lst = new ArrayList<>();
				pchlMap.put(key, lst);
			}
			lst.add(pchannel);
		}
		ChannelCache.insertPhysicalChannelMap(pchlMap);
	}

}
