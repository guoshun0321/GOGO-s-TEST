package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBLOGICEJBDATA")
public class WeblogicEjbDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "EJB_NAME")
    private String ejb_name;
    @Column(name = "ACCESS_COUNT")
    private int access_count;
    @Column(name = "BEANS_USE")
    private int beans_use;
    @Column(name = "BEANS_USECURR")
    private int beans_usecurr;
    @Column(name = "BEANS_IDLE")
    private int beans_idle;
    @Column(name = "DESTROYED_COUNT")
    private int destroyed_count;
    @Column(name = "MISS_COUNT")
    private int miss_count;
    @Column(name = "WAITER_CURRENT")
    private int waiter_current;
    @Column(name = "WAITER_TOTAL")
    private int waiter_total;
    @Column(name = "TRAN_COMMITED")
    private int tran_commited;
    @Column(name = "TRAN_ROLLBACK")
    private int tran_rollback;
    @Column(name = "TRAN_TIMEOUT")
    private int tran_timeout;

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

    public int getAccess_count()
    {
        return access_count;
    }

    public void setAccess_count(int access_count)
    {
        this.access_count = access_count;
    }

    public int getBeans_use()
    {
        return beans_use;
    }

    public void setBeans_use(int beans_use)
    {
        this.beans_use = beans_use;
    }

    public int getBeans_usecurr()
    {
        return beans_usecurr;
    }

    public void setBeans_usecurr(int beans_usecurr)
    {
        this.beans_usecurr = beans_usecurr;
    }

    public int getBeans_idle()
    {
        return beans_idle;
    }

    public void setBeans_idle(int beans_idle)
    {
        this.beans_idle = beans_idle;
    }

    public int getDestroyed_count()
    {
        return destroyed_count;
    }

    public void setDestroyed_count(int destroyed_count)
    {
        this.destroyed_count = destroyed_count;
    }

    public int getMiss_count()
    {
        return miss_count;
    }

    public void setMiss_count(int miss_count)
    {
        this.miss_count = miss_count;
    }

    public int getWaiter_current()
    {
        return waiter_current;
    }

    public void setWaiter_current(int waiter_current)
    {
        this.waiter_current = waiter_current;
    }

    public int getWaiter_total()
    {
        return waiter_total;
    }

    public void setWaiter_total(int waiter_total)
    {
        this.waiter_total = waiter_total;
    }

    public int getTran_commited()
    {
        return tran_commited;
    }

    public void setTran_commited(int tran_commited)
    {
        this.tran_commited = tran_commited;
    }

    public int getTran_rollback()
    {
        return tran_rollback;
    }

    public void setTran_rollback(int tran_rollback)
    {
        this.tran_rollback = tran_rollback;
    }

    public int getTran_timeout()
    {
        return tran_timeout;
    }

    public void setTran_timeout(int tran_timeout)
    {
        this.tran_timeout = tran_timeout;
    }

}
