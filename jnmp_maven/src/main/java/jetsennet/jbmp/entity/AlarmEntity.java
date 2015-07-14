/************************************************************************
日 期: 2011-12-30
作 者: 郭祥
版 本: v1.3
描 述: 报警规则
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 报警规则
 * @author 郭祥
 */
@Table(name = "BMP_ALARM")
public class AlarmEntity implements Cloneable
{

    /**
     * 报警规则ID
     */
    @Id
    @Column(name = "ALARM_ID")
    private int alarmId;
    /**
     * 报警规则名称
     */
    @Column(name = "ALARM_NAME")
    private String alarmName;
    /**
     * 报警规则类型 0，一般告警；1000-2000，广电报警；1000，信道指标报警； 1010，TS流报警；1020，异态报警（内容报警）
     */
    @Column(name = "ALARM_TYPE")
    private int alarmType;
    /**
     * 报警描述
     */
    @Column(name = "ALARM_DESC")
    private String alarmDesc;
    /**
     * 时间间隔
     */
    @Column(name = "CHECK_SPAN")
    private int checkSpan;
    /**
     * 检查次数
     */
    @Column(name = "CHECK_NUM")
    private int checkNum;
    /**
     * 超过阀值次数
     */
    @Column(name = "OVER_NUM")
    private int overNum;
    /**
     * 创建用户
     */
    @Column(name = "CREATE_USER")
    private String createUser;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;
    /**
     * JNMP_SC 中用来存储规则类型
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 规则是否生效。0，生效；1，无效
     */
    @Column(name = "IS_VALID")
    private int isValid;
    private List<AlarmLevelEntity> levels;
    /**
     * 规则类型，一般
     */
    public static final String RULE_TYPE_DEF = "0";
    /**
     * 规则类型，Trap
     */
    public static final String RULE_TYPE_TRAP = "1";
    /**
     * 规则生效
     */
    public static final int IS_VALID_TRUE = 0;
    /**
     * 规则不生效
     */
    public static final int IS_VALID_FALSE = 1;

    /**
     * 构造函数
     */
    public AlarmEntity()
    {
        this.isValid = IS_VALID_TRUE;
        this.checkSpan = 0;
        this.overNum = 1;
        this.checkNum = 1;
    }

    /**
     * 构造一个不生效的报警规则
     * 
     * @param name 规则名称
     * @return
     */
    public static AlarmEntity newUnValidAlarm(String name)
    {
        AlarmEntity alarm = new AlarmEntity();
        alarm.setAlarmName(name);
        alarm.setIsValid(IS_VALID_FALSE);
        alarm.setLevels(null);
        alarm.setCreateUser("管理员");
        return alarm;
    }

    /**
     * @param level 等级
     */
    public void addLevel(AlarmLevelEntity level)
    {
        if (levels == null)
        {
            levels = new ArrayList<AlarmLevelEntity>();
        }
        levels.add(level);
    }

    /**
     * @return 复制
     */
    public AlarmEntity copy()
    {
        AlarmEntity retval = null;
        try
        {
            retval = (AlarmEntity) this.clone();
        }
        catch (Exception ex)
        {
            throw new UnsupportedOperationException(ex);
        }
        return retval;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("规则名称：").append(this.alarmName);
        sb.append("；时间间隔：").append(this.checkSpan);
        sb.append("；检查次数：").append(this.checkNum);
        sb.append("；越限次数：").append(this.overNum);
        sb.append("；是否生效：").append(this.isValid == IS_VALID_FALSE ? "false" : "true");
        return sb.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
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
     * @return the alarmName
     */
    public String getAlarmName()
    {
        return alarmName;
    }

    /**
     * @param alarmName the alarmName to set
     */
    public void setAlarmName(String alarmName)
    {
        this.alarmName = alarmName;
    }

    /**
     * @return the alarmType
     */
    public int getAlarmType()
    {
        return alarmType;
    }

    /**
     * @param alarmType the alarmType to set
     */
    public void setAlarmType(int alarmType)
    {
        this.alarmType = alarmType;
    }

    /**
     * @return the alarmDesc
     */
    public String getAlarmDesc()
    {
        return alarmDesc;
    }

    /**
     * @param alarmDesc the alarmDesc to set
     */
    public void setAlarmDesc(String alarmDesc)
    {
        this.alarmDesc = alarmDesc;
    }

    /**
     * @return the checkNum
     */
    public int getCheckNum()
    {
        return checkNum;
    }

    /**
     * @param checkNum the checkNum to set
     */
    public void setCheckNum(int checkNum)
    {
        this.checkNum = checkNum;
    }

    /**
     * @return the overNum
     */
    public int getOverNum()
    {
        return overNum;
    }

    /**
     * @param overNum the overNum to set
     */
    public void setOverNum(int overNum)
    {
        this.overNum = overNum;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /**
     * @return the createUser
     */
    public String getCreateUser()
    {
        return createUser;
    }

    /**
     * @param createUser the createUser to set
     */
    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the levels
     */
    public List<AlarmLevelEntity> getLevels()
    {
        return levels;
    }

    /**
     * @param levels the levels to set
     */
    public void setLevels(List<AlarmLevelEntity> levels)
    {
        this.levels = levels;
    }

    /**
     * @return the checkSpan
     */
    public int getCheckSpan()
    {
        return checkSpan;
    }

    /**
     * @param checkSpan the checkSpan to set
     */
    public void setCheckSpan(int checkSpan)
    {
        this.checkSpan = checkSpan;
    }

    public int getIsValid()
    {
        return isValid;
    }

    public void setIsValid(int isValid)
    {
        this.isValid = isValid;
    }
}
