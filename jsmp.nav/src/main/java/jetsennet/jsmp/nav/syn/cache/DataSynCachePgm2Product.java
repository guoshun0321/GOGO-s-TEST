package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.Pgm2ProductEntity;
import jetsennet.jsmp.nav.syn.AbsUpdateMediaInfoCache;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePgm2Product extends AbsUpdateMediaInfoCache<Pgm2ProductEntity>
{

    @Override
    public void insert(Pgm2ProductEntity obj)
    {
        this.add2CachedSet(CachedKeyUtil.productPgm(obj.getProductId()), obj.getPgmId());
    }

    @Override
    public void update(Pgm2ProductEntity obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Pgm2ProductEntity obj)
    {
        this.del2CachedSet(CachedKeyUtil.productPgm(obj.getProductId()), obj.getPgmId());
    }
}
