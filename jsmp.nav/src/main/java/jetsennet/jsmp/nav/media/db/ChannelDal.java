package jetsennet.jsmp.nav.media.db;

import java.util.List;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.orm.session.Session;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.FilterUtil;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelDal extends AbsDal
{

	private static final Logger logger = LoggerFactory.getLogger(ChannelDal.class);

	public List<ChannelEntity> getChannels(String region, String lang, int begin, int max)
	{
		SelectEntity sel = Sql.select("*").from("NS_CHANNEL");
		FilterNode temp = null;
		if (region != null && !region.isEmpty())
		{
			temp = FilterUtil.eq("REGION_CODE", region);
		}
		if (lang != null && !lang.isEmpty())
		{
			if (temp != null)
			{
				temp.and(FilterUtil.eq("LANGUAGE_CODE", lang));
			}
			else
			{
				temp = FilterUtil.eq("LANGUAGE_CODE", lang);
			}
		}
		if (temp != null)
		{
			sel.where(temp);
		}
		Session session = dal.getSession();
		String sql = session.trans(sel);
		sql = sql + " LIMIT " + begin + ", " + max;

		List<ChannelEntity> retval = null;
		try
		{
			retval = dal.queryBusinessObjs(ChannelEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<String> getChannelIds(String region, String lang)
	{
		SelectEntity sel = Sql.select("ASSET_ID").from("NS_CHANNEL");
		FilterNode temp = null;
		if (region != null && !region.isEmpty())
		{
			temp = FilterUtil.eq("REGION_CODE", region);
		}
		if (lang != null && !lang.isEmpty())
		{
			if (temp != null)
			{
				temp.and(FilterUtil.eq("LANGUAGE_CODE", lang));
			}
			else
			{
				temp = FilterUtil.eq("LANGUAGE_CODE", lang);
			}
		}
		if (temp != null)
		{
			sel.where(temp);
		}
		Session session = dal.getSession();
		String sql = session.trans(sel);

		List<String> retval = null;
		try
		{
			retval = dal.queryBusinessObjs(String.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	/**
	 * 获取频道对应的物理频道
	 * 
	 * @param chlId
	 * @return
	 */
	public List<PhysicalChannelEntity> getPChannels(int chlId)
	{
		List<PhysicalChannelEntity> retval = null;
		String sql = "SELECT * FROM NS_PHYSICALCHANNEL WHERE CHL_ID=" + chlId;
		try
		{
			retval = dal.queryBusinessObjs(PhysicalChannelEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

}
