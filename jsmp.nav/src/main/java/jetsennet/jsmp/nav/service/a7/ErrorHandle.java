package jetsennet.jsmp.nav.service.a7;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import jetsennet.jsmp.nav.config.Config;

public class ErrorHandle
{

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

			}
		}
		else
		{
			resp.setStatus(400);
		}
		return resp;
	}

}
