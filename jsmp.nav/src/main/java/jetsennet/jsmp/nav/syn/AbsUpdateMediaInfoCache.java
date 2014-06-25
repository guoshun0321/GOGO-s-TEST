package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.List;

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
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = new ArrayList<Integer>();
		}
		((List<Integer>) cachedValue).add(addition);
		cache.put(cachedKey, cachedValue);
	}

	/**
	 * 从缓存中删除整数数据
	 * @param cachedKey
	 * @param discard
	 */
	protected void del2CachedSet(String cachedKey, Integer discard)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue != null && (cachedValue instanceof List))
		{
			((List<Integer>) cachedValue).remove(discard);
		}
		cache.put(cachedKey, cachedValue);
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
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = new ArrayList<String>();
		}
		((List<String>) cachedValue).add(addition);
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
		if (cachedValue != null && (cachedValue instanceof List))
		{
			((List<String>) cachedValue).remove(discard);
		}
		cache.put(cachedKey, cachedValue);
	}
}
