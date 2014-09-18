package jetsennet.jsmp.nav.media.db;

import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.orm.util.UncheckedOrmException;

public class ColumnDal extends AbsDal
{

	public ColumnEntity getByAssetId(String assetId)
	{
		ColumnEntity retval = null;
		try
		{
			String sql = "SELECT * FROM NS_COLUMN WHERE ASSET_ID = '" + assetId + "'";
			retval = dal.querySingleObject(ColumnEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	/**
	 * 获取顶级栏目
	 * 
	 * @param languageCode 语言编码
	 * @param regionCode 地区编码
	 * @param begin 开始记录
	 * @param size 记录条数
	 * @return
	 */
	public List<ColumnEntity> getTopColumns(String languageCode, String regionCode, int begin, int size)
	{
		List<ColumnEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_COLUMN WHERE PARENT_ID = 0";
			if (languageCode != null && !languageCode.isEmpty())
			{
				sql += " AND LANGUAGE_CODE = '" + languageCode + "'";
			}
			if (regionCode != null && !regionCode.isEmpty())
			{
				sql += " AND REGION_CODE = '" + regionCode + "'";
			}
			sql += " LIMIT " + begin + "," + size;
			retval = dal.queryBusinessObjs(ColumnEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public List<ColumnEntity> getSubColumn(int pColumnId, String region)
	{
		List<ColumnEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_COLUMN WHERE PARENT_ID = " + pColumnId;
			if (region != null && !region.trim().isEmpty())
			{
				sql += " AND REGION_CODE = '" + region + "'";
			}
			retval = dal.queryBusinessObjs(ColumnEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public List<PictureEntity> getPictrues(String assetId)
	{
		List<PictureEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PICTURE WHERE OBJ_ASSETID = '" + assetId + "'";
			retval = dal.queryBusinessObjs(PictureEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

}
