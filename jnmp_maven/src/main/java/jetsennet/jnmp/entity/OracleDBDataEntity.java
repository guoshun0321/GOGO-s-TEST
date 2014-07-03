package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author？
 */
@Table(name = "NMP_ORACLEDBDATA")
public class OracleDBDataEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CONN_TIMELEN")
    private int conn_timelen;
    @Column(name = "CON_NUM")
    private int con_num;
    @Column(name = "ACTIVECON_NUM")
    private int activecon_num;
    @Column(name = "LOCK_NUM")
    private int lock_num;
    @Column(name = "DEADLOCK_NUM")
    private int deadlock_num;
    @Column(name = "CACHE_HIT")
    private int cache_hit;
    @Column(name = "DIC_HIT")
    private int dic_hit;
    @Column(name = "LABCACHE_HIT")
    private int labcache_hit;
    @Column(name = "CURSOR_NUM")
    private int cursor_num;
    @Column(name = "TRAN_NUM")
    private int tran_num;

    /**
     * 构造方法
     */
    public OracleDBDataEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param conn_timelen 参数
     * @param con_num 参数
     * @param activecon_num 参数
     * @param lock_num 参数
     * @param deadlock_num 参数
     * @param cache_hit 参数
     * @param dic_hit 参数
     * @param labcache_hit 参数
     * @param cursor_num 参数
     * @param tran_num 参数
     */
    public OracleDBDataEntity(int obj_id, long coll_time, int conn_timelen, int con_num, int activecon_num, int lock_num, int deadlock_num,
            int cache_hit, int dic_hit, int labcache_hit, int cursor_num, int tran_num)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.conn_timelen = conn_timelen;
        this.con_num = con_num;
        this.activecon_num = activecon_num;
        this.lock_num = lock_num;
        this.deadlock_num = deadlock_num;
        this.cache_hit = cache_hit;
        this.dic_hit = dic_hit;
        this.labcache_hit = labcache_hit;
        this.cursor_num = cursor_num;
        this.tran_num = tran_num;
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

    public int getConn_timelen()
    {
        return conn_timelen;
    }

    public void setConn_timelen(int conn_timelen)
    {
        this.conn_timelen = conn_timelen;
    }

    public int getCon_num()
    {
        return con_num;
    }

    public void setCon_num(int con_num)
    {
        this.con_num = con_num;
    }

    public int getActivecon_num()
    {
        return activecon_num;
    }

    public void setActivecon_num(int activecon_num)
    {
        this.activecon_num = activecon_num;
    }

    public int getLock_num()
    {
        return lock_num;
    }

    public void setLock_num(int lock_num)
    {
        this.lock_num = lock_num;
    }

    public int getDeadlock_num()
    {
        return deadlock_num;
    }

    public void setDeadlock_num(int deadlock_num)
    {
        this.deadlock_num = deadlock_num;
    }

    public int getCache_hit()
    {
        return cache_hit;
    }

    public void setCache_hit(int cache_hit)
    {
        this.cache_hit = cache_hit;
    }

    public int getDic_hit()
    {
        return dic_hit;
    }

    public void setDic_hit(int dic_hit)
    {
        this.dic_hit = dic_hit;
    }

    public int getLabcache_hit()
    {
        return labcache_hit;
    }

    public void setLabcache_hit(int labcache_hit)
    {
        this.labcache_hit = labcache_hit;
    }

    public int getCursor_num()
    {
        return cursor_num;
    }

    public void setCursor_num(int cursor_num)
    {
        this.cursor_num = cursor_num;
    }

    public int getTran_num()
    {
        return tran_num;
    }

    public void setTran_num(int tran_num)
    {
        this.tran_num = tran_num;
    }

}
