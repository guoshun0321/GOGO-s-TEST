package jetsennet.jsmp.nav.service.a7;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.config.SysConfig;
import jetsennet.jsmp.nav.entity.ChannelEntity;
import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PhysicalChannelEntity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.PlaybillItemEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.media.db.ChannelDal;
import jetsennet.jsmp.nav.media.db.ColumnDal;
import jetsennet.jsmp.nav.media.db.PlayBillDal;
import jetsennet.jsmp.nav.media.db.PlayBillInfoMap;
import jetsennet.jsmp.nav.media.db.ProgramDal;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.xmem.XmemcachedException;
import jetsennet.jsmp.nav.xmem.XmemcachedUtil;

public class NavDataHandle
{

	private ColumnDal columnDal;
	private ProgramDal pgmDal;
	private PlayBillDal pbDal;
	private ChannelDal chlDal;

	public NavDataHandle()
	{
		this.columnDal = new ColumnDal();
		this.pgmDal = new ProgramDal();
		this.pbDal = new PlayBillDal();
		this.chlDal = new ChannelDal();
	}

	public List<ColumnEntity> getTopColumns(String languageCode, String regionCode, int begin, int size)
	{
		List<ColumnEntity> retval = columnDal.getTopColumns(languageCode, regionCode, begin, size);
		return retval == null ? new ArrayList<ColumnEntity>(0) : retval;
	}

	public List<ColumnEntity> subColumns(ColumnEntity column)
	{
		List<ColumnEntity> retval = columnDal.getSubColumn(column.getColumnId(), column.getRegionCode());
		return retval == null ? new ArrayList<ColumnEntity>(0) : retval;
	}

	public List<PictureEntity> columnPicturs(ColumnEntity column)
	{
		List<PictureEntity> retval = columnDal.getPictrues(column.getAssetId());
		return retval == null ? new ArrayList<PictureEntity>(0) : retval;
	}

	public ColumnEntity getColumnByAssetId(String assetId)
	{
		return columnDal.getByAssetId(assetId);
	}

	public List<String> columnProgramIds(String assetId)
	{
		List<String> retval = pgmDal.getPgmAssetIdByColumn(assetId);
		return retval == null ? new ArrayList<String>(0) : retval;
	}

	public ProgramEntity getProgramByAssetId(String assetId)
	{
		return pgmDal.getPgmByAssetId(assetId);
	}

	public List<ProgramEntity> getColumnPgms(String assetId, int begin, int max)
	{
		return pgmDal.getColumnPgms(assetId, begin, max);
	}

	public int getColumnPgmSize(String assetId)
	{
		return pgmDal.getColumnPgmSize(assetId);
	}

	public List<ProgramEntity> getPrograms(List<String> pgmAssetIds)
	{
		return pgmDal.getPgmsByAssetId(pgmAssetIds);
	}

	public List<ProgramEntity> getSubPrograms(int parentId)
	{
		List<ProgramEntity> retval = pgmDal.getSubPgms(parentId);
		return retval == null ? new ArrayList<ProgramEntity>(0) : retval;
	}

	/**
	 * 获取节目详细信息，包括节目的相关节目信息
	 * 
	 * @param prog
	 * @param column
	 * @return
	 */
	public ResponseEntity getContentItem(ProgramEntity prog, ColumnEntity column)
	{
		ResponseEntity retval = new ResponseEntity("ContentItem");
		if (prog == null)
		{
			return retval;
		}

		ResponseEntity contentFrameResp = new ResponseEntity("ContentFrame");

		contentFrameResp.addAttr("assetld", prog.getAssetId());
		if (column != null)
		{
			contentFrameResp.addAttr("folderAssetId", column.getAssetId());
		}
		contentFrameResp.addAttr("contentName", prog.getPgmName());
		contentFrameResp.addAttr("contentType", prog.getContentType());
		contentFrameResp.addAttr("sortIndex", prog.getSeqNo());
		contentFrameResp.addAttr("area", prog.getRegionCode());
		contentFrameResp.addAttr("summarvShort", prog.getContentInfo());
		// 添加导演/制片/演员信息
		addCreatorInfo(prog, contentFrameResp);
		// 添加图片信息
		List<FileItemEntity> pics = this.getPgmPictures(prog.getPgmId());
		for (FileItemEntity pic : pics)
		{
			if (pic != null)
			{
				ResponseEntity picResp = new ResponseEntity("Image");
				picResp.addAttr("posterUrl", pic.getFilePath());
				contentFrameResp.addChild(picResp);
			}
		}
		retval.addChild(contentFrameResp);

		// 子节目
		List<ProgramEntity> subProgs = this.getSubPrograms(prog.getParentId());
		if (subProgs != null && subProgs.size() > 0)
		{
			// 如果存在子节目，添加子节目信息
			for (ProgramEntity subProg : subProgs)
			{
				ResponseEntity tempResp = getSelectableltem(subProg);
				addCreatorInfo(subProg, tempResp);
				retval.addChild(tempResp);
			}
		}
		else
		{
			// 如果不存在子节目，添加节目本身的信息
			retval.addChild(getSelectableltem(prog));
		}
		return retval;
	}

