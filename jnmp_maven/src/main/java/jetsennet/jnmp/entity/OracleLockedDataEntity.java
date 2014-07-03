package jetsennet.jnmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author？
 */
@Table(name = "NMP_ORACLELOCKEDDATA")
public class OracleLockedDataEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "SESSION_ID")
    private int session_id;
    @Column(name = "USER_NAME")
    private String user_name;
    @Column(name = "SERIAL")
    private int serial;
    @Column(name = "MACHINE")
    private String machine;
    @Column(name = "LOGON_TIME")
    private Date logon_time;
    @Column(name = "SQL_TEXT")
    private String sql_text;

    /**
     * 构造方法
     */
    public OracleLockedDataEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param session_id 参数
     * @param user_name 参数
     * @param serial 参数
     * @param machine 参数
     * @param logon_time 参数
     * @param sql_text 参数
     */
    public OracleLockedDataEntity(int obj_id, long coll_time, int session_id, String user_name, int serial, String machine, Date logon_time,
            String sql_text)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.session_id = session_id;
        this.user_name = user_name;
        this.serial = serial;
        this.machine = machine;
        this.logon_time = logon_time;
        this.sql_text = sql_text;
    }

    public int getObj_id()
    {
        return obj_id;
    }

    public void setObj_id(int obj_id)
    {
        this.obj_id = obj_id;
    }

    public long getColl_time()
    {
        return coll_time;
    }

    public void setColl_time(long coll_time)
    {
        this.coll_time = coll_time;
    }

    public int getSession_id()
    {
        return session_id;
    }

    public void setSession_id(int session_id)
    {
        this.session_id = session_id;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public int getSerial()
    {
        return serial;
    }

    public void setSerial(int serial)
    {
        this.serial = serial;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine(String machine)
    {
        this.machine = machine;
    }

    public Date getLogon_time()
    {
        return logon_time;
    }

    public void setLogon_time(Date logon_time)
    {
        this.logon_time = logon_time;
    }

    public String getSql_text()
    {
        return sql_text;
    }

    public void setSql_text(String sql_text)
    {
        this.sql_text = sql_text;
    }

}
