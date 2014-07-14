/**
 * 日 期： 2012-2-21
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  SyslogEntity.java
 * 历 史： 2012-2-21 创建
 */
package jetsennet.jbmp.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.DbCommand;
import jetsennet.sqlclient.DbCommandType;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * SyslogEntity
 */
@Table(name = "BMP_SYSLOG")
public class SyslogEntity
{
    /**
     * 接收时间
     */
    @Column(name = "COLL_TIME")
    private Date collTime;
    /**
     * IP地址
     */
    @Column(name = "IP_ADDR")
    private String ipAddr;
    /**
     * 事件内容
     */
    @Column(name = "CONTENT")
    private String content;

    /**
     * @return the collTime
     */
    public Date getCollTime()
    {
        return collTime;
    }

    /**
     * @param collTime the collTime to set
     */
    public void setCollTime(Date collTime)
    {
        this.collTime = collTime;
    }

    /**
     * @return the ipAddr
     */
    public String getIpAddr()
    {
        return ipAddr;
    }

    /**
     * @param ipAddr the ipAddr to set
     */
    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
    }

    /**
     * @return the content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return content;
    }

    /**
     * @param time 时间
     * @return 查询
     */
    public static String getQuerySql(Long time)
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        DbCommand cmd = new DbCommand(exec.getSqlParser(), DbCommandType.SelectCommand);
        cmd.setTableName("BMP_SYSLOG");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", formater.format(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.DateTime) };
        cmd.setFilter(conds);
        cmd.setOrderString("ORDER BY COLL_TIME");
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
        cmd.setTableName("BMP_SYSLOG");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SqlCondition[] conds =
            new SqlCondition[] { new SqlCondition("COLL_TIME", formater.format(time), SqlLogicType.And, SqlRelationType.Less, SqlParamType.DateTime) };
        cmd.setFilter(conds);
        return cmd.toString();
    }

    public static void main(String[] args) throws Exception
    {
        DefaultDal dal = ClassWrapper.wrapTrans(DefaultDal.class);
        dal.get(SyslogEntity.getQuerySql(1111l));
        dal.delete(SyslogEntity.getDelSql(1111l));
    }
}
