package jetsennet.jsmp.nav.service.a7;

import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import junit.framework.TestCase;

public class NavBusinessTest extends TestCase
{

	protected void setUp() throws Exception
	{
	}

	protected void tearDown() throws Exception
	{
	}

	public void testGetUpdateInfo() throws Exception
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("getUpdateInfo : " + nb.GetUpdateInfo(null));
	}

	public void testNavCheck() throws Exception
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("navCheck : " + nb.navCheck(null));
	}

	public void testGetFolderContents() throws Exception
	{

	}

	public void testGetRootContents() throws Exception
	{
		String xml = "<GetRootContents  clientId='1' deviceId='2' startAt='0' maxItems='2'/>";
		GetFolderContentsRequest req = RequestEntityUtil.map2Obj(GetFolderContentsRequest.class, A7Util.requestXml2Map(xml));

		NavBusiness nb = new NavBusiness();
		String str = nb.getRootContents(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);

		xml = "<GetRootContents  clientId='1' deviceId='2' startAt='4' maxItems='10'/>";
		str = nb.getRootContents(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);
	}

	public void testGetAssociatedFolderContents()
	{
	}

	public void testGetSavedPrograms()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("getSavedPrograms : " + nb.getSavedPrograms(null));
	}

	public void testGetBookmarks()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("getBookMarks : " + nb.getBookmarks(null));
	}

	public void testGetItemData()
	{

	}

	public void testGetEntitlement()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("getEntitlement : " + nb.getEntitlement(null));
	}

	public void testAddBookmark()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("addBookmark : " + nb.addBookmark(null));
	}

	public void testDeleteBookmark()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("deleteBookmark : " + nb.deleteBookmark(null));
	}

	public void testDeleteSavedProgram()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("deleteSavedProgram : " + nb.deleteSavedProgram(null));
	}

	public void testSearchValidate()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("searchValidate : " + nb.searchValidate(null));
	}

	public void testSelectionStart() throws Exception
	{

	}

	public void testChannelSelectionStart() throws Exception
	{

	}

	public void testSelectionResume()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("SelectionResume : " + nb.selectionResume(null));
	}

	public void testValidatePlayEligibility()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("ValidatePlayEligibility : " + nb.validatePlayEligibility(null));
	}

	public void testAddSavedProgram()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("AddSavedProgram : " + nb.addSavedProgram(null));
	}

	public void testSetResumePoint()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("SetResumePoint : " + nb.setResumePoint(null));
	}

	public void testGetUpsellOffer()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("GetUpsellOffer : " + nb.getUpsellOffer(null));
	}

	public void testGetChannels() throws Exception
	{
		String xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetChannels clientId='1232' deviceId='123'/>";

		NavBusiness nb = new NavBusiness();
		String str = nb.getChannels(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);

		xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetChannels clientId='1232' deviceId='123' startAt='20' maxItems='5'/>";
		str = nb.getChannels(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);
	}

	public void testGetPrograms() throws Exception
	{

	}

	public void testGetAssociatedPrograms()
	{
	}

	public void testSearchContentInfo()
	{
		NavBusiness nb = new NavBusiness();
		System.out.println("SearchContentInfo : " + nb.searchContentInfo(null));
	}

}
