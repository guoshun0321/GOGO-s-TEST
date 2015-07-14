package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheChannel extends DataSynCache<ChannelEntity>
{

	@Override
	protected String genKey(ChannelEntity obj)
	{
		return CachedKeyUtil.channelKey(obj.getChlId());
	}

	public void insert(ChannelEntity obj)
	{
		super.insert(obj);
		this.add2CachedSet(CachedKeyUtil.channelIndex(obj.getRegionCode(), obj.getLanguageCode()), obj.getChlId());
	}

	public void update(ChannelEntity obj)
	{
		String key = this.genKey(obj);
		cache.put(key, obj);
	}

	public void delete(ChannelEntity obj)
	{
		super.delete(obj);
		this.del2CachedSet(CachedKeyUtil.channelIndex(obj.getRegionCode(), obj.getLanguageCode()), Integer.valueOf(obj.getChlId()));
	}

}
