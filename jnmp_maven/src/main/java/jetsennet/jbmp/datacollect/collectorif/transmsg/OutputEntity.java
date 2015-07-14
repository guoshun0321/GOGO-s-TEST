package jetsennet.jbmp.datacollect.collectorif.transmsg;

import java.io.Serializable;

/**
 * 服务器端返回值的结构
 */
public class OutputEntity implements Serializable
{

    /**
     * 计算值，可能为String，null或者Double.NaN
     */
    public Object value;
    /**
     * 实例化时，为对象属性名称
     */
    public String name;
    /**
     * SNMP实例化时，为对象属性公式表达式
     */
    public String exp;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;

    /**
     * @param value 参数
     * @return 结果
     */
    public static OutputEntity genOutput(Object value)
    {
        return genOutput(value, null, null);
    }

    /**
     * @param value 值
     * @param name 名称
     * @param exp 参数
     * @return 结果
     */
    public static OutputEntity genOutput(Object value, String name, String exp)
    {
        OutputEntity out = new OutputEntity();
        out.value = value;
        out.name = name;
        out.exp = exp;
        return out;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("表达式：");
        sb.append(exp);
        sb.append("；名称：");
        sb.append(name);
        sb.append("；值：");
        sb.append(value);
        return sb.toString();
    }

    /**
     * @return 值
     */
    public String getValue()
    {
        if (value == null || value instanceof Double)
        {
            return "";
        }
        return value.toString();
    }

    /**
     * 将采集到的值转换成合法的xml字符串
     * 
     * @return
     */
    public String toXmlValue()
    {
        String retval = "";
        if (value != null && value instanceof String)
        {
            retval = ((String) value).replaceAll("&", "&amp;").replaceAll("<", "&lt;");
        }
        return retval;
    }
}
