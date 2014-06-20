package jetsennet.jsmp.nav.service.a7.entity;

import java.util.HashMap;
import java.util.Map;

import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

public class RequestEntityUtilTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testMap2Obj()
	{
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("testId", "12");
		testMap.put("testStr", "string");
		testMap.put("testBool", "Y");
		testMap.put("testDate", "19870121112233");
		
		RequestTestEntity re = new RequestTestEntity();
		Class clz = re.getClass();
		System.out.println(clz);
		System.out.println(clz.getFields());
		RequestTestEntity entity = RequestEntityUtil.map2Obj(RequestTestEntity.class, testMap);
		assertNotNull(entity);
		assertEquals(12, entity.getId());
		assertEquals("string", entity.getStr1());
		assertEquals(true, entity.isBoolean1());
		assertEquals("19870121112233", SafeDateFormater.format(entity.getDate1(), "YYYYMMDDHHmmss"));
	}

}