	/**
	 * 获取节目基本信息
	 * 
	 * @param prog
	 * @param column
	 * @return
	 */
	public ResponseEntity getContent(ProgramEntity prog, ColumnEntity column)
	{
		ResponseEntity retval = new ResponseEntity("Content");
		if (prog == null)
		{
			return retval;
		}

		retval.addAttr("assetld", prog.getAssetId());
		if (column != null)
		{
			retval.addAttr("folderAssetId", column.getAssetId());
		}
		retval.addAttr("contentName", prog.getPgmName());
		retval.addAttr("contentType", prog.getContentType());
		retval.addAttr("sortIndex", prog.getSeqNo());

		// 添加导演/制片/演员信息
		addCreatorInfo(prog, retval);

		// 添加图片信息
		List<FileItemEntity> pics = this.getPgmPictures(prog.getPgmId());
		for (FileItemEntity pic : pics)
		{
			if (pic != null)
			{
				ResponseEntity picResp = new ResponseEntity("Image");
				picResp.addAttr("posterUrl", pic.getFilePath());
				retval.addChild(picResp);
			}
		}
		return retval;
	}

	/**
	 * 添加创作者信息
	 * 
	 * @param prog
	 * @param resp
	 */
	public void addCreatorInfo(ProgramEntity prog, ResponseEntity resp)
	{
		List<CreatorEntity> creators = this.getCreators(prog.getPgmId());
		StringBuilder actorSb = new StringBuilder();
		if (creators != null && !creators.isEmpty())
		{
			for (CreatorEntity creator : creators)
			{
				String roleMode = creator.getRoleMode();
				if (roleMode.equals(CreatorEntity.MODE_HERO) || roleMode.equals(CreatorEntity.MODE_HEROINE))
				{
					actorSb.append(creator.getName()).append(" ");
				}
				else if (roleMode.equals(CreatorEntity.MODE_DIRECTOR))
				{
					resp.addChild(new ResponseEntity("Director", creator.getName()));
				}
				else if (roleMode.equals(CreatorEntity.MODE_PRODUCER))
				{
					resp.addChild(new ResponseEntity("Producter", creator.getName()));
				}
			}
		}
		if (actorSb.length() > 0)
		{
			actorSb.deleteCharAt(actorSb.length() - 1);
		}
		resp.addAttr("actorsDisplay", actorSb.toString());
	}

	/**
	 * 获取节目详细信息
	 * 
	 * @param tempProg
	 * @return
	 */
	public ResponseEntity getSelectableltem(ProgramEntity tempProg)
	{
		ResponseEntity resp = ResponseEntityUtil.obj2Resp(tempProg, "Selectableltem", null);

		int pgmId = tempProg.getPgmId();
		Object pgmBase = this.getPgmBase(tempProg);
		if (pgmBase != null)
		{
			ResponseEntityUtil.obj2Resp(pgmBase, null, resp);
		}

		// 获取图片信息
		List<FileItemEntity> pics = this.getPgmPictures(pgmId);
		for (FileItemEntity pic : pics)
		{
			if (pic != null)
			{
				ResponseEntity picResp = new ResponseEntity("Image");
				picResp.addAttr("posterUrl", pic.getFilePath());
				resp.addChild(picResp);
			}
		}

		// 获取视频信息
		List<FileItemEntity> items = this.getPgmItems(pgmId);
		for (FileItemEntity item : items)
		{
			if (item != null)
			{
				resp.addChild(ResponseEntityUtil.obj2Resp(item, "SelectionChoice", null));
			}
		}
		return resp;
	}

	public Object getPgmBase(ProgramEntity pgm)
	{
		return pgmDal.getPgmBase(pgm);
	}

	public List<FileItemEntity> getPgmPictures(int pgmId)
	{
		List<FileItemEntity> retval = pgmDal.getPgmPic(pgmId);
		return retval == null ? new ArrayList<FileItemEntity>(0) : retval;
	}

	public List<FileItemEntity> getPgmItems(int pgmId)
	{
		List<FileItemEntity> retval = pgmDal.getPgmFile(pgmId);
		return retval == null ? new ArrayList<FileItemEntity>(0) : retval;
	}

	public List<CreatorEntity> getCreators(int pgmId)
	{
		List<CreatorEntity> retval = pgmDal.getCreator(pgmId);
		return retval == null ? new ArrayList<CreatorEntity>(0) : retval;
	}

	public FileItemEntity getFileItemByAssetId(String assetId)
	{
		return pgmDal.getFileByAssetId(assetId);
	}

	public PlaybillItemEntity getPlayBillItemByAssetId(String assetId)
	{
		return pbDal.getPbiByAssetId(assetId);
	}

	public String addSMKey(String playUrl)
	{
		String token = UUID.randomUUID().toString();
		String key = SysConfig.selectionStartKey(token);
		XmemcachedUtil.getInstance().putTimeout(key, playUrl, Config.SM_TIMEOUT);
		return token;
	}

	public List<String> getChannelIds(String region, String lang)
	{
		List<String> retval = chlDal.getChannelIds(region, lang);
		return retval == null ? new ArrayList<String>(0) : retval;
	}

	public List<ChannelEntity> getChannels(String region, String lang, int begin, int max)
	{
		List<ChannelEntity> retval = chlDal.getChannels(region, lang, begin, max);
		return retval == null ? new ArrayList<ChannelEntity>(0) : retval;
	}

	public PlayBillInfoMap getPbIds(List<String> chIds, String timeCond)
	{
		return pbDal.getPbIds(chIds, timeCond);
	}

	public List<PlaybillItemEntity> getPbis(List<Integer> pbIds, int begin, int max)
	{
		List<PlaybillItemEntity> retval = pbDal.getPbis(pbIds, begin, max);
		return retval == null ? new ArrayList<PlaybillItemEntity>(0) : retval;
	}

	public List<PhysicalChannelEntity> getPhysicalChannels(int chlId)
	{
		List<PhysicalChannelEntity> retval = chlDal.getPChannels(chlId);
		return retval == null ? new ArrayList<PhysicalChannelEntity>(0) : retval;
	}

}
