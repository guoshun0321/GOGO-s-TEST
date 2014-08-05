package jetsennet.jsmp.nav.service.a7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.monitor.MethodInvokeMMsg;
import jetsennet.jsmp.nav.monitor.Monitor;
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
import jetsennet.jsmp.nav.util.ArrayUtil;
import jetsennet.jsmp.nav.util.DataMockUtil;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavBusiness
{

	/**
	 * 方法映射map
	 */
	private static Map<String, Method> methodMap;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(NavBusiness.class);

	static
	{
		try
		{
			methodMap = new HashMap<String, Method>();
			Method[] methods = NavBusiness.class.getMethods();
			for (Method method : methods)
			{
				if (method.isAnnotationPresent(IdentAnnocation.class))
				{
					methodMap.put(method.getAnnotation(IdentAnnocation.class).value(), method);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
	}

	public String invoke(String method, Map<String, String> map) throws Throwable
	{
		String retval = null;
		Method m = methodMap.get(method);
		if (m != null)
		{
			MethodInvokeMMsg msg = new MethodInvokeMMsg();
			msg.setStartTime(System.currentTimeMillis());
			msg.setMethodName(m.getName());
			try
			{
				if (Config.ISDEBUG)
				{
					logger.debug("调用方法：" + m.getName());
				}
				retval = (String) m.invoke(this, new Object[] { map });
			}
			catch (InvocationTargetException ex)
			{
				msg.setException(true);
				throw ex.getTargetException();
			}
			msg.setEndTime(System.currentTimeMillis());
			Monitor.getInstance().put(msg);
		}
		else
		{
			logger.error("不存在方法：" + method);
			throw new UncheckedNavException("不存在方法：" + method);
		}
		return retval;
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
		ColumnEntity column = NavBusinessDal.getColumnByAssetId(entity.getAssetId());
		if (entity.getIncludeFolderProperties())
		{
			ResponseEntity temp = new ResponseEntity("FolderFrame");
			temp.addAttr("assetld", column.getAssetId());
			if (column.getParentId() >= 0)
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
			List<ColumnEntity> cols = NavBusinessDal.subColumns(column);
			for (ColumnEntity col : cols)
			{
				ResponseEntity tempResp = ResponseEntityUtil.obj2Resp(col, "ChildFolder", null);
				tempResp.addAttr("selectableltemSortby", column.getSortRule());
				tempResp.addAttr("selectableltemSortDirection", Integer.toString(column.getSortDirection()));
				retval.addChild(tempResp);
			}
		}

		// 节目信息ContentItem
		int retStart = -1;
		int retTotal = 0;
		int retSelTotal = 0;
		if (entity.getIncludeSelectableItem())
		{
			List<String> programIds = NavBusinessDal.columnProgramIds(column.getAssetId());
			if (programIds != null && !programIds.isEmpty())
			{
				// 分页信息
				int start = entity.getStartAt();
				int end = start + entity.getMaxItems();
				retTotal = programIds.size();
				if (retTotal > end)
				{
					retStart = end;
				}

				// 填充ContentItem数据
				List<ProgramEntity> pgms = NavBusinessDal.getPrograms(ArrayUtil.subList(programIds, start, end));
				for (ProgramEntity pgm : pgms)
				{
					retval.addChild(A7Util.getContent(pgm, column));
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

		List<ColumnEntity> topColumns = NavBusinessDal.getTopColumns();

		ResponseEntity resp = new ResponseEntity("RootContents");
		// 分页处理
		int begin = entity.getStartAt();
		begin = begin < 0 ? 0 : begin;
		// 最后一个的后一位
		int end = begin + entity.getMaxItems();
		int lastPos = -1;

		int j = -1;
		for (ColumnEntity column : topColumns)
		{
			if (column.getLanguageCode().equals(entity.getLanguageCode()) && column.getRegionCode().equals(entity.getRegionCode()))
			{
				j++;
				if (j >= begin)
				{
					if (j == end)
					{
						lastPos = j;
						break;
					}
					else
					{
						ResponseEntity child = ResponseEntityUtil.obj2Resp(column, "ChildFolder", null);
						resp.addChild(child);
					}
				}
			}

		}
		resp.addAttr("totalResults", Integer.toString(resp.getChildSize()));
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
		return A7Util.getContentItem(NavBusinessDal.getProgramByAssetId(req.getTitleAssetId()), null).toXml(null).toString();
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

		// 获取数据
		FileItemEntity item = NavBusinessDal.getFileItemByAssetId(req.getFileAssetId());

		String retval = null;
		if (SelectionStartRequest.OTT.equals(req.getServiceCode()))
		{
			retval = item.getPlayUrl();
		}
		else
		{
			// 生成token
			String token = NavBusinessDal.addSMKey(item.getPlayUrl());
			retval = Config.SM_RTSP + ";purchaseToken=" + token + ";serverID=" + Config.SM_SERVERID;
		}

		// 返回结果
		ResponseEntity tempResp = new ResponseEntity("StartResponse");
		tempResp.addAttr("purchaseToken", retval);
		//		tempResp.addAttr("previewAssetId", token);
		return tempResp.toXml(null).toString();
	}

	@IdentAnnocation("ChannelSelectionStart")
	public String channelSelectionStart(Map<String, String> map)
	{
		ChannelSelectionStartRequest req = RequestEntityUtil.map2Obj(ChannelSelectionStartRequest.class, map);

		// 获取数据
		PlaybillItemEntity item = NavBusinessDal.getPlayBillItemByAssetId(req.getAssetId());

		String retval = null;
		if (SelectionStartRequest.OTT.equals(req.getServiceCode()))
		{
			retval = item.getPlayUrl();
		}
		else
		{
			// 生成token
			String token = NavBusinessDal.addSMKey(item.getPlayUrl());
			retval = Config.SM_RTSP + ";purchaseToken=" + token + ";serverID=" + Config.SM_SERVERID;
		}

		// 返回结果
		ResponseEntity tempResp = new ResponseEntity("ChannelSelectionStartResponse");
		tempResp.addAttr("purchaseToken", retval);
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
		return "";
	}

	@IdentAnnocation("SetResumePoint")
	public String setResumePoint(Map<String, String> map)
	{
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

		List<String> chIds = NavBusinessDal.getChannelIds(req.getRegionCode(), req.getLanguageCode());
		if (chIds == null)
		{
			throw new UncheckedNavException("获取频道列表失败！");
		}
		int start = req.getStartAt() >= 0 ? req.getStartAt() : 0;
		int max = req.getMaxItems();
		int end = start + max;
		if (end < chIds.size())
		{
			retval.addAttr("restartAtToken", end);
		}
		chIds = ArrayUtil.subList(chIds, start, end);
		if (chIds != null)
		{
			List<ChannelEntity> channels = NavBusinessDal.getChannels(chIds);

			for (ChannelEntity chl : channels)
			{
				ResponseEntity chlResp = ResponseEntityUtil.obj2Resp(chl, "Channel", null);
				retval.addChild(chlResp);

				List<PhysicalChannelEntity> phys = NavBusinessDal.getPhysicalChannels(chl.getAssetId());
				for (PhysicalChannelEntity phy : phys)
				{
					chlResp.addChild(ResponseEntityUtil.obj2Resp(phy, "Parameter", null));
				}
			}
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
			chIds = NavBusinessDal.getChannelIds(req.getRegionCode(), req.getLanguageCode());
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

		// 日期信息
		List<Long> dayLst = A7Util.calPlayListTime(req);

		// 分页
		ResponseEntity retval = new ResponseEntity("Programs");
		int start = req.getStartAt();
		start = start >= 0 ? start : 0;
		int max = req.getMaxItems();
		int end = start + max;
		int i = 0;
		for (String chId : chIds)
		{
			for (Long day : dayLst)
			{
				List<String> itemIds = NavBusinessDal.getPlayBillItemIds(chId, day);
				for (String itemId : itemIds)
				{
					if (i >= start)
					{
						if (i == end)
						{
							retval.addAttr("restartAtToken", Integer.toString(i));
							break;
						}
						else
						{
							PlaybillItemEntity item = NavBusinessDal.getPalyBillItem(itemId);
							ResponseEntity itemResp = ResponseEntityUtil.obj2Resp(item, "Program", null);
							itemResp.addAttr("channelId", chId.toString());
							long startTime = day + item.getStartTime();
							long endTime = startTime + item.getDuration() * 1000;
							itemResp.addAttr("startDateTime", ResponseEntityUtil.dateFormat(startTime));
							itemResp.addAttr("endDateTime", ResponseEntityUtil.dateFormat(endTime));
							retval.addChild(itemResp);
						}
					}
					i++;
				}
			}
		}
		retval.addAttr("totalResults", retval.getChildSizeS());
		return retval.toXml(null).toString();
	}

	@IdentAnnocation("GetAssociatedPrograms")
	public String getAssociatedPrograms(Map<String, String> map)
	{
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
