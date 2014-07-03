package jetsennet.jsmp.nav.monitor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class MonitorServlet extends HttpServlet
{

    @Override
    public void init() throws ServletException
    {
        Monitor.getInstance().start();
    }

    @Override
    public void destroy()
    {
        Monitor.getInstance().stop();
    }

}
