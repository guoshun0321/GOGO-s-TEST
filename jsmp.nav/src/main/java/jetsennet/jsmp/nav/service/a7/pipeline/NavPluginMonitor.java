package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.Map;

import jetsennet.jsmp.nav.monitor.MethodInvokeMMsg;
import jetsennet.jsmp.nav.monitor.Monitor;

public class NavPluginMonitor extends AbsNavPlugin
{

	private MethodInvokeMMsg msg = null;

	public void actionBefore(Method m, Map<String, String> map)
	{
		msg = new MethodInvokeMMsg();
		msg.setStartTime(System.currentTimeMillis());
		msg.setMethodName(m.getName());
	}

	public void actionAfter(Method m, Map<String, String> map, Object obj)
	{
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

}
