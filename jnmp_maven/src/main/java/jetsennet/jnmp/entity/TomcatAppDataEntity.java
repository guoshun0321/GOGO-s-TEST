package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_TOMCATAPPDATA")
public class TomcatAppDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "APP_NAME")
    private String app_name;
    @Column(name = "CONN_MAX")
    private long conn_max;
    @Column(name = "CONN_ACTIVE")
    private long conn_active;
    @Column(name = "CONN_COUNT")
    private long conn_count;

    /**
     * 构造方法
     */
    public TomcatAppDataEntity()
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

    public String getApp_name()
    {
        return app_name;
    }

    public void setApp_name(String app_name)
    {
        this.app_name = app_name;
    }

    public long getConn_max()
    {
        return conn_max;
    }

    public void setConn_max(long conn_max)
    {
        this.conn_max = conn_max;
    }

    public long getConn_active()
    {
        return conn_active;
    }

    public void setConn_active(long conn_active)
    {
        this.conn_active = conn_active;
    }

    public long getConn_count()
    {
        return conn_count;
    }

    public void setConn_count(long conn_count)
    {
        this.conn_count = conn_count;
    }

}
