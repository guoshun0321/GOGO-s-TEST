package jetsennet.jsmp.nav.syn.cache;

import java.util.Iterator;
import java.util.List;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheCreator extends DataSynCache<CreatorEntity>
{

	@Override
	protected String genKey(CreatorEntity obj)
	{
		return CachedKeyUtil.pgmCreatorKey(obj.getPgmId());
	}

	@Override
	public void insert(CreatorEntity obj)
	{
		add2CachedSet(genKey(obj), obj);
	}

	@Override
	public void update(CreatorEntity obj)
	{
		this.delete(obj);
		this.insert(obj);
	}

	@Override
	public void delete(CreatorEntity obj)
	{
		String key = genKey(obj);
		Object cachedValue = cache.get(key, true);
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = null;
		}
		else
		{
			List<CreatorEntity> lst = (List<CreatorEntity>) cachedValue;
			Iterator<CreatorEntity> it = lst.iterator();
			while (it.hasNext())
			{
				CreatorEntity creator = it.next();
				if (creator.getId().equals(obj.getId()))
				{
					it.remove();
				}
			}
		}
		add2CachedSet(key, obj);
	}

}
