/************************************************************************
日 期: 2012-2-24
作 者: 郭祥
版 本: v1.3
描 述: XML工具
历 史:
 ************************************************************************/
package jetsennet.jbmp.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.SerializationException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * XML工具类
 * 
 * @author 郭祥
 */
public class XmlUtil
{

    private static final Logger logger = Logger.getLogger(XmlUtil.class);

    /**
     * 添加一个XML节点的开始
     * @param sb 参数
     * @param param 参数
     * @param content 参数
     */
    public static void appendTextNode(StringBuilder sb, String param, String content)
    {
        appendXmlBegin(sb, param);
        sb.append(content);
        appendXmlEnd(sb, param);
    }

    /**
     * 添加一个XML节点的开始
     * @param sb 参数
     * @param param 参数
     * @param content 参数
     */
    public static void appendTextNode(StringBuilder sb, String param, int content)
    {
        appendXmlBegin(sb, param);
        sb.append(content);
        appendXmlEnd(sb, param);
    }

    /**
     * 添加一个XML节点的开始
     * @param sb 参数
     * @param param 参数
     */
    public static void appendXmlBegin(StringBuilder sb, String param)
    {
        sb.append("<");
        sb.append(param);
        sb.append(">");
    }

    /**
     * 添加一个XML节点的结束
     * @param sb 参数
     * @param param 参数
     */
    public static void appendXmlEnd(StringBuilder sb, String param)
    {
        sb.append("</");
        sb.append(param);
        sb.append(">");
    }

    /**
     * 将对象转换成XML形式
     * @param obj 对象
     * @param names 名称
     * @return 结果
     */
    public static String objToXml(Object obj, String[] names)
    {
        Class<?> cls = obj.getClass();
        StringBuilder sb = new StringBuilder();
        try
        {
            sb.append("<Record>");
            for (String name : names)
            {
                String getMethodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                Method getMethod = cls.getMethod(getMethodName, new Class[0]);
                if (getMethod != null)
                {
                    sb.append("<" + name + ">");
                    Object rs = getMethod.invoke(obj, new Object[0]);
                    if (rs != null)
                    {
                        sb.append(rs.toString());
                    }
                    else
                    {
                        sb.append("null");
                    }
                    sb.append("</" + name + ">");
                }
            }
            sb.append("</Record>");
        }
        catch (Exception ex)
        {
            throw new SerializationException(obj.toString() + "转换成XML失败", ex);
        }
        return sb.toString();
    }

    /**
     * @param list 参数
     * @param cls 参数
     * @param names 参数
     * @return 结果
     */
    public static String ListToXml(List<?> list, Class cls, String[] names)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            sb.append("<RecordSet>");
            for (Object obj : list)
            {
                sb.append(objToXml(obj, names));
            }
            sb.append("</RecordSet>");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "error";
        }
        return sb.toString();
    }
    
    /**
     * 按照漂亮的格式打印jdom的Domcument对象
     * 
     * @param doc
     * @return
     */
    public static String jdomPrettyString(Document doc)
    {
        String retval = null;
        XMLOutputter out = null;
        ByteArrayOutputStream bout = null;
        try
        {
            bout = new ByteArrayOutputStream();
            out = new XMLOutputter(Format.getPrettyFormat());
            out.output(doc, bout);
            byte[] bytes = bout.toByteArray();
            retval = new String(bytes);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    bout.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    bout = null;
                }
            }
        }
        return retval;
    }
}
