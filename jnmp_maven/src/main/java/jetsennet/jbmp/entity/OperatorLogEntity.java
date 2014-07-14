/************************************************************************
日 期: 2012-02-13
作 者: 徐德海
版 本: v1.3
描 述: 动作日志
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

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
 * 操作日志
 * @author xdh
 */
@Table(name = "NET_OPERATORLOG")
public class OperatorLogEntity
{
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "USER_ID")
    private int userId;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "HOST_NAME")
    private String hostName;
    @Column(name = "SYS_NAME")
    private String sysName;
    @Column(name = "LOG_TIME")
    private Date logTime;

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the userId
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * @return the sysName
     */
    public String getSysName()
    {
        return sysName;
    }

    /**
     * @param sysName the sysName to set
     */
    public void setSysName(String sysName)
    {
        this.sysName = sysName;
    }

    /**
     * @return the logTime
     */
    public Date getLogTime()
    {
        return logTime;
    }

    /**
     * @param logTime the logTime to set
     */
    public void setLogTime(Date logTime)
    {
        this.logTime = logTime;
    }

    @Override
    public String toString()
    {
        return "ID:" + id + ",用户ID:" + userId + ",用户名称:" + userName + ",描述:" + description + ",日志时间:" + logTime;
    }

    /**
     * @param time 时间
     * @return 查询
     */
    public static String getQuerySql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName("NET_OPERATORLOG");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("LOG_TIME", formater.format(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.DateTime) };
        cmd.setFilter(conds);
        cmd.setOrderString("ORDER BY LOG_TIME");
        return cmd.toString();
    }

    /**
     * @param time 时间
     * @return 删除
     */
    public static String getDelSql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.DeleteCommand);
        cmd.setTableName("NET_OPERATORLOG");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("LOG_TIME", formater.format(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.DateTime) };
        cmd.setFilter(conds);
        return cmd.toString();
    }

    public static void main(String[] args) throws Exception
    {
        DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class);
        dal.get(OperatorLogEntity.getQuerySql(1111l));
        dal.delete(OperatorLogEntity.getDelSql(1111l));
    }
}
