package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_TOMCATTHREADPOOLDATA")
public class TomcatThreadPoolEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "POOL_NAME")
    private String pool_name;
    @Column(name = "POOL_PORT")
    private int pool_port;
    @Column(name = "THREAD_MAX")
    private int thread_max;
    @Column(name = "THREAD_CURRENT")
    private int thread_current;
    @Column(name = "THREAD_BUSY")
    private int thread_busy;

    /**
     * 构造方法
     */
    public TomcatThreadPoolEntity()
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

    public String getPool_name()
    {
        return pool_name;
    }

    public void setPool_name(String pool_name)
    {
        this.pool_name = pool_name;
    }

    public int getPool_port()
    {
        return pool_port;
    }

    public void setPool_port(int pool_port)
    {
        this.pool_port = pool_port;
    }

    public int getThread_max()
    {
        return thread_max;
    }

    public void setThread_max(int thread_max)
    {
        this.thread_max = thread_max;
    }

    public int getThread_current()
    {
        return thread_current;
    }

    public void setThread_current(int thread_current)
    {
        this.thread_current = thread_current;
    }

    public int getThread_busy()
    {
        return thread_busy;
    }

    public void setThread_busy(int thread_busy)
    {
        this.thread_busy = thread_busy;
    }

}
