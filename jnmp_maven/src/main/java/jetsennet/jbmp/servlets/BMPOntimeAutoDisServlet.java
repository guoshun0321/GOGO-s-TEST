/************************************************************************
日 期：2012-3-14
作 者: 郭祥
版 本: v1.3
描 述:
历 史:
 ************************************************************************/
package jetsennet.jbmp.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.OntimeAutoDis;

/**
 * @author 郭祥
 */
public class BMPOntimeAutoDisServlet extends HttpServlet
{

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BMPOntimeAutoDisServlet.class);

    /**
     * 构造方法
     */
    public BMPOntimeAutoDisServlet()
    {
        super();
    }

    @Override
    public void init() throws ServletException
    {
        try
        {
            logger.debug("定时自动发现开启。");
            OntimeAutoDis dis = OntimeAutoDis.getInstance();
            dis.start();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    @Override
    public void destroy()
    {
        try
        {
            logger.debug("定时自动发现关闭。");
            OntimeAutoDis dis = OntimeAutoDis.getInstance();
            if (dis != null)
            {
                dis.stop();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }
}
