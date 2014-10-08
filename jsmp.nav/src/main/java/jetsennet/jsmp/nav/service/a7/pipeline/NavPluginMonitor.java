package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.monitor.MethodInvokeMMsg;
import jetsennet.jsmp.nav.monitor.Monitor;

public class NavPluginMonitor extends AbsNavPlugin
{

	private MethodInvokeMMsg msg = null;

	private static final Logger logger = LoggerFactory.getLogger(NavPluginMonitor.class);

	public NavPluginMonitor()
	{
		this.clear();
	}

	public void actionBefore(Method m, Map<String, String> map)
	{
		msg = new MethodInvokeMMsg();
		if (Config.ISDEBUG)
		{
			logger.debug("NavPluginMonitor.actionBefore:" + msg);
		}
		msg.setStartTime(System.currentTimeMillis());
		msg.setMethodName(m.getName());
	}

	public void actionAfter(Method m, Map<String, String> map, Object obj)
	{
		if (Config.ISDEBUG)
		{
			logger.debug("NavPluginMonitor.actionAfter:" + msg);
		}
		msg.setEndTime(System.currentTimeMillis());
		Monitor.getInstance().put(msg);
		msg = null;
	}

	public void actionException(Method m, Map<String, String> map)
	{
		msg.setException(true);
		Monitor.getInstance().put(msg);
		msg = null;
	}

	@Override
	public void clear()
	{
		this.msg = null;
	}

}
