/************************************************************************
日  期：		2009-06-30
作  者:		李小敏
版  本：     1.0
描  述:	    
历  史：     2014-5-8 梁洪杰 移植uorm的DefaultPojoSerializer至此，用于增强pojo的serialize功能
************************************************************************/
package jetsennet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 序列化相关操作
 * 
 * TODO 为了能编译通过，注释掉一些方法
 * @author 李小敏
 */
public class SerializerUtil
{
    private static jetsennet.logger.ILog logger = jetsennet.logger.LogManager.getLogger("jetsennet.util");

    //    private static IPojoSerializer pojoSerializer = new DefaultPojoSerializer();
    //    private static IJsonSerializer jsonSerializer = new DefaultJsonSerializer();

    /**反序列化
     * @param <T> 类型
     * @param type 类型信息
     * @param serializedXml xml
     * @return 对象
     */
    public static <T extends ISerializer> T deserialize(Class<T> type, String serializedXml)
    {
        if (StringUtil.isNullOrEmpty(serializedXml))
        {
            return null;
        }

        try
        {
            ISerializer obj = type.newInstance();
            obj.deserialize(serializedXml, "root");
            return (T) obj;
        }
        catch (Exception ex)
        {
            logger.error("deSerializer", ex);
        }
        return null;
    }

