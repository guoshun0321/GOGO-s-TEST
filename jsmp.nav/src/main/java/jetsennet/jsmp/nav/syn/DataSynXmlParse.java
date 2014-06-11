package jetsennet.jsmp.nav.syn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.dal.DataSourceManager;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.tableinfo.TableInfoMgr;
import jetsennet.util.JDomUtil;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步协议XML解析
 * 
 * @author 郭祥
 */
public class DataSynXmlParse
{

    /**
     * 字段信息
     */
    private static final Map<String, Field> fieldMap = DataSynEntity.getEntityInfo();
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DataSynXmlParse.class);

    public static DataSynEntity parseXml(String xml)
    {
        DataSynEntity retval = null;
        InputStream in = null;
        try
        {
            SAXBuilder builder = new SAXBuilder(false);
            in = new ByteArrayInputStream(xml.getBytes());
            Document doc = builder.build(in);

            Element root = doc.getRootElement();
            Element headerEle = root.getChild("header");
            parseHeaderEle(headerEle, retval);

            Element bodyEle = root.getChild("body");
            parseBodyEles(bodyEle, retval);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new DataSynException(ex);
        }
        return retval;
    }

    private static DataSynEntity parseHeaderEle(Element headerEle, DataSynEntity entity)
    {
        try
        {
            List<Element> children = headerEle.getChildren();
            for (Element child : children)
            {
                String key = child.getName();
                String value = child.getText();
                Field f = fieldMap.get(key);
                if (f != null)
                {
                    Class<?> type = f.getType();
                    if (type == String.class)
                    {
                        f.set(entity, value);
                    }
                    else if (type == long.class)
                    {
                        f.set(entity, Long.valueOf(value));
                    }
                    else if (type == DataSynMsgTypeEnum.class)
                    {
                        f.set(entity, DataSynMsgTypeEnum.valueOf(value));
                    }
                    else if (type == DataSynOpCodeEnum.class)
                    {
                        f.set(entity, DataSynOpCodeEnum.valueOf(value));
                    }
                    else
                    {
                        throw new DataSynException("不支持的数据类型：" + type);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new DataSynException(ex);
        }
        return entity;
    }

    private static DataSynEntity parseBodyEles(Element bodyEle, DataSynEntity entity)
    {
        try
        {
            List<Element> children = bodyEle.getChildren();
            for (Element child : children)
            {
                DataSynContentEntity content = new DataSynContentEntity();
                content.setOpFlag(getAttrInt(child, "opFlag"));

                Element contentChild = (Element) child.getChildren().get(0);
                content.setObj(content2Entity(contentChild));

                entity.addContent(content);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new DataSynException(ex);
        }
        return entity;
    }

    private static Object content2Entity(Element ele)
    {
        String name = ele.getName();
        TableInfo table = DataSourceManager.MEDIA_FACTORY.getTableInfo(name);
        if (table == null)
        {
            throw new DataSynException("未知表结构：" + name);
        }
        Object retval = null;
        try
        {
            retval = table.getClass().newInstance();
        }
        catch (Exception ex)
        {
            throw new DataSynException(ex);
        }
        List<Element> children = ele.getChildren();
        for (Element child : children)
        {
            String key = child.getName();
            String value = child.getText();
            table.getFieldInfo(key).set(retval, value);
        }
        return retval;
    }

    private static int getAttrInt(Element ele, String name)
    {
        String temp = ele.getAttributeValue(name);
        return Integer.valueOf(temp);
    }
}
