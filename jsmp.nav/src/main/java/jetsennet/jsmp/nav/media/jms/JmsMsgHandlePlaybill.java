package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import jetsennet.jsmp.nav.entity.PlaybillEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.media.cache.PlaybillCache;
import jetsennet.jsmp.nav.media.db.AbsDal;
import jetsennet.jsmp.nav.media.db.DataSynDbResult;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IdentAnnocation("playbillTable,playbillItemTable")
public class JmsMsgHandlePlaybill extends AbsJmsMsgHandle
{

	private static final Logger logger = LoggerFactory.getLogger(JmsMsgHandlePlaybill.class);

	@Override
	public void handleModify(DataSynContentEntity content) throws Exception
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj instanceof PlaybillEntity)
			{
				PlaybillEntity pb = (PlaybillEntity) obj;
				PlaybillEntity old = (PlaybillEntity) DbHelper.checkObj(obj);
				if (old == null)
				{
					AbsDal.dal.saveBusinessObjs(pb);
				}
				else
				{
					if (pb.getUpdateTime() >= old.getUpdateTime())
					{
						AbsDal.dal.updateBusinessObjs(false, pb);
					}
				}
			}
			else if (obj instanceof PlaybillItemEntity)
			{
				PlaybillItemEntity pbItem = (PlaybillItemEntity) obj;
				PlaybillItemEntity old = (PlaybillItemEntity) DbHelper.checkObj(obj);
				if (old == null)
				{
					AbsDal.dal.saveBusinessObjs(pbItem);
				}
				else
				{
					if (pbItem.getUpdateTime() >= old.getUpdateTime())
					{
						AbsDal.dal.updateBusinessObjs(false, pbItem);
					}
				}
			}
			else
			{
				throw new UncheckedNavException("playbillTable,playbillItemTable不处理数据：" + obj.getClass());
			}
		}
	}

	@Override
	public void handleDelete(DataSynContentEntity content) throws Exception
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj instanceof PlaybillEntity)
			{
				DbHelper.delete(obj);
			}
			else if (obj instanceof PlaybillItemEntity)
			{
				DbHelper.delete(obj);
			}
			else
			{
				throw new UncheckedNavException("playbillTable,playbillItemTable不处理数据：" + obj.getClass());
			}
		}
	}

}
