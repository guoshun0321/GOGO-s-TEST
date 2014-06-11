package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheFileItem extends DataSynCache<CreatorEntity>
{

    @Override
    protected String genKey(CreatorEntity obj)
    {
        return CachedKeyUtil.pgmPicture(obj.getId());
    }

    @Override
    public void insert(CreatorEntity obj)
    {
        super.insert(obj);
        this.add2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getId());
    }

    @Override
    public void delete(CreatorEntity obj)
    {
        super.delete(obj);
        this.del2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getId());
    }

}
