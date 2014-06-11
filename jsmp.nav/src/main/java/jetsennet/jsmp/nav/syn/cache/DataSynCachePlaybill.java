package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;
import jetsennet.jsmp.nav.util.DateUtil;

public class DataSynCachePlaybill extends DataSynCache<PlaybillEntity>
{

    @Override
    protected String genKey(PlaybillEntity obj)
    {
        return CachedKeyUtil.playbillKey(obj.getPbId(), obj.getPlayDate().getTime());
    }

    @Override
    public void insert(PlaybillEntity obj)
    {
        // 添加节目单
        super.insert(obj);
        cache.put(CachedKeyUtil.channelPlaybill(obj.getChlId(), DateUtil.getPreTimeOfDay(obj.getPlayDate(), 0)), obj.getPbId());
    }

    @Override
    public void update(PlaybillEntity obj)
    {
        super.update(obj);
    }

    @Override
    public void delete(PlaybillEntity obj)
    {
        super.delete(obj);
        cache.del(CachedKeyUtil.channelPlaybill(obj.getChlId(), DateUtil.getPreTimeOfDay(obj.getPlayDate(), 0)));
    }

}
