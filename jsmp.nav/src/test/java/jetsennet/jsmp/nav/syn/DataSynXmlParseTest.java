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
//		item.setId("item_id");
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
//		item.setComplexRate(21);
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
		assertEquals("<copPortalMsg><header><messageID>msgId</messageID><senderID>sendId</senderID><recvID>recvId</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1000</time><responseUrl>responseUrl</responseUrl><msgType>REQUEST</msgType></header><body><NS_COLUMNTable opFlag='1'><NS_COLUMN><COLUMN_ID>10</COLUMN_ID><ASSET_ID>column_assetId</ASSET_ID><COLUMN_NAME>columnName</COLUMN_NAME><PARENT_ID>1</PARENT_ID><PARENT_ASSETID>parent_column_assetId</PARENT_ASSETID><COLUMN_CODE>column_code</COLUMN_CODE><COLUMN_STATE>1</COLUMN_STATE><COLUMN_DESC>column_desc</COLUMN_DESC><COLUMN_SEQ>-1</COLUMN_SEQ><COLUMN_PATH>column_path</COLUMN_PATH><COLUMN_TYPE>1</COLUMN_TYPE><UPDATE_TIME>2000</UPDATE_TIME><SORT_RULE>COLUMN_NAME</SORT_RULE><SORT_DIRECTION>1</SORT_DIRECTION><REGION_CODE>zh-CN</REGION_CODE><LANGUAGE_CODE>zh-CN</LANGUAGE_CODE></NS_COLUMN></NS_COLUMNTable><NS_FILEITEMTable opFlag='0'><NS_FILEITEM><FILE_ID>item_id</FILE_ID><ASSET_ID>item_asset</ASSET_ID><PGM_ID>1</PGM_ID><PGM_ASSETID>pgm_asset</PGM_ASSETID><FILE_TYPE>2</FILE_TYPE><DEST_PATH>dest_path</DEST_PATH><DEST_FILENAME>dest_file_name</DEST_FILENAME><FILE_SIZE>1000</FILE_SIZE><FILE_MD>file_md</FILE_MD><FILE_DESC>file_desc</FILE_DESC><FILE_DURATION>1001</FILE_DURATION><IF_3D>1</IF_3D><VIDEO_QUALITY>2</VIDEO_QUALITY><ASPECT_RATIO>800x600</ASPECT_RATIO><BROWSE_HEIGHT>600</BROWSE_HEIGHT><BROWSE_WIDTH>800</BROWSE_WIDTH><COMPLEX_RATE>21</COMPLEX_RATE><FRAME_RATE>0</FRAME_RATE><CHANNEL_NUM>20</CHANNEL_NUM><AUCODING_FORMAT>aucoding format</AUCODING_FORMAT><VICODING_FORMAT>vicoding format</VICODING_FORMAT><AUDIO_DATARATE>300</AUDIO_DATARATE><VIDEO_BITRATE>200</VIDEO_BITRATE><FILE_FORMAT>txt</FILE_FORMAT><CP_ID>13</CP_ID><CP_CODE>cp_code</CP_CODE><CP_NAME>cp_name</CP_NAME><UPDATE_TIME>4231</UPDATE_TIME><PLAY_URL>url</PLAY_URL></NS_FILEITEM></NS_FILEITEMTable></body></copPortalMsg>",
			xml);

		DataSynEntity parseDs = DataSynXmlParse.parseXml(xml);
		assertTrue(ObjectUtil.compare(DataSynEntity.class, ds, parseDs));
		
		xml = "<copPortalMsg><header><messageID>1405993557164</messageID><sendID>JCOP</sendID><recvID>Portal</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1405993557164</time><responseUrl>null</responseUrl><msgType>REQUEST</msgType></header><body><columnTable opFlag=\"0\"><NS_COLUMN><COLUMN_ID>48</COLUMN_ID><ASSET_ID>JETSENf92e47ce-63d6-4cfb-867c-2170c5de5446COL</ASSET_ID><COLUMN_NAME>people</COLUMN_NAME><PARENT_ID>109</PARENT_ID><PARENT_ASSETID>JETSENN201451cd4098-0858-457e-a7fb-9afb4c6ddbcaCOL</PARENT_ASSETID><COLUMN_CODE>lm010</COLUMN_CODE><COLUMN_STATE>0</COLUMN_STATE><COLUMN_DESC>null</COLUMN_DESC><COLUMN_SEQ>3</COLUMN_SEQ><COLUMN_PATH>×ÛºÏ/people</COLUMN_PATH><COLUMN_TYPE>1</COLUMN_TYPE><SORT_RULE>null</SORT_RULE><SORT_DIRECTION>null</SORT_DIRECTION><REGION_CODE>1</REGION_CODE><LANGUAGE_CODE>1</LANGUAGE_CODE><UPDATE_TIME>1405993557164</UPDATE_TIME></NS_COLUMN></columnTable></body></copPortalMsg>";
		parseDs = DataSynXmlParse.parseXml(xml);
		assertNotNull(parseDs);
		
		xml = "<copPortalMsg><header><messageID>1406013641585</messageID><sendID>JCOP</sendID><recvID>Portal</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1406013641585</time><responseUrl>null</responseUrl><msgType>REQUEST</msgType></header><body><programTable opFlag=\"0\"><NS_PROGRAM><PGM_ID>559</PGM_ID><ASSET_ID>jetsenLLMO0000000000000065</ASSET_ID><PGM_NAME>穿长靴的猫0709</PGM_NAME><PGM_TYPE>0</PGM_TYPE><CONTENT_TYPE>9</CONTENT_TYPE><PGM_STATE>10</PGM_STATE><OBJ_TYPE>1</OBJ_TYPE><PGM_DURATION>0</PGM_DURATION><SEARCH_LETTER>null</SEARCH_LETTER><KEY_WORDS>null</KEY_WORDS><CONTENT_INFO>null</CONTENT_INFO><EPISODES_NUMBER>0</EPISODES_NUMBER><LICENSING_START>1404702182000</LICENSING_START><LICENSING_END>1406791896000</LICENSING_END><UPDATE_TIME>1406013641585</UPDATE_TIME><SEQ_NO>999999</SEQ_NO><REGION_CODE>1</REGION_CODE><CLICK_NUM>0</CLICK_NUM><CP_ID>12</CP_ID><CP_CODE>jetsen</CP_CODE><CP_NAME>捷成媒资</CP_NAME><COLUMN_ASSETID>JETSEN53041b06-bd47-47ad-aa3a-fe291d5cf3dbCOL</COLUMN_ASSETID><SITE_ID>31</SITE_ID></NS_PROGRAM></programTable><baseTable opFlag=\"0\"><NS_PGMBASE><ID>559</ID><PGM_ID>559</PGM_ID><PGM_ASSETID>jetsenLLMO0000000000000065</PGM_ASSETID><ENGLISH_NAME>Transformers:Age of Extion</ENGLISH_NAME><ALIAS>Transformers:Age of Extion</ALIAS><COUNTRY>1</COUNTRY><WATCH_FOCUS>1</WATCH_FOCUS><PRODUCT_COMPANY>派拉蒙影业公司</PRODUCT_COMPANY><RESTRICT_CATEGORY>1</RESTRICT_CATEGORY><AGENCY>1</AGENCY><DETAIL_INFO>《变形金刚4：绝迹重生》官方6月12日发布全球终极预告片，时长2分09秒。李冰冰、韩庚、巫刚等中国影星终于出镜。</DETAIL_INFO><RECOMMEND_STAR>null</RECOMMEND_STAR><LABEL>null</LABEL><LANGUAGE>null</LANGUAGE><AWARDS>奥斯卡最佳题名奥斯卡最佳题名</AWARDS><ISSUE_YEAR>2014</ISSUE_YEAR><ISSUE_COMPANY>null</ISSUE_COMPANY><CONTENT_PRIORITY>null</CONTENT_PRIORITY><RELEASE_DATE>2014-07-22</RELEASE_DATE><BOX>0</BOX><DIALOGUE>1</DIALOGUE><CRITICS_LINK>1</CRITICS_LINK><BROADCAST_NUM>0</BROADCAST_NUM><IS_FINISHED>0</IS_FINISHED><SOURCE>null</SOURCE><LAUNCH_DATE>null</LAUNCH_DATE><PLAY_CYCLE>null</PLAY_CYCLE><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_PGMBASE></baseTable><creatorTable opFlag=\"0\"><NS_CREATOR><ID>3E222108-6478-4AF7-A99D-79F0FEEC77FB_559</ID><PGM_ID>559</PGM_ID><NAME>迈克尔·贝</NAME><PARALLEL_NAME>33</PARALLEL_NAME><ROLE_MODE>42</ROLE_MODE><DESCRIPTION>22</DESCRIPTION><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_CREATOR><NS_CREATOR><ID>6D77F704-4906-4AAD-99E7-C3B0B4F5D866_559</ID><PGM_ID>559</PGM_ID><NAME>史蒂文·斯皮尔卡伯格</NAME><PARALLEL_NAME>33</PARALLEL_NAME><ROLE_MODE>53</ROLE_MODE><DESCRIPTION>22</DESCRIPTION><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_CREATOR><NS_CREATOR><ID>1946D47B-5FBD-4044-96C4-FA4EA0233168_559</ID><PGM_ID>559</PGM_ID><NAME>伊伦·克鲁格</NAME><PARALLEL_NAME>333</PARALLEL_NAME><ROLE_MODE>49</ROLE_MODE><DESCRIPTION>22</DESCRIPTION><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_CREATOR><NS_CREATOR><ID>0C953E44-3E88-4B33-BF15-5D9598A7F77A_559</ID><PGM_ID>559</PGM_ID><NAME>宏达股份</NAME><PARALLEL_NAME>22</PARALLEL_NAME><ROLE_MODE>43</ROLE_MODE><DESCRIPTION>23</DESCRIPTION><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_CREATOR></creatorTable><copyRightTable opFlag=\"0\"><NS_DESCAUTHORIZE><ID>58B4F483-0370-49CB-BB1B-7F3F8A6B0E4D_559</ID><PGM_ID>559</PGM_ID><AUTHOR_USER>1</AUTHOR_USER><AUTHOR_USAGE>1</AUTHOR_USAGE><START_DATE>2014-07-07 11:03:02</START_DATE><AUTHOR_DEADLINE>2014-07-31 15:31:36</AUTHOR_DEADLINE><AUTHOR_GEOGRAAREA>深圳</AUTHOR_GEOGRAAREA><TIMES_USAGE>12</TIMES_USAGE><DESCRIPTION>null</DESCRIPTION><UPDATE_TIME>1406013641585</UPDATE_TIME></NS_DESCAUTHORIZE></copyRightTable></body></copPortalMsg>";
		parseDs = DataSynXmlParse.parseXml(xml);
		assertNotNull(parseDs);
		
		xml = "<copPortalMsg><header><messageID>1406099306154</messageID><sendID>JCOP</sendID><recvID>Portal</recvID><opCode>COP_PORTAL_META_PUB</opCode><time>1406099306154</time><responseUrl>null</responseUrl><msgType>REQUEST</msgType></header><body><columnTable opFlag=\"0\"><NS_COLUMN><COLUMN_ID>93</COLUMN_ID><ASSET_ID>JETSEN8f408a7d-af16-4438-b655-4176e729fd87COL</ASSET_ID><COLUMN_NAME>½»»»Ê±¼ä</COLUMN_NAME><PARENT_ID>59</PARENT_ID><PARENT_ASSETID>JETSENa8b9b66b-9f96-4d83-ae0c-09b21058e02fCOL</PARENT_ASSETID><COLUMN_CODE>a17</COLUMN_CODE><COLUMN_STATE>0</COLUMN_STATE><COLUMN_DESC>null</COLUMN_DESC><COLUMN_SEQ>17</COLUMN_SEQ><COLUMN_PATH>²Æ¾­/½»»» Ê±¼ä</COLUMN_PATH><COLUMN_TYPE>10</COLUMN_TYPE><SORT_RULE>null</SORT_RULE><SORT_DIRECTION>null</SORT_DIRECTION><REGION_CODE>null</REGION_CODE><LANGUAGE_CODE>null</LANGUAGE_CODE><UPDATE_TIME>1406099306154</UPDATE_TIME></NS_COLUMN></columnTable></body></copPortalMsg>";
		parseDs = DataSynXmlParse.parseXml(xml);
		assertNotNull(parseDs);
	}

}
