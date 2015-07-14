/************************************************************************
日 期：2012-8-9
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * yl
 */
public class BMPFilesUploadFilter implements Filter
{
    private String encoding = null;

    // 读取 web.xml 配置文件中的初始化参数
    @Override
    public void init(FilterConfig config) throws ServletException
    {
        this.encoding = config.getInitParameter("encoding");
    }

    // 此方法在被过滤的程序执行之前和执行之后各执行一次
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding(this.encoding); // 统一设置请求数据的编码格式

        String url = req.getRequestURI();
        url = new String(url.getBytes("ISO-8859-1"), "GBK");
        url = URLDecoder.decode(url, "UTF-8");
        url = url.substring(url.indexOf("upload"), url.length());

        res.setCharacterEncoding(this.encoding); // 统一设置响应数据的编码格式
        res.setContentType(req.getContentType());

        String path = req.getRealPath("/") + url;
        File file = new File(path);
        if (!file.exists())
        {
            return;
        }

        OutputStream out = res.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try
        {
            bis = new BufferedInputStream(new FileInputStream(path));
            bos = new BufferedOutputStream(out);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1)
            {
                bos.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
            if (bos != null)
            {
                bos.close();
            }
            if (out != null)
            {
                out.close();
            }
        }

        // chain.doFilter(req, res); // 将控制权交给下一个过滤器，如果不存在下一个过滤器则交给Servlet
    }

    @Override
    public void destroy()
    {
        try
        {
            Thread.sleep(3000); // 休眠 3 秒钟
            this.encoding = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
