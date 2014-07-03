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

import jetsennet.jbmp.business.ExportImportMib;

/**
 * @author liwei资源类型导出
 */
public class BmpExportMibXmlServlet extends HttpServlet
{
    private static final Logger logger = Logger.getLogger(BmpExportMibXmlServlet.class);

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
            int mibId = Integer.parseInt(req.getParameter("mibId"));
            String mibName = new String(req.getParameter("mibName").getBytes("ISO-8859-1"), "UTF-8");
            ExportImportMib mib = new ExportImportMib();
            Document xml = mib.getMibXml(mibId);
            resp.setContentType("application/x-download");
            resp.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(mibName, "UTF-8"));
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
