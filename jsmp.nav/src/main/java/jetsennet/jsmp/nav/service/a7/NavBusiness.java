package jetsennet.jsmp.nav.service.a7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.config.SysConfig;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.media.db.PlayBillInfoMap;
import jetsennet.jsmp.nav.media.db.PlayBillInfoMap.PlayBillInfoEntry;
import jetsennet.jsmp.nav.service.a7.entity.ChannelSelectionStartRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetChannelsRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetItemDataRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetProgramRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetRootContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.service.a7.entity.SelectionStartRequest;
import jetsennet.jsmp.nav.service.a7.pipeline.INavPlugin;
import jetsennet.jsmp.nav.service.a7.pipeline.NavPipeline;
import jetsennet.jsmp.nav.service.a7.pipeline.NavPipelineContext;
import jetsennet.jsmp.nav.service.a7.pipeline.NavPluginCache;
import jetsennet.jsmp.nav.service.a7.pipeline.NavPluginMonitor;
import jetsennet.jsmp.nav.util.DataMockUtil;
import jetsennet.jsmp.nav.util.DateUtil;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavBusiness
{

	private NavDataHandle navDal;

	private NavPipeline pipeline;
	/**
	 * 方法映射map
	 */
	private static final Map<String, Method> methodMap = NavBusinessUtil.genMethodMap(NavBusiness.class);
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(NavBusiness.class);

	public NavBusiness()
	{
		super();
		this.navDal = new NavDataHandle();
		this.pipeline = new NavPipeline();
		pipeline.addPlugin(new NavPluginMonitor());
		pipeline.addPlugin(new NavPluginCache());
	}

	/**
	 * a7接口相关方法调用
	 * 
	 * @param method
	 * @param map
	 * @return
	 * @throws Throwable
	 */
	public String invoke(String method, Map<String, String> params) throws Throwable
	{
		Method m = methodMap.get(method);
		if (m == null)
		{
			logger.error("不存在方法：" + method);
			throw new UncheckedNavException("不存在方法：" + method);
		}

		String retval = null;

		NavPipelineContext context = pipeline.resetContext(m, params);
		INavPlugin last = pipeline.before();
		if (context.isContinue())
		{
			try
			{
				if (Config.ISDEBUG)
				{
					logger.debug("调用方法：" + m.getName());
				}
				retval = (String) m.invoke(this, new Object[] { params });
				context.setRetObj(retval);
			}
			catch (InvocationTargetException ex)
			{
				logger.info("", ex);
				context.error(ex.getMessage());
			}
		}
		if (!context.isBreakError())
		{
			pipeline.after(last);
		}

		// 出现错误时，抛出异常
		if (context.isError() || context.isBreakError())
		{
			throw new UncheckedNavException(context.getErrorMsg());
		}

		return (String) context.getRetObj();
	}

	@IdentAnnocation("GetUpdateInfo")
	public String GetUpdateInfo(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("UpdateInfo");
		resp.addAttr("appName", "appName");
		resp.addAttr("appType", "appType");
		resp.addAttr("versionName", "versionName");
		resp.addAttr("versionCode", "versionCode");
		resp.addAttr("appUrl", "appUrl");
		resp.addAttr("flag", "flag");
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("NavCheck")
	public String navCheck(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("NavCheckResult");
		resp.addAttr("account", "test_account");
		resp.addAttr("customerGroup", "group_1");
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("GetFolderContents")
	public String getFolderContents(Map<String, String> map)
	{
		GetFolderContentsRequest entity = RequestEntityUtil.map2Obj(GetFolderContentsRequest.class, map);

		ResponseEntity retval = new ResponseEntity("FolderContents");

		// 栏目自身信息FolderFrame
		String assetId = entity.getAssetId();
		ColumnEntity column = navDal.getColumnByAssetId(assetId);
		if (entity.getIncludeFolderProperties())
		{
			ResponseEntity temp = new ResponseEntity("FolderFrame");
			temp.addAttr("assetld", column.getAssetId());
			if (column.getParentId() > 0)
			{
				temp.addAttr("parentAssetld", column.getParentAssetid());
			}
			temp.addAttr("displayName", column.getColumnName());
			temp.addAttr("folderType", Integer.toString(column.getColumnType()));
			retval.addChild(temp);
		}

		// 子栏目ChildFolder
		if (entity.getIncludeSubFolder())
		{
			List<ColumnEntity> cols = navDal.subColumns(column);
			for (ColumnEntity col : cols)
			{
				ResponseEntity tempResp = ResponseEntityUtil.obj2Resp(col, "ChildFolder", null);
				tempResp.addAttr("selectableltemSortby", column.getSortRule());
				tempResp.addAttr("selectableltemSortDirection", Integer.toString(column.getSortDirection()));

				List<PictureEntity> pics = navDal.columnPicturs(col);
				if (pics != null && !pics.isEmpty())
				{
					for (PictureEntity pic : pics)
					{
						ResponseEntity picResp = ResponseEntityUtil.obj2Resp(pic, "Image", null);
						tempResp.addChild(picResp);
					}
				}

				retval.addChild(tempResp);
			}
		}

		// 节目信息ContentItem
		int retStart = -1; // 下一次请求的开始字段
		int retTotal = 0; // 节目总数
		int retSelTotal = 0; // 本次获取的节目数
		if (entity.getIncludeSelectableItem())
		{
			int begin = entity.getStartAt();
			int max = entity.getMaxItems();
			retTotal = navDal.getColumnPgmSize(assetId);
			if (retTotal > 0 && retTotal > begin)
			{
				List<ProgramEntity> pgms = navDal.getColumnPgms(assetId, begin, max);
				for (ProgramEntity pgm : pgms)
				{
					retval.addChild(navDal.getContent(pgm, column));
				}
				retSelTotal = pgms.size();
				// 当还有剩余数据要取时，设在retStart的值
				if (retTotal > (begin + retSelTotal))
				{
					retStart = begin + retSelTotal;
				}
			}
		}

		if (retStart > 0)
		{
			retval.addAttr("restartAtToken", retStart);
		}
		retval.addAttr("currentResults", retSelTotal);
		retval.addAttr("totalResults", retTotal);
		return retval.toXml(null).toString();
	}

	@IdentAnnocation("GetRootContents")
	public String getRootContents(Map<String, String> map)
	{
		GetRootContentsRequest entity = RequestEntityUtil.map2Obj(GetRootContentsRequest.class, map);

		ResponseEntity resp = new ResponseEntity("RootContents");
		// 分页处理
		int begin = entity.getStartAt();
		begin = begin < 0 ? 0 : begin;
		int max = entity.getMaxItems();

		List<ColumnEntity> topColumns = navDal.getTopColumns(entity.getLanguageCode(), entity.getRegionCode(), begin, max);

		for (ColumnEntity column : topColumns)
		{
			ResponseEntity child = ResponseEntityUtil.obj2Resp(column, "ChildFolder", null);
			// 添加图片
			List<PictureEntity> pics = navDal.columnPicturs(column);
			if (pics != null && !pics.isEmpty())
			{
				for (PictureEntity pic : pics)
				{
					ResponseEntity picResp = ResponseEntityUtil.obj2Resp(pic, "Image", null);
					child.addChild(picResp);
				}
			}
			resp.addChild(child);
		}

		int size = resp.getChildSize();
		resp.addAttr("totalResults", size);

		int lastPos = -1;
		if (size > 0 && size == max)
		{
			lastPos = begin + size;
		}
		if (lastPos > 0)
		{
			resp.addAttr("restartAtToken", Integer.toString(lastPos));
		}

		return resp.toXml(null).toString();
	}

	@IdentAnnocation("GetAssociatedFolderContents")
	public String getAssociatedFolderContents(Map<String, String> map)
	{
		throw new UnsupportedOperationException("GetAssociatedFolderContents");
	}

	@IdentAnnocation("GetSavedPrograms")
	public String getSavedPrograms(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("SavedPrograms");
		resp.addAttr("totalResults", "10");
		resp.addAttr("restartAtToken", "-1");
		ResponseEntity temp = DataMockUtil.gen();
		for (int i = 0; i < 10; i++)
		{
			resp.addChild(temp);
		}
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("GetBookmarks")
	public String getBookmarks(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("Bookmarks");
		resp.addAttr("totalResults", "10");
		resp.addAttr("restartAtToken", "-1");
		for (int i = 0; i < 10; i++)
		{
			ResponseEntity temp = new ResponseEntity("BookmarkedItem");
			temp.addAttr("bookmarkedId", i);
			temp.addAttr("markDateTime", "19881122112233");
			temp.addAttr("custom", "custom" + i);
			temp.addAttr("endDateTime", "19881222112233");
			ResponseEntity temp1 = DataMockUtil.gen();
			for (int j = 0; j < 2; j++)
			{
				temp.addChild(temp1);
			}
			resp.addChild(temp);
		}
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("GetItemData")
	public String getItemData(Map<String, String> map)
	{
		GetItemDataRequest req = RequestEntityUtil.map2Obj(GetItemDataRequest.class, map);
		return navDal.getContentItem(navDal.getProgramByAssetId(req.getTitleAssetId()), null).toXml(null).toString();
	}

	@IdentAnnocation("GetEntitlement")
	public String getEntitlement(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("Entitlements");
		for (int i = 0; i < 10; i++)
		{
			ResponseEntity temp = new ResponseEntity("ServiceId");
			temp.addAttr("origin", "SVOD");
			temp.addAttr("name", "name" + i);
			resp.addChild(temp);
		}
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("AddBookmark")
	public String addBookmark(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("AddBookmarkResult");
		resp.addAttr("bookmarkedId", "bookmarkedId1");
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("DeleteBookmark")
	public String deleteBookmark(Map<String, String> map)
	{
		// TODO test
		return "";
	}

	@IdentAnnocation("DeleteSavedProgram")
	public String deleteSavedProgram(Map<String, String> map)
	{
		// TODO test
		return "";
	}

	@IdentAnnocation("SearchValidate")
	public String searchValidate(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("ValidateTitles");
		ResponseEntity temp = DataMockUtil.gen();
		for (int i = 0; i < 10; i++)
		{
			resp.addChild(temp);
		}
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("SelectionStart")
	public String selectionStart(Map<String, String> map)
	{
		SelectionStartRequest req = RequestEntityUtil.map2Obj(SelectionStartRequest.class, map);

		// 返回结果
		ResponseEntity tempResp = new ResponseEntity("StartResponse");

		// 获取数据
		FileItemEntity item = navDal.getFileItemByAssetId(req.getFileAssetId());

		if (item != null)
		{
			String retval = null;
			if (SelectionStartRequest.OTT.equals(req.getServiceCode()))
			{
				retval = item.getPlayUrl();
			}
			else
			{
				// 生成token
				String token = navDal.addSMKey(item.getPlayUrl());
				retval = Config.SM_RTSP + ";purchaseToken=" + token + ";serverID=" + Config.SM_SERVERID;
			}

			tempResp.addAttr("purchaseToken", retval);
		}

		return tempResp.toXml(null).toString();
	}

	@IdentAnnocation("ChannelSelectionStart")
	public String channelSelectionStart(Map<String, String> map)
	{
		ChannelSelectionStartRequest req = RequestEntityUtil.map2Obj(ChannelSelectionStartRequest.class, map);

		// 返回结果
		ResponseEntity tempResp = new ResponseEntity("ChannelSelectionStartResponse");

		// 获取数据
		PlaybillItemEntity item = navDal.getPlayBillItemByAssetId(req.getAssetId());

		if (item != null)
		{
			String retval = null;
			if (SelectionStartRequest.OTT.equals(req.getServiceCode()))
			{
				retval = item.getPlayUrl();
			}
			else
			{
				// 生成token
				String token = navDal.addSMKey(item.getPlayUrl());
				retval = Config.SM_RTSP + ";purchaseToken=" + token + ";serverID=" + Config.SM_SERVERID;
			}
			tempResp.addAttr("purchaseToken", retval);
		}

		return tempResp.toXml(null).toString();
	}

	@IdentAnnocation("SelectionResume")
	public String selectionResume(Map<String, String> map)
	{
		// TODO test
		return "";
	}

	@IdentAnnocation("ValidatePlayEligibility")
	public String validatePlayEligibility(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("PlayEligibility");
		resp.addAttr("price", "10");
		resp.addAttr("orderFlag", "Y");
		resp.addAttr("rentalExpiration", "20131122445533");
		resp.addAttr("previewAssetId", "previewAssetId1");
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("AddSavedProgram")
	public String addSavedProgram(Map<String, String> map)
	{
		// TODO test
		return "";
	}

	@IdentAnnocation("SetResumePoint")
	public String setResumePoint(Map<String, String> map)
	{
		// TODO test
		return "";
	}

	@IdentAnnocation("GetUpsellOffer")
	public String getUpsellOffer(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("GetUpsellOffer");
		resp.addAttr("serviceId", "serviceId1");
		resp.addAttr("title", "test_title_test");
		resp.addAttr("displayPrice", "分");
		return resp.toXml(null).toString();
	}

	@IdentAnnocation("GetChannels")
	public String getChannels(Map<String, String> map)
	{
		GetChannelsRequest req = RequestEntityUtil.map2Obj(GetChannelsRequest.class, map);

		ResponseEntity retval = new ResponseEntity("Channels");

		int start = req.getStartAt() >= 0 ? req.getStartAt() : 0;
		int max = req.getMaxItems();
		int end = start + max;
		List<ChannelEntity> channels = navDal.getChannels(req.getRegionCode(), req.getLanguageCode(), start, max);
		for (ChannelEntity chl : channels)
		{
			ResponseEntity chlResp = ResponseEntityUtil.obj2Resp(chl, "Channel", null);
			retval.addChild(chlResp);

			List<PhysicalChannelEntity> phys = navDal.getPhysicalChannels(chl.getChlId());
			for (PhysicalChannelEntity phy : phys)
			{
				chlResp.addChild(ResponseEntityUtil.obj2Resp(phy, "Parameter", null));
			}
		}
		if (channels.size() == max)
		{
			retval.addAttr("restartAtToken", end);
		}

		return retval.toXml(null).toString();
	}

	@IdentAnnocation("GetPrograms")
	public String getPrograms(Map<String, String> map)
	{
		GetProgramRequest req = RequestEntityUtil.map2Obj(GetProgramRequest.class, map);

		// 频道信息
		String channelIdS = req.getChannelIds().trim();
		List<String> chIds = null;
		if (channelIdS.isEmpty())
		{
			chIds = navDal.getChannelIds(req.getRegionCode(), req.getLanguageCode());
		}
		else
		{
			String[] temp = channelIdS.split(",");
			chIds = new ArrayList<String>(temp.length);
			for (String t : temp)
			{
				chIds.add(t);
			}
		}

		int days = req.getDays();
		days = days > SysConfig.PRE_DATE ? SysConfig.PRE_DATE : days;
		String timeCond = DateUtil.genDataCondition("PLAY_DATE", new Date(), days);

		PlayBillInfoMap pbInfo = navDal.getPbIds(chIds, timeCond);
		int start = req.getStartAt();
		start = start >= 0 ? start : 0;
		int max = req.getMaxItems();
		int end = start + max;
		List<PlaybillItemEntity> pbis = navDal.getPbis(pbInfo.getPbIds(), start, max);
		ResponseEntity retval = new ResponseEntity("Programs");
		for (PlaybillItemEntity pbi : pbis)
		{
			ResponseEntity itemResp = ResponseEntityUtil.obj2Resp(pbi, "Program", null);
			PlayBillInfoEntry info = pbInfo.getInfo(pbi.getPbId());
			itemResp.addAttr("channelId", info.chlAssetId);
			long startTime = info.startTime + pbi.getStartTime();
			long endTime = startTime + pbi.getDuration() * 1000;
			itemResp.addAttr("startDateTime", ResponseEntityUtil.dateFormat(startTime));
			itemResp.addAttr("endDateTime", ResponseEntityUtil.dateFormat(endTime));
			retval.addChild(itemResp);
		}
		if (pbis.size() == max)
		{
			retval.addAttr("restartAtToken", end);
		}
		retval.addAttr("totalResults", retval.getChildSizeS());
		return retval.toXml(null).toString();
	}

	@IdentAnnocation("GetAssociatedPrograms")
	public String getAssociatedPrograms(Map<String, String> map)
	{
		// TODO test
		throw new UnsupportedOperationException("GetAssociatedPrograms");
	}

	@IdentAnnocation("SearchContentInfo")
	public String searchContentInfo(Map<String, String> map)
	{
		// TODO test
		ResponseEntity resp = new ResponseEntity("ContentInfos");
		resp.addAttr("totalResults", 10);
		resp.addAttr("restartAtToken", -1);
		for (int i = 0; i < 10; i++)
		{
			ResponseEntity temp = new ResponseEntity("Content");
			temp.addAttr("providerld", "providerld");
			temp.addAttr("assetld", "assetld" + i);
			temp.addAttr("contentName", "contentName" + i);
			temp.addAttr("contentType", "9");
			temp.addAttr("sortIndex", "1");
			temp.addAttr("actorsDisplay", "player1 player2 player3");
			temp.addAttr("imageLocation", "imageLocation");

			ResponseEntity temp1 = new ResponseEntity("Director", "director1");
			ResponseEntity temp2 = new ResponseEntity("Director", "director2");
			temp.addChild(temp1);
			temp.addChild(temp2);

			ResponseEntity temp3 = new ResponseEntity("Producter", "productor1");
			ResponseEntity temp4 = new ResponseEntity("Producter", "productor2");
			temp.addChild(temp3);
			temp.addChild(temp4);

			resp.addChild(temp);
		}
		return resp.toXml(null).toString();
	}
}
