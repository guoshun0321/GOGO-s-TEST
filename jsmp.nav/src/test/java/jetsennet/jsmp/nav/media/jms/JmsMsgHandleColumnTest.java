package jetsennet.jsmp.nav.media.jms;

import jetsennet.jsmp.nav.util.FileUtil;
import junit.framework.TestCase;

public class JmsMsgHandleColumnTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testHandle()
	{
		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_COLUMN));
		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_COLUMN_DOWN));
	}

}
