package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.syn.cache.IDataSynCache;

public abstract class AbsUpdateMediaInfoCache<T> implements IDataSynCache<T>
{

	protected DataCacheOp cache = DataCacheOp.getInstance();

	/**
	 * 将整数数据添加到cache
	 * 
	 * @param cachedKey
	 * @param addition
	 */
	protected void add2CachedSet(String cachedKey, int addition)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue == null || !(cachedValue instanceof LinkedHashSet))
		{
			cachedValue = new LinkedHashSet<Integer>();
		}
		((LinkedHashSet<Integer>) cachedValue).add(addition);
		cache.put(cachedKey, cachedValue);
	}

	/**
	 * 从缓存中删除整数数据
	 * @param cachedKey
	 * @param discard
	 */
	protected void del2CachedSet(String cachedKey, int discard)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue != null && (cachedValue instanceof LinkedHashSet))
		{
			((LinkedHashSet<Integer>) cachedValue).remove(discard);
		}
	}

	/**
	 * 将字符数据添加到cache
	 * 
	 * @param cachedKey
	 * @param addition
	 */
	protected void add2CachedSet(String cachedKey, String addition)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue == null || !(cachedValue instanceof LinkedHashSet))
		{
			cachedValue = new LinkedHashSet<String>();
		}
		((LinkedHashSet<String>) cachedValue).add(addition);
		cache.put(cachedKey, cachedValue);
	}

	/**
	 * 从缓存中删除字符数据
	 * @param cachedKey
	 * @param discard
	 */
	protected void del2CachedSet(String cachedKey, String discard)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue != null && (cachedValue instanceof LinkedHashSet))
		{
			((LinkedHashSet<String>) cachedValue).remove(discard);
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
