package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NMP_DB2SYSDATA")
public class DB2SysDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CONN_TIMELEN")
    private int conn_timelen;
    @Column(name = "CONN_CURRNUM")
    private int conn_currnum;
    @Column(name = "CONN_LOCALNUM")
    private int conn_localnum;
    @Column(name = "CONN_LOCALINEXEC")
    private int conn_localinexec;
    @Column(name = "CONN_REMOTENUM")
    private int conn_remotenum;
    @Column(name = "CONN_REMOTEINEXEC")
    private int conn_remoteinexec;
    @Column(name = "AGENT_TOTALNUM")
    private int agent_totalnum;
    @Column(name = "AGENT_ACTIVENUM")
    private int agent_activenum;
    @Column(name = "AGENT_IDLENUM")
    private int agent_idlenum;
    @Column(name = "AGENT_WAITNUM")
    private int agent_waitnum;

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

    public int getConn_timelen()
    {
        return conn_timelen;
    }

    public void setConn_timelen(int conn_timelen)
    {
        this.conn_timelen = conn_timelen;
    }

    public int getConn_currnum()
    {
        return conn_currnum;
    }

    public void setConn_currnum(int conn_currnum)
    {
        this.conn_currnum = conn_currnum;
    }

    public int getConn_localnum()
    {
        return conn_localnum;
    }

    public void setConn_localnum(int conn_localnum)
    {
        this.conn_localnum = conn_localnum;
    }

    public int getConn_localinexec()
    {
        return conn_localinexec;
    }

    public void setConn_localinexec(int conn_localinexec)
    {
        this.conn_localinexec = conn_localinexec;
    }

    public int getConn_remotenum()
    {
        return conn_remotenum;
    }

    public void setConn_remotenum(int conn_remotenum)
    {
        this.conn_remotenum = conn_remotenum;
    }

    public int getConn_remoteinexec()
    {
        return conn_remoteinexec;
    }

    public void setConn_remoteinexec(int conn_remoteinexec)
    {
        this.conn_remoteinexec = conn_remoteinexec;
    }

    public int getAgent_totalnum()
    {
        return agent_totalnum;
    }

    public void setAgent_totalnum(int agent_totalnum)
    {
        this.agent_totalnum = agent_totalnum;
    }

    public int getAgent_activenum()
    {
        return agent_activenum;
    }

    public void setAgent_activenum(int agent_activenum)
    {
        this.agent_activenum = agent_activenum;
    }

    public int getAgent_idlenum()
    {
        return agent_idlenum;
    }

    public void setAgent_idlenum(int agent_idlenum)
    {
        this.agent_idlenum = agent_idlenum;
    }

    public int getAgent_waitnum()
    {
        return agent_waitnum;
    }

    public void setAgent_waitnum(int agent_waitnum)
    {
        this.agent_waitnum = agent_waitnum;
    }

}
