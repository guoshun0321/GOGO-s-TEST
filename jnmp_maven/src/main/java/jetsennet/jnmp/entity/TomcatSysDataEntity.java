package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_TOMCATSYSDATA")
public class TomcatSysDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "HEAP_MAX")
    private double heap_max;
    @Column(name = "HEAP_COMMIT")
    private double heap_commit;
    @Column(name = "HEAP_USED")
    private double heap_used;
    @Column(name = "HEAP_RATE")
    private double heap_rate;
    @Column(name = "NONHEAP_MAX")
    private double nonheap_max;
    @Column(name = "NONHEAP_COMMIT")
    private double nonheap_commit;
    @Column(name = "NONHEAP_USED")
    private double nonheap_used;
    @Column(name = "NONHEAP_RATE")
    private double nonheap_rate;
    @Column(name = "THREAD_PEAK")
    private int thread_peak;
    @Column(name = "THREAD_CURRENT")
    private int thread_current;
    @Column(name = "THREAD_DAEMON")
    private int thread_daemon;
    @Column(name = "THREAD_STARTED")
    private int thread_started;

    /**
     * 构造方法
     */
    public TomcatSysDataEntity()
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

    public double getHeap_max()
    {
        return heap_max;
    }

    public void setHeap_max(double heap_max)
    {
        this.heap_max = heap_max;
    }

    public double getHeap_commit()
    {
        return heap_commit;
    }

    public void setHeap_commit(double heap_commit)
    {
        this.heap_commit = heap_commit;
    }

    public double getHeap_used()
    {
        return heap_used;
    }

    public void setHeap_used(double heap_used)
    {
        this.heap_used = heap_used;
    }

    public double getHeap_rate()
    {
        return heap_rate;
    }

    public void setHeap_rate(double heap_rate)
    {
        this.heap_rate = heap_rate;
    }

    public double getNonheap_max()
    {
        return nonheap_max;
    }

    public void setNonheap_max(double nonheap_max)
    {
        this.nonheap_max = nonheap_max;
    }

    public double getNonheap_commit()
    {
        return nonheap_commit;
    }

    public void setNonheap_commit(double nonheap_commit)
    {
        this.nonheap_commit = nonheap_commit;
    }

    public double getNonheap_used()
    {
        return nonheap_used;
    }

    public void setNonheap_used(double nonheap_used)
    {
        this.nonheap_used = nonheap_used;
    }

    public double getNonheap_rate()
    {
        return nonheap_rate;
    }

    public void setNonheap_rate(double nonheap_rate)
    {
        this.nonheap_rate = nonheap_rate;
    }

    public int getThread_peak()
    {
        return thread_peak;
    }

    public void setThread_peak(int thread_peak)
    {
        this.thread_peak = thread_peak;
    }

    public int getThread_current()
    {
        return thread_current;
    }

    public void setThread_current(int thread_current)
    {
        this.thread_current = thread_current;
    }

    public int getThread_daemon()
    {
        return thread_daemon;
    }

    public void setThread_daemon(int thread_daemon)
    {
        this.thread_daemon = thread_daemon;
    }

    public int getThread_started()
    {
        return thread_started;
    }

    public void setThread_started(int thread_started)
    {
        this.thread_started = thread_started;
    }

}
