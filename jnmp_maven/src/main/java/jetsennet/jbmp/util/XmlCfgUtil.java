/************************************************************************
日 期：2011-11-28
作 者: lianghongjie
版 本：v1.3
描 述: xml文件配置工具
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jetsennet.jbmp.datacollect.util.CollConstants;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author lianghongjie xml文件配置工具
 */
public class XmlCfgUtil
{
    private static final Logger logger = Logger.getLogger(XmlCfgUtil.class);

    public static final String CFG_PRE = "/cfg[@name='";
    public static final String CFG_POS = "']";
    public static final String DEFAULT = "default";

    /**
     * 配置map
     */
    private static Map<String, Document> cfgMap = new ConcurrentHashMap<String, Document>();

    /**
     * 读取配置文件
     */
    private static Document readDocument(String fileName)
    {
        URL file = XmlCfgUtil.class.getClassLoader().getResource(fileName + ".xml");
        SAXReader reader = new SAXReader();
        Document doc = null;
        try
        {
            doc = reader.read(file);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
            logger.error("初始化失败," + fileName + ".xml" + "文件解析异常", e);
            return null;
        }

        return doc;
    }

    /**
     * 取根节点
     * @param fileName
     * @return
     */
    private static synchronized Element getRoot(String fileName)
    {
        Document doc = cfgMap.get(fileName);
        if (doc == null)
        {
            doc = readDocument(fileName);
            cfgMap.put(fileName, doc);
        }
        return doc.getRootElement();
    }

    /**
     * 取配置文件
     * @param fileName
     * @return
     */
    private static synchronized Document getDocument(String fileName)
    {
        Document doc = cfgMap.get(fileName);
        if (doc == null)
        {
            doc = readDocument(fileName);
            cfgMap.put(fileName, doc);
        }
        return doc;
    }

    /**
     * 读取整形配置值
     * @param fileName 文件名
     * @param path 路径
     * @param defaultValue 默认值
     * @return 结果
     */
    public static int getIntValue(String fileName, String path, int defaultValue)
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return defaultValue;
        }
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return defaultValue;
        }
        String result = node.getText();
        return Integer.parseInt(result);
    }

    /**
     * 读取浮点配置值
     * @param fileName 文件名
     * @param path 路径
     * @param defaultValue 默认值
     * @return 结果
     */
    public static double getDoubleValue(String fileName, String path, double defaultValue)
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return defaultValue;
        }
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return defaultValue;
        }
        String result = node.getText();
        return Double.parseDouble(result);
    }

    /**
     * 读取字符配置值
     * @param fileName 文件名
     * @param path 路径
     * @param defaultValue 默认值
     * @return 结果
     */
    public static String getStringValue(String fileName, String path, String defaultValue)
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return defaultValue;
        }
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return defaultValue;
        }
        String result = node.getText();
        return result;
    }

    /**
     * 读取字符配置值
     * @param fileName 文件名
     * @param pre 参数
     * @param nameSpace 参数
     * @param path 参数
     * @param attr 属性
     * @param defaultValue 默认值
     * @return 结果
     */
    public static String getStringValue(String fileName, String pre, String nameSpace, String path, String attr, String defaultValue)
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return defaultValue;
        }
        root.addNamespace(pre, nameSpace);
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return defaultValue;
        }
        String result = node.attributeValue(attr);
        return result;
    }

    /**
     * 获取配置项列表
     * @param fileName 文件名
     * @param path 路径
     * @return 结果
     */
    public static List<Element> getElements(String fileName, String path)
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return null;
        }
        return root.selectNodes(path);
    }

    /**
     * 设置配置项的值并保存
     * @param fileName 文件名
     * @param path 路径
     * @param value 值
     * @throws Exception 异常
     */
    public static void setStringValue(String fileName, String path, String value) throws Exception
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return;
        }
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return;
        }
        node.setText(value);
        writeBack(fileName);
    }

    /**
     * 设置配置项的值并保存
     * @param fileName 文件名
     * @param pre 参数
     * @param nameSpace 空间
     * @param path 路径
     * @param attr 属性
     * @param value 值
     * @throws Exception 异常
     */
    public static void setStringValue(String fileName, String pre, String nameSpace, String path, String attr, String value) throws Exception
    {
        Element root = getRoot(fileName);
        if (root == null)
        {
            return;
        }
        root.addNamespace(pre, nameSpace);
        Element node = (Element) root.selectSingleNode(path);
        if (node == null)
        {
            return;
        }
        node.addAttribute(attr, value);
        writeBack(fileName);
    }

    /**
     * @param fileName 文件名
     * @throws Exception 异常
     */
    private static void writeBack(String fileName) throws IOException, Exception
    {
        Document doc = getDocument(fileName);
        if (doc == null)
        {
            return;
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter(new File(XmlCfgUtil.class.getClassLoader().getResource(fileName + ".xml").toURI())), format);
        writer.write(doc);
        writer.close();
        cfgMap.remove(fileName);
    }
}
