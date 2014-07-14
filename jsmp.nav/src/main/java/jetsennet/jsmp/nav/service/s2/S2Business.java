package jetsennet.jsmp.nav.service.s2;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jetsennet.jsmp.nav.monitor.MethodInvokeMMsg;
import jetsennet.jsmp.nav.monitor.Monitor;
import jetsennet.jsmp.nav.monitor.MonitorServlet;
import jetsennet.jsmp.nav.service.a7.NavBusiness;
import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S2Business
{

    /**
     * 方法映射map
     */
    private static Map<String, Method> methodMap;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NavBusiness.class);

    static
    {
        try
        {
            methodMap = new HashMap<String, Method>();
            Method[] methods = NavBusiness.class.getMethods();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(IdentAnnocation.class))
                {
                    methodMap.put(method.getName(), method);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedNavException(ex);
        }
    }

    public String invoke(String method, Map<String, String> map) throws Exception
    {
        String retval = null;
        Method m = methodMap.get(method);
        if (m != null)
        {
            MethodInvokeMMsg msg = new MethodInvokeMMsg();
            msg.setStartTime(System.currentTimeMillis());
            msg.setMethodName(m.getName());
            try
            {
                retval = (String) m.invoke(this, new Object[] { map });
            }
            catch (Exception ex)
            {
                msg.setException(true);
                throw ex;
            }
            msg.setEndTime(System.currentTimeMillis());
            Monitor.getInstance().put(msg);
        }
        else
        {
            logger.error("不正确的方法，不存在方法：" + method);
        }
        return retval;
    }

}
