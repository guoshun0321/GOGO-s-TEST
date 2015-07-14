package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NMP_DB2DBDATA")
public class DB2DBDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "COMMIT_NUM")
    private int commit_num;
    @Column(name = "ROLLBACK_NUM")
    private int rollback_num;
    @Column(name = "FAIL_NUM")
    private int fail_num;
    @Column(name = "DEADLOCK_NUM")
    private int deadlock_num;
    @Column(name = "LOCK_WAITING")
    private int lock_waiting;
    @Column(name = "PACKAGE_HIT")
    private float package_hit;
    @Column(name = "CATALOG_HIT")
    private float catalog_hit;
    @Column(name = "SORT_OVERFLOW")
    private float sort_overflow;
    @Column(name = "LOG_USERD")
    private float log_used;
    @Column(name = "CACHE_HIT")
    private float cache_hit;
    @Column(name = "INDEX_HIT")
    private float index_hit;
    @Column(name = "READ_NUM")
    private int read_num;
    @Column(name = "WRITE_NUM")
    private int write_num;

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

    public int getCommit_num()
    {
        return commit_num;
    }

    public void setCommit_num(int commit_num)
    {
        this.commit_num = commit_num;
    }

    public int getRollback_num()
    {
        return rollback_num;
    }

    public void setRollback_num(int rollback_num)
    {
        this.rollback_num = rollback_num;
    }

    public int getFail_num()
    {
        return fail_num;
    }

    public void setFail_num(int fail_num)
    {
        this.fail_num = fail_num;
    }

    public int getDeadlock_num()
    {
        return deadlock_num;
    }

    public void setDeadlock_num(int deadlock_num)
    {
        this.deadlock_num = deadlock_num;
    }

    public int getLock_waiting()
    {
        return lock_waiting;
    }

    public void setLock_waiting(int lock_waiting)
    {
        this.lock_waiting = lock_waiting;
    }

    public float getPackage_hit()
    {
        return package_hit;
    }

    public void setPackage_hit(float package_hit)
    {
        this.package_hit = package_hit;
    }

    public float getCatalog_hit()
    {
        return catalog_hit;
    }

    public void setCatalog_hit(float catalog_hit)
    {
        this.catalog_hit = catalog_hit;
    }

    public float getSort_overflow()
    {
        return sort_overflow;
    }

    public void setSort_overflow(float sort_overflow)
    {
        this.sort_overflow = sort_overflow;
    }

    public float getLog_used()
    {
        return log_used;
    }

    public void setLog_used(float log_used)
    {
        this.log_used = log_used;
    }

    public float getCache_hit()
    {
        return cache_hit;
    }

    public void setCache_hit(float cache_hit)
    {
        this.cache_hit = cache_hit;
    }

    public float getIndex_hit()
    {
        return index_hit;
    }

    public void setIndex_hit(float index_hit)
    {
        this.index_hit = index_hit;
    }

    public int getRead_num()
    {
        return read_num;
    }

    public void setRead_num(int read_num)
    {
        this.read_num = read_num;
    }

    public int getWrite_num()
    {
        return write_num;
    }

    public void setWrite_num(int write_num)
    {
        this.write_num = write_num;
    }

}
