/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author？
 */
public class TimeUtil
{

    /**
     * 获取当前星期数，数据为1-7，表示星期一到星期日
     * @param date 日期
     * @return 结果
     */
    public static int getWeek(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(Calendar.DAY_OF_WEEK);
        result = result - 1;
        if (result == 0)
        {
            result = 7;
        }
        return result;
    }

    /**
     * 获取当前星期数，数据为"1"-"7"，表示星期一到星期日
     * @param date 日期
     * @return 结果
     */
    public static String getWeekStr(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(Calendar.DAY_OF_WEEK);
        result = result - 1;
        if (result == 0)
        {
            result = 7;
        }
        return Integer.toString(result);
    }

    /**
     * 当前的时间，数据为0-23
     * @param date 日期
     * @return 结果
     */
    public static int getHour(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(Calendar.HOUR_OF_DAY);
        return result;
    }

    /**
     * 当前的时间，数据为"01"-"24"
     * @param date 日期
     * @return 结果
     */
    public static String getHourStr(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY) + 1;
        String retval = Integer.toString(hour);
        if (hour >= 0 && hour <= 9)
        {
            retval = "0" + retval;
        }
        return retval;
    }

    /**
     * @param date 日期
     * @return 结果
     */
    public static int getMinute(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(Calendar.MINUTE);
        return result;
    }

    /**
     * @param date 日期
     * @return 结果
     */
    public static int getSecond(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(Calendar.SECOND);
        return result;
    }

    /**
     * @param hour 小时
     * @return 结果
     */
    public static long getMilliSecond(int hour)
    {
        return hour * 60 * 60 * 1000;
    }

    /**
     * @param date 日期
     * @return 结果
     */
    public static long getLastOfHour(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int mill = cal.get(Calendar.MILLISECOND);
        long last = 60 * 60 * 1000 - ((min * 60 + sec) * 1000 + mill);
        return last;
    }

    /**
     * @param date 日期
     * @return 结果
     */
    public static String dateToString(Date date)
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * @param str 参数
     * @return 结果
     * @throws ParseException 异常
     */
    public static Date stringToDate(String str) throws ParseException
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(str);
    }

    /**
     * 将long型的时间改为 day days, hour:min:sec.millsec
     * @param lSpan 参数
     * @return 结果
     */
    public static String longToSpan(long lSpan)
    {
        if (lSpan < 0)
        {
            return "";
        }
        long millsec = lSpan % 1000l;
        lSpan = lSpan / 1000l;

        long sec = lSpan % 60l;
        lSpan = lSpan / 60l;

        long min = lSpan % 60l;
        lSpan = lSpan / 60l;

        long hour = lSpan % 24l;
        lSpan = lSpan / 24l;

        long day = lSpan;

        System.out.println(lSpan);

        return String.format("%s days, %s:%s:%s.%s", day, hour, min, sec, millsec);
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        String str = TimeUtil.longToSpan(378312l);
        System.out.println(str);
    }
}
