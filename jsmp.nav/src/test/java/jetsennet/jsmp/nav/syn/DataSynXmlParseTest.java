package jetsennet.jsmp.nav.syn;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.util.ObjectUtil;
import junit.framework.TestCase;

public class DataSynXmlParseTest extends TestCase
{

	public void testTrans()
	{
		DataSynEntity ds = new DataSynEntity();
		ds.setMsgId("msgId");
		ds.setSenderId("sendId");
		ds.setRecId("recvId");
		ds.setOpCode(DataSynOpCodeEnum.COP_PORTAL_META_PUB);
		ds.setTime(1000l);
		ds.setResponseUrl("responseUrl");
		ds.setMsgType(DataSynMsgTypeEnum.REQUEST);

		DataSynContentEntity content = new DataSynContentEntity();
		ColumnEntity column = new ColumnEntity();
		column.setColumnId(10);
		column.setAssetId("column_assetId");
		column.setColumnName("columnName");
		column.setParentId(1);
		column.setParentAssetid("parent_column_assetId");
		column.setColumnCode("column_code");
		column.setColumnState(1);
		column.setColumnDesc("column_desc");
		column.setColumnSeq(-1);
		column.setColumnPath("column_path");
		column.setColumnType(1);
		column.setUpdateTime(2000);
		column.setSortRule("COLUMN_NAME");
		column.setSortDirection(1);
		column.setRegionCode("zh-CN");
		column.setLanguageCode("zh-CN");
		content.setObj(column);
		content.setOpFlag(1);
		ds.addContent(content);

		content = new DataSynContentEntity();
		FileItemEntity item = new FileItemEntity();
		content.setObj(item);
		item.setId("item_id");
		item.setAssetId("item_asset");
		item.setPgmId(1);
		item.setPgmAssetid("pgm_asset");
		item.setFileType(2);
		item.setDestPath("dest_path");
		item.setDestFilename("dest_file_name");
		item.setFileSize(1000);
		item.setFileMd("file_md");
		item.setFileDesc("file_desc");
		item.setFileDuration(1001);
		item.setIf3d(1);
		item.setVideoQuality(2);
		item.setAspectRatio("800x600");
		item.setBrowseHeight(600);
		item.setBrowseWidth(800);
		item.setComplexRate(21);
		item.setChannelNum(20);
		item.setAucodingFormat("aucoding format");
		item.setVicodingFormat("vicoding format");
		item.setAudioDatarate(300);
		item.setVideoBitrate(200);
		item.setFileFormat("txt");
		item.setCpId(13);
		item.setCpCode("cp_code");
		item.setCpName("cp_name");
		item.setUpdateTime(4231);
		item.setPlayUrl("url");
		content.setObj(item);
		content.setOpFlag(0);
		ds.addContent(content);

		String xml = ds.toXml(null).toString();
		System.out.println(xml);
		assertEquals("<copPortalMsg><header><messageID>msgId</messageID><senderID>sendId</senderID><recvID>recvId</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1000</time><responseUrl>responseUrl</responseUrl><msgType>REQUEST</msgType></header><body><NS_COLUMNTable opFlag='1'><NS_COLUMN><COLUMN_ID>10</COLUMN_ID><ASSET_ID>column_assetId</ASSET_ID><COLUMN_NAME>columnName</COLUMN_NAME><PARENT_ID>1</PARENT_ID><PARENT_ASSETID>parent_column_assetId</PARENT_ASSETID><COLUMN_CODE>column_code</COLUMN_CODE><COLUMN_STATE>1</COLUMN_STATE><COLUMN_DESC>column_desc</COLUMN_DESC><COLUMN_SEQ>-1</COLUMN_SEQ><COLUMN_PATH>column_path</COLUMN_PATH><COLUMN_TYPE>1</COLUMN_TYPE><UPDATE_TIME>2000</UPDATE_TIME><SORT_RULE>COLUMN_NAME</SORT_RULE><SORT_DIRECTION>1</SORT_DIRECTION><REGION_CODE>zh-CN</REGION_CODE><LANGUAGE_CODE>zh-CN</LANGUAGE_CODE></NS_COLUMN></NS_COLUMNTable><NS_FILEITEMTable opFlag='0'><NS_FILEITEM><ID>item_id</ID><ASSET_ID>item_asset</ASSET_ID><PGM_ID>1</PGM_ID><PGM_ASSETID>pgm_asset</PGM_ASSETID><FILE_TYPE>2</FILE_TYPE><DEST_PATH>dest_path</DEST_PATH><DEST_FILENAME>dest_file_name</DEST_FILENAME><FILE_SIZE>1000</FILE_SIZE><FILE_MD>file_md</FILE_MD><FILE_DESC>file_desc</FILE_DESC><FILE_DURATION>1001</FILE_DURATION><IF_3D>1</IF_3D><VIDEO_QUALITY>2</VIDEO_QUALITY><ASPECT_RATIO>800x600</ASPECT_RATIO><BROWSE_HEIGHT>600</BROWSE_HEIGHT><BROWSE_WIDTH>800</BROWSE_WIDTH><COMPLEX_RATE>21</COMPLEX_RATE><FRAME_RATE>0</FRAME_RATE><CHANNEL_NUM>20</CHANNEL_NUM><AUCODING_FORMAT>aucoding format</AUCODING_FORMAT><VICODING_FORMAT>vicoding format</VICODING_FORMAT><AUDIO_DATARATE>300</AUDIO_DATARATE><VIDEO_BITRATE>200</VIDEO_BITRATE><FILE_FORMAT>txt</FILE_FORMAT><CP_ID>13</CP_ID><CP_CODE>cp_code</CP_CODE><CP_NAME>cp_name</CP_NAME><UPDATE_TIME>4231</UPDATE_TIME><PLAY_URL>url</PLAY_URL></NS_FILEITEM></NS_FILEITEMTable></body></copPortalMsg>",
			xml);

		DataSynEntity parseDs = DataSynXmlParse.parseXml(xml);
		assertTrue(ObjectUtil.compare(DataSynEntity.class, ds, parseDs));
	}

}
