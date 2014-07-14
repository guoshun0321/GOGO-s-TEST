package jetsennet.jsmp.nav.service.a7;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetsennet.jsmp.nav.util.ServletUtil;
import jetsennet.util.IOUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavServicePost extends HttpServlet
{

	private static final NavBusiness busi = new NavBusiness();
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(NavServicePost.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		logger.error("NavServicePost不支持GET操作！");
		ErrorHandle.illegalRequest(resp, "NavServicePost不支持GET操作！");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		OutputStream out = resp.getOutputStream();

		try
		{
			String method = req.getRequestURI();
			int pos = method.lastIndexOf("/");
			if (pos > 0 && pos != (method.length() - 1))
			{
				method = method.substring(pos + 1);
				try
				{
					Map<String, String> map = A7Util.requestXml2Map(ServletUtil.getStream(req));
					String str = busi.invoke(method, map);
					resp.setHeader("Content-type", "text/html;charset=UTF-8");
					out.write(str.getBytes("UTF-8"));
					out.flush();
				}
				catch (Throwable ex)
				{
					logger.error("", ex);
					ErrorHandle.illegalRequest(resp, ex, null);
				}
			}
			else
			{
				String msg = "不合法的方法名称：" + method;
				logger.error(msg);
				ErrorHandle.illegalRequest(resp, msg);
			}
		}
		finally
		{
			IOUtil.close(out);
		}
	}

}
