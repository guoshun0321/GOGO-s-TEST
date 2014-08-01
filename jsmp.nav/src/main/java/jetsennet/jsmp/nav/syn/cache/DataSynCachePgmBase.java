package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePgmBase extends DataSynCache<PgmBase9Entity>
{

    @Override
    protected String genKey(PgmBase9Entity obj)
    {
		return CachedKeyUtil.pgmBaseKey(obj.getPgmId());
    }

}
