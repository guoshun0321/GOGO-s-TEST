package jetsennet.orm.transform;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.orm.util.UncheckedOrmException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单的xml解析器，只由于解析特定格式的xml。
 * 目前使用dom4j进行解析，以后可以考虑手写解析器。
 * 
 * <Records>
 *   <Record>
 *     <ID>0</ID>
 *     <NAME>name</NAME>
 *   </Record>
 *   <Record>
 *     <ID>1</ID>
 *     <NAME>name1</NAME>
 *   </Record>
 * </Records>
 * 
 * @author 郭祥
 */
public class SimpleXmlParse
{

    private static final Logger logger = LoggerFactory.getLogger(SimpleXmlParse.class);

    /**
     * 解析xml生成List<Map<String, String>>
     * 
     * @param xml
     * @return
     */
    public static final List<Map<String, Object>> parse(String xml)
    {
        List<Map<String, Object>> retval = null;
        ByteArrayInputStream in = null;
        try
        {
            in = new ByteArrayInputStream(xml.getBytes());
            SAXReader reader = new SAXReader();
            Document doc = reader.read(in);

            Element rootE = doc.getRootElement();
            List<Element> recordEles = rootE.elements();
            retval = new ArrayList<Map<String, Object>>(recordEles.size());
            for (Element recordEle : recordEles)
            {
                List<Element> nodes = recordEle.elements();
                Map<String, Object> recordMap = new HashMap<String, Object>();
                for (Element node : nodes)
                {
                    recordMap.put(node.getName(), node.getText());
                }
                retval.add(recordMap);
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                in = null;
            }
        }
        return retval;
    }

}
