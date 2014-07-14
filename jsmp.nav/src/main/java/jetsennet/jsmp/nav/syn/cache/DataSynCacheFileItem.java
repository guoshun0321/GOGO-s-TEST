package jetsennet.jsmp.nav.syn.cache;

import java.util.Iterator;
import java.util.List;

import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheFileItem extends DataSynCache<FileItemEntity>
{

	@Override
	protected String genKey(FileItemEntity obj)
	{
		return CachedKeyUtil.pgmFileItemKey(obj.getPgmId());
	}

	@Override
	public void insert(FileItemEntity obj)
	{
		add2CachedSet(genKey(obj), obj);
		cache.put(CachedKeyUtil.pgmFileItemAsset(obj.getAssetId()), obj);
	}

	@Override
	public void update(FileItemEntity obj)
	{
		this.delete(obj);
		this.insert(obj);
	}

	@Override
	public void delete(FileItemEntity obj)
	{
		String key = genKey(obj);
		Object cachedValue = cache.get(key, true);
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = null;
		}
		else
		{
			List<FileItemEntity> lst = (List<FileItemEntity>) cachedValue;
			Iterator<FileItemEntity> it = lst.iterator();
			while (it.hasNext())
			{
				FileItemEntity item = it.next();
				if (item.getAssetId().equals(obj.getAssetId()))
				{
					it.remove();
				}
			}
		}
		add2CachedSet(key, obj);
		cache.del(CachedKeyUtil.pgmFileItemAsset(obj.getAssetId()));
	}

}
