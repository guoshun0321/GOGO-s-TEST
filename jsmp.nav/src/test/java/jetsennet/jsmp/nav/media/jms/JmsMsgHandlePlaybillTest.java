package jetsennet.jsmp.nav.media.jms;

import jetsennet.jsmp.nav.util.FileUtil;
import junit.framework.TestCase;

/**
 * 未测试
 * @author jetsen
 *
 */
public class JmsMsgHandlePlaybillTest extends TestCase
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
		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_PLAYBILL));
		JmsMsgHandle.syn(DataSynXmlParse.parseXml(FileUtil.COP_PLAYBILL_DOWN));
	}

}
