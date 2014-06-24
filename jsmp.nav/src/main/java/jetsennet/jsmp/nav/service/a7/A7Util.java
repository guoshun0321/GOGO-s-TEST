package jetsennet.jsmp.nav.service.a7;

import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmBaseKey;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmFileItem;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmFileItems;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmPicture;
import static jetsennet.jsmp.nav.syn.CachedKeyUtil.pgmPictures;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jetsennet.jsmp.nav.cache.xmem.DataCacheOp;
import jetsennet.jsmp.nav.entity.PgmBaseEntity;
import jetsennet.jsmp.nav.entity.ProgramEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntity;
import jetsennet.jsmp.nav.service.a7.entity.ResponseEntityUtil;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.util.IOUtil;

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
		str = str.substring(1, str.length());
		String[] couples = str.split(",");
		if (couples.length != 4)
		{
			throw new UncheckedNavException("错误参数：" + str);
		}
		for (int i = 0; i < 4; i++)
		{
			String couple = couples[i];
			String[] tempLst = couple.split(":");
			if (couples.length != 2)
			{
				throw new UncheckedNavException("错误参数：" + str);
			}
			retval[i] = Integer.valueOf(tempLst[1]);
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
	 * 获取影片信息
	 * @param prog
	 * @return
	 */
	public static final ResponseEntity getItemInfo(ProgramEntity prog)
	{
		DataCacheOp cache = DataCacheOp.getInstance();
		ResponseEntity resp = ResponseEntityUtil.obj2Resp(prog, "Selectableltem", null);
		PgmBaseEntity pgmBase = cache.get(pgmBaseKey(prog.getPgmId()));
		ResponseEntityUtil.obj2Resp(pgmBase, null, resp);

		List<Integer> picIds = cache.getListInt(pgmPictures(prog.getPgmId()));
		List<String> picKeys = new ArrayList<>(picIds.size());
		for (Integer picId : picIds)
		{
			picKeys.add(pgmPicture(picId));
		}
		Map<String, Object> picMap = cache.gets(picKeys);
		Set<String> keys = picMap.keySet();
		for (String key : keys)
		{
			Object obj = picMap.get(key);
			if (obj != null)
			{
				resp.addChild(ResponseEntityUtil.obj2Resp(obj, "Image", null));
			}
		}

		List<String> itemIds = cache.getListString(pgmFileItems(prog.getPgmId()));
		List<String> itemKeys = new ArrayList<>(itemIds.size());
		for (String itemId : itemIds)
		{
			picKeys.add(pgmFileItem(itemId));
		}
		Map<String, Object> itemMap = cache.gets(itemKeys);
		keys = itemMap.keySet();
		for (String key : keys)
		{
			Object obj = itemMap.get(key);
			if (obj != null)
			{
				resp.addChild(ResponseEntityUtil.obj2Resp(obj, "SelectionChoice", null));
			}
		}
		return resp;
	}

}
