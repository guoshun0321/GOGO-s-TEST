package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.media.cache.ChannelCache;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IdentAnnocation("channelTable,phyChannelTable")
public class JmsMsgHandleChannel extends AbsJmsMsgHandle
{

	private static final Logger logger = LoggerFactory.getLogger(JmsMsgHandleChannel.class);

	@Override
	public void handleModify(DataSynContentEntity content)
	{
		try
		{
			List<Object> objs = content.getObjs();
			for (Object obj : objs)
			{
				if (obj instanceof ChannelEntity)
				{
					DbHelper.insertOrUpdate(obj);
					ChannelCache.update((ChannelEntity) obj);
				}
				else if (obj instanceof PhysicalChannelEntity)
				{
					DbHelper.insertOrUpdate(obj);
					ChannelCache.insertPhsical((PhysicalChannelEntity) obj);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	@Override
	public void handleDelete(DataSynContentEntity content)
	{
		throw new UncheckedNavException("禁止删除频道信息！");
	}

}
