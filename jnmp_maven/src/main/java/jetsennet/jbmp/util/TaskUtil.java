/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.CollectTaskEntity;

/**
 * @author？
 */
public class TaskUtil
{
    
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TaskUtil.class);

    /**
     * 目标主机里已有的进程
     * @param period 参数
     * @param single 参数
     * @return result 结果
     */
    public static boolean compairTask(CollectTaskEntity period, CollectTaskEntity single)
    {
        byte[] pByte = TaskUtil.periodToByte(period);
        byte[] sByte = TaskUtil.singleToByte(single);
        boolean result = false;
        for (int i = 0; i < pByte.length; i++)
        {
            byte add = (byte) (pByte[i] & sByte[i]);
            if (add != 0)
            {
                result = true;
                return result;
            }
        }
        return result;
    }

    /**
     * @param period 对象
     * @return 结果
     */
    public static byte[] periodToByte(CollectTaskEntity period)
    {
        String weekStr = period.getWeekMask();
        String hourStr = period.getHourMask();
        String[] weekArray = weekStr.split("");
        String[] hourArray = hourStr.split(",");
        byte[] result = new byte[21];
        for (int i = 1; i < weekArray.length; i++)
        {
            if (!"0".equals(weekArray[i]))
            {
                for (int j = 0; j < hourArray.length; j++)
                {
                    int index = (i - 1) * hourArray.length + j;
                    String s = hourArray[j];
                    if (!"00".equals(s))
                    {
                        TaskUtil.setByteValue(result, index);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param single 对象
     * @return 结果
     */
    public static byte[] singleToByte(CollectTaskEntity single)
    {
        Date begin = single.getStartTime();
        Date end = single.getEndTime();
        byte[] result = new byte[21];

        long weekNum = 7 * 24 * 60 * 60 * 1000;
        long span = end.getTime() - begin.getTime();
        if (span >= weekNum)
        {
            for (int i = 0; i < result.length; i++)
            {
                result[i] = (byte) 0xff;
            }
            return result;
        }

        int beginWeek = TimeUtil.getWeek(begin);
        int beginHour = TimeUtil.getHour(begin);
        int beginMinute = TimeUtil.getMinute(begin);
        int beginSec = TimeUtil.getSecond(begin);
        long beginTime = ((((beginWeek - 1) * 24 + beginHour) * 60) + beginMinute) * 60 + beginSec;
        int beginIndex = (beginWeek - 1) * 24 + beginHour;
        int endWeek = TimeUtil.getWeek(end);
        int endHour = TimeUtil.getHour(end);
        int endMinute = TimeUtil.getMinute(end);
        int endSec = TimeUtil.getSecond(end);
        long endTime = ((((endWeek - 1) * 24 + endHour) * 60) + endMinute) * 60 + endSec;
        int endIndex = (endWeek - 1) * 24 + endHour;

        if (endTime > beginTime)
        {
            for (int i = beginIndex; i <= endIndex; i++)
            {
                TaskUtil.setByteValue(result, i);
            }
        }
        else
        {
            for (int i = 0; i < endIndex; i++)
            {
                TaskUtil.setByteValue(result, i);
            }
            for (int i = beginIndex; i <= 7 * 24 - 1; i++)
            {
                TaskUtil.setByteValue(result, i);
            }
        }
        return result;
    }

    /**
     * @param b 参数
     * @param position 参数
     */
    public static void setByteValue(byte[] b, int position)
    {
        byte[] valOfPos = { (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01, };
        int index = position / 8;
        int pos = position - (index * 8);
        b[index] = (byte) (b[index] | valOfPos[pos]);
    }

    /**
     * @param prt 参数
     */
    public static void printAll(byte[] prt)
    {
        if (prt.length != 21)
        {
            System.out.println("输入数据错误");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prt.length; i++)
        {
            sb.append(TaskUtil.byteToString(prt[i]));
        }
        for (int i = 24; i < sb.length();)
        {
            sb.insert(i, '\n');
            i = i + 24 + 1;
        }
        System.out.println(sb.toString());
    }

    /**
     * @param b 参数
     * @return 结果
     */
    public static String byteToString(byte b)
    {
        String s = Integer.toBinaryString(b & 255);
        int last = 8 - s.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < last; i++)
        {
            sb.append("0");
        }
        sb.append(s);
        return sb.toString();
    }

    /**
     * 比较当前时间和任务的时间，检查该任务是否为有效任务
     * @param task 任务
     * @return 0,合法。1，过期（单次任务），不合法（周期任务）。2，开始时间大于当前时间（单次任务）。
     */
    public static int isValidate(CollectTaskEntity task)
    {
        Date cur = new Date();
        if (task.getTaskType() == CollectTaskEntity.TASK_TYPE_SINGLE)
        {
            Date start = task.getStartTime();
            Date end = task.getEndTime();

            if ((cur.after(start) || cur.equals(start)) && (cur.before(end)))
            {
                return 0;
            }
            else if (cur.after(end) || cur.equals(end))
            {
                return 1;
            }
            else if (cur.before(start))
            {
                return 2;
            }
            else
            {
                return 1;
            }
        }
        else
        {
            int day = TimeUtil.getWeek(cur);
            int hour = TimeUtil.getHour(cur) + 1;
            hour = hour >= 24 ? 0 : hour;
            String shour = Integer.toString(hour);
            if (hour >= 0 && hour <= 9)
            {
                shour = "0" + shour;
            }
            if (task.getWeekMask().lastIndexOf(Integer.toString(day)) != -1 && task.getHourMask().lastIndexOf(shour) != -1)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    /**
     * 验证当前时间是否满足日期掩码
     * @param weekMask 星期掩码
     * @param hourMask 时间掩码
     * @param cur 参数
     * @return 结果
     */
    public static boolean validWeek(String weekMask, String hourMask, Date cur)
    {
        if (weekMask == null || hourMask == null)
        {
            return false;
        }
        String sday = TimeUtil.getWeekStr(cur);
        String shour = TimeUtil.getHourStr(cur);
        if (weekMask.lastIndexOf(sday) != -1 && hourMask.lastIndexOf(shour) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 判断任务是否过期
     * @param task 任务
     * @return 结果
     */
    public static boolean isTimeout(CollectTaskEntity task)
    {
        if (task.getTaskType() == CollectTaskEntity.TASK_TYPE_SINGLE)
        {
            Date cur = new Date();
            Date end = task.getEndTime();
            if (cur.after(end) || cur.equals(end))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取任务第一次可运行的时间
     * @param task 任务
     * @return 结果
     */
    public static long getFirstTime(CollectTaskEntity task)
    {
        Date cur = new Date();
        if (task.getTaskType() == CollectTaskEntity.TASK_TYPE_SINGLE)
        {
            Date start = task.getStartTime();
            if (cur.after(start))
            {
                return TimeUnit.SECONDS.toNanos(task.getCollTimespan() - cur.getTime() / 1000 % task.getCollTimespan());
            }
            else
            {
                return TimeUnit.MILLISECONDS.toNanos(start.getTime() - cur.getTime());
            }
        }
        else
        {
            long seconds = getLeftSeconds(task, cur);
            if (seconds == 0)
            {
                return TimeUnit.SECONDS.toNanos(task.getCollTimespan() - cur.getTime() / 1000 % task.getCollTimespan());
            }
            else
            {
                return TimeUnit.SECONDS.toNanos(seconds);
            }
        }
    }

    /**
     * 获取任务下一次的运行时间
     * @param task 任务
     * @return 结果
     */
    public static long getNextTime(CollectTaskEntity task)
    {
        Date cur = new Date();
        if (task.getTaskType() == CollectTaskEntity.TASK_TYPE_SINGLE)
        {
            Date end = task.getEndTime();
            if (cur.after(end))
            {
                // -1表示任务已经过期
                return -1;
            }
            return TimeUnit.SECONDS.toNanos(task.getCollTimespan());
        }
        else
        {
            long seconds = getLeftSeconds(task, cur);
            if (seconds > 0)
            {
                return TimeUnit.SECONDS.toNanos(seconds);
            }
            else
            {
                return TimeUnit.SECONDS.toNanos(task.getCollTimespan());
            }
        }
    }

    /**
     * 获取当前时间到下一个间隔的时间差(单位:秒)
     * @param task
     * @param cur
     * @return
     */
    private static long getLeftSeconds(CollectTaskEntity task, Date cur)
    {
        long seconds = 0;
        int day = TimeUtil.getWeek(cur);
        int hour = TimeUtil.getHour(cur) + 1;
        String weekMask = task.getWeekMask();
        String hourMask = task.getHourMask();
        String shour = Integer.toString(hour);
        if (hour < 10)
        {
            shour = "0" + shour;
        }

        if (weekMask.lastIndexOf(Integer.toString(day)) > -1)
        {
            // 日期包含当天时
            // 从当前时间的下一个小时开始计算，第一个小时需要计算差值，其他的小时都加上60 * 60
            while (hourMask.lastIndexOf(shour) == -1)
            {
                if (seconds == 0)
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cur);
                    int min = cal.get(Calendar.MINUTE);
                    int sec = cal.get(Calendar.SECOND);
                    seconds = 60 * 60 - ((min * 60 + sec));
                }
                else
                {
                    seconds += 60 * 60;
                }
                hour++;
                if (hour < 10)
                {
                    shour = "0" + hour;
                }
                else if (hour > 24)
                {
                    hour = 1;
                    shour = "0" + hour;
                    day++;
                    if (day > 7)
                    {
                        day = 1;
                    }
                    break;
                }
                else
                {
                    shour = Integer.toString(hour);
                }
            }
        }
        else
        {
            // 日期不包含当天
            Calendar cal = Calendar.getInstance();
            cal.setTime(cur);
            int min = cal.get(Calendar.MINUTE);
            int sec = cal.get(Calendar.SECOND);
            seconds = 60 * 60 - ((min * 60 + sec));
            hour++;
            while (hour <= 24)
            {
                seconds += 60 * 60;
                hour++;
            }
            hour = 1;
            shour = "0" + hour;
            day++;
            if (day > 7)
            {
                day = 1;
            }
        }

        while (weekMask.lastIndexOf(Integer.toString(day)) == -1)
        {
            seconds += 24 * 60 * 60;
            day++;
            if (day > 7)
            {
                day = 1;
            }
        }

        while (hourMask.lastIndexOf(shour) == -1)
        {
            seconds += 60 * 60;
            hour++;
            if (hour < 10)
            {
                shour = "0" + hour;
            }
            else
            {
                shour = Integer.toString(hour);
            }
        }
        logger.debug("left time : " + genPrintSpan(seconds));
        return seconds;
    }

    public static String genPrintSpan(long time)
    {
        long day = time / (24 * 60 * 60);
        long dayReminder = time - day * (24 * 60 * 60);
        long hour = dayReminder / (60 * 60);
        long hourReminder = dayReminder - hour * (60 * 60);
        long min = hourReminder / 60;
        long minReminder = hourReminder - min * 60;

        StringBuilder sb = new StringBuilder();
        sb.append(day).append(" day ").append(hour).append(" hour ").append(min).append(" min ").append(minReminder).append(" sec");
        return sb.toString();
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        System.out.println(TaskUtil.genPrintSpan(24 * 60 * 60 + 2 * 60 * 60 + 15 * 60 + 45));
        String hourMask = "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24";
        String weekMask = "1234567";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 24);
        TaskUtil.validWeek(weekMask, hourMask, c.getTime());

        CollectTaskEntity task = new CollectTaskEntity();
        task.setTaskType(CollectTaskEntity.TASK_TYPE_PERIOD);
        Date cur = null;
        long seconds = 0;

        task.setWeekMask("1234567");
        task.setHourMask("01");
        cur = new Date(2011 - 1900, 7 - 1, 8, 0, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 0);

        task.setWeekMask("1234567");
        task.setHourMask("01,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 6, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60);

        task.setWeekMask("1234567");
        task.setHourMask("01,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 8, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 15 * 60 * 60);

        task.setWeekMask("1234567");
        task.setHourMask("01,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60);

        task.setWeekMask("1234567");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 2 * 60 * 60);

        task.setWeekMask("1234507");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 26 * 60 * 60);

        task.setWeekMask("1234500");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 50 * 60 * 60);

        task.setWeekMask("0234500");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 74 * 60 * 60);

        task.setWeekMask("1234060");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 23, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 2 * 60 * 60);

        task.setWeekMask("1234060");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 2, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 23 * 60 * 60);

        task.setWeekMask("1234060");
        task.setHourMask("03,08");
        cur = new Date(2011 - 1900, 7 - 1, 8, 1, 14);
        seconds = getLeftSeconds(task, cur);
        assert (seconds == 46 * 60 + 24 * 60 * 60);
    }
}
