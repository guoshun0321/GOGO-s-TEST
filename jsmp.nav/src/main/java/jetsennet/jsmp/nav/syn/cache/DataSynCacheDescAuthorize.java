package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheDescAuthorize extends DataSynCache<DescauthorizeEntity>
{

    @Override
    protected String genKey(DescauthorizeEntity obj)
    {
        return CachedKeyUtil.pgmDescAuthorize(obj.getPgmId());
    }

}
