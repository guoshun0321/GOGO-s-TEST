package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.util.DateUtil;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayBillDal extends AbsDal
{

	public static final int PRE_DATE = 14;

	private static final Logger logger = LoggerFactory.getLogger(PlayBillDal.class);

	public PlaybillEntity getPbByAssetId(String assetId)
	{
		PlaybillEntity retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PLAYBILL WHERE ASSET_ID='" + assetId + "'";
			retval = dal.querySingleObject(PlaybillEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	/**
	 * 获取频道在某个时间范围内的节目ID集合
	 * 
	 * @param chIds
	 * @param timeCond
	 * @return
	 */
	public List<Integer> getPbIds(List<String> chIds, String timeCond)
	{
		StringBuilder sb = new StringBuilder("SELECT PB_ID FROM NS_PLAYBILL WHERE ");
		sb.append(timeCond);
		if (chIds != null && chIds.isEmpty())
		{
			sb.append(" AND CHL_ASSETID IN (");
			for (String chId : chIds)
			{
				sb.append(chId).append(",");
			}
			sb.deleteCharAt(sb.length() - 1).append(")");
		}

		List<Integer> retval = null;
		try
		{
			retval = dal.queryBusinessObjs(Integer.class, sb.toString());
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public List<PlaybillItemEntity> getPbis(List<Integer> pbIds, int begin, int max)
	{
		if (pbIds == null || pbIds.isEmpty())
		{
			return new ArrayList<PlaybillItemEntity>(0);
		}
		StringBuilder sb = new StringBuilder("SELECT * FROM NS_PLAYBILLITEM WHERE PB_ID IN(");
		for (Integer pbId : pbIds)
		{
			sb.append(pbId).append(",");
		}
		sb.deleteCharAt(sb.length() - 1).append(")").append(" LIMIT ").append(begin).append(",").append(max);

		List<PlaybillItemEntity> retval = null;
		try
		{
			retval = dal.queryBusinessObjs(PlaybillItemEntity.class, sb.toString());
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public PlaybillItemEntity getPbiByAssetId(String assetId)
	{
		PlaybillItemEntity retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PLAYBILLITEM WHERE ASSET_ID='" + assetId + "'";
			retval = dal.querySingleObject(PlaybillItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public List<Integer> getRecentPlayBillId()
	{
		List<Integer> retval = null;
		try
		{
			// 获取两个星期的数据
			long firstPlayDate = DateUtil.getPreTimeOfDay(new Date(), PRE_DATE);
			String sql = "SELECT PB_ID FROM NS_PLAYBILL WHERE PLAY_DATE >= " + firstPlayDate;
			retval = dal.queryBusinessObjs(Integer.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public PlaybillEntity get(int pbId)
	{
		PlaybillEntity retval = null;
		try
		{
			retval = dal.queryBusinessObjByPk(PlaybillEntity.class, pbId);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}

	public List<PlaybillItemEntity> getItems(int pbId)
	{
		List<PlaybillItemEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PLAYBILLITEM WHERE PB_ID=" + pbId;
			retval = dal.queryBusinessObjs(PlaybillItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}
}
