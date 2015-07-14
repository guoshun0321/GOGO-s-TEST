package jetsennet.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import jetsennet.orm.util.UncheckedOrmException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 安全的线程格式转换类。
 * joda time 在jdk6下转换时，转换出的时间比实际时间少5秒。所以先放弃使用joda time。
 * 
 * @author 郭祥
 */
public class SafeDateFormater
{

    /**
     * 默认的时间转换实例
     */
    public static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 采用默认格式，将date转换为字符串格式。默认格式为：yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static final String format(Date date)
    {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将时间转换为字符串格式
     * 
     * @param date
     * @param format
     * @return
     */
    public static final String format(Date date, String formatStr)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            return format.format(date);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException();
        }
    }

    /**
     * 将字符串转换为date
     * 
     * @param date
     * @return
     */
    public static final Date parse(String date)
    {
        if (date.length() == "xxxx-xx-xx".length())
        {
            date = date + " 00:00:00";
        }
        return parse(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将字符串转换为date。
     * 
     * @param date
     * @param format
     * @return
     */
    public static final Date parse(String date, String formatStr)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            return format.parse(date);
        }
        catch (Exception ex)
        {
            throw new UncheckedOrmException("SafeDateFormat解析出错：" + date);
        }
    }

    /**
     * 传入参数为null时，返回默认的时间处理实例
     * 
     * @param format
     * @return
     */
    private static final DateTimeFormatter ensureFormatter(String format)
    {
        DateTimeFormatter formatter = null;
        if (format == null)
        {
            formatter = DEFAULT_FORMAT;
        }
        else
        {
            formatter = DateTimeFormat.forPattern(format).withZoneUTC();
        }
        return formatter;
    }

    public static void printOffset(Date date)
    {
        System.out.println("date.getTime : " + date.getTime());
        long offset = TimeZone.getTimeZone("Asia/Shanghai").getOffset(date.getTime()) / 1000;
        System.out.println(offset);
        int offsetHours1 = (int) (offset / 3600);
        long minutes1 = (offset % 3600);
        System.out.println(offsetHours1 + ":0" + minutes1 / 60 + ":" + (minutes1 % 60));
    }

    public static void main(String[] args) throws Exception
    {

        printOffset(parse("2013-11-12 11:11:11"));
        printOffset(parse("2013-11-12 11:11:12"));
        printOffset(parse("2013-11-12 11:11:21"));

        DateTimeZone tz = DateTimeZone.forID("Asia/Shanghai");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(tz);
        DateTime dt = formatter.parseDateTime("1927-12-11 11:22:38");
        Date date1 = dt.toDate();
        System.out.println(dt);
        System.out.println(date1);
        printOffset(date1);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setLenient(false);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date2 = df.parse("1927-12-11 11:22:38");
        System.out.println(date2);
        printOffset(date2);
    }
}
