package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_SQLSERVLOCKDATA")
public class SqlServLockDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "LOCK_REQUEST")
    private int lock_request;
    @Column(name = "LOCK_WAIT")
    private int lock_wait;
    @Column(name = "LOCK_TIMEOUT")
    private int lock_timeout;
    @Column(name = "LOCK_DEAD")
    private int lock_dead;
    @Column(name = "LOCK_WAITTIME")
    private int lock_waittime;

    /**
     * 构造方法
     */
    public SqlServLockDataEntity()
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

    public int getLock_request()
    {
        return lock_request;
    }

    public void setLock_request(int lock_request)
    {
        this.lock_request = lock_request;
    }

    public int getLock_wait()
    {
        return lock_wait;
    }

    public void setLock_wait(int lock_wait)
    {
        this.lock_wait = lock_wait;
    }

    public int getLock_timeout()
    {
        return lock_timeout;
    }

    public void setLock_timeout(int lock_timeout)
    {
        this.lock_timeout = lock_timeout;
    }

    public int getLock_dead()
    {
        return lock_dead;
    }

    public void setLock_dead(int lock_dead)
    {
        this.lock_dead = lock_dead;
    }

    public int getLock_waittime()
    {
        return lock_waittime;
    }

    public void setLock_waittime(int lock_waittime)
    {
        this.lock_waittime = lock_waittime;
    }

}
