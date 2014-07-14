package jetsennet.jbmp.util;

/**
 * @version 1.0 date 2011-12-15上午9:31:16
 * @author xli
 */

public class StringUtil
{

    /**
     * 比较两个字符串
     * @param first 参数
     * @param sec 参数
     * @return 结果
     */
    public static boolean stringCompare(String first, String sec)
    {
        if ((first == null && sec == null) || (first != null && first.equals(sec)))
        {
            return true;
        }
        return false;
    }

    /**
     * @param inp 参数
     * @return 结果
     */
    public static String quote(String inp)
    {
        StringBuffer sb = new StringBuffer(inp.length());
        sb.append('\"');
        sb.append(escape(inp));
        sb.append('\"');
        return sb.toString();
    }

    /**
     * @param str 参数
     * @return 结果
     */
    public static String escape(String str)
    {
        int size = str.length();
        StringBuffer sb = new StringBuffer(size);
        for (int i = 0; i < size; i++)
        {
            char ch = str.charAt(i);
            switch (ch)
            {
            case 0:
                continue;
            case '\n':
                sb.append("\\n");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\'':
                sb.append("\\\'");
                break;
            case '\"':
                sb.append("\\\"");
                break;
            default:
                if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e)
                {
                    String s = Integer.toString(ch, 16);
                    sb.append("\\x" + "0000".substring(s.length() - 4) + s);
                }
                else
                {
                    sb.append(ch);
                }
                break;
            }
        }
        return sb.toString();
    }

    /**
     * @param inp 参数
     * @return 结果
     */
    public static String unescapeString(String inp)
    {
        StringBuffer sb = new StringBuffer();
        int size = inp.length();
        for (int i = 0; i < size; i++)
        {
            char ch = inp.charAt(i);
            if (ch == '\\')
            {
                i++;
                if (i >= size)
                {
                    throw new IllegalArgumentException("String ended with an escape, but there was no subsequent character to escape");
                }
                ch = inp.charAt(i);
                switch (ch)
                {
                case 'n':
                    sb.append('\n');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case '\\':
                case '\'':
                case '\"':
                    sb.append(ch);
                    break;
                case 'X':
                case 'x':
                    sb.append("\\x");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid escape sequence '" + ch
                        + "' (valid sequences are  \\b  \\t  \\n  \\f  \\r  \\\"  \\\'  \\\\ \\x0000 \\X0000 )");
                }
            }
            else
            {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * @param value 值
     * @return 结果
     */
    public static String unquote(String value)
    {
        if (value.startsWith("\""))
        {
            if (value.endsWith("\""))
            {
                value = unescapeString(value.substring(1, value.length() - 1));
            }
            else
            {
                throw new IllegalArgumentException("String literal " + value + " is not properly closed by a double quote.");
            }
        }
        return value;
    }
}
