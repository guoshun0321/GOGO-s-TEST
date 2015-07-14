package jetsennet.jbmp.dataaccess;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.dataaccess.buffer.SysConfigBuffer;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AlarmEventLogEntity;
import jetsennet.jbmp.exception.UncheckedSQLException;
import jetsennet.jbmp.log.OperatorLog;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * 报警事件
 * @author Guo
 */
public class AlarmEventDal extends DefaultDal<AlarmEventEntity>
{

    private AlarmActionDal aadal;
    private ActionStateDal asdal;
    private static final Logger logger = Logger.getLogger(AlarmEventDal.class);

    /**
     * 构造方法
     */
    public AlarmEventDal()
    {
        super(AlarmEventEntity.class);
        aadal = ClassWrapper.wrapTrans(AlarmActionDal.class);
        asdal = ClassWrapper.wrapTrans(ActionStateDal.class);
    }

    /**
     * 报警恢复
     * @param objAttrId 对象属性ID
     * @param alarmId 报警ID
     * @param levelId 报警级别ID
     * @param time 时间
     * @throws Exception 异常
     */
    // @Transactional
    public void resumeEvent(int objAttrId, int alarmId, int levelId, Date time) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("RESUME_TIME", String.valueOf(time.getTime()));
        cmd.setFilter(new SqlCondition("OBJATTR_ID", Integer.toString(objAttrId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("ALARM_ID", Integer.toString(alarmId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("ALARM_LEVEL", Integer.toString(levelId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("RESUME_TIME", "0", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        update(cmd.toString());
    }

    /**
     * 报警恢复
     * @param eventId 参数
     * @param resumeTime 恢复时间
     * @param duration 参数
     * @throws Exception 异常
     */
    public void resumeEvent(int eventId, long resumeTime, int duration) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("RESUME_TIME", Long.toString(resumeTime));
        cmd.addField("EVENT_DURATION", duration);
        cmd.setFilter(new SqlCondition("ALARMEVT_ID", Integer.toString(eventId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        update(cmd.toString());
    }

    /**
     * 报警恢复
     * @param aee 参数
     * @throws Exception 异常
     */
    public void resumeEvent(AlarmEventEntity aee) throws Exception
    {
        resumeEvent(aee.getAlarmEvtId(), aee.getResumeTime(), aee.getEventDuration());
    }

    /**
     * 修改报警次数
     * @param alarmevtId 参数
     * @param count 此时
     * @throws Exception 异常
     */
    public void updateEventCount(int alarmevtId, int count) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("ALARM_COUNT", String.valueOf(count));
        cmd.setFilter(new SqlCondition("ALARMEVT_ID", Integer.toString(alarmevtId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        update(cmd.toString());
    }

    /**
     * 处理一组报警。 如果报警的ID这里只会插入报警事件不大于0事件。的注意报警插入后会修改对象的AlarmEvtId。
     * @param events 事件
     * @throws Exception 异常
     */
    @Transactional
    public void handleAlarmEventArrays(ArrayList<AlarmEventEntity> events) throws Exception
    {
        if (events == null || events.isEmpty())
        {
            return;
        }
        for (int i = 0; i < events.size(); i++)
        {
            this.handleAlarmEvent(events.get(i));
        }
    }

    /**
     * 处理单个报警。 这里只会插入报警事件不大于0事件。注意报警插入后会修改对象的AlarmEvtId。
     * @param event 事件
     */
    @Transactional
    public void handleAlarmEvent(AlarmEventEntity event)
    {
        try
        {
            if (event == null)
            {
                return;
            }
            int alarmEvtId = event.getAlarmEvtId();
            if (alarmEvtId <= 0)
            {
                insert(event);
            }
            else if (event.isResume())
            {
                resumeEvent(alarmEvtId, event.getResumeTime(), event.getEventDuration());
            }
            else
            {
                logger.debug("无法处理事件ID大于0，且报警未恢复的数据。");
                return;
            }
            // 设置actionIds
            int levelId = event.getLevelId();
            if (levelId > 0 && !event.isResume())
            {
                ArrayList<Integer> actionIds = aadal.getActionIdsByLevelId(event.getLevelId());
                event.setActionIds(actionIds);
                asdal.insert(event);
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedSQLException(ex);
        }
    }

    /**
     * 处理NCC报警事件
     * @param event 事件
     * @return 结果
     */
    @Transactional
    public AlarmEventEntity handleNccEvent(AlarmEventEntity event)
    {
        if (event == null)
        {
            return null;
        }
        AlarmEventEntity retval = null;
        try
        {
            if (event.getResumeTime() > 0)
            {
                SqlCondition[] conds =
                    new SqlCondition[] {
                        new SqlCondition("OBJ_ID", Integer.toString(event.getObjId()), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                        new SqlCondition("COLL_VALUE", event.getCollValue(), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String) };
                AlarmEventEntity tevent = this.get(conds);
                if (tevent != null)
                {
                    if (tevent.getCollTime() <= 0)
                    {
                        tevent.setCollTime(event.getCollTime());
                    }
                    else if (tevent.getResumeTime() <= 0)
                    {
                        tevent.setResumeTime(event.getResumeTime());
                    }
                    tevent.resume();
                    this.update(tevent);
                    retval = tevent;
                }
            }
            else
            {
                this.insert(event);
                retval = event;
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedSQLException(ex);
        }
        return retval;
    }

    /**
     * 处理报警恢复。如果未设置自动清除，直接更新对象；如果设置了自动清除，对象状态设置为“已清除”，并移到LOG表
     * @param event 事件
     */
    @Transactional
    public boolean handleAlarmEventResume(AlarmEventEntity iEvent)
    {
        boolean retval = false;
        if (iEvent == null || iEvent.getAlarmId() <= 0)
        {
            return retval;
        }
        int evtId = iEvent.getAlarmEvtId();

        try
        {
            AlarmEventEntity event = this.get(evtId);
            int isDel = this.delete(evtId);

            if (event != null && isDel > 0)
            {
                // 是否自动清除
                boolean isAutoDis = SysConfigBuffer.isAutoClean;
                if (isAutoDis)
                {
                    // 生成AlarmEventLogEntity
                    AlarmEventLogEntity eventLog = new AlarmEventLogEntity(event);
                    eventLog.setEventState(AlarmEventEntity.EVENT_STATE_CLEAR);
                    eventLog.setResumeTime(iEvent.getResumeTime());
                    eventLog.setEventDuration(iEvent.getEventDuration());
                    eventLog.setCheckUser(BMPConstants.LOG_USER_NAME);
                    eventLog.setCheckUserId(BMPConstants.LOG_USER_ID);
                    eventLog.setCheckTime(new Date());

                    // 将AlarmEventLogEntity插入数据库
                    AlarmEventLogDal aeldal = new AlarmEventLogDal();
                    aeldal.insert(eventLog, false);
                    OperatorLog.log(BMPConstants.LOG_USER_ID, BMPConstants.LOG_USER_NAME, String.format("清除ID为%s的报警事件(自动)", event.getAlarmEvtId()));

                    // 自动清除的报警，需要将传入报警的状态改为CLEAR
                    // 服务器端会根据报警的状态来处理报警恢复
                    iEvent.setEventState(AlarmEventEntity.EVENT_STATE_CLEAR);
                }
                else
                {
                    resumeEvent(event.getAlarmEvtId(), event.getResumeTime(), event.getEventDuration());
                }
                retval = true;
            }
        }
        catch (Exception ex)
        {
            throw new UncheckedSQLException(ex);
        }
        return retval;
    }

    /**
     * 获取报警事件，博汇
     * @param objId 对象id
     * @param sourceId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AlarmEventEntity getByObjIdAndSourceId(int objId, int sourceId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.setFilter(new SqlCondition[] {
            new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("SOURCE_ID", Integer.toString(sourceId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) });
        String sql = cmd.toString();
        return get(sql);
    }

    /**
     * 获取报警事件
     * @param objAttrId 对象属性id
     * @param sourceId 告警id
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AlarmEventEntity getByObjAttrIdAndSourceId(int objAttrId, String sourceId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.setFilter(new SqlCondition[] {
            new SqlCondition("OBJATTR_ID", Integer.toString(objAttrId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("SOURCE_ID", sourceId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric) });
        cmd.setOrderString("ORDER BY ALARMEVT_ID DESC");
        String sql = cmd.toString();
        return get(sql);
    }

    /**
     * 获取最后一条未恢复的报警
     * @param objAttrId 对象属性id
     * @return 结果
     */
    public AlarmEventEntity getLastAlarm(int objAttrId)
    {
        AlarmEventEntity retval = null;
        try
        {
            ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
            String sql =
                exec.getSqlParser().getSelectCommandString(tableInfo.tableName,
                    1,
                    false,
                    "*",
                    null,
                    "ORDER BY ALARMEVT_ID DESC",
                    new SqlCondition[] {
                        new SqlCondition("OBJATTR_ID", Integer.toString(objAttrId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
                        new SqlCondition("RESUME_TIME", "0", SqlLogicType.And, SqlRelationType.LessEqual, SqlParamType.Numeric) });
            retval = this.get(sql);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * @param collValue 值
     * @param objId 对象id
     * @param collTime 事件
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public AlarmEventEntity getAlarmEventJinShuXin(int collValue, int objId, long collTime) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.setFilter(new SqlCondition[] {
            new SqlCondition("COLL_VALUE", Integer.toString(collValue), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("COLL_TIME", Long.toString(collTime), SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric) });
        cmd.setOrderString("ORDER BY COLL_TIME DESC");
        return get(cmd.toString());
    }

    /**
     * @param collValue1 参数
     * @param collValue2 参数
     * @param objId 参数
     * @param sourceType 参数
     * @param collTime 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public ArrayList<AlarmEventEntity> getAlarmEventsJinShuXin(int collValue1, int collValue2, int objId, int sourceType, long collTime)
            throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.setFilter(new SqlCondition[] {
            new SqlCondition("OBJ_ID", Integer.toString(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("SOURCE_TYPE", Integer.toString(sourceType), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("COLL_TIME", Long.toString(collTime), SqlLogicType.AndAll, SqlRelationType.Than, SqlParamType.Numeric),
            new SqlCondition("COLL_VALUE", Long.toString(collValue1), SqlLogicType.Or, SqlRelationType.Than, SqlParamType.Numeric),
            new SqlCondition("COLL_VALUE", Long.toString(collValue2), SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric), });
        cmd.setOrderString("ORDER BY COLL_TIME DESC");
        return (ArrayList<AlarmEventEntity>) getLst(cmd.toString());
    }

    /**
     * 批量移除报警
     * 中国有线项目的要求，必须在停止其他数据库操作的前提下执行。否则可能导致未知后果。
     * 
     * @throws Exception 异常
     */
    @Transactional
    public void batchRemoveAlarm() throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        logger.info("开始更新状态。");
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.UpdateCommand);
        cmd.setTableName(tableInfo.tableName);
        cmd.addField("EVENT_STATE", Integer.toString(AlarmEventEntity.EVENT_STATE_CLEAR));
        SqlCondition cond = new SqlCondition("ALARMEVT_ID", "0", SqlLogicType.And, SqlRelationType.Than, SqlParamType.Numeric);
        cmd.setFilter(cond);
        update(cmd.toString());
        logger.info("开始迁移数据。");
        String moveSql =
            "INSERT INTO BMP_ALARMEVENTLOG "
                + "(ALARMEVT_ID,OBJATTR_ID,OBJ_ID,ATTRIB_ID,SOURCE_ID,SOURCE_TYPE,COLL_TIME,"
                + "COLL_VALUE,RESUME_TIME,EVENT_DURATION,LEVEL_ID,ALARM_ID,ALARM_LEVEL,SUB_LEVEL,"
                + "ALARM_TYPE,ALARM_DESC,LEVEL_NAME,EVENT_STATE,EVENT_DESC,EVENT_TYPE,EVENT_LEVEL,"
                + "EVENT_REASON,EVENT_CHECK,CHECK_USER,CHECK_USERID,CHECK_DESC,CHECK_TIME,ALARM_COUNT) "
                + "(SELECT ALARMEVT_ID,OBJATTR_ID,OBJ_ID,ATTRIB_ID,SOURCE_ID,SOURCE_TYPE,COLL_TIME,"
                + "COLL_VALUE,RESUME_TIME,EVENT_DURATION,LEVEL_ID,ALARM_ID,ALARM_LEVEL,SUB_LEVEL,ALARM_TYPE,"
                + "ALARM_DESC,LEVEL_NAME,EVENT_STATE,EVENT_DESC,EVENT_TYPE,EVENT_LEVEL,EVENT_REASON,"
                + "EVENT_CHECK,CHECK_USER,CHECK_USERID,CHECK_DESC,CHECK_TIME,ALARM_COUNT FROM BMP_ALARMEVENT)";
        delete(moveSql);
        logger.info("开始删除原始数据。");
        String delSQL = "DELETE FROM BMP_ALARMEVENT";
        delete(delSQL);
    }

    public int getRelAlarmNum(String cond) throws Exception
    {
        final int[] retval = new int[1];
        String sql = "SELECT COUNT(*) AS A FROM BMP_ALARMEVENT WHERE EVENT_STATE = 0 AND RESUME_TIME = 0 AND OBJATTR_ID IN (%s)";
        sql = String.format(sql, cond);
        read(sql, new IReadHandle()
        {

            @Override
            public void handle(ResultSet rs) throws Exception
            {
                if (rs.next())
                {
                    retval[0] = rs.getInt("A");
                }
            }

        });
        return retval[0];
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        AlarmEventDal aedal = ClassWrapper.wrapTrans(AlarmEventDal.class);
        aedal.getRelAlarmNum("29364");
    }
}
