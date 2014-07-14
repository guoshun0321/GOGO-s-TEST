/************************************************************************
日 期：2012-2-16
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jetsennet.jbmp.util.FileUtil;
import jetsennet.util.StringUtil;

/**
 * @author yl
 */
public class BMPFilesUploadServlet extends HttpServlet
{
    private int maxPostSize = 10 * 1024 * 1024; // 限制文件的上传大小 ,10M
    private String uploadPath = ""; // 保存文件的相对路径
    private String fileName = ""; // 重命名文件

    /**
     * 构造方法
     */
    public BMPFilesUploadServlet()
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
        System.out.println("开始上传！");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("utf-8");
        upload.setSizeMax(maxPostSize);

        try
        {
            List fileItems = upload.parseRequest(request);
            Iterator iter = fileItems.iterator();
            while (iter.hasNext())
            {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField())
                {
                    String name = item.getName();
                    System.out.println(name);

                    // 若指定了其他名字，则覆盖原来的文件名
                    if (!StringUtil.isNullOrEmpty(fileName))
                    {
                        name = fileName;
                    }

                    String rootPath = request.getRealPath("/");
                    String filePath = rootPath + uploadPath;

                    // 若该路径目录不存在，则创建
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
                            if(item.getName().endsWith(".gif")||item.getName().endsWith(".jpg")||item.getName().endsWith(".jpeg")||item.getName().endsWith(".png")||item.getName().endsWith(".GIF")||item.getName().endsWith(".JPG")||item.getName().endsWith(".PNG"))
                            {
                                int[] widthHeight=FileUtil.getNetImageWidthHeight(filePath + File.separator + name);
                                out.print("上传成功w="+widthHeight[0]+"h="+widthHeight[1]);  
                            }else
                            {
                                out.print("上传成功");  
                            }
                            
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            out.print(e.getMessage());
                        }
                    }
                }
            }
        }
        catch (SizeLimitExceededException es)
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
            uploadPath = request.getParameter("uploadPath");
            if (request.getParameter("fileName") != null)
            {
                fileName = new String(request.getParameter("fileName").getBytes("ISO-8859-1"), "GBK");
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
