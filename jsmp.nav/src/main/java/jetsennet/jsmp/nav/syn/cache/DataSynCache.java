package jetsennet.jsmp.nav.syn.cache;

import jetsennet.jsmp.nav.syn.AbsUpdateMediaInfoCache;


public abstract class DataSynCache<T> extends AbsUpdateMediaInfoCache<T>
{

    protected abstract String genKey(T obj);

    public void insert(T obj)
    {
        String key = this.genKey(obj);
        cache.put(key, obj);
    }

    public void update(T obj)
    {
        String key = this.genKey(obj);
        cache.put(key, obj);
    }

    public void delete(T obj)
    {
        String key = this.genKey(obj);
        cache.del(key);
    }

}
