package jetsennet.jsmp.nav.media.cache;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;

public class ColumnCache extends AbsCache
{

	/**
	 * 更新缓存中的column
	 * 
	 * @param column
	 */
	public static void update(ColumnEntity column)
	{
		cache.put(columnKey(column.getAssetId()), column);

		String assetId = column.getAssetId();
		String key = column.getParentId() == 0 ? topColumn() : subColumn(column.getParentAssetid(), column.getRegionCode());
		List<String> assetIds = cache.getT(key);
		assetIds = assetIds == null ? new ArrayList<String>() : assetIds;
		if (!assetIds.contains(assetId))
		{
			assetIds.add(assetId);
		}
		cache.put(key, assetIds);
	}

	public static void delete(ColumnEntity column)
	{
		cache.del(columnKey(column.getAssetId()));

		String assetId = column.getAssetId();
		String key = column.getParentId() == 0 ? topColumn() : subColumn(column.getParentAssetid(), column.getRegionCode());
		List<String> assetIds = cache.getT(key);
		assetIds = assetIds == null ? new ArrayList<String>() : assetIds;
		assetIds.remove(assetId);
		cache.put(key, assetIds);
	}

	/**
	 * 向缓存中插入column
	 * 
	 * @param column
	 */
	public static void insert(ColumnEntity column)
	{
		cache.put(columnKey(column.getAssetId()), column);
	}

	public static void insertPic(PictureEntity pic)
	{
		String key = columnPicKey(pic.getObjAssetId());
		List<PictureEntity> pics = cache.getT(key);
		if (pics == null)
		{
			pics = new ArrayList<>();
		}
		boolean isUpdate = false;
		for (int i = 0; i < pics.size(); i++)
		{
			PictureEntity temp = pics.get(i);
			if (temp.getPicId().equals(pic.getPicId()))
			{
				pics.set(i, pic);
				isUpdate = true;
				break;
			}
		}
		if (!isUpdate)
		{
			pics.add(pic);
		}
		cache.put(key, pics);
	}

	/**
	 * 删除栏目下所有的图片
	 * @param assetId
	 */
	public static void delPicByColAssetId(String assetId)
	{
		cache.del(columnPicKey(assetId));
	}

	public static void deletePic(PictureEntity pic)
	{
		String key = columnPicKey(pic.getObjAssetId());
		List<PictureEntity> pics = cache.getT(key);
		if (pics != null)
		{
			int pos = -1;
			for (int i = 0; i < pics.size(); i++)
			{
				PictureEntity temp = pics.get(i);
				if (temp.getPicId().equals(pic.getPicId()))
				{
					pos = i;
					break;
				}
			}
			if (pos >= 0)
			{
				pics.remove(pos);
			}
			if (!pics.isEmpty())
			{
				cache.put(key, pics);
			}
			else
			{
				cache.del(key);
			}
		}
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
