package jetsennet.jsmp.nav.service.s2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetsennet.jsmp.nav.service.a7.NavBusinessUtil;
import jetsennet.jsmp.nav.service.a7.NavErrorHandle;
import jetsennet.jsmp.nav.util.ServletUtil;
import jetsennet.util.IOUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S2ServiceServlet extends HttpServlet
{

	private static final S2Business busi = new S2Business();

	private static final Logger logger = LoggerFactory.getLogger(S2ServiceServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		NavErrorHandle.illegalRequest(resp, null);
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
					Map<String, String> map = NavBusinessUtil.requestXml2Map(ServletUtil.getStream(req));
					String str = busi.invoke(method, map);
					resp.setHeader("Content-type", "text/html;charset=UTF-8");
					out.write(str.getBytes("UTF-8"));
					out.flush();
				}
				catch (Exception ex)
				{
					logger.error("", ex);
					NavErrorHandle.illegalRequest(resp, ex, null);
				}
			}
			else
			{
				String msg = "不合法的方法名称：" + method;
				logger.error(msg);
				NavErrorHandle.illegalRequest(resp, msg);
			}
		}
		finally
		{
			IOUtil.close(out);
		}
	}
}
