package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePChannel extends DataSynCache<PhysicalChannelEntity>
{

    @Override
    protected String genKey(PhysicalChannelEntity obj)
    {
        return CachedKeyUtil.physicalChannelKey(obj.getPhychlId());
    }

    @Override
    public void insert(PhysicalChannelEntity obj)
    {
        super.insert(obj);
        this.add2CachedSet(CachedKeyUtil.channel2pchannel(obj.getChlId()), obj.getPhychlId());
    }

    @Override
    public void update(PhysicalChannelEntity obj)
    {
        super.update(obj);
    }

    @Override
    public void delete(PhysicalChannelEntity obj)
    {
        super.delete(obj);
        this.del2CachedSet(CachedKeyUtil.channel2pchannel(obj.getChlId()), obj.getPhychlId());
    }

}
