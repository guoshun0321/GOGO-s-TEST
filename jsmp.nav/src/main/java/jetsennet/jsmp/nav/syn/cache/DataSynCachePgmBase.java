package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePgmBase extends DataSynCache<PgmBaseEntity>
{

    @Override
    protected String genKey(PgmBaseEntity obj)
    {
        return CachedKeyUtil.pgmBaseKey(obj.getPgmId());
    }

}
