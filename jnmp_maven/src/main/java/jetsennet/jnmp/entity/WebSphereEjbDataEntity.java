package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBSPHEREEJBDATA")
public class WebSphereEjbDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "EJB_NAME")
    private String ejb_name;
    @Column(name = "APP_NAME")
    private String app_name;
    @Column(name = "EJB_TYPE")
    private String ejb_type;
    @Column(name = "CREATE_COUNT")
    private int create_count;
    @Column(name = "REMOVE_COUNT")
    private int remove_count;
    @Column(name = "METHOD_CALLCOUNT")
    private int method_callcount;
    @Column(name = "AVG_TIME")
    private int avg_time;
    @Column(name = "MIN_TIME")
    private int min_time;
    @Column(name = "MAX_TIME")
    private int max_time;
    @Column(name = "TOTAL_TIME")
    private int total_time;

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

    public String getEjb_name()
    {
        return ejb_name;
    }

    public void setEjb_name(String ejb_name)
    {
        this.ejb_name = ejb_name;
    }

    public String getApp_name()
    {
        return app_name;
    }

    public void setApp_name(String app_name)
    {
        this.app_name = app_name;
    }

    public String getEjb_type()
    {
        return ejb_type;
    }

    public void setEjb_type(String ejb_type)
    {
        this.ejb_type = ejb_type;
    }

    public int getCreate_count()
    {
        return create_count;
    }

    public void setCreate_count(int create_count)
    {
        this.create_count = create_count;
    }

    public int getRemove_count()
    {
        return remove_count;
    }

    public void setRemove_count(int remove_count)
    {
        this.remove_count = remove_count;
    }

    public int getMethod_callcount()
    {
        return method_callcount;
    }

    public void setMethod_callcount(int method_callcount)
    {
        this.method_callcount = method_callcount;
    }

    public int getAvg_time()
    {
        return avg_time;
    }

    public void setAvg_time(int avg_time)
    {
        this.avg_time = avg_time;
    }

    public int getMin_time()
    {
        return min_time;
    }

    public void setMin_time(int min_time)
    {
        this.min_time = min_time;
    }

    public int getMax_time()
    {
        return max_time;
    }

    public void setMax_time(int max_time)
    {
        this.max_time = max_time;
    }

    public int getTotal_time()
    {
        return total_time;
    }

    public void setTotal_time(int total_time)
    {
        this.total_time = total_time;
    }

}
