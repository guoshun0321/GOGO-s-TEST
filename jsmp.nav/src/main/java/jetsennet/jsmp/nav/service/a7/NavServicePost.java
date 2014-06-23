package jetsennet.jsmp.nav.service.a7;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetsennet.util.IOUtil;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
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
		ErrorHandle.illegalRequest(resp);
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
					Map<String, String> map = A7Util.requestXml2Map(this.getStream(req));
					String str = busi.invoke(method, map);
					resp.setHeader("Content-type", "text/html;charset=UTF-8");
					out.write(str.getBytes("UTF-8"));
					out.flush();
				}
				catch (Exception ex)
				{
					logger.error("", ex);
					ErrorHandle.illegalRequest(resp);
				}
			}
			else
			{
				logger.error("不合法的方法名称：" + method);
				ErrorHandle.illegalRequest(resp);
			}
		}
		finally
		{
			IOUtil.close(out);
		}
	}

	private String getStream(HttpServletRequest request) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		char[] chs = new char[1024];
		while (true)
		{
			int l = br.read(chs);
			sb.append(chs);
			if (l < 0)
			{
				break;
			}
		}
		return sb.toString();
	}

}
