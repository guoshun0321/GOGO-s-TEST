package jetsennet.jsmp.nav.service.a7;

import java.util.Date;
import java.util.List;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.entity.A7Constants;
import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.NavCheckRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;
import jetsennet.jsmp.nav.util.DateUtil;
import jetsennet.util.SafeDateFormater;
import junit.framework.TestCase;

public class NavBusinessTest extends TestCase
{

	protected void setUp() throws Exception
	{
	}

	protected void tearDown() throws Exception
	{
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
		DataCacheOp op = DataCacheOp.getInstance();
		String xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><GetFolderContents clientId='1232' includeFolderProperties='Y' includeSubFolder='Y' includeSelectableItem='Y' startAt=\"0\" assetId ='%s'/>";

		// top
		List<Integer> tops = op.get(CachedKeyUtil.topColumn());
		ColumnEntity column = op.get(CachedKeyUtil.columnKey(tops.get(0)));
		assertNotNull(column);
		String xml1 = String.format(xml, column.getAssetId());
		NavBusiness nb = new NavBusiness();
		String str = nb.getFolderContents(A7Util.requestXml2Map(xml1));
		System.out.println(str);

		// level2
		List<Integer> subs = op.getListInt(CachedKeyUtil.subColumn(column.getColumnId(), column.getRegionCode()));
		ColumnEntity column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		assertNotNull(column1);
		xml1 = String.format(xml, column1.getAssetId());
		str = nb.getFolderContents(A7Util.requestXml2Map(xml1));
		System.out.println(str);
		
		// level3
		subs = op.getListInt(CachedKeyUtil.subColumn(column1.getColumnId(), column1.getRegionCode()));
		column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		assertNotNull(column1);
		xml1 = String.format(xml, column1.getAssetId());
		str = nb.getFolderContents(A7Util.requestXml2Map(xml1));
		System.out.println(str);
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
	}

	public void testGetBookmarks()
	{
	}

	public void testGetItemData()
	{
	}

	public void testGetEntitlement()
	{
	}

	public void testAddBookmark()
	{
	}

	public void testDeleteBookmark()
	{
	}

	public void testDeleteSavedProgram()
	{
	}

	public void testSearchValidate()
	{
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

		subs = op.getListInt(CachedKeyUtil.subColumn(column1.getColumnId(), column1.getRegionCode()));
		column1 = op.get(CachedKeyUtil.columnKey(subs.get(0)));
		assertNotNull(column1);

		subs = op.getListInt(CachedKeyUtil.columnPgm(column1.getColumnId()));
		ProgramEntity pgm = op.get(CachedKeyUtil.programKey(subs.get(0)));
		List<FileItemEntity> files = op.getList(CachedKeyUtil.pgmFileItemKey(pgm.getPgmId()));
		assertNotNull(files);
		FileItemEntity file = files.get(0);

		String xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><SelectionStart clientId='1232' deviceId ='12345' fileAssetId='" + file.getAssetId() + "' />";
		NavBusiness nb = new NavBusiness();
		String str = nb.selectionStart(A7Util.requestXml2Map(xml));
		System.out.println(str);

		xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><SelectionStart clientId='1232' deviceId ='12345' serviceCode='OTT' fileAssetId='"
				+ file.getAssetId()
				+ "' />";
		nb = new NavBusiness();
		str = nb.selectionStart(A7Util.requestXml2Map(xml));
		System.out.println(str);
	}

	public void testChannelSelectionStart() throws Exception
	{
		DataCacheOp op = DataCacheOp.getInstance();
		String xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><ChannelSelectionStart  clientId='1232'  deviceId ='12345' channelId='1213'  assetId='%s'/>";

		List<Integer> chlIds = op.get(CachedKeyUtil.channelIndex("", A7Constants.DEF_LANG));
		assertNotNull(chlIds);

		Date date = new Date();
		int pbId = op.getInt(CachedKeyUtil.channelPlaybill(chlIds.get(0), DateUtil.getPreTimeOfDay(date, 0)));
		PlaybillEntity pb = op.get(CachedKeyUtil.playbillKey(pbId));
		assertNotNull(pb);

		List<Integer> items = op.getListInt(CachedKeyUtil.playbillItemList(pb.getPbId()));
		PlaybillItemEntity pbi = op.get(CachedKeyUtil.playbillItemKey(items.get(0)));
		assertNotNull(pbi);
		String assertId = pbi.getAssetId();

		xml = String.format(xml, assertId);
		NavBusiness nb = new NavBusiness();
		String str = nb.channelSelectionStart(A7Util.requestXml2Map(xml));
		System.out.println(str);

		xml =
			"<?xml version='1.0' encoding=\"UTF-8\" ?><ChannelSelectionStart  clientId='1232'  deviceId ='12345' channelId='1213' serviceCode='OTT'  assetId='%s'/>";
		xml = String.format(xml, assertId);
		str = nb.channelSelectionStart(A7Util.requestXml2Map(xml));
		System.out.println(str);
	}

	public void testSelectionResume()
	{
	}

	public void testValidatePlayEligibility()
	{
	}

	public void testAddSavedProgram()
	{
	}

	public void testSetResumePoint()
	{
	}

	public void testGetUpsellOffer()
	{
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
		String xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetPrograms clientId='1232'  channelIds='%s'/>";

		DataCacheOp op = DataCacheOp.getInstance();
		List<Integer> chlIds = op.get(CachedKeyUtil.channelIndex("", A7Constants.DEF_LANG));
		assertNotNull(chlIds);

		xml = String.format(xml, chlIds.get(0));
		NavBusiness nb = new NavBusiness();
		String str = nb.getPrograms(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);

		xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetPrograms clientId='1232'  channelIds='%s' startAt='30' maxItems='20'/>";
		xml = String.format(xml, chlIds.get(0));
		str = nb.getPrograms(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);

		xml = "<?xml version='1.0' encoding=\"UTF-8\" ?> <GetPrograms clientId='1232'  channelIds='%s' startAt='30' maxItems='20' days='-1'/>";
		xml = String.format(xml, chlIds.get(0));
		str = nb.getPrograms(A7Util.requestXml2Map(xml));
		assertNotNull(str);
		System.out.println(str);
	}

	public void testGetAssociatedPrograms()
	{
	}

}
