package jetsennet.jsmp.nav.service.a7;

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
import jetsennet.jsmp.nav.monitor.MonitorServlet;
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
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavBusiness
{

	/**
	 * 缓存操作
	 */
	//	private DataCacheOp cache = DataCacheOp.getInstance();
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
					methodMap.put(method.getName(), method);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
	}

	public String invoke(String method, Map<String, String> map) throws Exception
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
				retval = (String) m.invoke(this, new Object[] { map });
			}
			catch (Exception ex)
			{
				msg.setException(true);
				throw ex;
			}
			msg.setEndTime(System.currentTimeMillis());
			MonitorServlet.put(msg);
		}
		else
		{
			logger.error("不正确的方法，不存在方法：" + method);
		}
		return retval;
	}

	@IdentAnnocation("NavCheck")
	public String navCheck(Map<String, String> map)
	{
		throw new UnsupportedOperationException("NavCheck");
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
			List<Integer> subIds = NavBusinessDal.subColumnIds(column);
			List<ColumnEntity> cols = NavBusinessDal.getColumns(subIds);
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
			List<Integer> programIds = NavBusinessDal.columnProgramIds(column.getColumnId());
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
					retval.addChild(A7Util.getContentItem(pgm, column));
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
		throw new UnsupportedOperationException("GetSavedPrograms");
	}

	@IdentAnnocation("GetBookmarks")
	public String getBookmarks(Map<String, String> map)
	{
		throw new UnsupportedOperationException("GetBookmarks");
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
		throw new UnsupportedOperationException("GetEntitlement");
	}

	@IdentAnnocation("AddBookmark")
	public String addBookmark(Map<String, String> map)
	{
		throw new UnsupportedOperationException("AddBookmark");
	}

	@IdentAnnocation("DeleteBookmark")
	public String deleteBookmark(Map<String, String> map)
	{
		throw new UnsupportedOperationException("DeleteBookmark");
	}

	@IdentAnnocation("DeleteSavedProgram")
	public String deleteSavedProgram(Map<String, String> map)
	{
		throw new UnsupportedOperationException("DeleteSavedProgram");
	}

	@IdentAnnocation("SearchValidate")
	public String searchValidate(Map<String, String> map)
	{
		throw new UnsupportedOperationException("SearchValidate");
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
		throw new UnsupportedOperationException("SelectionResume");
	}

	@IdentAnnocation("ValidatePlayEligibility")
	public String validatePlayEligibility(Map<String, String> map)
	{
		throw new UnsupportedOperationException("ValidatePlayEligibility");
	}

	@IdentAnnocation("AddSavedProgram")
	public String addSavedProgram(Map<String, String> map)
	{
		throw new UnsupportedOperationException("AddSavedProgram");
	}

	@IdentAnnocation("SetResumePoint")
	public String setResumePoint(Map<String, String> map)
	{
		throw new UnsupportedOperationException("SetResumePoint");
	}

	@IdentAnnocation("GetUpsellOffer")
	public String getUpsellOffer(Map<String, String> map)
	{
		throw new UnsupportedOperationException("GetUpsellOffer");
	}

	@IdentAnnocation("GetChannels")
	public String getChannels(Map<String, String> map)
	{
		GetChannelsRequest req = RequestEntityUtil.map2Obj(GetChannelsRequest.class, map);

		ResponseEntity retval = new ResponseEntity("Channels");

		List<Integer> chIds = NavBusinessDal.getChannelIds(req.getRegionCode(), req.getLanguageCode());
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

				List<PhysicalChannelEntity> phys = NavBusinessDal.getPhysicalChannels(chl.getChlId());
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
		List<Integer> chIds = null;
		if (channelIdS.isEmpty())
		{
			chIds = NavBusinessDal.getChannelIds(req.getRegionCode(), req.getLanguageCode());
		}
		else
		{
			String[] temp = channelIdS.split(",");
			chIds = new ArrayList<Integer>(temp.length);
			for (String t : temp)
			{
				chIds.add(Integer.valueOf(t));
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
		for (Integer chId : chIds)
		{
			for (Long day : dayLst)
			{
				List<Integer> itemIds = NavBusinessDal.getPlayBillItemIds(chId, day);
				for (Integer itemId : itemIds)
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
}
