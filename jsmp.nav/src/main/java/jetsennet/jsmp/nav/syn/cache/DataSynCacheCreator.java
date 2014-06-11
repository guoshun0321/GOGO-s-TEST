package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheCreator extends DataSynCache<CreatorEntity>
{

    @Override
    protected String genKey(CreatorEntity obj)
    {
        return CachedKeyUtil.pgmCreatorKey(obj.getPgmId());
    }

}
