/************************************************************************
 * 日 期：2012-04-10 
 * 作 者: 徐德海 
 * 版 本：v1.3 
 * 描 述: 报警动作相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.alarmaction;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DataRecordInfo;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlValue;
import jetsennet.util.FormatUtil;

/**
 * 代码优化改动
 * @author liwei
 */
public final class SendMessageAction
{
    private static final Logger logger = Logger.getLogger(SendMessageAction.class);
    private ConnectionInfo smsConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private static SendMessageAction instance = new SendMessageAction();

    private SendMessageAction()
    {
        smsConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("sms_driver"), DbConfig.getProperty("sms_dburl"), DbConfig.getProperty("sms_dbuser"), DbConfig
                .getProperty("sms_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(smsConnectionInfo);
    }

    /**
     * 单例
     * @return 单例
     */
    public static SendMessageAction getInstance()
    {
        return instance;
    }

    /**
     * 发送短信
     * @param phone 用户手机
     * @param msg 短信内容
     * @throws
     */
    public void addMessage(String phone, String msg)
    {
        try
        {
            sqlExecutor.transBegin();
            DbCommand objCommand = new DbCommand(sqlExecutor.getSqlParser(), DbCommandType.InsertCommand);
            objCommand.setTableName("OutBox");
            objCommand.addField("Mbno", phone);
            objCommand.addField("Msg", msg);
            objCommand.addField("SendTime", new Date());
            objCommand.addField("Report", 0);
            objCommand.addField("ComPort", 0);

            sqlExecutor.executeNonQuery(objCommand.toString());
            sqlExecutor.transCommit();
        }
        catch (Exception ex)
        {
            sqlExecutor.transRollback();
            logger.error(String.format("向用户(%s)发送报警(%s)失败。", phone, msg), ex);
        }
    }

    /**
     * @param date 日期
     * @return 结果获取短信
     */
    public List<DataRecordInfo> getMessage(Date date)
    {
        List<DataRecordInfo> messages = null;
        try
        {
            messages =
                sqlExecutor.load(DataRecordInfo.class, sqlExecutor.getSqlParser().formatCommand("SELECT mbno, Msg FROM InBox WHERE ArriveTime>%s",
                    new SqlValue(date)));
        }
        catch (Exception ex)
        {
            logger.error(String.format("获取(%s)以后的短信失败。", FormatUtil.formatDateString(new Date(), "yyyy-MM-dd HH:mm:ss")));
        }

        return messages;
    }
}
