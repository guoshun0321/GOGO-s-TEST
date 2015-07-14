/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 报警事件类
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.AlarmConfig;
import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * 报警事件类 注意：可序列化
 */
@Table(name = "BMP_ALARMEVENT")
public class AlarmEventEntity implements Cloneable, Serializable
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
    /**
     * 报警发送状态
     */
    private int alarmSend;
    /**
     * 未确认
     */
    public static final int EVENT_STATE_NOTACK = 0;
    /**
     * 已确认
     */
    public static final int EVENT_STATE_ACK = 1;
    /**
     * 已清除
     */
    public static final int EVENT_STATE_CLEAR = 2;
    /**
     * 已处理
     */
    public static final int EVENT_STATE_HANDLE = 3;
    /**
     * 普通事件
     */
    public static final int EVENT_TYPE_NORMAL = 0;
    /**
     * 异常事件
     */
    public static final int EVENT_TYPE_EXCEPTION = 1;
    /**
     * TRAP事件
     */
    public static final int EVENT_TYPE_TRAP = 2;
    /**
     * 默认的源类型
     */
    public static final int DEFAULT_SOURCE_ID = -1;
    /**
     * 新报警
     */
    public static final int ALARM_SEND_NEW = 0;
    /**
     * 报警更新
     */
    public static final int ALARM_SEND_UPDATE = 1;
    /**
     * 报警恢复
     */
    public static final int ALARM_SEND_RESUME = 2;
    /**
     * 对象状态通知
     */
    public static final int OBJ_COLL_STATE = 3;
    /**
     * 报警次数
     */
    public static final int ALARM_SEND_UPDATE_COUNT = 4;
    /**
     * 报警是否恢复
     */
    private boolean isResume = false;
    /**
     * 配置信息
     */
    private transient AlarmConfig config;
    /**
     * 报警动作集合
     */
    public ArrayList<Integer> actionIds;

    static final long serialVersionUID = -1L;
    private static final Logger logger = Logger.getLogger(AlarmEventEntity.class);

    /**
     * 构造函数
     */
    public AlarmEventEntity()
    {
        this.alarmCount = 1;
    }

    /**
     * 构造函数
     * @param objAttrId 对象属性id
     * @param objId 对象id
     * @param attribId 属性id
     * @param collTime 采集时间
     * @param resumeTime 恢复时间
     * @param collValue 值
     * @param eventDesc 参数
     * @param levelName 参数
     * @param sourceId 参数
     */
    public AlarmEventEntity(int objAttrId, int objId, int attribId, long collTime, long resumeTime, String collValue, String eventDesc,
            String levelName, int sourceId)
    {
        this.objAttrId = objAttrId;
        this.objId = objId;
        this.attribId = attribId;
        this.collTime = collTime;
        this.resumeTime = resumeTime;
        this.collValue = collValue;
        this.eventDesc = eventDesc;
        this.levelName = levelName;
        this.eventDuration = 0;
        this.eventState = 0;
        this.sourceId = sourceId;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * @return 复制
     */
    public AlarmEventEntity copy()
    {
        AlarmEventEntity result = null;
        try
        {
            result = (AlarmEventEntity) this.clone();
        }
        catch (Exception ex)
        {
            result = null;
            logger.error("", ex);
        }
        return result;
    }

    /**
     * 报警恢复
     * @return 结果
     */
    public boolean resume()
    {
        if (collTime != 0 && resumeTime != 0)
        {
            this.eventDuration = (int) (this.resumeTime - this.collTime);
            if (this.eventDuration < 1000)//少于1秒置为1秒
            {
                this.eventDuration = 1000;
            }
            this.isResume = true;
            return true;
        }
        else
        {
            this.eventDuration = 0;
            this.isResume = false;
            return false;
        }
    }

    public boolean resume(long resumeTime)
    {
        this.resumeTime = resumeTime;
        return this.resume();
    }

    /**
     * 报警是否恢复
     * @return 结果
     */
    public boolean isResume()
    {
        if (resumeTime != 0)
        {
            return true;
        }
        return false;
    }

    /**
     * @param actionId 参数
     */
    public void setActionId(Integer actionId)
    {
        if (actionIds == null)
        {
            actionIds = new ArrayList<Integer>();
        }
        this.setActionId(actionId);
    }

    public void setActionIds(ArrayList<Integer> actionIds)
    {
        this.actionIds = actionIds;
    }

    public ArrayList<Integer> getActionIds()
    {
        return actionIds;
    }

    /**
     * @param alarm 告警
     * @param level 等级
     */
    public void setLevel(AlarmEntity alarm, AlarmLevelEntity level)
    {
        this.levelId = level.getLevelId();
        this.alarmId = alarm.getAlarmId();
        this.alarmLevel = level.getAlarmLevel();
        this.subLevel = level.getSubLevel();
        this.alarmType = alarm.getAlarmType();
    }

    /**
     * @return the alarmEvtId
     */
    public int getAlarmEvtId()
    {
        return alarmEvtId;
    }

    /**
     * @param alarmEvtId the alarmEvtId to set
     */
    public void setAlarmEvtId(int alarmEvtId)
    {
        this.alarmEvtId = alarmEvtId;
    }

    /**
     * @return the objAttrId
     */
    public int getObjAttrId()
    {
        return objAttrId;
    }

    /**
     * @param objAttrId the objAttrId to set
     */
    public void setObjAttrId(int objAttrId)
    {
        this.objAttrId = objAttrId;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {
        this.objId = objId;
    }

    /**
     * @return the attribId
     */
    public int getAttribId()
    {
        return attribId;
    }

    /**
     * @param attribId the attribId to set
     */
    public void setAttribId(int attribId)
    {
        this.attribId = attribId;
    }

    /**
     * @return the sourceId
     */
    public int getSourceId()
    {
        return sourceId;
    }

    /**
     * @param sourceId the sourceId to set
     */
    public void setSourceId(int sourceId)
    {
        this.sourceId = sourceId;
    }

    /**
     * @return the sourceType
     */
    public int getSourceType()
    {
        return sourceType;
    }

    /**
     * @param sourceType the sourceType to set
     */
    public void setSourceType(int sourceType)
    {
        this.sourceType = sourceType;
    }

    /**
     * @return the collTime
     */
    public long getCollTime()
    {
        return collTime;
    }

    /**
     * @param collTime the collTime to set
     */
    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    /**
     * @return the collValue
     */
    public String getCollValue()
    {
        return collValue;
    }

    /**
     * @param collValue the collValue to set
     */
    public void setCollValue(String collValue)
    {
        this.collValue = collValue;
    }

    /**
     * @return the resumeTime
     */
    public long getResumeTime()
    {
        return resumeTime;
    }

    /**
     * @param resumeTime the resumeTime to set
     */
    public void setResumeTime(long resumeTime)
    {
        this.resumeTime = resumeTime;
    }

    /**
     * @return the eventDuration
     */
    public int getEventDuration()
    {
        return eventDuration;
    }

    /**
     * @param eventDuration the eventDuration to set
     */
    public void setEventDuration(int eventDuration)
    {
        this.eventDuration = eventDuration;
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
     * @return the eventState
     */
    public int getEventState()
    {
        return eventState;
    }

    /**
     * @param eventState the eventState to set
     */
    public void setEventState(int eventState)
    {
        this.eventState = eventState;
    }

    /**
     * @return the eventDesc
     */
    public String getEventDesc()
    {
        return eventDesc;
    }

    /**
     * @param eventDesc the eventDesc to set
     */
    public void setEventDesc(String eventDesc)
    {
        this.eventDesc = eventDesc;
    }

    /**
     * @return the eventType
     */
    public int getEventType()
    {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(int eventType)
    {
        this.eventType = eventType;
    }

    /**
     * @return the eventLevel
     */
    public String getEventLevel()
    {
        return eventLevel;
    }

    /**
     * @param eventLevel the eventLevel to set
     */
    public void setEventLevel(String eventLevel)
    {
        this.eventLevel = eventLevel;
    }

    /**
     * @return the eventReason
     */
    public String getEventReason()
    {
        return eventReason;
    }

    /**
     * @param eventReason the eventReason to set
     */
    public void setEventReason(String eventReason)
    {
        this.eventReason = eventReason;
    }

    /**
     * @return the eventCheck
     */
    public String getEventCheck()
    {
        return eventCheck;
    }

    /**
     * @param eventCheck the eventCheck to set
     */
    public void setEventCheck(String eventCheck)
    {
        this.eventCheck = eventCheck;
    }

    /**
     * @return the checkUser
     */
    public String getCheckUser()
    {
        return checkUser;
    }

    /**
     * @param checkUser the checkUser to set
     */
    public void setCheckUser(String checkUser)
    {
        this.checkUser = checkUser;
    }

    /**
     * @return the checkUserId
     */
    public int getCheckUserId()
    {
        return checkUserId;
    }

    /**
     * @param checkUserId the checkUserId to set
     */
    public void setCheckUserId(int checkUserId)
    {
        this.checkUserId = checkUserId;
    }

    /**
     * @return the checkDesc
     */
    public String getCheckDesc()
    {
        return checkDesc;
    }

    /**
     * @param checkDesc the checkDesc to set
     */
    public void setCheckDesc(String checkDesc)
    {
        this.checkDesc = checkDesc;
    }

    /**
     * @return the checkTime
     */
    public Date getCheckTime()
    {
        return checkTime;
    }

    /**
     * @param checkTime the checkTime to set
     */
    public void setCheckTime(Date checkTime)
    {
        this.checkTime = checkTime;
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
     * @return the isResume
     */
    public boolean isIsResume()
    {
        return isResume;
    }

    /**
     * @param isResume the isResume to set
     */
    public void setIsResume(boolean isResume)
    {
        this.isResume = isResume;
    }

    /**
     * @return the config
     */
    public AlarmConfig getConfig()
    {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(AlarmConfig config)
    {
        this.config = config;
    }

    public int getAlarmCount()
    {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount)
    {
        this.alarmCount = alarmCount;
    }

    public int getAlarmSend()
    {
        return alarmSend;
    }

    public void setAlarmSend(int alarmSend)
    {
        this.alarmSend = alarmSend;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (this.alarmEvtId < 0)
        {
            sb.append("对象采集状态通知。对象ID：").append(this.objId);
            sb.append("，对象采集状态：").append(this.alarmDesc);
        }
        else
        {
            sb.append("ID:").append(alarmEvtId).append(",对象ID:").append(objId);
            sb.append(",对象属性ID:").append(objAttrId).append(",采集时间:").append(collTime);
            sb.append(",采集值:").append(collValue).append(",恢复时间：").append(resumeTime).append(",级别ID:").append(levelId);
            sb.append(",报警级别:").append(alarmLevel).append(",级别名称:").append(levelName);
            sb.append(",报警ID:").append(alarmId).append(",报警类型:").append(alarmType).append(",报警描述:").append(alarmDesc);
        }
        return sb.toString();
    }

    /**
     * @param time 时间
     * @return 结果
     */
    public static String getQuerySql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName("BMP_ALARMEVENTLOG");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", Long.toString(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.Numeric) };
        cmd.setFilter(conds);
        cmd.setOrderString("ORDER BY ALARMEVT_ID");
        return cmd.toString();
    }

    /**
     * @param time 时间
     * @return 结果
     */
    public static String getDelSql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.DeleteCommand);
        cmd.setTableName("BMP_ALARMEVENTLOG");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", Long.toString(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.Numeric) };
        cmd.setFilter(conds);
        return cmd.toString();
    }

    public static void main(String[] args) throws Exception
    {
        DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class);
        dal.get(AlarmEventEntity.getQuerySql(1111l));
        dal.delete(AlarmEventEntity.getDelSql(1111l));
    }
}
