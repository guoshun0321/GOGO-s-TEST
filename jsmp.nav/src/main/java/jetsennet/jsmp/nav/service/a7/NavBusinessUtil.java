package jetsennet.jsmp.nav.service.a7;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetsennet.jsmp.nav.util.IdentAnnocation;
import jetsennet.jsmp.nav.util.UncheckedNavException;
import jetsennet.util.IOUtil;

public class NavBusinessUtil
{

	private static final Logger logger = LoggerFactory.getLogger(NavBusinessUtil.class);

	public static final Map<String, Method> genMethodMap(Class<?> clz)
	{
		Map<String, Method> retval = null;
		try
		{
			retval = new HashMap<String, Method>();
			Method[] methods = clz.getMethods();
			for (Method method : methods)
			{
				if (method.isAnnotationPresent(IdentAnnocation.class))
				{
					retval.put(method.getAnnotation(IdentAnnocation.class).value(), method);
				}
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
			throw new UncheckedNavException(ex);
		}
		return retval;
	}

	/**
	 * 将a7请求转换成HashMap
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> requestXml2Map(String xml) throws Exception
	{
		Map<String, String> retval = new HashMap<String, String>();
		InputStream in = null;
		try
		{
			in = new ByteArrayInputStream(xml.getBytes());
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(in);
			Element ele = doc.getRootElement();
			List<Attribute> attrs = ele.getAttributes();
			for (Attribute attr : attrs)
			{
				retval.put(attr.getName(), attr.getValue());
			}
		}
		finally
		{
			IOUtil.close(in);
		}
		return retval;
	}

}
