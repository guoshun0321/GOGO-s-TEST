package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

@Table(name = "BMP_ALARMEVENTLOG")
public class AlarmEventLogEntity
{

    /**
     * 告警事件ID
     */
    @Id
    @Column(name = "ALARMEVT_ID")
    private int alarmEvtId;
    /**
     * 对象属性ID
     */
    @Column(name = "OBJATTR_ID")
    private int objAttrId;
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 属性ID
     */
    @Column(name = "ATTRIB_ID")
    private int attribId;
    /**
     * 报警源
     */
    @Column(name = "SOURCE_ID")
    private int sourceId;
    /**
     * 源类型
     */
    @Column(name = "SOURCE_TYPE")
    private int sourceType;
    /**
     * 采集时间
     */
    @Column(name = "COLL_TIME")
    private long collTime;
    /**
     * 采集值
     */
    @Column(name = "COLL_VALUE")
    private String collValue;
    /**
     * 恢复时间
     */
    @Column(name = "RESUME_TIME")
    private long resumeTime;
    /**
     * 持续时间
     */
    @Column(name = "EVENT_DURATION")
    private int eventDuration;
    /**
     * 告警级别ID
     */
    @Column(name = "LEVEL_ID")
    private int levelId;
    /**
     * 告警ID
     */
    @Column(name = "ALARM_ID")
    private int alarmId;
    /**
     * 告警级别
     */
    @Column(name = "ALARM_LEVEL")
    private int alarmLevel;
    /**
     * 告警子级别
     */
    @Column(name = "SUB_LEVEL")
    private int subLevel;
    /**
     * 告警类型
     */
    @Column(name = "ALARM_TYPE")
    private int alarmType;
    /**
     * 告警描述
     */
    @Column(name = "ALARM_DESC")
    private String alarmDesc;
    /**
     * 级别名称
     */
    @Column(name = "LEVEL_NAME")
    private String levelName;
    /**
     * 事件状态
     */
    @Column(name = "EVENT_STATE")
    private int eventState;
    /**
     * 事件描述
     */
    @Column(name = "EVENT_DESC")
    private String eventDesc;
    /**
     * 事件类型
     */
    @Column(name = "EVENT_TYPE")
    private int eventType;
    /**
     * 事件级别
     */
    @Column(name = "EVENT_LEVEL")
    private String eventLevel;
    /**
     * 事件缘由
     */
    @Column(name = "EVENT_REASON")
    private String eventReason;
    /**
     * 事件确认
     */
    @Column(name = "EVENT_CHECK")
    private String eventCheck;
    /**
     * 处理用户
     */
    @Column(name = "CHECK_USER")
    private String checkUser;
    /**
     * 处理用户ID
     */
    @Column(name = "CHECK_USERID")
    private int checkUserId;
    /**
     * 处理意见
     */
    @Column(name = "CHECK_DESC")
    private String checkDesc;
    /**
     * 处理时间
     */
    @Column(name = "CHECK_TIME")
    private Date checkTime;
    /**
     * 报警累计
     */
    @Column(name = "ALARM_COUNT")
    private int alarmCount;
    
    public AlarmEventLogEntity()
    {
        // TODO Auto-generated constructor stub
    }
    
    public AlarmEventLogEntity(AlarmEventEntity alarm) {
        this.alarmEvtId = alarm.getAlarmEvtId();
        this.objAttrId = alarm.getObjAttrId();
        this.objId = alarm.getObjId();
        this.attribId = alarm.getAttribId();
        this.sourceId = alarm.getSourceId();
        this.sourceType = alarm.getSourceType();
        this.collTime = alarm.getCollTime();
        this.collValue = alarm.getCollValue();
        this.resumeTime = alarm.getResumeTime();
        this.eventDuration = alarm.getEventDuration();
        this.levelId = alarm.getLevelId();
        this.alarmId = alarm.getAlarmId();
        this.alarmLevel = alarm.getAlarmLevel();
        this.subLevel = alarm.getSubLevel();
        this.alarmType = alarm.getAlarmType();
        this.alarmDesc = alarm.getAlarmDesc();
        this.levelName = alarm.getLevelName();
        this.eventState = alarm.getEventState();
        this.eventDesc = alarm.getEventDesc();
        this.eventType = alarm.getEventType();
        this.eventLevel = alarm.getEventLevel();
        this.eventReason = alarm.getEventReason();
        this.eventCheck = alarm.getEventCheck();
        this.checkUser = alarm.getCheckUser();
        this.checkUserId = alarm.getCheckUserId();
        this.checkDesc = alarm.getCheckDesc();
        this.checkTime = alarm.getCheckTime();
        this.alarmCount = alarm.getAlarmCount();
    }

