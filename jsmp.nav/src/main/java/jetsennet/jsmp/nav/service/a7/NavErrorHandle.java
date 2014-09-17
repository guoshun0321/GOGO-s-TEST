package jetsennet.jsmp.nav.service.a7;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.config.Config;

public class NavErrorHandle
{

	private static final Logger logger = LoggerFactory.getLogger(NavErrorHandle.class);

	public static final HttpServletResponse illegalRequest(HttpServletResponse resp, String msg)
	{
		if (Config.ISDEBUG)
		{
			try
			{
				if (msg != null)
				{
					resp.setHeader("Content-type", "text/html;charset=UTF-8");
					OutputStream out = resp.getOutputStream();
					out.write(msg.getBytes("UTF-8"));
					out.flush();
				}
			}
			catch (Exception ex)
			{
				logger.error("", ex);
			}
		}
		else
		{
			resp.setStatus(400);
		}
		return resp;
	}

	public static final HttpServletResponse illegalRequest(HttpServletResponse resp, Throwable ex, String msg)
	{
		if (Config.ISDEBUG)
		{
			try
			{
				if (ex != null && ex.getMessage() != null)
				{
					resp.setHeader("Content-type", "text/html;charset=UTF-8");
					OutputStream out = resp.getOutputStream();
					out.write(ex.getMessage().getBytes("UTF-8"));
					out.flush();
				}
			}
			catch (Exception ex1)
			{
				logger.error("", ex);
			}
		}
		else
		{
			resp.setStatus(400);
		}
		return resp;
	}

}
