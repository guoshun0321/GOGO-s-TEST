package jetsennet.jbmp.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import jetsennet.jbmp.datacollect.collectorif.ClusterManager;

/**
 * @author ？
 */
public class BMPServerSideAlarmServlet extends HttpServlet
{

    /**
     * 集群
     */
    private ClusterManager cluster;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BMPServerSideAlarmServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException
    {
        try
        {
            this.cluster = new ClusterManager();
            this.cluster.start();
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
            this.cluster.stop();
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            this.cluster = null;
        }
    }

}
