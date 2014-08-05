package jetsennet.jsmp.nav.media.jms;

import jetsennet.jsmp.nav.util.FileUtil;
import junit.framework.TestCase;

public class DataSynXmlParseTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testParseXml()
	{
		DataSynEntity entity = DataSynXmlParse.parseXml(FileUtil.COP_COLUMN);
		assertNotNull(entity);
		assertEquals(2, entity.getContents().size());

		entity = DataSynXmlParse.parseXml(FileUtil.COP_PROGRAM);
		assertNotNull(entity);

		entity = DataSynXmlParse.parseXml(FileUtil.COP_CHANNEL);
		assertNotNull(entity);

		entity = DataSynXmlParse.parseXml(FileUtil.COP_PLAYBILL);
		assertNotNull(entity);

		entity = DataSynXmlParse.parseXml(FileUtil.COP_PROGRAM_DOWN);
		assertNotNull(entity);
	}

}
