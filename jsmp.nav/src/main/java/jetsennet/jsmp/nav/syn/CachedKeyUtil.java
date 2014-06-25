package jetsennet.jsmp.nav.syn;

import java.util.ArrayList;
import java.util.List;

public class CachedKeyUtil
{

	// 栏目相关
	public static final String columnKey(int columnId)
	{
		return "COLUMN$" + columnId;
	}

	public static final String columnAssetKey(String assetId)
	{
		return "COLUMN_ASSET$" + assetId;
	}

	public static final List<String> columnKey(List<Integer> columnIds)
	{
		if (columnIds == null)
		{
			return new ArrayList<String>(0);
		}
		List<String> retval = new ArrayList<String>(columnIds.size());
		for (Integer columnId : columnIds)
		{
			retval.add(columnKey(columnId));
		}
		return retval;
	}

	/**
	 * 顶级栏目
	 * 
	 * @return
	 */
	public static final String topColumn()
	{
		return "COLUMN_QUERY_TOP";
	}

	public static final String subColumn(int columnId, String region)
	{
		return "COLUMN_QUERY_SUB$" + columnId + "$" + region;
	}

	public static final String assColumn(int columnId)
	{
		return "COLUMN_QUERY_ASS$" + columnId;
	}

	// 节目相关
	public static final String programKey(int pgmId)
	{
		return "PGM$" + pgmId;
	}

	public static final List<String> programKeys(List<Integer> pgmIds)
	{
		List<String> retval = new ArrayList<String>(pgmIds.size());
		for (Integer pgmId : pgmIds)
		{
			retval.add(programKey(pgmId));
		}
		return retval;
	}

	public static final String programAsset(String assetId)
	{
		return "PGM_ASSETID$" + assetId;
	}

	public static final String columnPgm(int chId)
	{
		return "COLUMN_PGM$" + chId;
	}

	public static final String pgmBaseKey(int pgmId)
	{
		return "PGM_BASE$" + pgmId;
	}

	public static final String pgmCreatorKey(int pgmId)
	{
		return "PGM_CREATOR$" + pgmId;
	}

	public static final String pgmDescAuthorize(int pgmId)
	{
		return "PGM_AUTH$" + pgmId;
	}

	public static final String pgmPicture(int picId)
	{
		return "PGM_PIC$" + picId;
	}

	public static final String pgmPictures(int pgmId)
	{
		return "PGM_PICS$" + pgmId;
	}

	public static final String pgmFileItem(String id)
	{
		return "PGM_FILE$" + id;
	}
	
	public static final String pgmFileItemAsset(String id)
	{
		return "PGM_FILE_ASSET$" + id;
	}

	public static final String pgmFileItems(int pgmId)
	{
		return "PGM_FILES$" + pgmId;
	}

	public static final String pgmChannel(int pgmId)
	{
		return "PGM_CHL$" + pgmId;
	}

	public static final String pgm2pgmKey(int pgmId)
	{
		return "PGM_REL$" + pgmId;
	}

	// 频道相关
	public static final String channelKey(int chlId)
	{
		return "CHL$" + chlId;
	}

	public static final String channelIndex(String region, String lang)
	{
		return "CHLLIST$" + region + "$" + lang;
	}

	public static final String physicalChannelKey(int chlId)
	{
		return "PCHL$" + chlId;
	}

	public static final List<String> physicalChannelKeys(List<Integer> pchlIds)
	{
		List<String> retval = new ArrayList<String>();
		for (Integer pchlId : pchlIds)
		{
			retval.add(physicalChannelKey(pchlId));
		}
		return retval;
	}

	public static final String channel2pchannel(int chlId)
	{
		return "CHL_PCHL$" + chlId;
	}

	// 节目单相关
	public static final String playbillKey(int pbId)
	{
		return "PLAYBILL$" + pbId;
	}

	public static final String channelPlaybill(int chlId, long time)
	{
		return "CHL_PLAYBILL$" + chlId + "$" + time;
	}

	public static final String playbillItemKey(int pbiId)
	{
		return "PLAYBILITEM$" + pbiId;
	}

	public static final String playbillItemList(int pbId)
	{
		return "PLAYBILITEMLIST$" + pbId;
	}
	
	public static final String playbillItemListAsset(String assetId)
	{
		return "PLAYBILITEMLISTASSET$" + assetId;
	}

	// 产品相关
	public static final String productKey(int productId)
	{
		return "PRODUCT$" + productId;
	}

	public static final String productList()
	{
		return "PRODUCT_LIST";
	}

	public static final String productPgm(int productId)
	{
		return "PRODUCT_PGM$" + productId;
	}

	// 播放频道地址
	public static final String selectionStartKey(String uuid)
	{
		return "SELECTION_START$" + uuid;
	}
}
