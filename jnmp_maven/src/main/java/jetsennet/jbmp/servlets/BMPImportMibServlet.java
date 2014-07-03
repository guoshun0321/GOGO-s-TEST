package jetsennet.jbmp.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import jetsennet.jbmp.business.ExportImportMib;

/**
 * 导入mib库xml文件
 * @author liwei
 */
public class BMPImportMibServlet extends HttpServlet
{
    private static final Logger logger = Logger.getLogger(BMPImportMibServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String fag;
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(10 * 1024 * 1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(10 * 1024 * 1024);
        List<FileItem> list;
        PrintWriter out = resp.getWriter();
        try
        {
            list = upload.parseRequest(req);
            StringBuffer strxml = new StringBuffer();
            Iterator<FileItem> it = list.iterator();
            while (it.hasNext())
            {
                FileItem fi = it.next();
                if (!fi.isFormField())
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(fi.getInputStream(), "UTF-8"));
                    String str = "";
                    while ((str = br.readLine()) != null)
                    {
                        strxml.append(str);
                    }
                    br.close();
                }
            }
            String mibXml = strxml.toString();
            ExportImportMib expmib = new ExportImportMib();
            fag = expmib.importMibXml(mibXml);
            if ("fail".equals(fag))
            {
                out.print("导入失败！");
            }
            else if ("success".equals(fag))
            {
                out.print("导入成功！");
            }
            else
            {
                out.print(fag);
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            out.print("导入失败！");
        }
    }
}
