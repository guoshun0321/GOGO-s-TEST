package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavPipeline
{

	private INavPlugin first;

	private INavPlugin last;

	private List<INavPlugin> plugins;

	private NavPipelineContext context;

	public NavPipeline()
	{
		context = new NavPipelineContext();
		plugins = new ArrayList<>(5);
	}

	public void addPlugin(INavPlugin plugin)
	{
		plugin.setContext(context);
		if (this.first == null)
		{
			this.first = plugin;
			this.last = plugin;
		}
		else
		{
			this.last.addNext(plugin);
			plugin.addPre(this.last);
			this.last = plugin;
		}
		plugins.add(plugin);
	}

	public NavPipelineContext resetContext(Method m, Map<String, String> params)
	{
		context.clear();
		context.setM(m);
		context.setParams(params);
		context.setUrl(context.genCacheKey());

		for (INavPlugin plugin : plugins)
		{
			plugin.clear();
		}

		return context;
	}

	public INavPlugin before()
	{
		INavPlugin retval = this.first;
		INavPlugin temp = this.first;
		while (temp != null)
		{
			if (context.isContinue())
			{
				temp.actionBefore(context.getM(), context.getParams());
				retval = temp;
				temp = temp.next();
			}
			else
			{
				break;
			}
		}
		return retval;
	}

	public void after(INavPlugin lastPlugin)
	{
		INavPlugin temp = lastPlugin;
		if (temp == null)
		{
			temp = last;
		}
		while (temp != null)
		{
			if (!context.isError() || !context.isBreakError())
			{
				temp.actionAfter(context.getM(), context.getParams(), context.getRetObj());
				temp = temp.pre();
			}
			else
			{
				break;
			}
		}
	}
}