package jetsennet.jsmp.nav.cache.xmem;

import jetsennet.jsmp.nav.config.Config;
import junit.framework.TestCase;

public class MemcachedOpTest1 extends TestCase
{

    @Override
    protected void setUp() throws Exception
    {

    }

    @Override
    protected void tearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testMemcached()
    {
        DataCacheOp client = DataCacheOp.getInstance();

        assertEquals("192.168.8.175:12000 192.168.8.175:12001", Config.CACHE_SERVERS);
        assertEquals(5, Config.CACHE_POOLSIZE);
        assertEquals(3600, Config.CACHE_TIMEOUT);

        client.deleteAll();

        Object value = client.get("key1");
        assertNull(value);
        System.out.println(value);

        client.put("key1", "value1");
        value = client.get("key1");
        assertEquals("value1", value);

        client.put("key1", "value2");
        value = client.get("key1");
        assertEquals("value2", value);

        client.shutdown();
    }

}
