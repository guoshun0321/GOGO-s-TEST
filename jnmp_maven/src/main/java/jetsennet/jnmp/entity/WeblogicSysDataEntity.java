package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBLOGICSYSDATA")
public class WeblogicSysDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "HEAP_MAX")
    private int heap_max;
    @Column(name = "HEAP_COMMIT")
    private int heap_commit;
    @Column(name = "HEAP_USED")
    private int heap_used;
    @Column(name = "HEAP_RATE")
    private int heap_rate;
    @Column(name = "NONHEAP_MAX")
    private int nonheap_max;
    @Column(name = "NONHEAP_COMMIT")
    private int nonheap_commit;
    @Column(name = "NONHEAP_USED")
    private int nonheap_used;
    @Column(name = "NONHEAP_RATE")
    private int nonheap_rate;
    @Column(name = "THREAD_PEAK")
    private int thread_peak;
    @Column(name = "THREAD_CURRENT")
    private int thread_current;
    @Column(name = "THREAD_DAEMON")
    private int thread_daemon;
    @Column(name = "THREAD_STARTED")
    private int thread_started;

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

    public int getHeap_max()
    {
        return heap_max;
    }

    public void setHeap_max(int heap_max)
    {
        this.heap_max = heap_max;
    }

    public int getHeap_commit()
    {
        return heap_commit;
    }

    public void setHeap_commit(int heap_commit)
    {
        this.heap_commit = heap_commit;
    }

    public int getHeap_used()
    {
        return heap_used;
    }

    public void setHeap_used(int heap_used)
    {
        this.heap_used = heap_used;
    }

    public int getHeap_rate()
    {
        return heap_rate;
    }

    public void setHeap_rate(int heap_rate)
    {
        this.heap_rate = heap_rate;
    }

    public int getNonheap_max()
    {
        return nonheap_max;
    }

    public void setNonheap_max(int nonheap_max)
    {
        this.nonheap_max = nonheap_max;
    }

    public int getNonheap_commit()
    {
        return nonheap_commit;
    }

    public void setNonheap_commit(int nonheap_commit)
    {
        this.nonheap_commit = nonheap_commit;
    }

    public int getNonheap_used()
    {
        return nonheap_used;
    }

    public void setNonheap_used(int nonheap_used)
    {
        this.nonheap_used = nonheap_used;
    }

    public int getNonheap_rate()
    {
        return nonheap_rate;
    }

    public void setNonheap_rate(int nonheap_rate)
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
