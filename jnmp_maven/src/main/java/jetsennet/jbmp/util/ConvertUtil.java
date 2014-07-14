/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 类型转换工具类
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.exception.ConvertException;

/**
 * @author 郭祥
 */
public class ConvertUtil
{

    private static final Logger logger = Logger.getLogger(ConvertUtil.class);

    /**
     * 将str转换成整数，出错时返回null。
     * @param value 值
     * @return 结果
     */
    public static Integer stringToInt(String value)
    {
        Integer result = null;
        if (value != null)
        {
            try
            {
                result = Integer.valueOf(value);
            }
            catch (Exception ex)
            {
                result = null;
            }
        }
        return result;
    }

    /**
     * 将str转换成整数，出错时返回def。
     * @param value 值
     * @param def 参数
     * @return 结果
     */
    public static int stringToInt(String value, int def)
    {
        try
        {
            return Integer.valueOf(value);
        }
        catch (Exception ex)
        {
            return def;
        }
    }

    /**
     * 将对象转换为Double型数据。如果不能转换，则返回NULL。
     * @param obj 对象
     * @return 结果
     */
    public static Double stringToDouble(Object obj)
    {
        Double retval = null;
        if (obj == null)
        {
            return retval;
        }
        String temp = obj.toString();
        try
        {
            retval = Double.valueOf(temp);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        return retval;
    }

    /**
     * 将str转换成整数，出错时返回null。
     * @param value 值
     * @return 结果
     */
    public static Long stringToLong(String value)
    {
        Long result = null;
        try
        {
            result = Long.valueOf(value);
        }
        catch (Exception ex)
        {
            result = null;
        }
        return result;
    }

    /**
     * 将str转换成整数，出错时返回def。
     * @param value 值
     * @param def 参数
     * @return 结果
     */
    public static long stringToLong(String value, long def)
    {
        try
        {
            return Long.valueOf(value);
        }
        catch (Exception ex)
        {
            return def;
        }
    }

    /**
     * 将对象转化成LONG型。无法转换时返回NULL。
     * @param obj 对象
     * @return 结果
     */
    public static Long ObjectToLong(Object obj)
    {
        Long retval = null;
        if (obj != null)
        {
            try
            {
                retval = Double.valueOf(obj.toString()).longValue();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }
        return retval;
    }

    /**
     * 在当前时间往前推几天的时间，结果用LONG表示
     * @param date 日期
     * @param day 天数
     * @return 结果
     */
    public static long parseTimeToLong(Date date, int day)
    {
        if (date == null)
        {
            throw new NullPointerException();
        }
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime().getTime();
    }

    /**
     * 将字符串str用符号pos分割开
     * @param str 参数
     * @param pos 参数
     * @return 结果
     */
    public static ArrayList<String> split(String str, String pos)
    {
        if (str == null || pos == null)
        {
            throw new NullPointerException();
        }
        ArrayList<String> result = new ArrayList<String>();
        if (str == null || pos == null)
        {
            return result;
        }
        String[] strs = str.split(pos);
        result.addAll(Arrays.asList(strs));
        return result;
    }

    /**
     * 将所有的空格，回车等替换成" "
     * @param str 参数
     * @return 结果
     */
    public static String replaceAllSpace(String str)
    {
        if (str == null || "".equals(str.trim()))
        {
            return " ";
        }
        String reg = "\\s+";
        str = str.replaceAll(reg, " ");
        return str;
    }

    /**
     * 将一个List转换成"xxxxx,xxxxxx,xxxxx"的形式
     * @param list list
     * @param div 分割标识
     * @param isNull 是否添加NULL
     * @return 结果
     */
    public static String listToString(List<? extends Object> list, String div, boolean isNull)
    {
        if (list == null || list.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : list)
        {
            if (o != null)
            {
                sb.append(o.toString());
            }
            else
            {
                if (isNull)
                {
                    sb.append("NULL");
                }
            }
            sb.append(div);
        }
        int divLength = div == null ? 0 : div.length();
        if (sb.length() > 0 && divLength > 0)
        {
            sb.deleteCharAt(sb.length() - divLength);
        }
        return sb.toString();
    }

    /**
     * 将一个数组转换成"xxxxx,xxxxxx,xxxxx"的形式
     * @param list
     * @param div 分割标识
     * @param isNull 是否添加NULL
     * @return 结果
     */
    public static String arrayToString(Object[] list, String div, boolean isNull)
    {
        if (list == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : list)
        {
            if (o != null)
            {
                sb.append(o.toString());
            }
            else
            {
                if (isNull)
                {
                    sb.append("NULL");
                }
            }
            sb.append(div);
        }
        int divLength = div == null ? 0 : div.length();
        if (sb.length() > 0 && divLength > 0)
        {
            sb.deleteCharAt(sb.length() - divLength);
        }
        return sb.toString();
    }

    /**
     * 将数据包装成客服端和服务器端传输的格式，数据组织格式如下 name1 name2 name3 name4 name5 val11 val21 val31 val41 val51 val12 val22 val32 val42 val52 val13 val23 val33 val43
     * val53
     * @param names 参数
     * @param values 参数
     * @return 结果
     */
    public static String wrapToTrans(String[] names, List<List<String>> values)
    {
        if (names == null || values == null)
        {
            throw new NullPointerException();
        }
        if (names.length != values.size())
        {
            throw new ConvertException("名称和值的长度不相等。");
        }
        int rowNum = -1;
        for (List<String> column : values)
        {
            if (column == null)
            {
                throw new NullPointerException();
            }
            if (rowNum < 0)
            {
                rowNum = column.size();
            }
            else if (rowNum != column.size())
            {
                throw new ConvertException("列长度不相同");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        XmlUtil.appendXmlBegin(sb, "RecordSet");
        for (int i = 0; i < rowNum; i++)
        {
            XmlUtil.appendXmlBegin(sb, "Record");
            for (int j = 0; j < names.length; j++)
            {
                XmlUtil.appendTextNode(sb, names[j], values.get(j).get(i));
            }
            XmlUtil.appendXmlEnd(sb, "Record");
        }
        XmlUtil.appendXmlBegin(sb, "Record1");
        XmlUtil.appendTextNode(sb, "TotalCount", Integer.toString(rowNum));
        XmlUtil.appendXmlEnd(sb, "Record1");
        XmlUtil.appendXmlEnd(sb, "RecordSet");
        return sb.toString();
    }

    /**
     * 检查字符串是否包含WebService不可传递的字符
     * @param str 字符串
     * @return 结果
     */
    public static boolean containIllegalChar(String str)
    {
        boolean retval = false;
        if (str == null || "".equals(str))
        {
            return retval;
        }
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            int ci = ch;
            if (ci == 9 || ci == 10 || ci == 13 || (ci >= 32 && !Character.isISOControl(ci)))
            {
                continue;
            }
            else
            {
                retval = true;
            }
        }
        return retval;
    }

    /**
     * 去除WebService不可传递的字符
     * @param str 字符串
     * @return 结果
     */
    public static String chopWhitespace(String str)
    {
        if (null == str || "".equals(str))
        {
            return "";
        }

        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            int ci = ch;
            if (9 == ci || 10 == ci || 13 == ci || 32 <= ci && !Character.isISOControl(ci))
            {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 将星期掩码转换成数组，不检查weekMask的合法性
     * @param weekMask 参数
     * @return 结果
     */
    public static boolean[] weekMaskToArray(String weekMask)
    {
        boolean[] retval = new boolean[7];
        for (int i = 1; i <= 7; i++)
        {
            char c = weekMask.charAt(i - 1);
            int j = Integer.valueOf(String.valueOf(c));
            if (i == j)
            {
                retval[i - 1] = true;
            }
            else
            {
                retval[i - 1] = false;
            }
        }
        return retval;
    }

    /**
     * 计算从curDay数起到下一个有效的星期需要多少天 curDay为[1,7]的正整数
     * @param weekMask 星期掩码
     * @param curDay 当前星期
     * @param isToday 是否考虑curDay
     * @return 结果
     */
    public static int daySpan(String weekMask, int curDay, boolean isToday)
    {
        if (curDay < 1 || curDay > 7)
        {
            return -1;
        }
        int temp = curDay - 1;
        boolean[] weeks = weekMaskToArray(weekMask);
        for (int i = 0; i < 7; i++)
        {
            // if (temp >= 7)
            // {
            // temp = temp - 7;
            // }
            if (i == 0)
            {
                if (isToday && weeks[temp])
                {
                    return i;
                }
            }
            else
            {
                if (weeks[temp])
                {
                    return i;
                }
            }
            temp++;
            temp = temp >= 7 ? 0 : temp;
        }
        if (!isToday && weeks[curDay - 1])
        {
            return 7;
        }
        return -1;
    }

    /**
     * 解析形如：xx:xx:xx，未判断时间格式
     * @param time 时间
     * @return 结果
     */
    public static int[] parseTime(String time)
    {
        int[] retval = new int[3];
        String[] temps = time.split(":");
        for (int i = 0; i < 3; i++)
        {
            retval[i] = Integer.valueOf(temps[i]);
        }
        return retval;
    }

    /**
     * byte[] 合并
     * @param bytes1 参数1
     * @param bytes2 参数2
     * @return 结果
     */
    public static byte[] mergeByteArray(byte[] bytes1, byte[] bytes2)
    {
        if (bytes1 == null && bytes2 == null)
        {
            return new byte[0];
        }
        else if (bytes1 == null)
        {
            return bytes2;
        }
        else if (bytes2 == null)
        {
            return bytes1;
        }
        byte[] retval = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, retval, 0, bytes1.length);
        System.arraycopy(bytes2, 0, retval, bytes1.length, bytes2.length);
        return retval;
    }

    /**
     * 将String转换成日期
     * @param value 需要转换的字符串
     * @param form 字符串格式
     * @return null，转换失败
     */
    public static Date string2Date(String value, String form)
    {
        Date retval = null;
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(form);
            retval = format.parse(value);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }
}
