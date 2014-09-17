package jetsennet.jsmp.nav.service.a7;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.util.IOUtil;

public class NavServiceGet extends HttpServlet
{

	private static final NavBusiness busi = new NavBusiness();
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(NavServiceGet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		OutputStream out = resp.getOutputStream();
		try
		{
			if (Config.ISDEBUG)
			{
				logger.debug("GET : " + req.getContextPath() + "?" + req.getQueryString());
			}
			String method = req.getParameter("method");
			if (method != null)
			{
				Map<String, String> map = parseRequest(req);
				try
				{
					String str = busi.invoke(method, map);
					str = str == null ? "" : str;
					resp.setHeader("Content-type", "text/xml;charset=UTF-8");
					out.write(str.getBytes("UTF-8"));
					out.flush();
				}
				catch (Throwable ex)
				{
					logger.error("", ex);
					NavErrorHandle.illegalRequest(resp, ex, null);
				}
			}
			else
			{
				String msg = "找不到method参数！";
				logger.error(msg);
				NavErrorHandle.illegalRequest(resp, msg);
			}
		}
		finally
		{
			IOUtil.close(out);
		}
	}

	private Map<String, String> parseRequest(HttpServletRequest req)
	{
		Map<String, String[]> map = req.getParameterMap();
		Map<String, String> retval = new HashMap<String, String>(map.size());
		Set<String> keys = map.keySet();
		for (String key : keys)
		{
			String[] values = map.get(key);
			if (values != null && values.length > 0)
			{
				retval.put(key, values[0]);
			}
		}
		return retval;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		logger.error("NavServiceGet不支持POST操作！");
		NavErrorHandle.illegalRequest(resp, "NavServiceGet不支持POST操作！");
	}

}
