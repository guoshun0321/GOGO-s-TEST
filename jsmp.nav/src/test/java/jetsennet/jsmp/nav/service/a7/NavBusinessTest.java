package jetsennet.jsmp.nav.service.a7;

import java.util.List;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.NavCheckRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;
import junit.framework.TestCase;

public class NavBusinessTest extends TestCase
{

	protected void setUp() throws Exception
	{
	}

	protected void tearDown() throws Exception
	{
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
		String xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><GetFolderContents clientId='1232' includeFolderProperties='Y' startAt=\"{moviePageSize:2,movieCurrentPage:1,folderPageSize:2,folderCurrentPage:1}\" assetId ='%s'/>";

		List<Integer> tops = DataCacheOp.getInstance().get(CachedKeyUtil.topColumn());
		ColumnEntity column = DataCacheOp.getInstance().get(CachedKeyUtil.columnKey(tops.get(0)));
		assertNotNull(column);

		String xml1 = String.format(xml, column.getAssetId());
		NavBusiness nb = new NavBusiness();
		String str = nb.getFolderContents(A7Util.requestXml2Map(xml1));
		System.out.println(str);

		List<Integer> subs = DataCacheOp.getInstance().getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
		ColumnEntity column1 = DataCacheOp.getInstance().get(CachedKeyUtil.columnKey(subs.get(0)));
		assertNotNull(column1);

		xml1 = String.format(xml, column1.getAssetId());
		str = nb.getFolderContents(A7Util.requestXml2Map(xml1));
		System.out.println(str);
	}

