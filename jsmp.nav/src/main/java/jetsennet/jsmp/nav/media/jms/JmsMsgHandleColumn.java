package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

@IdentAnnocation("columnTable,pictureTable")
public class JmsMsgHandleColumn extends AbsJmsMsgHandle
{

	@Override
	public void handleModify(DataSynContentEntity content)
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj instanceof ColumnEntity)
			{
				DbHelper.insertOrUpdate(obj);
			}
			else if (obj instanceof PictureEntity)
			{
				DbHelper.insertOrUpdate(obj);
			}
			else
			{
				throw new UncheckedNavException("columnTable,pictureTable不处理数据：" + obj.getClass());
			}
		}
	}

	@Override
	public void handleDelete(DataSynContentEntity content)
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj instanceof ColumnEntity)
			{
				DbHelper.delete(obj);
			}
			else if (obj instanceof PictureEntity)
			{
				DbHelper.delete(obj);
			}
		}
	}

}
