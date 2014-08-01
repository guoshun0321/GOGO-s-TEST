package jetsennet.jsmp.nav.media.cache;

import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;

public class ColumnCache extends AbsCache
{

	public static void insert(ColumnEntity column)
	{
		cache.put(columnKey(column.getAssetId()), column);
	}

	public static void insertPic(PictureEntity pic)
	{
		cache.put(columnPicKey(pic.getAssetId()), pic);
	}

	public static void insertTopColumn(List<String> tops)
	{
		cache.put(topColumn(), tops);
	}

	public static void insertSub(String key, List<String> subs)
	{
		cache.put(key, subs);
	}

	public static void insertPic2Column(String assetId, List<String> picIds)
	{
		cache.put(assetId, picIds);
	}

	public static final String columnKey(String assetId)
	{
		return "COLUMN$" + assetId;
	}

	public static final String columnPicKey(String assetId)
	{
		return "COLUMN_PIC$" + assetId;
	}

	//	public static final String columnAssetKey(String assetId)
	//	{
	//		return "COLUMN_ASSET$" + assetId;
	//	}

	//	public static final List<String> columnKey(List<Integer> columnIds)
	//	{
	//		if (columnIds == null)
	//		{
	//			return new ArrayList<String>(0);
	//		}
	//		List<String> retval = new ArrayList<String>(columnIds.size());
	//		for (Integer columnId : columnIds)
	//		{
	//			retval.add(columnKey(columnId));
	//		}
	//		return retval;
	//	}

	/**
	 * 顶级栏目
	 * 
	 * @return
	 */
	public static final String topColumn()
	{
		return "COLUMN_QUERY_TOP";
	}

	public static final String subColumn(String pColumnAssetId, String region)
	{
		return "COLUMN_QUERY_SUB$" + pColumnAssetId + "$" + region;
	}

	public static final String picColumn(String columnAssetId)
	{
		return "PIC2COLUMN$" + columnAssetId;
	}

}
