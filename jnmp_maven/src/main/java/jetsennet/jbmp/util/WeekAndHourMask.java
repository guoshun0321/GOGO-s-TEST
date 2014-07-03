/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 时间和星期掩码
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

/**
 * 时间和星期掩码
 * @author 郭祥
 */
public class WeekAndHourMask
{

    private String oriWeek;
    private String oriHour;
    private boolean[] weeks;
    private boolean[] hours;
    private String weekReg = "^[]{7,7}$";
    private String hourReg = "^([012]\\d)(\\.)$";

    /**
     * @param weekStr 星期
     * @param hourStr 小时
     */
    public WeekAndHourMask(String weekStr, String hourStr)
    {
        this.oriWeek = weekStr;
        this.oriHour = hourStr;
    }

    /**
     * @param iWeek 星期
     * @param iHour 小时
     */
    private void validAndAssign(String iWeek, String iHour)
    {

    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        assert false;
    }
}
