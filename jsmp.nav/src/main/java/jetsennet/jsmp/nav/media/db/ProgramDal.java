package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBase10Entity;
import jetsennet.jsmp.nav.entity.PgmBase11Entity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramDal extends AbsDal
{

	private static final Logger logger = LoggerFactory.getLogger(ProgramDal.class);

	public List<Integer> getPgmIds()
	{
		List<Integer> retval = null;
		try
		{
			String sql = "SELECT PGM_ID FROM NS_PROGRAM";
			retval = dal.queryBusinessObjs(Integer.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<Object> getProgram(int pgmId)
	{
		List<Object> retval = null;
		try
		{
			ProgramEntity pgm = dal.queryBusinessObjByPk(ProgramEntity.class, pgmId);
			if (pgm != null)
			{
				retval = new ArrayList<>();
				retval.add(pgm);

				// 节目基本信息
				int contentType = pgm.getContentType();
				Object temp = null;
				String sql = "SELECT * FROM " + "NS_PGMBASE" + contentType + " WHERE PGM_ID=" + pgmId;
				switch (contentType)
				{
				case ProgramEntity.CONTENT_TYPE_MOVIE:
					temp = dal.queryForObject(PgmBase9Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_TV:
					temp = dal.queryForObject(PgmBase10Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_VARITY:
					temp = dal.queryForObject(PgmBase11Entity.class, sql);
					break;
				default:
					logger.info("未知类型：" + contentType);
				}
				retval.add(temp);

				// 版权信息
				sql = "SELECT * FROM NS_DESCAUTHORIZE WHERE PGM_ID=" + pgmId;
				List<DescauthorizeEntity> authors = dal.queryBusinessObjs(DescauthorizeEntity.class, sql);
				retval.addAll(authors);

				// 责任人信息
				sql = "SELECT * FROM NS_CREATOR WHERE PGM_ID=" + pgmId;
				List<CreatorEntity> creators = dal.queryBusinessObjs(CreatorEntity.class, sql);
				retval.addAll(creators);

				// 图片信息
				sql = "SELECT * FROM NS_FILEITEM WHERE PGM_ID=" + pgmId + " AND FILE_TYPE >= 100";
				List<FileItemEntity> pics = dal.queryBusinessObjs(FileItemEntity.class, sql);
				retval.addAll(pics);

				// 影片信息
				sql = "SELECT * FROM NS_FILEITEM WHERE PGM_ID=" + pgmId + " AND FILE_TYPE < 100";
				List<FileItemEntity> movies = dal.queryBusinessObjs(FileItemEntity.class, sql);
				retval.addAll(movies);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public void deleteProgram(int pgmId)
	{
		try
		{
			SqlCondition cond = new SqlCondition("PGM_ID", Integer.toString(pgmId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
			dal.delete(ProgramEntity.class, cond);
			dal.delete(PgmBase9Entity.class, cond);
			dal.delete(PgmBase10Entity.class, cond);
			dal.delete(PgmBase11Entity.class, cond);
			dal.delete(CreatorEntity.class, cond);
			dal.delete(DescauthorizeEntity.class, cond);
			dal.delete(FileItemEntity.class, cond);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
	}
}
