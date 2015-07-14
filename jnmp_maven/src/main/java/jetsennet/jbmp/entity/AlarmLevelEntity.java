package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 报警级数
 */
@Table(name = "BMP_ALARMLEVEL")
public class AlarmLevelEntity
{

    /**
     * ID
     */
    @Id
    @Column(name = "LEVEL_ID")
    private int levelId;
    /**
     * 报警规则ID
     */
    @Column(name = "ALARM_ID")
    private int alarmId;
    /**
     * 报警级数ID
     */
    @Column(name = "ALARM_LEVEL")
    private int alarmLevel;
    /**
     * 子级别
     */
    @Column(name = "SUB_LEVEL")
    private int subLevel;
    /**
     * 变量名称
     */
    @Column(name = "VAR_NAME")
    private String varName;
    /**
     * 报警条件 LT：小于 LE：小于等于 EQ：等于 GT：大于 GE：大于等于
     */
    @Column(name = "CONDITION")
    private String condition;
    /**
     * 报警级数阀值
     */
    @Column(name = "THRESHOLD")
    private String threshold;
    /**
     * 报警级数名称
     */
    @Column(name = "LEVEL_NAME")
    private String levelName;
    /**
     * 报警级数描述
     */
    @Column(name = "LEVEL_DESC")
    private String levelDesc;
    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    /**
     * 星期掩码
     */
    @Column(name = "WEEK_MASK")
    private String weekMask;
    /**
     * 时间掩码
     */
    @Column(name = "HOUR_MASK")
    private String hourMask;
    /**
     * 处理动作
     */
    private int actionId;
    /**
     * 严重告警
     */
    public static final int ALARM_LEVEL_SERIOUS = 40;
    /**
     * 重要告警
     */
    public static final int ALARM_LEVEL_IMP = 30;
    /**
     * 一般告警
     */
    public static final int ALARM_LEVEL_GEN = 20;
    /**
     * 警告告警
     */
    public static final int ALARM_LEVEL_WARN = 10;
    /**
     * 正常
     */
    public static final int ALARM_LEVEL_NORMAL = 0;

    /**
     * in 错误条件标记
     */
    public static final String CON_IN_ERR_FLAG = "0,0";

    /**
     *构造函数
     */
    public AlarmLevelEntity()
    {
    }

    /**
     * 比较报警等级高低
     * @param level 参数
     * @return 1,one > two; 2,one < two; 3, one == two
     */
    public int compare(AlarmLevelEntity level)
    {
        return compare(this, level);
    }

    /**
     * 比较报警等级高低
     * @param one 参数1
     * @param two 参数2
     * @return 1,one > two; 2,one < two; 3, one == two
     */
    public static int compare(AlarmLevelEntity one, AlarmLevelEntity two)
    {
        if (one == null || two == null)
        {
            throw new NullPointerException();
        }
        if (one.getAlarmLevel() > two.getAlarmLevel())
        {
            return 1;
        }
        else if (one.getAlarmLevel() < two.getAlarmLevel())
        {
            return 2;
        }
        else
        {
            if (one.getSubLevel() > two.getSubLevel())
            {
                return 1;
            }
            else if (one.getSubLevel() < two.getSubLevel())
            {
                return 2;
            }
            else
            {
                return 3;
            }
        }
    }

    /**
     * @return the levelId
     */
    public int getLevelId()
    {
        return levelId;
    }

    /**
     * @param levelId the levelId to set
     */
    public void setLevelId(int levelId)
    {
        this.levelId = levelId;
    }

    /**
     * @return the alarmId
     */
    public int getAlarmId()
    {
        return alarmId;
    }

    /**
     * @param alarmId the alarmId to set
     */
    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }

    /**
     * @return the alarmLevel
     */
    public int getAlarmLevel()
    {
        return alarmLevel;
    }

    /**
     * @param alarmLevel the alarmLevel to set
     */
    public void setAlarmLevel(int alarmLevel)
    {
        this.alarmLevel = alarmLevel;
    }

    /**
     * @return the condition
     */
    public String getCondition()
    {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    /**
     * @return the threshold
     */
    public String getThreshold()
    {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(String threshold)
    {
        this.threshold = threshold;
    }

    /**
     * @return the levelName
     */
    public String getLevelName()
    {
        return levelName;
    }

    /**
     * @param levelName the levelName to set
     */
    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    /**
     * @return the levelDesc
     */
    public String getLevelDesc()
    {
        return levelDesc;
    }

    /**
     * @param levelDesc the levelDesc to set
     */
    public void setLevelDesc(String levelDesc)
    {
        this.levelDesc = levelDesc;
    }

    /**
     * @return the actionId
     */
    public int getActionId()
    {
        return actionId;
    }

    /**
     * @param actionId the actionId to set
     */
    public void setActionId(int actionId)
    {
        this.actionId = actionId;
    }

    /**
     * @return the subLevel
     */
    public int getSubLevel()
    {
        return subLevel;
    }

    /**
     * @param subLevel the subLevel to set
     */
    public void setSubLevel(int subLevel)
    {
        this.subLevel = subLevel;
    }

    /**
     * @return the varName
     */
    public String getVarName()
    {
        return varName;
    }

    /**
     * @param varName the varName to set
     */
    public void setVarName(String varName)
    {
        this.varName = varName;
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    /**
     * @return the weekMask
     */
    public String getWeekMask()
    {
        return weekMask;
    }

    /**
     * @param weekMask the weekMask to set
     */
    public void setWeekMask(String weekMask)
    {
        this.weekMask = weekMask;
    }

    /**
     * @return the hourMask
     */
    public String getHourMask()
    {
        return hourMask;
    }

    /**
     * @param hourMask the hourMask to set
     */
    public void setHourMask(String hourMask)
    {
        this.hourMask = hourMask;
    }
}
