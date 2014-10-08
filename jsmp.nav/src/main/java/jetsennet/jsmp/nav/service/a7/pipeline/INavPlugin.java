package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.Map;

public interface INavPlugin
{

	public void setContext(NavPipelineContext context);

	public INavPlugin addNext(INavPlugin next);

	public INavPlugin addPre(INavPlugin pre);

	public INavPlugin next();

	public INavPlugin pre();
	
	public void clear();

	public void actionBefore(Method m, Map<String, String> map);

	public void actionAfter(Method m, Map<String, String> map, Object obj);

	public void actionException(Method m, Map<String, String> map);

}
