package jetsennet.jbmp.entity;

import java.util.concurrent.TimeUnit;

import org.dom4j.Element;

import jetsennet.jbmp.business.AlarmPubSub;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 告警信息，显示在拓扑的告警列表中
 * @author lianghongjie
 */
@Table(name = "ALARM_INFO")
public class AlarmInfoEntity
{
    private static String RES_FIELDS =
        "SELECT A.ALARMEVT_ID,A.OBJATTR_ID,B.OBJATTR_NAME,A.OBJ_ID,C.CLASS_GROUP,C.OBJ_NAME"
            + ",A.COLL_TIME,A.COLL_VALUE,A.RESUME_TIME,A.EVENT_DURATION,A.ALARM_LEVEL"
            + ",A.ALARM_TYPE,A.ALARM_DESC,A.LEVEL_NAME,A.EVENT_STATE,A.EVENT_DESC,A.EVENT_TYPE,A.ALARM_COUNT FROM BMP_ALARMEVENT"
            + " A LEFT JOIN BMP_OBJATTRIB B ON A.OBJATTR_ID=B.OBJATTR_ID LEFT JOIN BMP_OBJECT C ON A.OBJ_ID=C.OBJ_ID";
    private static DefaultDal<AlarmInfoEntity> dal = ClassWrapper.wrapTrans(DefaultDal.class, AlarmInfoEntity.class);
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
     * 对象属性名称
     */
    @Column(name = "OBJATTR_NAME")
    private String objAttrName = "";
    /**
     * 对象ID
     */
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 对象名称
     */
    @Column(name = "OBJ_NAME")
    private String objName = "";
    /**
     * 对象分类(设备、码流、节目)
     */
    @Column(name = "CLASS_GROUP")
    private int classGroup;
    /**
     * 采集时间
     */
    @Column(name = "COLL_TIME")
    private long collTime;
    /**
     * 采集值
     */
    @Column(name = "COLL_VALUE")
    private String collValue = "";
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
     * 告警级别
     */
    @Column(name = "ALARM_LEVEL")
    private int alarmLevel;
    /**
     * 告警类型
     */
    @Column(name = "ALARM_TYPE")
    private int alarmType;
    /**
     * 告警描述
     */
    @Column(name = "ALARM_DESC")
    private String alarmDesc = "";
    /**
     * 级别名称
     */
    @Column(name = "LEVEL_NAME")
    private String levelName = "";
    /**
     * 事件状态
     */
    @Column(name = "EVENT_STATE")
    private int eventState;
    /**
     * 事件描述
     */
    @Column(name = "EVENT_DESC")
    private String eventDesc = "";
    /**
     * 事件类型
     */
    @Column(name = "EVENT_TYPE")
    private int eventType;
    /**
     * 报警累计
     */
    @Column(name = "ALARM_COUNT")
    private int alarmCount;

    /**
     * 构造函数
     */
    public AlarmInfoEntity()
    {
    }

    /**
     * @return 结果
     */
    public static String getInitSql()
    {
        long lastTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(AlarmPubSub.REAL_ALARM_KEEP_HOURS);
        return RES_FIELDS + " WHERE A.COLL_TIME>" + lastTime + " ORDER BY A.ALARMEVT_ID ASC";
    }

    /**
     * @param alarmId 告警
     * @return 结果
     * @throws Exception 异常
     */
    public static AlarmInfoEntity createInstance(int alarmId) throws Exception
    {
        return dal.get(RES_FIELDS + " WHERE A.ALARMEVT_ID=" + alarmId);
    }

    /**
     * @param elm 参数
     */
    public void toXml(Element elm)
    {
        elm.addAttribute("ALARMEVT_ID", String.valueOf(alarmEvtId));
        elm.addAttribute("OBJATTR_ID", String.valueOf(objAttrId));
        elm.addAttribute("OBJATTR_NAME", String.valueOf(objAttrName));
        elm.addAttribute("OBJ_ID", String.valueOf(objId));
        elm.addAttribute("OBJ_NAME", String.valueOf(objName));
        elm.addAttribute("CLASS_GROUP", String.valueOf(classGroup));
        elm.addAttribute("COLL_TIME", String.valueOf(collTime));
        elm.addAttribute("COLL_VALUE", String.valueOf(collValue));
        elm.addAttribute("RESUME_TIME", String.valueOf(resumeTime));
        elm.addAttribute("EVENT_DURATION", String.valueOf(eventDuration));
        elm.addAttribute("ALARM_LEVEL", String.valueOf(alarmLevel));
        elm.addAttribute("ALARM_TYPE", String.valueOf(alarmType));
        elm.addAttribute("ALARM_DESC", String.valueOf(alarmDesc));
        elm.addAttribute("LEVEL_NAME", String.valueOf(levelName));
        elm.addAttribute("EVENT_STATE", String.valueOf(eventState));
        elm.addAttribute("EVENT_DESC", String.valueOf(eventDesc));
        elm.addAttribute("EVENT_TYPE", String.valueOf(eventType));
        elm.addAttribute("ALARM_COUNT", String.valueOf(alarmCount));
    }

    /**
     * @param alarm 参数
     */
    public void update(AlarmEventEntity alarm)
    {
        // 增加判断防止前台确认跟后台的恢复更新冲突
        if (this.eventState < alarm.getEventState())
        {
            this.eventState = alarm.getEventState();
        }
        if (this.resumeTime < alarm.getResumeTime())
        {
            this.resumeTime = alarm.getResumeTime();
        }
        if (this.eventDuration < alarm.getEventDuration())
        {
            this.eventDuration = alarm.getEventDuration();
        }
    }

    public int getAlarmEvtId()
    {
        return alarmEvtId;
    }

    public void setAlarmEvtId(int alarmEvtId)
    {
        this.alarmEvtId = alarmEvtId;
    }

    public int getObjId()
    {
        return objId;
    }

    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    public int getObjAttrId()
    {
        return objAttrId;
    }

    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
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

    public int getAlarmLevel()
    {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel)
    {
        this.alarmLevel = alarmLevel;
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + alarmEvtId;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AlarmInfoEntity other = (AlarmInfoEntity) obj;
        if (alarmEvtId != other.alarmEvtId)
        {
            return false;
        }
        return true;
    }

    public String getObjAttrName()
    {
        return objAttrName;
    }

    public void setObjAttrName(String objAttrName)
    {
        this.objAttrName = objAttrName;
    }

    public String getObjName()
    {
        return objName;
    }

    public void setObjName(String objName)
    {
        this.objName = objName;
    }

    /**
     * @return the classGroup
     */
    public int getClassGroup()
    {
        return classGroup;
    }

    /**
     * @param classGroup the classGroup to set
     */
    public void setClassGroup(int classGroup)
    {
        this.classGroup = classGroup;
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
