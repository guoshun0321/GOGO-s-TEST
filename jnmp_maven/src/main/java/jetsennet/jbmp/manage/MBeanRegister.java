package jetsennet.jbmp.manage;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanRegister
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MBeanRegister.class);

    private static MBeanServer server;

    public static void register(String key, Object value) throws Exception
    {
        ensureServer();
        server.registerMBean(value, new ObjectName(key));
    }

    private synchronized static MBeanServer ensureServer()
    {
        long t1 = System.currentTimeMillis();

        if (server == null)
        {
            if (MBeanServerFactory.findMBeanServer(null).size() > 0)
            {
                server = MBeanServerFactory.findMBeanServer(null).get(0);
                if (logger.isDebugEnabled())
                {
                    logger.debug("Using existing MBeanServer " + (System.currentTimeMillis() - t1));
                }
            }
            else
            {
                server = ManagementFactory.getPlatformMBeanServer();
                if (logger.isDebugEnabled())
                {
                    logger.debug("Creating MBeanServer" + (System.currentTimeMillis() - t1));
                }
            }
        }
        return (server);
    }

}
