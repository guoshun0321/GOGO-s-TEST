package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBLOGICAPPDATA")
public class WeblogicAppDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "APP_NAME")
    private String app_name;
    @Column(name = "CONN_MAX")
    private int conn_max;
    @Column(name = "CONN_ACTIVE")
    private int conn_active;
    @Column(name = "CONN_TOTAL")
    private int conn_total;

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

    public int getConn_max()
    {
        return conn_max;
    }

    public void setConn_max(int conn_max)
    {
        this.conn_max = conn_max;
    }

    public int getConn_active()
    {
        return conn_active;
    }

    public void setConn_active(int conn_active)
    {
        this.conn_active = conn_active;
    }

    public int getConn_total()
    {
        return conn_total;
    }

    public void setConn_total(int conn_total)
    {
        this.conn_total = conn_total;
    }

}
