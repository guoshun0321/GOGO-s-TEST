package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCachePicture extends DataSynCache<PictureEntity>
{

    @Override
    protected String genKey(PictureEntity obj)
    {
        return CachedKeyUtil.pgmPicture(obj.getPicId());
    }

    @Override
    public void insert(PictureEntity obj)
    {
        super.insert(obj);
        this.add2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getPicId());
    }

    @Override
    public void delete(PictureEntity obj)
    {
        super.delete(obj);
        this.del2CachedSet(CachedKeyUtil.pgmPictures(obj.getPgmId()), obj.getPicId());
    }

}
