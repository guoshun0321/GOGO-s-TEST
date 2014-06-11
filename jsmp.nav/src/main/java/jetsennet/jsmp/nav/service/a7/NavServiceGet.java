package jetsennet.jsmp.nav.service.a7;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.util.IOUtil;

public class NavServiceGet extends HttpServlet
{

    private static final NavBusiness busi = new NavBusiness();
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NavServiceGet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        OutputStream out = resp.getOutputStream();
        try
        {
            String method = req.getParameter("method");
            if (method != null)
            {
                Map<String, String> map = req.getParameterMap();
                try
                {
                    String str = busi.invoke(method, map);
                    str = str == null ? "" : str;
                    resp.setHeader("Content-type", "text/html;charset=UTF-8");
                    out.write(str.getBytes("UTF-8"));
                    out.flush();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                    ErrorHandle.illegalRequest(resp);
                }
            }
            else
            {
                logger.error("找不到方法名称！");
                ErrorHandle.illegalRequest(resp);
            }
        }
        finally
        {
            IOUtil.close(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.error("NavServiceGet不支持POST操作！");
        ErrorHandle.illegalRequest(resp);
    }

}