	public void testGetRootContents() throws Exception
	{
		String xml = "<GetRootContents  clientId='1' deviceId='2' startAt='2' maxItems='2'/>";
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

	public void testSelectionStart() throws Exception
	{
		DataCacheOp op = DataCacheOp.getInstance();
		List<Integer> tops = op.get(CachedKeyUtil.topColumn());
		ColumnEntity column = op.get(CachedKeyUtil.columnKey(tops.get(0)));
		assertNotNull(column);

		List<Integer> subs = op.getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
		ColumnEntity column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		assertNotNull(column1);

		subs = op.getListInt(CachedKeyUtil.columnPgm(column1.getColumnId()));
		List<String> subs1 = op.getListString(CachedKeyUtil.pgmFileItems(subs.get(0)));
		assertNotNull(subs1);

		FileItemEntity file = op.get(CachedKeyUtil.pgmFileItem(subs1.get(0)));
		assertNotNull(file);

		String xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><SelectionStart clientId='1232' deviceId ='12345' fileAssetId='" + file.getAssetId() + "' />";
		NavBusiness nb = new NavBusiness();
		String str = nb.selectionStart(A7Util.requestXml2Map(xml));
		System.out.println(str);
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

	public void testGetChannels() throws Exception
	{
		String xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetChannels clientId='1232' deviceId='123'/>";

		NavBusiness nb = new NavBusiness();
		String str = nb.getChannels(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);
		assertTrue("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Channels restartAtToken=\"10\"><Channel logo=\"CHL_LOGO202479\" channelName=\"CHL_NAME202473\" isNPVR=\"202481\" channelID=\"202472\" isStandardChannel=\"202482\" channelNumber=\"202478\" isTVAnyTime=\"202483\" isStartOver=\"202480\"><Parameter qam=\"QAM202495\" frequency=\"FREQ202494\" symbolRate=\"SYMBOL_RATE202496\" serviceId=\"SERVICE_ID202492\" tsId=\"TSID202493\" bitRate=\"BIT_RATE202497\"></Parameter></Channel><Channel logo=\"CHL_LOGO202765\" channelName=\"CHL_NAME202759\" isNPVR=\"202767\" channelID=\"202758\" isStandardChannel=\"202768\" channelNumber=\"202764\" isTVAnyTime=\"202769\" isStartOver=\"202766\"><Parameter qam=\"QAM202781\" frequency=\"FREQ202780\" symbolRate=\"SYMBOL_RATE202782\" serviceId=\"SERVICE_ID202778\" tsId=\"TSID202779\" bitRate=\"BIT_RATE202783\"></Parameter></Channel><Channel logo=\"CHL_LOGO203051\" channelName=\"CHL_NAME203045\" isNPVR=\"203053\" channelID=\"203044\" isStandardChannel=\"203054\" channelNumber=\"203050\" isTVAnyTime=\"203055\" isStartOver=\"203052\"><Parameter qam=\"QAM203067\" frequency=\"FREQ203066\" symbolRate=\"SYMBOL_RATE203068\" serviceId=\"SERVICE_ID203064\" tsId=\"TSID203065\" bitRate=\"BIT_RATE203069\"></Parameter></Channel><Channel logo=\"CHL_LOGO203337\" channelName=\"CHL_NAME203331\" isNPVR=\"203339\" channelID=\"203330\" isStandardChannel=\"203340\" channelNumber=\"203336\" isTVAnyTime=\"203341\" isStartOver=\"203338\"><Parameter qam=\"QAM203353\" frequency=\"FREQ203352\" symbolRate=\"SYMBOL_RATE203354\" serviceId=\"SERVICE_ID203350\" tsId=\"TSID203351\" bitRate=\"BIT_RATE203355\"></Parameter></Channel><Channel logo=\"CHL_LOGO203623\" channelName=\"CHL_NAME203617\" isNPVR=\"203625\" channelID=\"203616\" isStandardChannel=\"203626\" channelNumber=\"203622\" isTVAnyTime=\"203627\" isStartOver=\"203624\"><Parameter qam=\"QAM203639\" frequency=\"FREQ203638\" symbolRate=\"SYMBOL_RATE203640\" serviceId=\"SERVICE_ID203636\" tsId=\"TSID203637\" bitRate=\"BIT_RATE203641\"></Parameter></Channel><Channel logo=\"CHL_LOGO203909\" channelName=\"CHL_NAME203903\" isNPVR=\"203911\" channelID=\"203902\" isStandardChannel=\"203912\" channelNumber=\"203908\" isTVAnyTime=\"203913\" isStartOver=\"203910\"><Parameter qam=\"QAM203925\" frequency=\"FREQ203924\" symbolRate=\"SYMBOL_RATE203926\" serviceId=\"SERVICE_ID203922\" tsId=\"TSID203923\" bitRate=\"BIT_RATE203927\"></Parameter></Channel><Channel logo=\"CHL_LOGO204195\" channelName=\"CHL_NAME204189\" isNPVR=\"204197\" channelID=\"204188\" isStandardChannel=\"204198\" channelNumber=\"204194\" isTVAnyTime=\"204199\" isStartOver=\"204196\"><Parameter qam=\"QAM204211\" frequency=\"FREQ204210\" symbolRate=\"SYMBOL_RATE204212\" serviceId=\"SERVICE_ID204208\" tsId=\"TSID204209\" bitRate=\"BIT_RATE204213\"></Parameter></Channel><Channel logo=\"CHL_LOGO204481\" channelName=\"CHL_NAME204475\" isNPVR=\"204483\" channelID=\"204474\" isStandardChannel=\"204484\" channelNumber=\"204480\" isTVAnyTime=\"204485\" isStartOver=\"204482\"><Parameter qam=\"QAM204497\" frequency=\"FREQ204496\" symbolRate=\"SYMBOL_RATE204498\" serviceId=\"SERVICE_ID204494\" tsId=\"TSID204495\" bitRate=\"BIT_RATE204499\"></Parameter></Channel><Channel logo=\"CHL_LOGO204767\" channelName=\"CHL_NAME204761\" isNPVR=\"204769\" channelID=\"204760\" isStandardChannel=\"204770\" channelNumber=\"204766\" isTVAnyTime=\"204771\" isStartOver=\"204768\"><Parameter qam=\"QAM204783\" frequency=\"FREQ204782\" symbolRate=\"SYMBOL_RATE204784\" serviceId=\"SERVICE_ID204780\" tsId=\"TSID204781\" bitRate=\"BIT_RATE204785\"></Parameter></Channel><Channel logo=\"CHL_LOGO205053\" channelName=\"CHL_NAME205047\" isNPVR=\"205055\" channelID=\"205046\" isStandardChannel=\"205056\" channelNumber=\"205052\" isTVAnyTime=\"205057\" isStartOver=\"205054\"><Parameter qam=\"QAM205069\" frequency=\"FREQ205068\" symbolRate=\"SYMBOL_RATE205070\" serviceId=\"SERVICE_ID205066\" tsId=\"TSID205067\" bitRate=\"BIT_RATE205071\"></Parameter></Channel></Channels>".equals(str));
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
