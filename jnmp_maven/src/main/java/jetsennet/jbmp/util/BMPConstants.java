/************************************************************************
日 期：2011-12-2
作 者: 郭祥
版 本：v1.3
描 述: JBMP项目通用的一些常量
历 史：
 ************************************************************************/
package jetsennet.jbmp.util;

/**
 * JBMP项目通用的一些常量
 * @author 郭祥
 */
public class BMPConstants
{

    /*************************************** SNMP协议相关 ***************************************/
    /**
     * SNMP协议默认端口
     */
    public static final int SNMP_PORT = 161;
    /**
     * SNMP协议默认共同体
     */
    public static final String SNMP_COMMUNITY = "public";
    /**
     * 系统解析SNMP时的默认编码
     */
    public static final String DEFAULT_SNMP_CODING = "GBK";

    /*************************************** 报警自动清除相关 ***************************************/
    /**
     * 数据库中标识是否自动清楚报警的字符串
     */
    public static final String AUTO_RECOVER_STR = "AlarmAutoRecover";
    /**
     * 报警不自动恢复
     */
    public static final int AUTO_RECOVER_NO = 1;
    /**
     * 报警自动恢复
     */
    public static final int AUTO_RECOVER_YES = 0;

    /**
     * 不自动生成对象
     */
    public static final int AUTO_INS_NO = 0;
    /**
     * 自动生成对象
     */
    public static final int AUTO_INS_YES = 1;

    /*************************************** 通断性报警相关 ***************************************/
    /**
     * 通断性报警属性ID
     */
    public static final int ON_OFF_ATTRIB_ID = 40001;
    /**
     * 通断性报警规则ID
     */
    public static final int ON_OFF_ALARM_ID = 1;
    /**
     * 通断性报警级别ID
     */
    public static final int ON_OFF_ALARM_LEVEL_ID = 1;
    /*************************************** 通断性报警相关 ***************************************/
    /**
     * 数据采集失败报警的报警ID
     */
    public static final int COLL_MISS_ALARM_ID = 10;
    /**
     * 数据采集失败报警的报警级别ID
     */
    public static final int COLL_MISS_ALARM_LEVEL_ID = 10;

    /*************************************** MIB相关 ***************************************/
    /**
     * 默认MIB库名称
     */
    public static final String DEFAULT_MIB_NAME = "SNMP_MIB_DEFAULT";
    /**
     * 默认MIB库ID
     */
    public static final int DEFAULT_MIB_NAME_ID = 1;

    /*************************************** 日志相关 ***************************************/
    /**
     * USER_ID
     */
    public static final int LOG_USER_ID = 0;
    /**
     * USER_NAME
     */
    public static final String LOG_USER_NAME = "SYSTEM";

    /*************************************** 自动发现相关 ***************************************/
    /**
     * 未知类型
     */
    public static final int CLASS_ID_UNKNOWN = 99999;
    /**
     * Catv码流默认ID
     */
    public static final int CATV_CLASS_ID_TS = 7;
    /**
     * Catv频道默认ID
     */
    public static final int CATV_CLASS_ID_PGM = 25;

    /*************************************** 事件处理相关 ***************************************/
    /**
     * 性能报警事件处理类
     */
    public static final String EVENT_HANDLE_CLASS_PERF = "jetsennet.jbmp.alarm.eventhandle.PerformanceAlarmEventHandle";
}
