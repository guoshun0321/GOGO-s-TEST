package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheFileItem extends DataSynCache<FileItemEntity>
{

	@Override
	protected String genKey(FileItemEntity obj)
	{
		return CachedKeyUtil.pgmFileItem(obj.getId());
	}

	@Override
	public void insert(FileItemEntity obj)
	{
		super.insert(obj);
		cache.put(CachedKeyUtil.pgmFileItemAsset(obj.getAssetId()), obj.getId());
		this.add2CachedSet(CachedKeyUtil.pgmFileItems(obj.getPgmId()), obj.getId());
	}

	@Override
	public void delete(FileItemEntity obj)
	{
		this.del2CachedSet(CachedKeyUtil.pgmFileItems(obj.getPgmId()), obj.getId());
		cache.del(CachedKeyUtil.pgmFileItemAsset(obj.getAssetId()));
		super.delete(obj);
	}

}
