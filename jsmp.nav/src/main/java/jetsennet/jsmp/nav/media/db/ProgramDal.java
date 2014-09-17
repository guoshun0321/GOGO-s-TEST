package jetsennet.jsmp.nav.media.db;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBase10Entity;
import jetsennet.jsmp.nav.entity.PgmBase11Entity;
import jetsennet.jsmp.nav.entity.PgmBase12Entity;
import jetsennet.jsmp.nav.entity.PgmBase13Entity;
import jetsennet.jsmp.nav.entity.PgmBase14Entity;
import jetsennet.jsmp.nav.entity.PgmBase15Entity;
import jetsennet.jsmp.nav.entity.PgmBase16Entity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.session.Session;
import jetsennet.orm.sql.FilterUtil;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.Sql;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramDal extends AbsDal
{

	private static final Logger logger = LoggerFactory.getLogger(ProgramDal.class);

	public ProgramEntity getPgmByAssetId(String assetId)
	{
		ProgramEntity retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PROGRAM WHERE ASSET_ID='" + assetId + "'";
			retval = dal.querySingleObject(ProgramEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<ProgramEntity> getPgmsByAssetId(List<String> assetIds)
	{
		List<ProgramEntity> retval = null;
		SelectEntity sql = Sql.select("*").from("NS_PROGRAM").where(FilterUtil.in("ASSET_ID", assetIds));
		Session session = dal.getSession();
		retval = session.query(sql, new RowsResultSetExtractor<>(ProgramEntity.class, session.getTableInfo(ProgramEntity.class)));
		return retval;
	}

	public List<ProgramEntity> getSubPgms(String assetId)
	{
		List<ProgramEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PROGRAM WHERE PARENT_ASSET_ID='" + assetId + "'";
			retval = dal.queryBusinessObjs(ProgramEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<ProgramEntity> getColumnPgms(String assetId, int begin, int max)
	{
		List<ProgramEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_PROGRAM WHERE COLUMN_ASSETID='" + assetId + "' LIMIT " + begin + ", " + max;
			retval = dal.queryBusinessObjs(ProgramEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public int getColumnPgmSize(String assetId)
	{
		int retval = 0;
		try
		{
			String sql = "SELECT COUNT(0) FROM NS_PROGRAM WHERE COLUMN_ASSETID='" + assetId + "'";
			retval = dal.querySingleObject(int.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<String> getPgmAssetIdByColumn(String assetId)
	{
		List<String> retval = null;
		try
		{
			String sql = "SELECT ASSET_ID FROM NS_PROGRAM WHERE PARENT_ASSET_ID='" + assetId + "'";
			retval = dal.queryBusinessObjs(String.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	/**
	 * 获取节目基本信息
	 * 
	 * @param prog
	 * @return
	 */
	public Object getPgmBase(ProgramEntity prog)
	{
		Object retval = null;
		int pgmId = prog.getPgmId();
		int contentType = prog.getContentType();
		String sql = "SELECT * FROM NS_PGMBASE_" + contentType + " WHERE PGM_ID=" + pgmId;
		try
		{
			switch (contentType)
			{
			case ProgramEntity.CONTENT_TYPE_MOVIE:
				retval = dal.querySingleObject(PgmBase9Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_TV:
				retval = dal.querySingleObject(PgmBase10Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_VARITY:
				retval = dal.querySingleObject(PgmBase11Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_COM:
				retval = dal.querySingleObject(PgmBase12Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_DOC:
				retval = dal.querySingleObject(PgmBase13Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_MUSIC:
				retval = dal.querySingleObject(PgmBase14Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_TXT:
				retval = dal.querySingleObject(PgmBase15Entity.class, sql);
				break;
			case ProgramEntity.CONTENT_TYPE_CHL:
				retval = dal.querySingleObject(PgmBase16Entity.class, sql);
				break;
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	/**
	 * 获取节目的图片信息
	 * 
	 * @param pgmId
	 * @return
	 */
	public List<FileItemEntity> getPgmPic(int pgmId)
	{
		List<FileItemEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_FILEITEM WHERE PGM_ID=" + pgmId + " AND FILE_TYPE >" + 200;
			retval = dal.queryBusinessObjs(FileItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<FileItemEntity> getPgmFile(int pgmId)
	{
		List<FileItemEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_FILEITEM WHERE PGM_ID=" + pgmId + " AND FILE_TYPE <=" + 200;
			retval = dal.queryBusinessObjs(FileItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public FileItemEntity getFileByAssetId(String assetId)
	{
		FileItemEntity retval = null;
		try
		{
			String sql = "SELECT * FROM NS_FILEITEM WHERE ASSET_ID=" + assetId;
			retval = dal.querySingleObject(FileItemEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public List<CreatorEntity> getCreator(int pgmId)
	{
		List<CreatorEntity> retval = null;
		try
		{
			String sql = "SELECT * FROM NS_CREATOR WHERE PGM_ID=" + pgmId;
			retval = dal.queryBusinessObjs(CreatorEntity.class, sql);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

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
				String sql = "SELECT * FROM " + "NS_PGMBASE_" + contentType + " WHERE PGM_ID=" + pgmId;
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
				case ProgramEntity.CONTENT_TYPE_COM:
					temp = dal.queryForObject(PgmBase12Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_DOC:
					temp = dal.queryForObject(PgmBase13Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_MUSIC:
					temp = dal.queryForObject(PgmBase14Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_TXT:
					temp = dal.queryForObject(PgmBase15Entity.class, sql);
					break;
				case ProgramEntity.CONTENT_TYPE_CHL:
					temp = dal.queryForObject(PgmBase16Entity.class, sql);
					break;
				default:
					logger.info("未知类型：" + contentType);
				}
				if (temp != null)
				{
					retval.add(temp);
				}

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
			else
			{
				logger.debug("找不到对应ID的PROGRAM：" + pgmId);
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
			String sql = "SELECT * FROM NS_PROGRAM WHRER PGM_ID = " + pgmId;
			ProgramEntity pgm = dal.querySingleObject(ProgramEntity.class, sql);
			if (pgm != null)
			{
				dal.delete(ProgramEntity.class, cond);
				sql = "DELETE FROM NS_PGMBASE_" + pgm.getContentType() + " WHERE PGM_ID=" + pgmId;
				dal.getSession().delete(sql);
				dal.delete(CreatorEntity.class, cond);
				dal.delete(DescauthorizeEntity.class, cond);
				dal.delete(FileItemEntity.class, cond);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
	}
}
