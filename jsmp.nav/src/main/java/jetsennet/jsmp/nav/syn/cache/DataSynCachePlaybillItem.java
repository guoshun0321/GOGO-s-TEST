package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePlaybillItem extends DataSynCache<PlaybillItemEntity>
{

	@Override
	protected String genKey(PlaybillItemEntity obj)
	{
		return CachedKeyUtil.playbillItemKey(obj.getPbiId());
	}

	@Override
	public void insert(PlaybillItemEntity obj)
	{
		super.insert(obj);
		cache.put(CachedKeyUtil.playbillItemListAsset(obj.getAssetId()), obj.getPbiId());
		this.add2CachedSet(CachedKeyUtil.playbillItemList(obj.getPbId()), obj.getPbiId());
	}

	@Override
	public void update(PlaybillItemEntity obj)
	{
		super.update(obj);
	}

	@Override
	public void delete(PlaybillItemEntity obj)
	{
		this.del2CachedSet(CachedKeyUtil.playbillItemList(obj.getPbId()), obj.getPbiId());
		cache.del(CachedKeyUtil.playbillItemListAsset(obj.getAssetId()));
		super.delete(obj);
	}

}
