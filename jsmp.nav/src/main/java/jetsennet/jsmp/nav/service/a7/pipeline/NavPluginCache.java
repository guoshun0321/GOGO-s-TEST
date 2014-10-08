package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.config.Config;
import jetsennet.jsmp.nav.xmem.XmemcachedUtil;

public class NavPluginCache extends AbsNavPlugin
{

	/**
	 * 本次操作是否命中
	 */
	private boolean isHit;
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(NavPluginCache.class);

	public NavPluginCache()
	{
		clear();
	}

	public void actionBefore(Method m, Map<String, String> map)
	{
		String url = context.getUrl();
		String retval = null;
		try
		{
			retval = XmemcachedUtil.getInstance().get(url);
			if (retval != null)
			{
				if (Config.ISDEBUG)
				{
					logger.debug("从缓存取数据成功：" + url);
				}
				context.setRetObj(retval);
				context.breakFinish();
				isHit = true;
			}
			else
			{
				if (Config.ISDEBUG)
				{
					logger.debug("从缓存取数据失败：" + url);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	public void actionAfter(Method m, Map<String, String> map, Object obj)
	{
		Object retObj = context.getRetObj();
		if (!context.isError() && !context.isBreakError() && !isHit)
		{
			String url = context.getUrl();
			XmemcachedUtil.getInstance().put(url, retObj);
		}
	}

	public void actionException(Method m, Map<String, String> map)
	{
	}

	@Override
	public void clear()
	{
		this.isHit = false;
	}

}
