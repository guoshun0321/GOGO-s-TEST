package jetsennet.jbmp.protocols.http;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class HttpUtil
{

	private static final Logger logger = Logger.getLogger(HttpUtil.class);

	/**
	 * 确定HTTP请求地址字符串，GET请求需要拼接查询字符串
	 * @param req
	 * @return
	 */
	public static String ensureSendUrl(HttpRequest req) throws Exception
	{
		String method = req.method;
		String urlStr = req.urlString;
		Map<String, String> params = req.params;
		// GET时，如果参数不为空，编辑请求地址
		if (method.equalsIgnoreCase("GET") && params != null)
		{
			StringBuilder param = new StringBuilder();
			for (String key : params.keySet())
			{
				if (param.length() == 0)
				{
					param.append("?");
				}
				else
				{
					param.append("&");
				}
				//				param.append(key).append("=").append(URLEncoder.encode(params.get(key), "GBK"));
				param.append(key).append("=").append(params.get(key));
			}
			urlStr += param;
		}
		return urlStr;
	}

	public static void setConnInfo(HttpRequest req, HttpURLConnection conn) throws Exception
	{
		String method = req.method;

		conn.setRequestMethod(method);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		// 设置HTTP头部参数
		Map<String, String> headParams = req.headParams;
		if (headParams != null)
		{
			for (String key : headParams.keySet())
			{
				conn.addRequestProperty(key, headParams.get(key));
			}
		}

		// 设置参数
		Map<String, String> params = req.params;
		if (method.equalsIgnoreCase("POST") && params != null)
		{
			StringBuilder param = new StringBuilder();
			for (String key : params.keySet())
			{
				param.append("&");
				param.append(key).append("=").append(params.get(key));
			}
			// 将内容添加到HTTP报文中
			conn.getOutputStream().write(param.toString().getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
		}
	}

	/**
	   * Utf8URL编码
	   * @param s
	   * @return
	   */
	public static String encode(String text, String coding)
	{
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c >= 0 && c <= 255)
			{
				result.append(c);
			}
			else
			{
				try
				{
					byte[] b = Character.toString(c).getBytes("UTF-8");
					for (int j = 0; j < b.length; j++)
					{
						result.append("%");
						int k = b[j] & 255;
						result.append(Integer.toHexString(k).toUpperCase());
					}
				}
				catch (Exception ex)
				{
					logger.error("", ex);
				}
			}
		}
		return result.toString();
	}

	/**
	 * 判读请求为GET还是POST
	 * 
	 * @param req
	 * @return
	 */
	public static boolean isGet(HttpServletRequest req)
	{
		if (req == null)
		{
			throw new NullPointerException();
		}
		if ("GET".equalsIgnoreCase(req.getMethod()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
