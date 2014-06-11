package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.syn.AbsUpdateMediaInfoCache;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePgm2Pgm extends AbsUpdateMediaInfoCache<Pgm2PgmEntity>
{

    @Override
    public void insert(Pgm2PgmEntity obj)
    {
        this.cache.put(CachedKeyUtil.pgm2pgmKey(obj.getPgmId()), obj);
    }

    @Override
    public void update(Pgm2PgmEntity obj)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Pgm2PgmEntity obj)
    {
        this.cache.del(CachedKeyUtil.pgm2pgmKey(obj.getPgmId()));
    }

}
