package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author
 */
@Table(name = "NMP_ORACLESESSWAITDATA")
public class OracleSessWaitDataEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "SESSION_ID")
    private int session_id;
    @Column(name = "USER_NAME")
    private String user_name;
    @Column(name = "WAIT_EVENT")
    private String wait_event;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "WAIT_TIME")
    private int wait_time;

    /**
     * 构造方法
     */
    public OracleSessWaitDataEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param session_id 参数
     * @param user_name 参数
     * @param wait_event 参数
     * @param status 参数
     * @param wait_time 参数
     */
    public OracleSessWaitDataEntity(int obj_id, long coll_time, int session_id, String user_name, String wait_event, String status, int wait_time)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.session_id = session_id;
        this.user_name = user_name;
        this.wait_event = wait_event;
        this.status = status;
        this.wait_time = wait_time;
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

    public String getWait_event()
    {
        return wait_event;
    }

    public void setWait_event(String wait_event)
    {
        this.wait_event = wait_event;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getWait_time()
    {
        return wait_time;
    }

    public void setWait_time(int wait_time)
    {
        this.wait_time = wait_time;
    }

}
