package jetsennet.jsmp.nav.cache.xmem;

import jetsennet.jsmp.nav.config.Config;
import net.rubyeye.xmemcached.KeyIterator;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class GetMemcachedKeys
{
	
	public static void main(String[] args) throws Exception
	{
		MemcachedClient client = DataCacheOp.getInstance().getClient();
		Object obj = client.getStats();
		KeyIterator iterator = client.getKeyIterator(AddrUtil.getOneAddress(Config.CACHE_SERVERS));
		while(iterator.hasNext()) {
			String key = iterator.next();
			System.out.println(key);
		}
		System.out.println(obj);
	}

}
