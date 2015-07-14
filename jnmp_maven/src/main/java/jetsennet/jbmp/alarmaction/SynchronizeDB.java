/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.sqlclient.DataRecordInfo;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author xdh
 */
public class SynchronizeDB
{
    private static final Logger logger = Logger.getLogger(ActionUtil.class);
    private List<AlarmEventEntity> alarmEvents;

    /**
     * 构造函数
     */
    public SynchronizeDB()
    {
        alarmEvents = new ArrayList<AlarmEventEntity>();
    }

    /**
     * 同步数据库
     * @param 参数
     * @throws Exception 异常
     */
    public void syncDBAction() throws Exception
    {
        try
        {
            // 同步数据库
            try
            {
                getAlarm();
                ActionUtil.processAlarm(alarmEvents);
            }
            catch (Exception e)
            {
                logger.error("同步数据库失败", e);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取报警
     * @throws Exception
     */
    private void getAlarm() throws Exception
    {
        ISqlExecutor sqlExecutor = SqlExecutorFacotry.getSqlExecutor();
        try
        {
            alarmEvents.clear();
            final List<DataRecordInfo> alarmActions =
                sqlExecutor.load(DataRecordInfo.class, sqlExecutor.getSqlParser().getSelectCommandString("BMP_ACTIONSTATE", 0, true,
                    "ALARMEVT_ID,ACTION_ID,ACTION_STATE", null, null,
                    new SqlCondition("ACTION_STATE", String.valueOf(2), SqlLogicType.And, SqlRelationType.LessEqual, SqlParamType.Numeric)));
            if (alarmActions == null || alarmActions.size() == 0)
            {
                return;
            }

            //IN (...) 在oracle中数据列表超过1000会报错，这里把过长的分成几份用OR连接。
            String[] alarmEvtIds = this.getAlarmEvtIds(alarmActions, 999);
            String condition = "";
            for(String ids : alarmEvtIds){
            	condition += " ALARMEVT_ID in (" + ids + ") OR ";
            }
            condition = condition.substring(0, condition.length() - 3);
            String cmd =
                "SELECT ALARMEVT_ID,COLL_TIME,RESUME_TIME,EVENT_DURATION,OBJ_ID,ATTRIB_ID FROM BMP_ALARMEVENT WHERE " + condition;
            DefaultDal.read(cmd, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rdr) throws Exception
                {
                    while (rdr.next())
                    {
                        AlarmEventEntity item = new AlarmEventEntity();
                        item.setAlarmEvtId(rdr.getInt("ALARMEVT_ID"));
                        item.setCollTime(rdr.getLong("COLL_TIME"));
                        item.setResumeTime(rdr.getLong("RESUME_TIME"));
                        item.setEventDuration(Integer.valueOf(rdr.getString("EVENT_DURATION")));
                        item.setObjId(Integer.valueOf(rdr.getString("OBJ_ID")));
                        item.setAttribId(Integer.valueOf(rdr.getString("ATTRIB_ID")));
                        item.actionIds = getActionIds(alarmActions, item);

                        if (item.actionIds != null && item.actionIds.size() > 0)
                        {
                            alarmEvents.add(item);
                        }
                    }
                }
            });
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            sqlExecutor.closeConnection();
        }
    }

    /**
     * 获得对应报警的动作ID
     * @param alarmActions 报警动作集合
     * @param alarm 报警
     * @return actionIds 报警的动作ID
     */
    private ArrayList<Integer> getActionIds(List<DataRecordInfo> alarmActions, AlarmEventEntity alarm)
    {
        ArrayList<Integer> actionIds = new ArrayList<Integer>();
        if (alarmActions == null || alarmActions.size() == 0)
        {
            return null;
        }

        for (DataRecordInfo item : alarmActions)
        {
            if (alarm.getAlarmEvtId() == Integer.valueOf(item.getField_1()))
            {
                // 恢复了的报警进行恢复动作 ，没恢复且没有动作过的报警进行开始动作
                if (alarm.getResumeTime() > 0 || (alarm.getResumeTime() == 0 && "0".equals(item.getField_3())))
                {
                    actionIds.add(Integer.parseInt(item.getField_2()));
                }
            }
        }

        return actionIds;
    }

    /**
     * 获得所有报警ID
     * 例：alarmActions中id = {1,2,3,4,5}、len = 2, 结果{{"1,2"},{"3,4"},{"5"}}. 
     * @param alarmActions 报警动作集合
     * @param len 一份ID字符串中ID个数限制
     * @return 所有报警ID字符串数组
     */
    private String[] getAlarmEvtIds(List<DataRecordInfo> alarmActions, int len)
    {
        if (alarmActions == null || alarmActions.size() == 0 || len <= 0)
        {
            return null;
        }
        int alarmActionsSize = alarmActions.size();
        String[] result = new String[alarmActionsSize / len + (alarmActionsSize % len == 0 ? 0 : 1) ];
        
        ArrayList<String> alarmIds = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int i = 0, j = 0;
        for (DataRecordInfo item : alarmActions)
        {
            if (!alarmIds.contains(item.getField_1()))
            {
            	if(i >= len){
            		result[j] = sb.toString().substring(0, sb.length() - 1);
            		j++;
            		sb = new StringBuilder();
            		i = 0;
            	}
                alarmIds.add(item.getField_1());

                sb.append(item.getField_1());
                sb.append(",");
                i++;
            }
        }
        if(j < result.length){
        	result[j] = sb.toString().substring(0, sb.length() - 1);
        }

        return result;
    }
}
