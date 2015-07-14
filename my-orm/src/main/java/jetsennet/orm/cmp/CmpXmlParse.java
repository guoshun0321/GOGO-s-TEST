package jetsennet.orm.cmp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.Dom4JUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmpXmlParse
{

    private static final String OP = "op";

    private static final String FIELD = "field";

    private static final String ATTR_TABLE = "table";

    private static final String ATTR_ACTION = "action";

    private static final String ATTR_FILTER = "filter";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_VALUE = "value";

    private static final String ATTR_REF = "ref";

    private static final Logger logger = LoggerFactory.getLogger(CmpXmlParse.class);

    public static CmpOpEntity parse(String xml)
    {
        CmpOpEntity retval = null;

        BufferedInputStream in = null;
        try
        {
            SAXReader reader = new SAXReader();
            in = new BufferedInputStream(new ByteArrayInputStream(xml.getBytes()));
            Document doc = reader.read(in);

            Element rootEle = doc.getRootElement();
            Element opEle = rootEle.element(OP);
            if (opEle != null)
            {
                retval = handleOpEle(opEle);
            }
            else
            {
                throw new UncheckedOrmException("xml不包含op元素：" + xml);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        return retval;
    }

    private static CmpOpEntity handleOpEle(Element opEle)
    {
        CmpOpEntity retval = null;
        if (opEle != null)
        {
            retval = new CmpOpEntity();
            String table = Dom4JUtil.getAttrString(opEle, ATTR_TABLE, null, true);
            String action = Dom4JUtil.getAttrString(opEle, ATTR_ACTION, null, true);
            String filter = Dom4JUtil.getAttrString(opEle, ATTR_FILTER, null, false);
            CmpOpEnum opEnum = CmpOpEnum.ignoreCaseValueOf(action);
            if ((opEnum.equals(CmpOpEnum.DELETE) || opEnum.equals(CmpOpEnum.SELECT)) && filter == null)
            {
                throw new UncheckedOrmException("未设置条件字段，操作：" + opEnum.name());
            }
            retval.setTableName(table);
            retval.setAction(opEnum);
            retval.setFilterName(filter);

            List<Element> children = opEle.elements();
            if (children != null)
            {
                for (Element child : children)
                {
                    String childName = child.getName();
                    if (childName.equals(FIELD))
                    {
                        String fieldName = Dom4JUtil.getAttrString(child, ATTR_NAME, null, true);
                        String fieldValue = Dom4JUtil.getAttrString(child, ATTR_VALUE, null, false);
                        String fieldRef = Dom4JUtil.getAttrString(child, ATTR_REF, null, false);
                        String temp = fieldValue != null ? fieldValue : (fieldRef != null ? fieldRef : null);
                        if (filter != null && fieldName.equals(filter))
                        {
                            if (temp == null)
                            {
                                throw new UncheckedOrmException("找不到过滤字段对应的值：" + filter);
                            }
                            else
                            {
                                retval.setFilterValue(temp);
                            }
                        }
                        retval.addValue(fieldName, temp);
                    }
                    else if (childName.equals(OP))
                    {
                        CmpOpEntity subCmp = handleOpEle(child);
                        retval.addSub(subCmp);
                    }
                    else
                    {
                        throw new UncheckedOrmException("非法标识：" + childName);
                    }
                }
            }
        }
        return retval;
    }

}
