package jetsennet.jsmp.nav.syn.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePicture extends DataSynCache<PictureEntity>
{

	@Override
	protected String genKey(PictureEntity obj)
	{
		return null;
		// TODO 图片处理
		//		return CachedKeyUtil.pgmPictureKey(obj.getPgmId());
	}

	@Override
	public void insert(PictureEntity obj)
	{
		String key = genKey(obj);
		Object cachedValue = cache.get(key, true);

		if (cachedValue != null && cachedValue instanceof List)
		{
			// 从集合中移除key
			List<PictureEntity> lst = (List<PictureEntity>) cachedValue;
			Iterator<PictureEntity> it = lst.iterator();
			while (it.hasNext())
			{
				PictureEntity pic = it.next();
				if (pic.getPicId().equals(obj.getPicId()))
				{
					it.remove();
				}
			}
		}
		else
		{
			cachedValue = new ArrayList<>();
		}
		((List<PictureEntity>) cachedValue).add(obj);
		cache.put(key, cachedValue);
	}

	@Override
	public void update(PictureEntity obj)
	{
		this.delete(obj);
		this.insert(obj);
	}

	@Override
	public void delete(PictureEntity obj)
	{
		String key = genKey(obj);
		Object cachedValue = cache.get(key, true);

		if (cachedValue != null && cachedValue instanceof List)
		{
			List<PictureEntity> lst = (List<PictureEntity>) cachedValue;
			Iterator<PictureEntity> it = lst.iterator();
			while (it.hasNext())
			{
				PictureEntity item = it.next();
				if (item.getAssetId().equals(obj.getAssetId()))
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
