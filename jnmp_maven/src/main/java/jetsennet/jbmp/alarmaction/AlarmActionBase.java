/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.log.OperatorLog;
import jetsennet.jbmp.protocols.jgroup.AbsGroupServer;
import jetsennet.jbmp.util.ConfigUtil;
import jetsennet.jbmp.util.TimeUtil;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DataRecordInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.FormatUtil;
import jetsennet.util.StringUtil;

/**
 * @author xdh
 */
public final class AlarmActionBase extends AbsGroupServer
{
    private static final Logger logger = Logger.getLogger(AlarmActionBase.class);
    private static AlarmActionBase instance = new AlarmActionBase();
    private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private HashMap<String, List<DataRecordInfo>> cacheActionData;
    private int orderId;

    private AlarmActionBase()
    {
        cacheActionData = new HashMap<String, List<DataRecordInfo>>();
        nmpConnectionInfo = new ConnectionInfo(DbConfig.getProperty("bmp_driver"),
                DbConfig.getProperty("bmp_dburl"),
                DbConfig.getProperty("bmp_dbuser"),
                DbConfig.getProperty("bmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
    }

    /**
     * 单例
     * @return 结果
     */
    public static AlarmActionBase getInstance()
    {
        return instance;
    }

    /**
     * 报警处理
     * @param alarm 参数
     */
    @Override
    public void handle(AlarmEventEntity alarm)
    {
        try
        {
            if (alarm == null)
            {
                throw new Exception("传入报警为空");
            }
            if (alarm.getAlarmSend() == AlarmEventEntity.ALARM_SEND_UPDATE)
            {
                return;
            }
            if (alarm.actionIds == null || alarm.actionIds.size() == 0)
            {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Integer item : alarm.actionIds)
            {
                sb.append(item.toString());
                sb.append(",");
            }
            String actionIds = sb.toString().substring(0, sb.length() - 1);

            // 报警动作数据
            List<DataRecordInfo> actionInfos =
                sqlExecutor.load(DataRecordInfo.class, sqlExecutor.getSqlParser().getSelectCommandString("BMP_ACTION",
                    "ACTION_TYPE,ASSIGN_TYPE,ASSIGN_OBJID,ACTION_ID,WEEK_MASK,HOUR_MASK", null,
                    new SqlCondition("ACTION_ID", actionIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.Numeric)));

            String[] objName = sqlExecutor.find("SELECT OBJ_NAME FROM BMP_OBJECT WHERE OBJ_ID=" + alarm.getObjId());
            String[] evtName = sqlExecutor.find("SELECT ATTRIB_NAME FROM BMP_ATTRIBUTE WHERE ATTRIB_ID=" + alarm.getAttribId());
            if (objName == null || evtName == null)
            {
                logger.warn("找不到报警对象名称或者对象属性名称");
                return;
            }

            // 当前日期和时间
            int week = TimeUtil.getWeek(new Date());
            int hour = TimeUtil.getHour(new Date());

            // 发送报警
            if (actionInfos != null && actionInfos.size() > 0)
            {
                for (DataRecordInfo actionInfo : actionInfos)
                {
                    int isAction = 1; // 0表示不执行，1表示执行

                    if (matchWeek(actionInfo.getField_5(), week) && matchHour(actionInfo.getField_6(), hour))
                    {
                        int actionType = Integer.parseInt(actionInfo.getField_1()); // 1：发邮件 10：发短信 20：生成工单
                        int assignType = Integer.parseInt(actionInfo.getField_2());
                        int objId = Integer.parseInt(actionInfo.getField_3());

                        // 获取用户邮箱或电话或ID
                        String[] userInfos = this.getUserInfo(actionType, assignType, objId);
                        int ret = 0; // 0表示发送失败，1表示发送成功

                        String msg = "";
                        if (alarm.getResumeTime() > 0)
                        {
                            msg =
                                objName[0] + " - " + evtName[0] + " - "
                                    + FormatUtil.formatDateString(new Date(alarm.getCollTime()), "yyyy-MM-dd HH:mm:ss") + " 至 "
                                    + FormatUtil.formatDateString(new Date(alarm.getResumeTime()), "yyyy-MM-dd HH:mm:ss") + " - 报警恢复 - 持续时间："
                                    + alarm.getEventDuration() / 1000 + " 秒！";
                        }
                        else
                        {
                            msg =
                                objName[0] + " - " + evtName[0] + " - "
                                    + FormatUtil.formatDateString(new Date(alarm.getCollTime()), "yyyy-MM-dd HH:mm:ss") + " - 报警开始！";
                        }

                        switch (actionType)
                        {
                        case 1:
                        {
                            if (userInfos != null && userInfos.length > 0)
                            {
                                logger.debug("开始发送邮件！");

                                // 发送邮件
                                ret = EmailUtil.sendEmail(userInfos, msg);

                                // 添加日志
                                msg += (ret == 0 ? "- 邮件失败" : "- 邮件成功") + " - " + getUserInfo(userInfos);
                                this.actionLog(StringUtil.left(msg, 200));
                                // this.addActionLog(alarm, actionInfo.getField_4(), msg, ret);
                            }

                            break;
                        }
                        case 10:
                        {
                            if (userInfos != null && userInfos.length > 0)
                            {
                                logger.debug("开始发送短信！");

                                // 发送短信
                                ret = MessageUtil.sendMessage(userInfos, msg);

                                // 添加日志
                                msg += (ret == 0 ? "- 短信失败" : "- 短信成功") + " - " + getUserInfo(userInfos);
                                this.actionLog(StringUtil.left(msg, 200));
                                // this.addActionLog(alarm, actionInfo.getField_4(), msg, ret);
                            }

                            break;
                        }
                        case 20:
                        {
                            if (alarm.getResumeTime() == 0)
                            {
                                logger.debug("开始生成工单！");
                                String userId = (userInfos != null && userInfos.length > 0) ? userInfos[0] : "";

                                // 生成工单
                                ret = this.generateWorkOrder(alarm, userId, msg);

                                // 添加日志
                                msg += ret == 0 ? "- 生成工单失败" : ("- 生成工单成功" + " - 工单ID：" + orderId);
                                this.actionLog(StringUtil.left(msg, 200));
                                // this.addActionLog(alarm, actionInfo.getField_4(), msg, ret);
                            }

                            break;
                        }
                        default:
                        {
                            logger.error("动作类型错误, 动作类型为:'" + actionType + "'");
                            break;
                        }
                        }
                    }
                    else
                    {
                        isAction = 0; // 不执行动作
                    }

                    // 更新当前告警数据库的动作状态
                    this.updateAlarmAction(alarm, actionInfo.getField_4(), isAction);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 实现同步数据库
     * @param
     * @return
     * @throws
     */
    @Override
    protected void syncDB()
    {
        try
        {
            new SynchronizeDB().syncDBAction();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 更新数据库的动作状态
     * @param alarm 报警对象
     * @param actionId 动作ID
     * @param isAction 是否执行动作，0表示不执行，1表示执行
     * @return
     * @throws Exception
     */
    private void updateAlarmAction(AlarmEventEntity alarm, String actionId, int isAction) throws Exception
    {
        try
        {
            if (isAction == 1)
            {
                // 更新当前告警数据库的动作状态
                if (alarm.getResumeTime() > 0)
                {
                    // 结束时设完成
                    sqlExecutor.executeNonQuery(this.sqlExecutor.getSqlParser().formatCommand(
                        "UPDATE BMP_ACTIONSTATE SET ACTION_STATE=10,UPDATE_TIME=%s WHERE ALARMEVT_ID=%s AND ACTION_ID=%s",
                        new SqlValue[] { new SqlValue(new Date(), SqlParamType.DateTime), new SqlValue(alarm.getAlarmEvtId(), SqlParamType.Numeric),
                            new SqlValue(actionId, SqlParamType.Numeric) }));
                }
                else
                {
                    // 发生时设执行中
                    sqlExecutor.executeNonQuery(this.sqlExecutor.getSqlParser().formatCommand(
                        "UPDATE BMP_ACTIONSTATE SET ACTION_STATE=2,UPDATE_TIME=%s WHERE ALARMEVT_ID=%s AND ACTION_ID=%s",
                        new SqlValue[] { new SqlValue(new Date(), SqlParamType.DateTime), new SqlValue(alarm.getAlarmEvtId(), SqlParamType.Numeric),
                            new SqlValue(actionId, SqlParamType.Numeric) }));
                }
            }
            else if (isAction == 0)
            {
                // 不执行则修改状态为3
                sqlExecutor.executeNonQuery(this.sqlExecutor.getSqlParser().formatCommand(
                    "UPDATE BMP_ACTIONSTATE SET ACTION_STATE=3,UPDATE_TIME=%s WHERE ALARMEVT_ID=%s AND ACTION_ID=%s",
                    new SqlValue[] { new SqlValue(new Date(), SqlParamType.DateTime), new SqlValue(alarm.getAlarmEvtId(), SqlParamType.Numeric),
                        new SqlValue(actionId, SqlParamType.Numeric) }));
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 添加动作日志
     * @param alarm 报警对象
     * @param actionId 动作ID
     * @param msg 动作内容
     * @param ret 动作返回值，0表示发送失败，1表示发送成功
     * @return
     * @throws Exception
     */
    private void addActionLog(AlarmEventEntity alarm, String actionId, String msg, int ret) throws Exception
    {
        HashMap<String, String> model = new HashMap<String, String>();
        model.put("ID", UUID.randomUUID().toString());
        model.put("ALARMEVT_ID", String.valueOf(alarm.getAlarmEvtId()));
        model.put("ACTION_ID", actionId);
        model.put("ACTION_MSG", msg);
        model.put("ACTION_RESULT", "");
        model.put("STATE", ret == 1 ? "10" : "11");
        model.put("FIELD_1", "");

        sqlExecutor.transBegin();
        try
        {
            SqlField[] param =
                new SqlField[] { SqlField.tryCreate("ID", model.get("ID")),
                    SqlField.tryCreate("ALARMEVT_ID", model.get("ALARMEVT_ID"), SqlParamType.Numeric),
                    SqlField.tryCreate("ACTION_ID", model.get("ACTION_ID"), SqlParamType.Numeric),
                    SqlField.tryCreate("ACTION_MSG", model.get("ACTION_MSG")), SqlField.tryCreate("ACTION_RESULT", model.get("ACTION_RESULT")),
                    SqlField.tryCreate("STATE", model.get("STATE"), SqlParamType.Numeric), SqlField.tryCreate("FIELD_1", model.get("FIELD_1")),
                    new SqlField("LOG_TIME", new Date(), SqlParamType.DateTime) };

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getInsertCommandString("BMP_ACTIONLOG", Arrays.asList(param)));

            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 生成工单
     * @param alarm 报警对象
     * @param userId 用户ID
     * @param msg 动作内容
     * @param ret 动作返回值，0表示发送失败，1表示发送成功
     * @return
     * @throws Exception
     */
    private int generateWorkOrder(AlarmEventEntity alarm, String userId, String msg) throws Exception
    {
        int ret = 0;

        HashMap<String, String> model = new HashMap<String, String>();
        model.put("ORDER_ID", String.valueOf(sqlExecutor.getNewId("BMP_WORKORDER")));
        model.put("EVENT_ID", String.valueOf(alarm.getAlarmEvtId()));
        model.put("CHECK_USERID", userId);
        model.put("ORDER_DESC", msg);
        model.put("ORDER_STATE", "0");

        logger.debug("正在生成工单，请稍候.......");
        sqlExecutor.transBegin();
        try
        {
            SqlField[] param =
                new SqlField[] { SqlField.tryCreate("ORDER_ID", model.get("ORDER_ID"), SqlParamType.Numeric),
                    SqlField.tryCreate("EVENT_ID", model.get("EVENT_ID"), SqlParamType.Numeric),
                    SqlField.tryCreate("CHECK_USERID", model.get("CHECK_USERID"), SqlParamType.Numeric),
                    SqlField.tryCreate("ORDER_DESC", model.get("ORDER_DESC")),
                    SqlField.tryCreate("ORDER_STATE", model.get("ORDER_STATE"), SqlParamType.Numeric),
                    new SqlField("CREATE_TIME", new Date(), SqlParamType.DateTime), new SqlField("UPDATE_TIME", new Date(), SqlParamType.DateTime) };

            sqlExecutor.executeNonQuery(sqlExecutor.getSqlParser().getInsertCommandString("BMP_WORKORDER", Arrays.asList(param)));

            sqlExecutor.transCommit();
            ret = 1;

            // 保存工单ID
            orderId = Integer.valueOf(model.get("ORDER_ID"));
            logger.debug("恭喜你，工单生成成功!" + msg);
        }
        catch (Exception ex)
        {
            ret = 0;
            sqlExecutor.transRollback();
            logger.error("工单生成失败!" + msg, ex);
        }

        return ret;
    }

    /**
     * 记录动作日志，暂时记录到日志表
     * @param msg 记录信息
     * @return
     * @throws
     */
    private void actionLog(String msg)
    {
        try
        {
            OperatorLog.log(1, "admin", msg);
        }
        catch (Exception e)
        {
            logger.error("记录动作日志错误:'" + msg + "'");
        }
    }

    /**
     * 获取用户邮箱或电话
     * @param actionType 动作类型
     * @param assignType 指派类型
     * @param objId 对象ID
     * @return arrUserInfo 用户数组
     * @throws Exception
     */
    private String[] getUserInfo(int actionType, int assignType, int objId) throws Exception
    {
        String[] arrUserInfo = null;
        List<String> userInfoNum = new ArrayList<String>();
        List<String> userInfo = null;

        String alarmActionType = "";
        if (actionType == 1)
        {
            // 获取用户邮件
            alarmActionType = "EMAIL";
        }
        else if (actionType == 10)
        {
            // 获取用户电话
            alarmActionType = "MOBILE_PHONE";
        }
        else if (actionType == 20)
        {
            // 获取用户ID
            alarmActionType = "ID";
        }

        if (!StringUtil.isNullOrEmpty(alarmActionType))
        {
            switch (assignType)
            {
            case 1:
                // 部门
                // break;
            case 2:
                // 分组
                userInfo =
                    sqlExecutor.load("SELECT " + alarmActionType + " FROM UUM_USER WHERE ID IN (SELECT USER_ID FROM UUM_USERTOGROUP WHERE GROUP_ID="
                        + objId + ")", true);
                break;
            case 3:
                // 角色
                userInfo =
                    sqlExecutor.load("SELECT " + alarmActionType + " FROM UUM_USER WHERE ID IN (SELECT USER_ID FROM UUM_USERTOROLE WHERE ROLE_ID="
                        + objId + ")", true);
                break;
            case 4:
                // 权限
                userInfo =
                    sqlExecutor.load("SELECT " + alarmActionType + " FROM UUM_USER WHERE ID IN ("
                        + "SELECT USER_ID FROM UUM_USERTOROLE WHERE ROLE_ID IN ("
                        + "SELECT ROLE_ID FROM UUM_ROLEAUTHORITY WHERE FUNCTION_ID in (SELECT ID FROM UUM_FUNCTION WHERE ID =" + objId
                        + " OR PARENT_ID=" + objId + ")) and state = 0)", true);
                break;
            default:
                break;
            }
        }

        if (userInfo != null && userInfo.size() > 0)
        {
            for (String item : userInfo)
            {
                if (!userInfoNum.contains(item))
                {
                    userInfoNum.add(item);
                }
            }
        }

        if (userInfoNum != null && userInfoNum.size() > 0)
        {
            arrUserInfo = (String[]) userInfoNum.toArray(new String[0]);
            return arrUserInfo;
        }
        else
        {
            return null;
        }
    }

    /**
     * 是否满足日期
     * @param weekMask 日期掩码
     * @param dayOfWeek 当前日期
     * @return true or false
     * @throws
     */
    private boolean matchWeek(String weekMask, int dayOfWeek)
    {
        if (StringUtil.isNullOrEmpty(weekMask) || weekMask.length() < 7)
        {
            return false;
        }

        // return weekMask.substring(dayOfWeek - 1, dayOfWeek).equals("1");
        return "1".equals(weekMask.substring(dayOfWeek - 1, dayOfWeek));
    }

    /**
     * 是否满足时间
     * @param hourMask 时间掩码
     * @param hour 当前时间
     * @return true or false
     * @throws
     */
    private boolean matchHour(String hourMask, int hour)
    {
        if (StringUtil.isNullOrEmpty(hourMask) || hourMask.length() < 24)
        {
            return false;
        }
        // return hourMask.substring(hour, hour + 1).equals("1")
        return "1".equals(hourMask.substring(hour, hour + 1));
    }

    /**
     * 用户数组转为字符串
     * @param userInfos 用户数组
     * @return userInfo 用户字符串
     */
    private String getUserInfo(String[] userInfos)
    {
        ArrayList<String> users = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        if (userInfos == null || userInfos.length == 0)
        {
            return "";
        }
        for (String item : userInfos)
        {
            if (!users.contains(item))
            {
                users.add(item);

                sb.append(item);
                sb.append(",");
            }
        }

        String userInfo = sb.toString().substring(0, sb.length() - 1);
        return userInfo;
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        try
        {
            new AlarmActionBase().syncDB();
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }
}
