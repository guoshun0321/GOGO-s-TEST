/************************************************************************
日 期：2012-5-10
作 者: 
版 本: v1.3
描 述: 文件上传servlet
历 史:
 ************************************************************************/
package jetsennet.jbmp.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author？
 */
public class BMPBinaryFileUploadServlet extends HttpServlet
{
    // 限制文件的上传大小 ,4M
    private int maxPostSize = 4 * 1024 * 1024;
    private String uploadPath = ""; // 保存文件的相对路径
    private String fileName = ""; // 重命名文件

    /**
     * 构造函数，初始化
     */
    public BMPBinaryFileUploadServlet()
    {
        super();
    }

    @Override
    public void destroy()
    {
        super.destroy();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        System.out.println("开始!" + fileName);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(maxPostSize);

        try
        {
            String rootPath = request.getRealPath("/");
            String filePath = rootPath + uploadPath;

            // 若该路径目录不存在，则创建
            File f = new File(filePath);
            if (!f.exists())
            {
                f.mkdirs();
            }

            File f2 = new File(filePath + File.separator + fileName + ".jpg");
            if (f2.exists())
            {
                out.print("文件已存在");
            }
            else
            {
                InputStream inputStream = request.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(f2);

                try
                {
                    int formlength = request.getContentLength();
                    byte[] formcontent = new byte[formlength];
                    int totalread = 0;
                    int nowread = 0;
                    while (totalread < formlength)
                    {
                        nowread = inputStream.read(formcontent, totalread, formlength);
                        totalread += nowread;
                    }

                    outputStream.write(formcontent);
                    outputStream.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    out.print("错误：" + e.getMessage());
                }
                finally
                {
                    outputStream.close();
                    inputStream.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            out.print("错误：" + e.getMessage());
        }
        finally
        {
            out.close();
        }
        System.out.println("结束");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            request.setCharacterEncoding("utf-8");
            uploadPath = request.getParameter("uploadPath");
            fileName = new String(request.getParameter("fileName").getBytes("ISO-8859-1"), "GBK");
            processRequest(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            request.setCharacterEncoding("utf-8");
            uploadPath = request.getParameter("uploadPath");
            fileName = new String(request.getParameter("fileName").getBytes("ISO-8859-1"), "GBK");
            processRequest(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getServletInfo()
    {
        return "Short description";
    }
}
