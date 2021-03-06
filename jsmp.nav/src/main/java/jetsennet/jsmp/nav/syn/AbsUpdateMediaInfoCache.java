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
		List<Integer> valueLst = null;
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			valueLst = new ArrayList<Integer>();
		}
		else
		{
			valueLst = (List<Integer>) cachedValue;
		}

		if (!valueLst.contains(addition))
		{
			valueLst.add(addition);
		}
		cache.put(cachedKey, valueLst);
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
		} else {
			cachedValue = new ArrayList<>();
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
		List<String> valueLst = null;
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			valueLst = new ArrayList<String>();
		}
		else
		{
			valueLst = (List<String>) cachedValue;
		}

		if (!valueLst.contains(addition))
		{
			valueLst.add(addition);
		}
		cache.put(cachedKey, valueLst);
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

	/**
	 * 将对象数据添加到cache
	 * 
	 * @param cachedKey
	 * @param addition
	 */
	protected void add2CachedSet(String cachedKey, Object addition)
	{
		Object cachedValue = cache.get(cachedKey, true);
		if (cachedValue == null || !(cachedValue instanceof List))
		{
			cachedValue = new ArrayList<Object>();
		}
		((List<Object>) cachedValue).add(addition);
		cache.put(cachedKey, cachedValue);
	}

}
