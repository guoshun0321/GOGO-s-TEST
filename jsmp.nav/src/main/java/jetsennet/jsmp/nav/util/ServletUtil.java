package jetsennet.jsmp.nav.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class ServletUtil
{
	
	public static String getStream(HttpServletRequest request) throws IOException
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
