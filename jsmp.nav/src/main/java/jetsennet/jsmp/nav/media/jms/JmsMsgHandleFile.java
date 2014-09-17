package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IdentAnnocation("fileTable")
public class JmsMsgHandleFile extends AbsJmsMsgHandle
{

	private static final Logger logger = LoggerFactory.getLogger(JmsMsgHandleFile.class);

	@Override
	public void handleModify(DataSynContentEntity content) throws Exception
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj != null && obj instanceof FileItemEntity)
			{
				FileItemEntity fileItem = (FileItemEntity) obj;
				DbHelper.insertOrUpdate(fileItem);
			}
			else
			{
				throw new UncheckedNavException("fileTable只处理FileItemEntity");
			}
		}
	}

	@Override
	public void handleDelete(DataSynContentEntity content) throws Exception
	{
		List<Object> objs = content.getObjs();
		for (Object obj : objs)
		{
			if (obj != null && obj instanceof FileItemEntity)
			{
				FileItemEntity fileItem = (FileItemEntity) obj;
				DbHelper.delete(fileItem);
			}
			else
			{
				throw new UncheckedNavException("fileTable只处理FileItemEntity");
			}
		}
	}

}
