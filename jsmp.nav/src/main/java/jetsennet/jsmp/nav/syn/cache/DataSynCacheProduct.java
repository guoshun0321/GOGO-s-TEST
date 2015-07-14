package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.entity.ProductEntity;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;

public class DataSynCacheProduct extends DataSynCache<ProductEntity>
{

    @Override
    protected String genKey(ProductEntity obj)
    {
        return CachedKeyUtil.productKey(obj.getProductId());
    }

    @Override
    public void insert(ProductEntity obj)
    {
        super.insert(obj);
        this.add2CachedSet(CachedKeyUtil.productList(), obj.getProductId());
    }

    @Override
    public void update(ProductEntity obj)
    {
        super.update(obj);
    }

    @Override
    public void delete(ProductEntity obj)
    {
        super.delete(obj);
        this.del2CachedSet(CachedKeyUtil.productList(), obj.getProductId());
    }

}
