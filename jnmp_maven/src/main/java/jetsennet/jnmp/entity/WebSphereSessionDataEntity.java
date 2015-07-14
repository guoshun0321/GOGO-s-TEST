package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBSPHERESESSIONDATA")
public class WebSphereSessionDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "APP_NAME")
    private String app_name;
    @Column(name = "SESSION_MAX")
    private int session_max;
    @Column(name = "SESSION_MIN")
    private int session_min;
    @Column(name = "SESSION_CURR")
    private int session_curr;

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

    public String getApp_name()
    {
        return app_name;
    }

    public void setApp_name(String app_name)
    {
        this.app_name = app_name;
    }

    public int getSession_max()
    {
        return session_max;
    }

    public void setSession_max(int session_max)
    {
        this.session_max = session_max;
    }

    public int getSession_min()
    {
        return session_min;
    }

    public void setSession_min(int session_min)
    {
        this.session_min = session_min;
    }

    public int getSession_curr()
    {
        return session_curr;
    }

    public void setSession_curr(int session_curr)
    {
        this.session_curr = session_curr;
    }

}
