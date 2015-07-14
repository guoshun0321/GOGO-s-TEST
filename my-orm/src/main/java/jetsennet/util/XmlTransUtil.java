package jetsennet.util;

public class XmlTransUtil
{

    /**
     * 替换掉xml中的特殊字符
     * 
     * @param value
     * @return
     */
    public static String xmlSpecialChar(String value)
    {
        StringBuilder sb = new StringBuilder(value.length());
        char[] chars = value.toCharArray();
        for (char c : chars)
        {
            switch (c)
            {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

}
