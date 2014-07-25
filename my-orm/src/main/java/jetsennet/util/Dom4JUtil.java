package jetsennet.util;

import org.dom4j.Element;

import jetsennet.orm.util.UncheckedOrmException;

public class Dom4JUtil
{

    /**
     * 获取xml元素属性的值
     * 
     * @param ele 被解析的xml元素
     * @param attrName 属性名
     * @param def 默认值
     * @param isThrown 当属性不存在时，是否抛出异常
     * @return
     */
    public static final String getAttrString(Element ele, String attrName, String def, boolean isThrown)
    {
        String retval = def;
        try
        {
            retval = ele.attributeValue(attrName);
        }
        catch (Exception ex)
        {
            if (isThrown)
            {
                throw new UncheckedOrmException(ex);
            }
        }
        return retval;
    }

    /**
     * 当取值为"true"时，返回true；取值为"false"时，返回false，其他返回def
     * 
     * @param ele
     * @param key
     * @param def
     * @param isThrown
     * @return
     */
    public static final boolean getAttrBoolean(Element ele, String key, boolean def, boolean isThrown)
    {
        boolean retval = def;
        try
        {
            String temp = ele.attributeValue(key);
            if (temp != null)
            {
                if (temp.equalsIgnoreCase("true"))
                {
                    retval = true;
                }
                else if (temp.equalsIgnoreCase("false"))
                {
                    retval = false;
                }
            }
        }
        catch (Exception ex)
        {
            if (isThrown)
            {
                throw new UncheckedOrmException(ex);
            }
        }
        return retval;
    }

    public static final boolean getAttrTrue(Element ele, String key, boolean isThrown)
    {
        boolean retval = false;
        try
        {
            String str = ele.attributeValue(key);
            if (str.equalsIgnoreCase("true"))
            {
                retval = true;
            }
        }
        catch (Exception ex)
        {
            if (isThrown)
            {
                throw new UncheckedOrmException(ex);
            }
        }
        return retval;
    }

}
