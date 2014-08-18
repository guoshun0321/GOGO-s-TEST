package jetsennet.jsmp.nav.media.jms;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.media.db.DataSynDbResult;
import jetsennet.jsmp.nav.media.db.DbHelper;
import jetsennet.jsmp.nav.media.db.SynDbProgram;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

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
				int pgmId = fileItem.getPgmId();
				DataSynDbResult dbRet = DbHelper.insertOrUpdate(fileItem);
				if (dbRet.isValidate())
				{
					SynDbProgram.synProgram(pgmId);
				}
				else
				{
					logger.info("不存在节目：" + pgmId);
				}
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
		throw new UnsupportedOperationException("JmsMsgHandleFile.handleDelete");
	}

}
