package jetsennet.jsmp.nav.service.a7;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.NavCheckRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import jetsennet.jsmp.nav.util.TestObjGenUtil;
import junit.framework.TestCase;

public class NavBusinessTest extends TestCase
{

	protected void setUp() throws Exception
	{
		//		TestObjGenUtil.builderCache();
	}

	protected void tearDown() throws Exception
	{
		//		DataCacheOp.getInstance().deleteAll();
	}

	public void testInvoke()
	{
		fail("Not yet implemented");
	}

	public void testNavCheck() throws Exception
	{
		// request解析
		String xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <NavCheck clientId=\"1\" deviceId=\"12345\" smcId=\"1234\"/>";
		NavCheckRequest req = RequestEntityUtil.map2Obj(NavCheckRequest.class, A7Util.requestXml2Map(xml));
		assertNotNull(req);
		assertEquals("1", req.getClientId());
		assertEquals("12345", req.getDeviceId());
		assertEquals("1234", req.getSmcId());
	}

	public void testGetFolderContents() throws Exception
	{
		fail("Not yet implemented");
	}

	public void testGetRootContents() throws Exception
	{
		String xml = "<GetRootContents  clientId='1' deviceId='2' startAt='2' maxItems='2'/>";
		GetFolderContentsRequest req = RequestEntityUtil.map2Obj(GetFolderContentsRequest.class, A7Util.requestXml2Map(xml));

		NavBusiness nb = new NavBusiness();
		String str = nb.getRootContents(A7Util.requestXml2Map(xml));
		System.out.println(str);
	}

	public void testGetAssociatedFolderContents()
	{
		fail("Not yet implemented");
	}

	public void testGetSavedPrograms()
	{
		fail("Not yet implemented");
	}

	public void testGetBookmarks()
	{
		fail("Not yet implemented");
	}

	public void testGetItemData()
	{
		fail("Not yet implemented");
	}

	public void testGetEntitlement()
	{
		fail("Not yet implemented");
	}

	public void testAddBookmark()
	{
		fail("Not yet implemented");
	}

	public void testDeleteBookmark()
	{
		fail("Not yet implemented");
	}

	public void testDeleteSavedProgram()
	{
		fail("Not yet implemented");
	}

	public void testSearchValidate()
	{
		fail("Not yet implemented");
	}

	public void testSelectionStart()
	{
		fail("Not yet implemented");
	}

	public void testChannelSelectionStart()
	{
		fail("Not yet implemented");
	}

	public void testSelectionResume()
	{
		fail("Not yet implemented");
	}

	public void testValidatePlayEligibility()
	{
		fail("Not yet implemented");
	}

	public void testAddSavedProgram()
	{
		fail("Not yet implemented");
	}

	public void testSetResumePoint()
	{
		fail("Not yet implemented");
	}

	public void testGetUpsellOffer()
	{
		fail("Not yet implemented");
	}

	public void testGetChannels()
	{
		fail("Not yet implemented");
	}

	public void testGetPrograms()
	{
		fail("Not yet implemented");
	}

	public void testGetAssociatedPrograms()
	{
		fail("Not yet implemented");
	}

}
