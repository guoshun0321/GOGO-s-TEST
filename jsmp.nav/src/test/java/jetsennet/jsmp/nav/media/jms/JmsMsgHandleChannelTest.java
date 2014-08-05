package jetsennet.jsmp.nav.media.jms;

import jetsennet.jsmp.nav.util.FileUtil;
import junit.framework.TestCase;

/**
 * 
 * @author jetsen
 */
public class JmsMsgHandleChannelTest extends TestCase
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
		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_CHANNEL));
//		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_CHANNEL_DOWN));
	}

}
