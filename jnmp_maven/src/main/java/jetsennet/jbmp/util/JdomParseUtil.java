/************************************************************************
日 期：2011-12-29
作 者: 郭祥
版 本：v1.3
描 述: Jdom解析工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import org.jdom.Element;

import jetsennet.jbmp.exception.ConfigureException;

/**
 * Jdom解析工具类
 * @author 郭祥
 */
public class JdomParseUtil
{

    /**
     * 获取el节点下，名称为name的值
     * @param el 父节点
     * @param name 节点名称
     * @param isThrow 不存在时是否抛出异常
     * @param defaultStr 默认值
     * @return 结果
     */
    public static String getElementString(Element el, String name, String defaultStr, boolean isThrow)
    {
        Element child = el.getChild(name);
        if (child != null)
        {
            return child.getText().trim();
        }
        else
        {
            if (isThrow)
            {
                throw new ConfigureException("配置模块：无法获取名称为：" + name + " 的元素对应的值");
            }
        }
        return defaultStr;
    }

    /**
     * 获取el节点下，名称为name的值，并将值转化成int类型
     * @param el 父节点
     * @param name 节点名称
     * @param isThrow 不存在时是否抛出异常
     * @param defaultInt 默认值
     * @return 结果
     */
    public static int getElementInt(Element el, String name, int defaultInt, boolean isThrow)
    {
        Element child = el.getChild(name);
        int result = defaultInt;
        if (child != null)
        {
            try
            {
                result = Integer.valueOf(child.getText().trim());
            }
            catch (Exception ex)
            {
                throw new ConfigureException("配置模块：类型：" + name + "解析出错。", ex);
            }
        }
        else
        {
            if (isThrow)
            {
                throw new ConfigureException("配置模块：无法获取名称为：" + name + " 的元素对应的值");
            }
        }
        return result;
    }

    /**
     * @param el 参数
     * @param name 参数
     * @param isThrow 参数
     * @return 结果
     */
    public static int[] getElementIntArray(Element el, String name, boolean isThrow)
    {
        Element child = el.getChild(name);
        int[] result = null;
        if (child != null)
        {
            try
            {
                String str = child.getText().trim();
                if (str.trim().isEmpty())
                {
                    return null;
                }
                else
                {
                    String[] temps = str.split(",");
                    result = new int[temps.length];
                    for (int i = 0; i < temps.length; i++)
                    {
                        result[i] = Integer.valueOf(temps[i]);
                    }
                }
            }
            catch (Exception ex)
            {
                throw new ConfigureException("配置模块：类型：" + name + "解析出错。", ex);
            }
        }
        else
        {
            if (isThrow)
            {
                throw new ConfigureException("配置模块：无法获取名称为：" + name + " 的元素对应的值");
            }
        }
        return result;
    }

    /**
     * 获取el节点下，名称为name的值。当节点存在值，且值为false时，返回false。其他情况下返回默认值
     * @param el
     * @param name
     * @param isThrow
     * @return
     */
    public static boolean getElementBoolean(Element el, String name, boolean def, boolean isThrow)
    {
        Element child = el.getChild(name);
        boolean retval = def;
        if (child != null)
        {
            String temp = child.getText().trim();
            if ("false".equalsIgnoreCase(temp))
            {
                retval = false;
            }
            else if ("true".equalsIgnoreCase(temp))
            {
                retval = true;
            }
        }
        else
        {
            if (isThrow)
            {
                throw new ConfigureException("配置模块：无法获取名称为：" + name + " 的元素对应的值");
            }
        }
        return retval;
    }
}
