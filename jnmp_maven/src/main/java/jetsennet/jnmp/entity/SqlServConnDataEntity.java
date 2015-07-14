package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_SQLSERVCONNDATA")
public class SqlServConnDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CONN_TIME")
    private int conn_time;
    @Column(name = "USER_CONN")
    private int user_conn;
    @Column(name = "LOGINS")
    private int logins;
    @Column(name = "LOGOUTS")
    private int logouts;

    /**
     * 构造方法
     */
    public SqlServConnDataEntity()
    {

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

    public int getConn_time()
    {
        return conn_time;
    }

    public void setConn_time(int conn_time)
    {
        this.conn_time = conn_time;
    }

    public int getUser_conn()
    {
        return user_conn;
    }

    public void setUser_conn(int user_conn)
    {
        this.user_conn = user_conn;
    }

    public int getLogins()
    {
        return logins;
    }

    public void setLogins(int logins)
    {
        this.logins = logins;
    }

    public int getLogouts()
    {
        return logouts;
    }

    public void setLogouts(int logouts)
    {
        this.logouts = logouts;
    }

}
