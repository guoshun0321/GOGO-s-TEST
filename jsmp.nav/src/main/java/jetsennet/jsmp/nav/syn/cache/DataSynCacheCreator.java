package jetsennet.jsmp.nav.syn.cache;

import java.util.ArrayList;
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
		String key = genKey(obj);
		Object cachedValue = cache.get(key, true);

		if (cachedValue != null && cachedValue instanceof List)
		{
			// 从集合中移除key
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
		else
		{
			cachedValue = new ArrayList<>();
		}
		((List<CreatorEntity>) cachedValue).add(obj);
		cache.put(key, cachedValue);
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

		if (cachedValue != null && cachedValue instanceof List)
		{
			// 从集合中移除key
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
			if (lst.isEmpty())
			{
				cachedValue = null;
			}
		}
		else
		{
			cachedValue = null;
		}

		if (cachedValue != null)
		{
			cache.put(key, cachedValue);
		}
		else
		{
			cache.del(key);
		}
	}

}
