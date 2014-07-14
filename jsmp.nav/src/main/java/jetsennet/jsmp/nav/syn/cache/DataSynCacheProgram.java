package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheProgram extends DataSynCache<ProgramEntity>
{

	@Override
	protected String genKey(ProgramEntity obj)
	{
		return CachedKeyUtil.programKey(obj.getPgmId());
	}

	@Override
	public void insert(ProgramEntity obj)
	{
		super.insert(obj);
		cache.put(CachedKeyUtil.programAsset(obj.getAssetId()), obj.getPgmId());
		this.add2CachedSet(CachedKeyUtil.columnPgm(obj.getColumnId()), obj.getPgmId());
	}

	@Override
	public void update(ProgramEntity obj)
	{
		super.update(obj);
	}

	@Override
	public void delete(ProgramEntity obj)
	{
		super.delete(obj);
		cache.del(CachedKeyUtil.programAsset(obj.getAssetId()));
		this.del2CachedSet(CachedKeyUtil.columnPgm(obj.getColumnId()), obj.getPgmId());
	}

}