    public int getAlarmEvtId()
    {
        return alarmEvtId;
    }

    public void setAlarmEvtId(int alarmEvtId)
    {
        this.alarmEvtId = alarmEvtId;
    }

    public int getObjAttrId()
    {
        return objAttrId;
    }

    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getAttribId()
    {
        return attribId;
    }

    public void setAttribId(int attribId)
    {
        this.attribId = attribId;
    }

    public int getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(int sourceId)
    {
        this.sourceId = sourceId;
    }

    public int getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(int sourceType)
    {
        this.sourceType = sourceType;
    }

    public long getCollTime()
    {
        return collTime;
    }

    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    public String getCollValue()
    {
        return collValue;
    }

    public void setCollValue(String collValue)
    {
        this.collValue = collValue;
    }

    public long getResumeTime()
    {
        return resumeTime;
    }

    public void setResumeTime(long resumeTime)
    {
        this.resumeTime = resumeTime;
    }

    public int getEventDuration()
    {
        return eventDuration;
    }

    public void setEventDuration(int eventDuration)
    {
        this.eventDuration = eventDuration;
    }

    public int getLevelId()
    {
        return levelId;
    }

    public void setLevelId(int levelId)
    {
        this.levelId = levelId;
    }

    public int getAlarmId()
    {
        return alarmId;
    }

    public void setAlarmId(int alarmId)
    {
        this.alarmId = alarmId;
    }

    public int getAlarmLevel()
    {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel)
    {
        this.alarmLevel = alarmLevel;
    }

    public int getSubLevel()
    {
        return subLevel;
    }

    public void setSubLevel(int subLevel)
    {
        this.subLevel = subLevel;
    }

    public int getAlarmType()
    {
        return alarmType;
    }

    public void setAlarmType(int alarmType)
    {
        this.alarmType = alarmType;
    }

    public String getAlarmDesc()
    {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc)
    {
        this.alarmDesc = alarmDesc;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public int getEventState()
    {
        return eventState;
    }

    public void setEventState(int eventState)
    {
        this.eventState = eventState;
    }

    public String getEventDesc()
    {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc)
    {
        this.eventDesc = eventDesc;
    }

    public int getEventType()
    {
        return eventType;
    }

    public void setEventType(int eventType)
    {
        this.eventType = eventType;
    }

    public String getEventLevel()
    {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel)
    {
        this.eventLevel = eventLevel;
    }

    public String getEventReason()
    {
        return eventReason;
    }

    public void setEventReason(String eventReason)
    {
        this.eventReason = eventReason;
    }

    public String getEventCheck()
    {
        return eventCheck;
    }

    public void setEventCheck(String eventCheck)
    {
        this.eventCheck = eventCheck;
    }

    public String getCheckUser()
    {
        return checkUser;
    }

    public void setCheckUser(String checkUser)
    {
        this.checkUser = checkUser;
    }

    public int getCheckUserId()
    {
        return checkUserId;
    }

    public void setCheckUserId(int checkUserId)
    {
        this.checkUserId = checkUserId;
    }

    public String getCheckDesc()
    {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc)
    {
        this.checkDesc = checkDesc;
    }

    public Date getCheckTime()
    {
        return checkTime;
    }

    public void setCheckTime(Date checkTime)
    {
        this.checkTime = checkTime;
    }

    public int getAlarmCount()
    {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount)
    {
        this.alarmCount = alarmCount;
    }

}
