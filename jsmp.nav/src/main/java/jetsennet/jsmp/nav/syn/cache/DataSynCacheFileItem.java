package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.CreatorEntity;
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
		this.add2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getId());
	}

	@Override
	public void delete(FileItemEntity obj)
	{
		super.delete(obj);
		this.del2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getId());
	}

}
