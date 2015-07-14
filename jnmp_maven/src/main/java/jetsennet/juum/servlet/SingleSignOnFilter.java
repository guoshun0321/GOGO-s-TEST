/************************************************************************
日 期：2013-07-25
作 者: 徐德海
版 本：v1.3
描 述: 单点登录
历 史：
 ************************************************************************/
package jetsennet.juum.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 单点登录拦截器
 * 
 * @author xdh
 */
public class SingleSignOnFilter implements Filter
{

	private static final String SSO_ID = "hr_uid";
	private static final Logger logger = Logger.getLogger(SingleSignOnFilter.class);

	public SingleSignOnFilter()
	{
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		try
		{
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			Map<String, String[]> map = req.getParameterMap();
			if(map != null && map.get("username") != null && map.get("password") != null)
			{
				String userName = map.get("username")[0];
				String password = map.get("password")[0];
				
				if(userName != null && !userName.trim().isEmpty() && password != null && !password.trim().isEmpty())
				{
					logger.debug("极地单点登录用户：" + userName);
					Cookie userCookie = new Cookie("sso_userName", userName);
					Cookie passwordCookie = new Cookie("sso_password", password);
					res.addCookie(userCookie);
					res.addCookie(passwordCookie);
				}
			}
			else 
			{
				Cookie userCookie = new Cookie("sso_userName", null);
				Cookie passwordCookie = new Cookie("sso_password", null);
				userCookie.setMaxAge(0);
				passwordCookie.setMaxAge(0);
				res.addCookie(userCookie);
				res.addCookie(passwordCookie);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
		// TODO Auto-generated method stub

	}

}
