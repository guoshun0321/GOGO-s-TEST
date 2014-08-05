package jetsennet.jsmp.nav.media.cache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.DescauthorizeEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.Pgm2PgmEntity;
import jetsennet.jsmp.nav.entity.PgmBase10Entity;
import jetsennet.jsmp.nav.entity.PgmBase11Entity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.util.UncheckedNavException;

public class ProgramCache extends AbsCache
{

	private static final Logger logger = LoggerFactory.getLogger(ProgramCache.class);

	public static void insert(List<Object> objs)
	{

		// 先确定ProgramEntity的数据
		ProgramEntity pgm = null;
		int pgmId = -1;
		for (Object obj : objs)
		{
			if (obj instanceof ProgramEntity)
			{
				pgm = (ProgramEntity) obj;
				cache.put(programKey(pgm.getPgmId()), pgm);
				cache.put(programAsset(pgm.getAssetId()), pgm);
				break;
			}
		}
		if (pgm == null)
		{
			throw new UncheckedNavException("数据中不包含ProgramEntity数据，数据不合法。");
		}
		else
		{
			pgmId = pgm.getPgmId();
		}

		// 插入节目相关信息
		List<CreatorEntity> creators = new ArrayList<>();
		List<FileItemEntity> pics = new ArrayList<>();
		List<FileItemEntity> files = new ArrayList<>();
		for (Object obj : objs)
		{
			if (obj instanceof ProgramEntity)
			{
				if (Config.ISDEBUG)
				{
					logger.debug("忽略数据：" + obj);
				}
			}
			else if (obj instanceof PgmBase9Entity)
			{
				PgmBase9Entity base = (PgmBase9Entity) obj;
				cache.put(pgmBaseKey(pgmId), base);
			}
			else if (obj instanceof PgmBase10Entity)
			{
				PgmBase10Entity base = (PgmBase10Entity) obj;
				cache.put(pgmBaseKey(pgmId), base);
			}
			else if (obj instanceof PgmBase11Entity)
			{
				PgmBase11Entity base = (PgmBase11Entity) obj;
				cache.put(pgmBaseKey(pgmId), base);
			}
			else if (obj instanceof DescauthorizeEntity)
			{
				DescauthorizeEntity desc = (DescauthorizeEntity) obj;
				cache.put(pgmDescAuthorize(pgmId), desc);
			}
			else if (obj instanceof CreatorEntity)
			{
				creators.add((CreatorEntity) obj);
			}
			else if (obj instanceof FileItemEntity)
			{
				FileItemEntity file = (FileItemEntity) obj;
				if (file.getFileType() < 100)
				{
					files.add(file);
				}
				else
				{
					pics.add(file);
				}
				cache.put(pgmFileItemAsset(file.getAssetId()), file);
			}
			else
			{
				logger.error("ProgramCache不处理数据：" + obj);
			}
		}
		if (!files.isEmpty())
		{
			cache.put(pgmFileItemKey(pgmId), files);
		}
		if (!pics.isEmpty())
		{
			cache.put(pgmPictureKey(pgmId), pics);
		}
		if (!creators.isEmpty())
		{
			cache.put(pgmCreatorKey(pgmId), creators);
		}

		// 节目和栏目的关系，节目和节目的关系
		if (pgm != null)
		{
			int columnId = pgm.getColumnId();
			String columnAssetId = pgm.getColumnAssetid();
			if (columnId != 0)
			{
				List<Integer> columnPgmIds = cache.getT(columnPgm(columnAssetId));
				if (columnPgmIds == null)
				{
					columnPgmIds = new ArrayList<>();
				}
				if (!columnPgmIds.contains(Integer.valueOf(pgmId)))
				{
					columnPgmIds.add(pgmId);
				}
				cache.put(columnPgm(columnAssetId), columnPgmIds);
			}
		}
	}

	public static void delete(ProgramEntity pgm)
	{
		int pgmId = pgm.getPgmId();
		cache.del(programKey(pgmId));
		cache.del(programAsset(pgm.getAssetId()));
		cache.del(pgmBaseKey(pgmId));
		cache.del(pgmDescAuthorize(pgmId));
		cache.del(pgmFileItemKey(pgmId));
		cache.del(pgmPictureKey(pgmId));

		int columnId = pgm.getColumnId();
		String columnAssetId = pgm.getColumnAssetid();
		if (columnId != 0)
		{
			List<Integer> columnPgmIds = cache.get(columnPgm(columnAssetId));
			if (columnPgmIds == null)
			{
				columnPgmIds = new ArrayList<>();
			}
			columnPgmIds.remove(Integer.valueOf(pgmId));
			cache.put(columnPgm(columnAssetId), columnPgmIds);
		}
	}

	public static void insertPgm2Pgm(Pgm2PgmEntity obj)
	{
		cache.put(pgm2pgmKey(obj.getPgmId()), obj);
	}

	public static void updatePgm2Pgm(Pgm2PgmEntity obj)
	{
		cache.put(pgm2pgmKey(obj.getPgmId()), obj);

	}

	public static void deletePgm2Pgm(Pgm2PgmEntity obj)
	{
		cache.del(pgm2pgmKey(obj.getPgmId()));
	}

	public static final String programKey(int pgmId)
	{
		return "PGM$" + pgmId;
	}

	public static final String programAsset(String assetId)
	{
		return "PGM_ASSETID$" + assetId;
	}

	public static final String columnPgm(String assetId)
	{
		return "COLUMN_PGM$" + assetId;
	}

	public static final String pgmBaseKey(int pgmId)
	{
		return "PGM_BASE$" + pgmId;
	}

	public static final String pgmCreatorKey(int pgmId)
	{
		return "PGM_CREATOR$" + pgmId;
	}

	public static final String pgmDescAuthorize(int pgmId)
	{
		return "PGM_AUTH$" + pgmId;
	}

	public static final String pgmPictureKey(int pgmId)
	{
		return "PGM_PIC$" + pgmId;
	}

	public static final String pgmPictures(int pgmId)
	{
		return "PGM_PICS$" + pgmId;
	}

	public static final String pgmFileItemKey(int pgmId)
	{
		return "PGM_FILE$" + pgmId;
	}

	public static final String pgmFileItemAsset(String id)
	{
		return "PGM_FILE_ASSET$" + id;
	}

	public static final String pgmFileItems(int pgmId)
	{
		return "PGM_FILES$" + pgmId;
	}

	public static final String pgmChannel(int pgmId)
	{
		return "PGM_CHL$" + pgmId;
	}

	public static final String pgm2pgmKey(int pgmId)
	{
		return "PGM_REL$" + pgmId;
	}

}
