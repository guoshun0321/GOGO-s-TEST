package jetsennet.jsmp.nav.service.a7;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.entity.ColumnEntity;
import jetsennet.jsmp.nav.entity.CreatorEntity;
import jetsennet.jsmp.nav.entity.FileItemEntity;
import jetsennet.jsmp.nav.entity.PgmBase9Entity;
import jetsennet.jsmp.nav.entity.PictureEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.entity.GetProgramRequest;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.util.DateUtil;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.util.IOUtil;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class A7Util
{

	/**
	 * 将a7请求转换成HashMap
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> requestXml2Map(String xml) throws Exception
	{
		Map<String, String> retval = new HashMap<String, String>();
		InputStream in = null;
		try
		{
			in = new ByteArrayInputStream(xml.getBytes());
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			Element ele = doc.getRootElement();
			List<Attribute> attrs = ele.getAttributes();
			for (Attribute attr : attrs)
			{
				retval.put(attr.getName(), attr.getValue());
			}
		}
		finally
		{
			IOUtil.close(in);
		}
		return retval;
	}

	/**
	 * 解析GetFolderContents的分页参数
	 * 
	 * @param str
	 * @return
	 */
	public static int[] columnAndMoviePageInfo(String str)
	{
		int[] retval = new int[4];
		str = str.substring(1, str.length() - 1);
		String[] couples = str.split(",");
		if (couples.length != 4)
		{
			throw new UncheckedNavException("错误参数：" + str);
		}
		for (int i = 0; i < 4; i++)
		{
			String couple = couples[i];
			String[] tempLst = couple.split(":");
			if (tempLst.length != 2)
			{
				throw new UncheckedNavException("错误参数：" + str);
			}
			retval[i] = Integer.valueOf(tempLst[1].trim());
			// 如果出现非法参数，直接返回[10, 1, 10, 1]
		}
		return retval;
	}

	/**
	 * 生成GetFolderContents分页参数
	 * 
	 * @param str
	 * @return
	 */
	public static String genColumnAndMoviePageInfo(int[] nums)
	{
		if (nums.length != 4)
		{
			throw new UncheckedNavException("GetFolderContents分页参数生成失败！");
		}
		StringBuilder sb = new StringBuilder(100);
		sb.append("{moviePageSize:").append(nums[0]).append(",");
		sb.append("movieCurrentPage:").append(nums[1]).append(",");
		sb.append("folderPageSize:").append(nums[2]).append(",");
		sb.append("folderCurrentPage:").append(nums[3]).append("}");
		return sb.toString();
	}

	/**
	 * 获取节目信息，包括节目的相关节目信息
	 * 
	 * @param prog
	 * @param column
	 * @return
	 */
	public static final ResponseEntity getContentItem(ProgramEntity prog, ColumnEntity column)
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
		retval.addChild(contentFrameResp);

		// 子节目
		List<ProgramEntity> subProgs = NavBusinessDal.getSubPrograms(prog.getPgmId());
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
	public static final ResponseEntity getContent(ProgramEntity prog, ColumnEntity column)
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
		List<PictureEntity> pics = NavBusinessDal.getPgmPictures(prog.getPgmId());
		for (PictureEntity pic : pics)
		{
			if (pic != null)
			{
				retval.addChild(ResponseEntityUtil.obj2Resp(pic, "Image", null));
			}
		}
		return retval;
	}

	/**
	 * 获取节目详细信息
	 * 
	 * @param tempProg
	 * @return
	 */
	public static final ResponseEntity getSelectableltem(ProgramEntity tempProg)
	{
		ResponseEntity resp = ResponseEntityUtil.obj2Resp(tempProg, "Selectableltem", null);

		int pgmId = tempProg.getPgmId();
		PgmBase9Entity pgmBase = NavBusinessDal.getPgmBase(pgmId);
		if (pgmBase != null)
		{
			ResponseEntityUtil.obj2Resp(pgmBase, null, resp);
		}

		List<PictureEntity> pics = NavBusinessDal.getPgmPictures(pgmId);
		for (PictureEntity pic : pics)
		{
			if (pic != null)
			{
				resp.addChild(ResponseEntityUtil.obj2Resp(pic, "Image", null));
			}
		}

		List<FileItemEntity> items = NavBusinessDal.getPgmItems(pgmId);
		for (FileItemEntity item : items)
		{
			if (item != null)
			{
				resp.addChild(ResponseEntityUtil.obj2Resp(item, "SelectionChoice", null));
			}
		}
		return resp;
	}

	public static final void addCreatorInfo(ProgramEntity prog, ResponseEntity resp)
	{
		List<CreatorEntity> creators = NavBusinessDal.getCreators(prog.getPgmId());
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

	public static final List<Long> calPlayListTime(GetProgramRequest req)
	{
		int days = req.getDays();
		List<Long> dayLst = null;
		Date now = new Date();
		int keepedDays = 7;
		if (days == 0)
		{
			dayLst = new ArrayList<Long>(1);
			dayLst.add(DateUtil.getPreTimeOfDay(now, 0));
		}
		else if (days > 0)
		{
			days = days > keepedDays ? keepedDays : days;
			dayLst = new ArrayList<Long>(days + 1);
			for (int i = 0; i <= days; i++)
			{
				dayLst.add(DateUtil.getPreTimeOfDay(now, i));
			}
		}
		else
		{
			dayLst = new ArrayList<Long>(1);
			dayLst.add(DateUtil.getPreTimeOfDay(now, Math.abs(days)));
		}
		return dayLst;
	}

}
