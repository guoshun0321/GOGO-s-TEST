package jetsennet.jsmp.nav.service.a7.pipeline;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NavPipelineContext
{

	/**
	 * 请求url
	 */
	private String url;
	/**
	 * 请求状态
	 */
	private int status;
	/**
	 * 请求参数
	 */
	private Map<String, String> params;
	/**
	 * 请求方法
	 */
	private Method m;
	/**
	 * 处理结果
	 */
	private Object retObj;
	/**
	 * 异常消息
	 */
	private String errorMsg;
	/**
	 * 请求附加信息
	 */
	private Map<String, Object> info;
	/**
	 * pipeline继续执行
	 */
	public static final int STATUS_CONTINUE = 0;
	/**
	 * pipeline因为异常停止执行
	 */
	public static final int STATUS_BREAK_ERROR = 1;
	/**
	 * pipeline因为满足条件停止执行
	 */
	public static final int STATUS_BREAK_FINISH = 2;
	/**
	 * 执行时出错
	 */
	public static final int STATUS_ERROR = 3;
	/**
	 * 生成key时忽略的字段
	 */
	private static final Set<String> ignoreKeys = new HashSet<>();

	static
	{
		ignoreKeys.add("clientId");
		ignoreKeys.add("deviceId");
		ignoreKeys.add("account");
	}

	public NavPipelineContext()
	{
		this.status = STATUS_CONTINUE;
		info = new HashMap<String, Object>();
	}

	public void breakFinish()
	{
		this.status = STATUS_BREAK_FINISH;
	}

	public void breakError(String errorMsg)
	{
		this.status = STATUS_BREAK_ERROR;
		this.errorMsg = errorMsg;
	}

	public void error(String errorMsg)
	{
		this.status = STATUS_ERROR;
		this.errorMsg = errorMsg;
	}

	public boolean isContinue()
	{
		return this.status == STATUS_CONTINUE;
	}

	public boolean isBreakFinish()
	{
		return this.status == STATUS_BREAK_FINISH;
	}

	public boolean isBreakError()
	{
		return this.status == STATUS_BREAK_ERROR;
	}

	public boolean isError()
	{
		return this.status == STATUS_ERROR;
	}

	public void clear()
	{
		this.url = null;
		this.retObj = null;
		this.status = STATUS_CONTINUE;
		this.params = null;
		this.m = null;
		info.clear();
	}

	public String genCacheKey()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m.getName()).append("-");
		Set<String> keys = params.keySet();
		for (String key : keys)
		{
			if (!ignoreKeys.contains(key))
			{
				sb.append(key).append(params.get(key));
			}
		}
		return sb.toString();
	}

	public String getUrl()
	{
		return url;
	}

	public Object getRetObj()
	{
		return retObj;
	}

	public void setRetObj(Object retObj)
	{
		this.retObj = retObj;
	}

	public Map<String, String> getParams()
	{
		return params;
	}

	public void setParams(Map<String, String> params)
	{
		this.params = params;
	}

	public Method getM()
	{
		return m;
	}

	public void setM(Method m)
	{
		this.m = m;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

}
