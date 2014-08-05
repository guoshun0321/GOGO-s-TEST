package jetsennet.jsmp.nav.media.db;

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

	public List<Integer> getRecentPlayBillId()
	{
		List<Integer> retval = null;
		try
		{
			// 获取两个星期的数据
			long firstPlayDate = DateUtil.getPreTimeOfDay(new Date(), PRE_DATE);
			String sql = "SELECT PB_ID FROM NS_PALYBILL WHERE PLAY_DATE >= " + firstPlayDate;
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
			String sql = "SELECT * FROM NS_PALYBILLITEM WHERE PB_ID=" + pbId;
			retval = dal.queryBusinessObjs(PlaybillItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			throw new UncheckedOrmException(ex);
		}
		return retval;
	}
}
