package jetsennet.jbmp.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetsennet.util.StringUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class BMPFilesBatchUploadServlet extends HttpServlet
{
    private int maxPostSize = 10485760*10;
    private String uploadPath = "";
    private String[] fileName = null;

    public void destroy()
    {
        super.destroy();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        System.out.println("开始上传！");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("utf-8");
        upload.setSizeMax(this.maxPostSize);
        try
        {
            List fileItems = upload.parseRequest(request);
            Iterator iter = fileItems.iterator();
            int i = 0;
            while (iter.hasNext())
            {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField())
                    continue;
                String name = item.getName();
                System.out.println(name);

                if (!StringUtil.isNullOrEmpty(this.fileName[i]))
                {
                    name = this.fileName[i];
                }
                i++;

                String rootPath = request.getRealPath("/");
                String filePath = rootPath + this.uploadPath;

                File f = new File(filePath);
                if (!f.exists())
                {
                    f.mkdirs();
                }

                File f2 = new File(filePath + File.separator + name);
                if (f2.exists())
                {
                    out.print("文件已存在");
                }
                else
                {
                    try
                    {
                        item.write(new File(filePath + File.separator + name));
                        out.print("上传成功");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        out.print(e.getMessage());
                    }
                }
            }

        }
        catch (FileUploadBase.SizeLimitExceededException es)
        {
            es.printStackTrace();
            out.print("错误：文件大小不能超过10M");
        }
        catch (FileUploadException e)
        {
            e.printStackTrace();
            out.print(e.getMessage());
            System.out.println("错误：" + e.getMessage());
        }
        finally
        {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            request.setCharacterEncoding("utf-8");
            this.uploadPath = request.getParameter("uploadPath");
            if (request.getParameter("fileName") != null)
            {
                String names = new String(request.getParameter("fileName").getBytes("ISO-8859-1"), "GBK");
                this.fileName = names.split(",");
                processRequest(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
