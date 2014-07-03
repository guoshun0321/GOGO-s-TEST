package jetsennet.jbmp.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import jetsennet.jbmp.dataaccess.ResourceDal;

/**
 * 资源类型xml导出
 * @author liwei
 */
public class BmpExportResXmlServlet extends HttpServlet
{
    private static final Logger logger = Logger.getLogger(BmpExportResXmlServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        try
        {
            int classId = Integer.parseInt(req.getParameter("classId"));
            String className = new String(req.getParameter("className").getBytes("ISO-8859-1"), "UTF-8");
            ResourceDal res = new ResourceDal();
            Document xml = res.exportXml(classId);
            resp.setContentType("application/x-download");
            resp.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(className, "UTF-8"));
            PrintWriter out = resp.getWriter();
            out.print(xml.asXML());
            out.close();
        }

        catch (NumberFormatException e)
        {
            logger.error(e);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

}
