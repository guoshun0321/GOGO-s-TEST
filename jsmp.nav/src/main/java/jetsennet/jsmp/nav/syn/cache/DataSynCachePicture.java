package jetsennet.jsmp.nav.syn.cache;

import java.util.Iterator;
import java.util.List;

import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePicture extends DataSynCache<PictureEntity>
{

	@Override
	protected String genKey(PictureEntity obj)
	{
		return CachedKeyUtil.pgmPictureKey(obj.getPgmId());
	}

	@Override
	public void insert(PictureEntity obj)
	{
		add2CachedSet(genKey(obj), obj);
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
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = null;
		}
		else
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
		}
		add2CachedSet(key, obj);
	}

}
