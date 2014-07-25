package jetsennet.jsmp.nav.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.config.Config;

import org.apache.log4j.Logger;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.danga.MemCached.SockIOPool.SockIO;

public class MemCachedKeys
{
	protected static MemCachedClient mc = new MemCachedClient();

	private static final Logger logger = Logger.getLogger(MemCachedKeys.class);

	static
	{

		// 设置缓存服务器列表
		String[] servers = { Config.CACHE_SERVERS };

		// 设置服务器权重
		Integer[] weights = { 10 };

		// 创建一个Socked连接池实例
		SockIOPool pool = SockIOPool.getInstance();

		// 向连接池设置服务器和权重
		pool.setServers(servers);
		pool.setWeights(weights);

		// set some TCP settings
		// disable nagle
		// set the read timeout to 3 secs
		// and don't set a connect timeout
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);

		// initialize the connection pool
		pool.initialize();
	}

	public Map<String, Object> getKeys()
	{
		Map<String, Object> retval = new LinkedHashMap<>();

		//		System.out.println(mc.stats());

		// 取得 所有 memcached server ,可能有多台 server
		Map<String, Map<String, String>> slabs = mc.statsItems();
		// key= ip:port, ex: 192.168.1.2:11211,192.168.1.2:11212...
		Iterator<String> itemsItr = slabs.keySet().iterator();

		// 以server IP key值去循环,可能有多台 server
		while (itemsItr.hasNext())
		{
			String serverInfo1 = itemsItr.next().toString();
			// 取得這個server的各種 status [itemname:number:field=value]
			Map<String, String> itemNames = (Map<String, String>) slabs.get(serverInfo1);
			Iterator<String> itemNameItr = itemNames.keySet().iterator();

			// 以status key值去迴圈
			while (itemNameItr.hasNext())
			{
				String itemName = itemNameItr.next().toString();
				// 拆解status 欄位
				// itemAtt[0] = itemname
				// itemAtt[1] = CacheDump的參數
				// itemAtt[2] = field:number or age
				String[] itemAtt = itemName.split(":");
				// 要取得field為number的CacheDump參數
				if (itemAtt[2].startsWith("number"))
				{
					// 以status取到的參數,取得cachedDump Map...(下面Map名稱命錯了)
					// ServerIP<cachekey<byte size;unix timestamp>>
					Map<String, Map<String, String>> chcheDump = mc.statsCacheDump(Integer.parseInt(itemAtt[1]), 0);

					Iterator<String> itr = chcheDump.keySet().iterator();
					// 以server IP key值去迴圈,可能有多台 server
					int i = 0;
					while (itr.hasNext())
					{
						// key=ip:port
						String serverInfo2 = itr.next().toString();
						// 取得Cached Key Map...<-終於,這才是我要的Key集合
						Map<String, String> items = (Map<String, String>) chcheDump.get(serverInfo2);
						Iterator<String> keyItr = items.keySet().iterator();
						//以Cached Key 去迴圈,取key出來,或是要取size,unix timestamp 也有
						while (keyItr.hasNext())
						{
							String key = keyItr.next().toString();
							String memKey = key;
							i++;
							try
							{
								key = URLDecoder.decode(key, "UTF-8");
								String value = ((String) items.get(memKey));
								value = value.substring(value.indexOf(";") + 2, value.indexOf(" s"));
								Date date = new Date();
								date.setTime(Long.valueOf(value + "000"));
								retval.put(key, date);
							}
							catch (Exception ex)
							{
								logger.error("", ex);
							}
						}
					}
				}
			}
		}
		return retval;
	}

	public static void main(String[] args)
	{
		//  mcc.set("foo3", "This is a test String");
		// mcc.delete("foo");
		// String bar = mcc.get("foo3").toString();
		//		long startDate = System.currentTimeMillis();
		//
		//		for (int i = 0; i < 100; i++)
		//		{
		//			mc.add("test" + i, "中国" + i);
		//		}
		//
		//		System.out.print(" get value : " + mc.get("test1"));
		//		long endDate = System.currentTimeMillis();

		//		long nowDate = (endDate - startDate) / 1000;

		//  System.out.println(nowDate); 

		// System.out.println("="+mc.statsItems());

		for (int i = 0; i < 100; i++)
		{
			mc.delete("test" + i);
		}

		MemCachedKeys aa = new MemCachedKeys();
		Map<String, Object> keyMap = aa.getKeys();

		DataCacheOp op = DataCacheOp.getInstance();
		for (String key : keyMap.keySet())
		{
			Object temp = op.get(key);
			System.out.println(key + " -> " + temp);
		}
		op.shutdown();

		// System.out.println("aa="+ mc.statsCacheDump(0, 100).toString());

	}

}
