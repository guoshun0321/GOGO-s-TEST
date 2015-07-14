package jetsennet.jsmp.nav.cache.xmem;

import jetsennet.jsmp.nav.config.Config;
import junit.framework.TestCase;

public class DataCacheOpTest1 extends TestCase
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

		Object value = client.get("key1", true);
		assertNull(value);
		System.out.println(value);

		client.put("key1", "value1");
		value = client.get("key1", true);
		assertEquals("value1", value);

		client.put("key1", "value2");
		value = client.get("key1", true);
		assertEquals("value2", value);

		client.shutdown();
	}

}
