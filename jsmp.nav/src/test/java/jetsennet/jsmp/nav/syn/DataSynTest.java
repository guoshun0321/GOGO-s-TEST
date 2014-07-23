package jetsennet.jsmp.nav.syn;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.syn.db.DataSourceManager;
import jetsennet.orm.session.Session;
import jetsennet.orm.session.SqlSessionFactory;
import junit.framework.TestCase;

public class DataSynTest extends TestCase
{

	public void testDateSyn()
	{
		SqlSessionFactory factory = DataSourceManager.MEDIA_FACTORY;
		Session session = factory.openSession();
		session.deleteAll("NS_COLUMN");
		
		DataCacheOp.getInstance().deleteAll();
		String xml =
			"<copPortalMsg><header><messageID>1406015853503</messageID><sendID>JCOP</sendID><recvID>Portal</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1406015853503</time><responseUrl>null</responseUrl><msgType>REQUEST</msgType></header><body><columnTable opFlag=\"0\"><NS_COLUMN><COLUMN_ID>109</COLUMN_ID><ASSET_ID>JETSENN201451cd4098-0858-457e-a7fb-9afb4c6ddbcaCOL</ASSET_ID><COLUMN_NAME>×ÛºÏ</COLUMN_NAME><PARENT_ID>0</PARENT_ID><PARENT_ASSETID>null</PARENT_ASSETID><COLUMN_CODE>11212</COLUMN_CODE><COLUMN_STATE>0</COLUMN_STATE><COLUMN_DESC>121</COLUMN_DESC><COLUMN_SEQ>3</COLUMN_SEQ><COLUMN_PATH>×ÛºÏ</COLUMN_PATH><COLUMN_TYPE>1</COLUMN_TYPE><SORT_RULE>null</SORT_RULE><SORT_DIRECTION>null</SORT_DIRECTION><REGION_CODE>212</REGION_CODE><LANGUAGE_CODE>2121</LANGUAGE_CODE><UPDATE_TIME>1406015853503</UPDATE_TIME></NS_COLUMN></columnTable></body></copPortalMsg>";

		DataSynEntity synData = DataSynXmlParse.parseXml(xml);
		DataHandleUtil.handleData(synData, xml);
	}

}
