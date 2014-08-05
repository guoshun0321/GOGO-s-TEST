package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.media.cache.ColumnCache;
import jetsennet.orm.session.Session;

public class SynDbColumn implements ISynDb
{

	@Override
	public void syn() throws Exception
	{
		Session session = DataSourceManager.MEDIA_FACTORY.openSession();

		String sql = "SELECT * FROM NS_COLUMN";
		List<ColumnEntity> columns = session.query(sql, ColumnEntity.class);
		// 顶级栏目ID
		List<String> tops = new ArrayList<>(20);
		// 栏目之间的关系
		Map<String, List<String>> subMap = new LinkedHashMap<>();
		for (ColumnEntity column : columns)
		{
			// 更新栏目数据
			ColumnCache.insert(column);
			int parentId = column.getParentId();
			String pAssetId = column.getParentAssetid();
			if (parentId == 0)
			{
				tops.add(column.getAssetId());
			}
			else
			{
				String key = ColumnCache.subColumn(pAssetId, column.getRegionCode());
				List<String> subs = subMap.get(key);
				if (subs == null)
				{
					subs = new ArrayList<>();
					subMap.put(key, subs);
				}
				subs.add(column.getAssetId());
			}
		}
		// 更新顶级栏目
		ColumnCache.insertTopColumn(tops);
		// 更新栏目之间的关系
		Set<String> keys = subMap.keySet();
		for (String key : keys)
		{
			ColumnCache.insertSub(key, subMap.get(key));
		}

		// 栏目图片
		sql = "SELECT * FROM NS_PICTURE";
		List<PictureEntity> pics = session.query(sql, PictureEntity.class);
		Map<String, List<String>> picMap = new LinkedHashMap<>();
		for (PictureEntity pic : pics)
		{
			ColumnCache.insertPic(pic);
			String key = ColumnCache.picColumn(pic.getObjId());
			List<String> subPics = picMap.get(key);
			if (subPics == null)
			{
				subPics = new ArrayList<>();
			}
			subPics.add(pic.getAssetId());
		}

		// 栏目和图片的关系
		keys = picMap.keySet();
		for (String key : keys)
		{
			ColumnCache.insertPic2Column(key, picMap.get(key));
		}
	}

}
