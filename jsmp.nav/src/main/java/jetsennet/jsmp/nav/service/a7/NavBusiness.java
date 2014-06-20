package jetsennet.jsmp.nav.service.a7;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jetsennet.jsmp.nav.cache.xmem.MemcachedOp;
import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.monitor.MonitorMsg;
import jetsennet.jsmp.nav.monitor.MethodInvokeMMsg;
import jetsennet.jsmp.nav.monitor.MonitorServlet;
import jetsennet.jsmp.nav.service.a7.entity.A7Constants;
import jetsennet.jsmp.nav.service.a7.entity.GetChannelsRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetFolderContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetItemDataRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetProgramRequest;
import jetsennet.jsmp.nav.service.a7.entity.GetRootContentsRequest;
import jetsennet.jsmp.nav.service.a7.entity.SelectionStartRequest;
import jetsennet.jsmp.nav.service.a7.entity.RequestEntityUtil;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.syn.CachedKeyUtil;
import jetsennet.jsmp.nav.syn.cache.DataSynCache;
import jetsennet.jsmp.nav.syn.cache.DataSynCacheColumn;
import jetsennet.jsmp.nav.util.DateUtil;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavBusiness
{

	/**
	 * 缓存操作
	 */
	private MemcachedOp cache = MemcachedOp.getInstance();
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

		// 栏目自身信息
		ColumnEntity column = cache.get(columnKey(cache.getInt(columnAssetKey(entity.getAssetId()))));
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
		}

		int[] pageInfo = null;
		if (entity.getStartAt().isEmpty())
		{
			pageInfo = new int[] { 10, 1, 10, 1 };
		}
		else
		{
			pageInfo = A7Util.columnAndMoviePageInfo(entity.getStartAt());
		}

		int[] retPageInfo = new int[] { pageInfo[0], -1, pageInfo[2], -1 };
		int total = 0;
		int selTotal = 0;
		// 子栏目
		if (entity.getIncludeSubFolder())
		{
			int start = (pageInfo[3] - 1) * pageInfo[2];
			int end = start + pageInfo[2];
			List<Integer> subIds = cache.getListInt(CachedKeyUtil.columnPgm(column.getColumnId()));
			int totalSizeSub = subIds.size();
			total += totalSizeSub;
			subIds = subIds.subList(start, end);
			if (totalSizeSub > end)
			{
				retPageInfo[3] = end;
			}
			List<String> tempKeys = columnKey(subIds);
			selTotal += tempKeys.size();
			Map<String, Object> subMap = cache.gets(tempKeys);
			for (String tempKey : tempKeys)
			{
				ColumnEntity temp = (ColumnEntity) subMap.get(tempKey);
				ResponseEntity tempResp = ResponseEntityUtil.obj2Resp(temp, "ChildFolder", null);
				tempResp.addAttr("selectableltemSortby", column.getSortRule());
				tempResp.addAttr("selectableltemSortDirection", Integer.toString(column.getSortDirection()));
			}
		}

		// 影片
		if (entity.getIncludeSelectableItem())
		{
			int start = (pageInfo[3] - 1) * pageInfo[2];
			int end = start + pageInfo[2];
			List<Integer> programIds = cache.getListInt(CachedKeyUtil.columnPgm(column.getColumnId()));
			int totalSizePgm = programIds.size();
			total += totalSizePgm;
			programIds = programIds.subList(start, end);
			if (totalSizePgm > end)
			{
				retPageInfo[1] = end;
			}
			List<String> tempKeys = programKeys(programIds);
			selTotal += tempKeys.size();
			Map<String, Object> pgmMap = cache.gets(tempKeys);
			for (String tempKey : tempKeys)
			{
				ProgramEntity pgm = (ProgramEntity) pgmMap.get(tempKey);
				retval.addChild(NavBusinessUtil.getItemInfo(pgm));
			}
		}

		retval.addAttr("restartAtToken", A7Util.genColumnAndMoviePageInfo(retPageInfo));
		retval.addAttr("currentResults", Integer.toString(selTotal));
		retval.addAttr("totalResults", Integer.toString(total));
		return retval.toXml(null).toString();
	}

	@IdentAnnocation("GetRootContents")
	public String getRootContents(Map<String, String> map)
	{
		GetRootContentsRequest entity = RequestEntityUtil.map2Obj(GetRootContentsRequest.class, map);
		List<Integer> topKeys = cache.getListInt(CachedKeyUtil.topColumn());
		List<String> topKeyStrs = CachedKeyUtil.columnKey(topKeys);
		Map<String, Object> objMap = cache.gets(topKeyStrs);

		ResponseEntity resp = new ResponseEntity("RootContents");
		// 分页处理
		int begin = entity.getStartAt();
		begin = begin < 0 ? 0 : begin;
		// 最后一个的后一位
		int end = begin + entity.getMaxItems();
		int lastPos = -1;
		for (int i = 0, j = -1; i < topKeyStrs.size(); i++)
		{
			String topKeyStr = topKeyStrs.get(i);
			Object obj = (Object) objMap.get(topKeyStr);
			if (obj != null)
			{
				ColumnEntity column = (ColumnEntity) obj;
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
							ResponseEntity child = ResponseEntityUtil.obj2Resp(obj, "ChildFolder", null);
							resp.addChild(child);
						}
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
		ProgramEntity prog = cache.get(CachedKeyUtil.programAsset(req.getTitleAssetId()));
		return NavBusinessUtil.getItemInfo(prog).toXml(null).toString();
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
		ProgramEntity prog = cache.get(CachedKeyUtil.programAsset(req.getTitleAssetId()));
		
		// 生成token
		String token = UUID.randomUUID().toString();
		String key = CachedKeyUtil.selectionStartKey(token);
		cache.putTimeout(key, null, Config.SM_TIMEOUT);
		
		// 返回结果
		ResponseEntity tempResp = new ResponseEntity("StartResponse");
		tempResp.addAttr("purchaseToken", token);
		tempResp.addAttr("previewAssetId", token);
		return tempResp.toXml(null).toString();
	}

	@IdentAnnocation("ChannelSelectionStart")
	public String channelSelectionStart(Map<String, String> map)
	{
		throw new UnsupportedOperationException("ChannelSelectionStart");
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
		int start = req.getStartAt();
		start = start >= 0 ? start : 0;
		int max = req.getMaxItems();
		int end = start + max;
		int i = 0;
		List<Integer> chIds = cache.getListInt(channelIndex(req.getRegionCode(), req.getLanguageCode()));
		for (Integer chId : chIds)
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
					ChannelEntity chl = cache.get(channelKey(chId));
					retval.addChild(ResponseEntityUtil.obj2Resp(chl, "Channel", null));
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
			chIds = cache.getListInt(channelIndex(req.getRegionCode(), req.getLanguageCode()));
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
		int days = req.getDays();
		List<Long> dayLst = null;
		Date now = new Date();
		int keepedDays = 7;
		if (days == 0)
		{
			dayLst = new ArrayList<Long>(1);
			dayLst.add(DateUtil.getPreTimeOfDay(now, 0));
		}
		else if (days > 0)
		{
			days = days > keepedDays ? keepedDays : days;
			dayLst = new ArrayList<Long>(days + 1);
			for (int i = 0; i <= days; i++)
			{
				dayLst.add(DateUtil.getPreTimeOfDay(now, i));
			}
		}
		else
		{
			dayLst = new ArrayList<Long>(1);
			dayLst.add(DateUtil.getPreTimeOfDay(now, Math.abs(days)));
		}

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
				PlaybillEntity chl = cache.get(CachedKeyUtil.channelPlaybill(chId, day), true);
				if (chl != null)
				{
					List<Integer> itemIds = cache.getListInt(CachedKeyUtil.playbillItemList(chl.getPbId()));
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
								PlaybillItemEntity item = cache.get(CachedKeyUtil.playbillItemKey(itemId));
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
