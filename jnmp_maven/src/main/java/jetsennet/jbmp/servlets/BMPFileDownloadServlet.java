/************************************************************************
日 期：2012-5-10
作 者: 
版 本: v1.3
描 述: 文件下载 servlet
历 史:
 ************************************************************************/
package jetsennet.jbmp.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ？
 */
public class BMPFileDownloadServlet extends HttpServlet
{
    /**
     * 构造方法
     */
    public BMPFileDownloadServlet()
    {

    }

    @Override
    public void destroy()
    {
        super.destroy();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String path = request.getParameter("path");
        String name = request.getParameter("name");
        if (name == null || "".equals(name) || path == null || "".equals(path))
        {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().print("文件不存在！");
            return;
        }
        String fileName = URLDecoder.decode(name, "UTF-8");
        path = getServletContext().getRealPath("/") + path;
        File file = new File(path);
        if (!file.exists())
        {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().print("文件不存在！");
            return;
        }
        response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO-8859-1"));
        ServletOutputStream out = response.getOutputStream();
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
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    public String getServletInfo()
    {
        return "Short description";
    }
}
