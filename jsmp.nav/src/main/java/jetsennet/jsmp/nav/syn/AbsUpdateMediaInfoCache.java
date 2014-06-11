package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jetsennet.jsmp.nav.cache.xmem.MemcachedOp;
import jetsennet.jsmp.nav.syn.cache.IDataSynCache;

public abstract class AbsUpdateMediaInfoCache<T> implements IDataSynCache<T>
{

    protected MemcachedOp cache = MemcachedOp.getInstance();

    protected void add2CachedSet(String cachedKey, int addition)
    {
        Object cachedValue = cache.get(cachedKey);
        if (cachedValue == null || !(cachedValue instanceof List))
        {
            cachedValue = new ArrayList<Integer>();
        }
        ((List<Integer>) cachedValue).add(addition);
        cache.put(cachedKey, cachedValue);
    }

    protected void del2CachedSet(String cachedKey, int discard)
    {
        Object cachedValue = cache.get(cachedKey);
        if (cachedValue != null && (cachedValue instanceof List))
        {
            ((List<Integer>) cachedValue).remove(discard);
        }
    }

    protected void add2CachedSet(String cachedKey, String addition)
    {
        Object cachedValue = cache.get(cachedKey);
        if (cachedValue == null || !(cachedValue instanceof List))
        {
            cachedValue = new ArrayList<String>();
        }
        ((List<String>) cachedValue).add(addition);
        cache.put(cachedKey, cachedValue);
    }

    protected void del2CachedSet(String cachedKey, String discard)
    {
        Object cachedValue = cache.get(cachedKey);
        if (cachedValue != null && (cachedValue instanceof List))
        {
            ((List<String>) cachedValue).remove(discard);
        }
    }

    protected void addSet(String key)
    {
        cache.put(key, new HashSet<Integer>());
    }

    protected void delSet(String key)
    {
        cache.del(key);
    }
}