    /**反序列化
     * @param <T> 类型
     * @param type 类型信息
     * @param serializedXml xml
     * @return 对象
     */
    public static <T> T deserialize1(Class<T> type, String serializedXml)
    {
        if (StringUtil.isNullOrEmpty(serializedXml))
        {
            return null;
        }

        try
        {
            return deserialize(type, DocumentHelper.parseText(serializedXml));
        }
        catch (Exception ex)
        {
            logger.error("deSerializer", ex);
        }
        return null;
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param xml xml对象
     * @return 对象
     * @throws Exception
     */
    public static <T> T deserialize(Class<T> cls, Document xml) throws Exception
    {
        return deserialize(cls, xml.getRootElement());
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param element xml根节点
     * @return 对象
     * @throws Exception
     */
    public static <T> T deserialize(Class<T> cls, Element element) throws Exception
    {
        return null;
        //        return pojoSerializer.deserialize(cls, element);
    }

    /**反序列化
     * @param <T> 类型
     * @param type 类型信息
     * @param serializedXml xml
     * @param itemName 节点名称
     * @return 对象列表
     */
    @SuppressWarnings("unchecked")
    public static <T extends ISerializer> List<T> deserialize(Class<T> type, String serializedXml, String itemName)
    {
        List<T> items = new ArrayList<T>();
        if (StringUtil.isNullOrEmpty(serializedXml))
        {
            return items;
        }
        try
        {
            Document doc = DocumentHelper.parseText(serializedXml);
            List<Node> nodes = doc.getRootElement().elements();
            for (Node node : nodes)
            {
                T obj = type.newInstance();
                obj.deserialize(node.asXML(), itemName);
                items.add(obj);
            }
        }
        catch (Exception ex)
        {
            logger.error("deSerializer", ex);
        }
        return items;
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param json json字符串
     * @return 对象
     * @throws Exception
     */
    public static <T> T deserialize4json(Class<T> cls, String json) throws Exception
    {
        //        return jsonSerializer.deserialize(cls, json);
        return null;
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param xml xml字符串
     * @return map对象
     * @throws Exception
     */
    public static Map<String, Object> deserialize2map(Class<?> cls, String xml) throws Exception
    {
        Document doc = DocumentHelper.parseText(xml);
        return deserialize2map(cls, doc);
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param xml xml文档
     * @return map对象
     * @throws Exception
     */
    public static Map<String, Object> deserialize2map(Class<?> cls, Document xml) throws Exception
    {
        return deserialize2map(cls, xml.getRootElement());
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param element xml根节点
     * @return map对象
     * @throws Exception
     */
    public static Map<String, Object> deserialize2map(Class<?> cls, Element element) throws Exception
    {
        return null;
        //        return pojoSerializer.deserialize2(cls, element);
    }

    /**
     * 反序列化
     * @param cls 类型信息
     * @param json json字符串
     * @return map
     * @throws Exception
     */
    public static Map<String, Object> deserialize2map4json(Class<?> cls, String json) throws Exception
    {
        return null;
        //        return jsonSerializer.deserialize2(cls, json);
    }

    /**
     * 反序列化
     * @param serializedXml xml字符串
     * @return map对象
     */
    public static HashMap<String, String> deserialize2strmap(String serializedXml)
    {
        return deserialize(serializedXml, "");
    }

    /**反序列化
     * @param serializedXml 序列化字串
     * @param rootName 根元素名称
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static HashMap<String, String> deserialize(String serializedXml, String rootName)
    {
        if (StringUtil.isNullOrEmpty(serializedXml))
        {
            return null;
        }

        HashMap<String, String> obj = new HashMap<String, String>();
        List<Node> nodes = null;
        try
        {
            Document doc = DocumentHelper.parseText(serializedXml);
            nodes = doc.getRootElement().elements();
            if (nodes != null)
            {
                for (int i = 0; i < nodes.size(); i++)
                {
                    obj.put(nodes.get(i).getName(), nodes.get(i).getText());
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("deSerializer", ex);
        }
        return obj;
    }

    /**
     * 反序列化
     * @param json json字符串
     * @return map对象
     */
    public static Map<String, String> deserialize2strmap4json(String json)
    {
        //        JsonStreamParser parser = new JsonStreamParser(json);
        //        if (!parser.hasNext())
        //        {
        //            return null;
        //        }
        //        Map<String, String> instance = new HashMap<String, String>();
        //        JsonObject jsonobj = parser.next().getAsJsonObject();
        //        for (Entry<String, JsonElement> entry : jsonobj.entrySet())
        //        {
        //            String name = entry.getKey();
        //            JsonElement val = entry.getValue();
        //            instance.put(name, val.getAsString());
        //        }
        //        return instance;
        return null;
    }

    /**序列化
     * @param <T>
     * @param obj 对象
     * @param xmlRoot 根节点名称
     * @return xml字符串
     */
    public static <T extends ISerializer> String serialize(T obj, String xmlRoot)
    {
        return obj.serialize(xmlRoot);
    }

    /**
     * 序列化
     * @param obj 对象
     * @param xmlRoot 根节点名称
     * @return xml字符串
     * @throws Exception 
     */
    public static <T> String serialize(T obj, String xmlRoot) throws Exception
    {
        //        return pojoSerializer.serialize2(obj, xmlRoot);
        return null;
    }

    /**序列化
     * @param <T>
     * @param objs
     * @param xmlRoot
     * @param itemName
     * @return
     */
    public static <T extends ISerializer> String serialize(List<T> objs, String xmlRoot, String itemName)
    {
        StringBuilder sbText = new StringBuilder();
        sbText.append("<");
        sbText.append(xmlRoot);
        sbText.append(">");

        for (T obj : objs)
        {
            sbText.append(obj.serialize(itemName));
        }

        sbText.append("</");
        sbText.append(xmlRoot);
        sbText.append(">");
        return sbText.toString();
    }

    /**序列化
     * @param obj
     * @param rootName 根元素名称
     * @return
     */
    public static String serialize(HashMap<String, String> obj, String rootName)
    {
        if (obj == null)
        {
            return "";
        }

        Document xmlDoc = DocumentHelper.createDocument(DocumentHelper.createElement(rootName));
        Element root = xmlDoc.getRootElement();
        for (String name : obj.keySet())
        {
            root.addElement(name).addText(obj.get(name));
        }
        return xmlDoc.asXML();
    }

    /**
     * 序列化
     * @param obj 对象
     * @return json字符串
     * @throws Exception
     */
    public static String serialize2json(Object obj) throws Exception
    {
        //        return jsonSerializer.serialize(obj);
        return null;
    }

    /**
     * 序列化节点
     * @param nodeName
     * @param nodeValue
     * @return
     */
    public static String serializeNode(String nodeName, String nodeValue)
    {
        if (nodeValue != null)
        {
            return StringUtil.format("<%s>%s</%s>", nodeName, XmlUtil.escapeXml(nodeValue), nodeName);
        }

        return "";
    }

    /**
     * 序列化节点
     * @param nodeName
     * @param nodeValue
     * @return
     */
    public static String serializeNode(String nodeName, int nodeValue)
    {
        return StringUtil.format("<%s>%s</%s>", nodeName, nodeValue, nodeName);
    }

    public static void main(String[] args) throws Exception
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        System.out.println(map);
        System.out.println(deserialize2strmap4json(serialize2json(map)));
        System.out.println(deserialize2strmap(serialize(map, "test")));
    }
}
