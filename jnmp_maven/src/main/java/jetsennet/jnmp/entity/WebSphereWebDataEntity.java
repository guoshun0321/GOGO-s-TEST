package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBSPHEREWEBDATA")
public class WebSphereWebDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "PAGE_NAME")
    private String page_name;
    @Column(name = "APP_NAME")
    private String app_name;
    @Column(name = "PAGE_TYPE")
    private String page_type;
    @Column(name = "REQUEST_NUM")
    private int request_num;
    @Column(name = "AVG_TIME")
    private int avg_time;
    @Column(name = "MAX_TIME")
    private int max_time;
    @Column(name = "MIN_TIME")
    private int min_time;
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

    public String getPage_name()
    {
        return page_name;
    }

    public void setPage_name(String page_name)
    {
        this.page_name = page_name;
    }

    public String getApp_name()
    {
        return app_name;
    }

    public void setApp_name(String app_name)
    {
        this.app_name = app_name;
    }

    public String getPage_type()
    {
        return page_type;
    }

    public void setPage_type(String page_type)
    {
        this.page_type = page_type;
    }

    public int getRequest_num()
    {
        return request_num;
    }

    public void setRequest_num(int request_num)
    {
        this.request_num = request_num;
    }

    public int getAvg_time()
    {
        return avg_time;
    }

    public void setAvg_time(int avg_time)
    {
        this.avg_time = avg_time;
    }

    public int getMax_time()
    {
        return max_time;
    }

    public void setMax_time(int max_time)
    {
        this.max_time = max_time;
    }

    public int getMin_time()
    {
        return min_time;
    }

    public void setMin_time(int min_time)
    {
        this.min_time = min_time;
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
