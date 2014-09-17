package jetsennet.jsmp.nav.util;

import java.util.Calendar;
import java.util.Date;

import jetsennet.util.SafeDateFormater;

public class DateUtil
{

	/**
	 * 从当前时间开始计算，往前推pre天得到日期N。返回结果为N的0点0分0秒自从标准基准时间（称为“历元（epoch）”，即 1970 年 1 月 1 日 00:00:00 GMT）以来的指定毫秒数
	 * 
	 * @param date
	 * @param pre
	 * @return
	 */
	public static long getPreTimeOfDay(Date date, int pre)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (pre != 0)
		{
			calendar.add(Calendar.DAY_OF_MONTH, pre * -1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.getTimeInMillis();
		return calendar.getTimeInMillis();
	}

	public static Date preDate(Date date, int pre)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (pre != 0)
		{
			calendar.add(Calendar.DAY_OF_MONTH, pre * -1);
		}
		return calendar.getTime();
	}

	/**
	 * 生成时间条件(COLUMN >= yyyy-MM-dd 00:00:00 AND COLUMN <= yyyy-MM-dd 23:59:59)
	 * 
	 * @param column
	 * @param date
	 * @param pre
	 * @return
	 */
	public static String genDataCondition(String column, Date date, int pre)
	{
		String max = SafeDateFormater.format(date, "yyyy-MM-dd") + " 23:59:59";

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, pre * -1);
		String min = SafeDateFormater.format(calendar.getTime(), "yyyy-MM-dd") + " 00:00:00";

		StringBuilder sb = new StringBuilder();
		sb.append(column).append(" >= ").append(min);
		sb.append(" AND ").append(column).append(" <= ").append(max);
		return sb.toString();
	}

}
