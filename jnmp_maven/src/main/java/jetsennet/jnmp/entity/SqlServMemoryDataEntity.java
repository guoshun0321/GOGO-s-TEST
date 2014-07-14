package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author liwei
 */
@Table(name = "NMP_SQLSERVMEMORYDATA")
public class SqlServMemoryDataEntity
{

    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "TOTAL_MEMORY")
    private int total_memory;
    @Column(name = "SQLCACHE_MEMORY")
    private int sqlcache_memory;
    @Column(name = "LOCK_MEMORY")
    private int lock_memory;
    @Column(name = "OPT_MEMORY")
    private int opt_memory;
    @Column(name = "CONN_MEMORY")
    private int conn_memory;
    @Column(name = "WORKSPACE_MEMORY")
    private int workspace_memory;
    @Column(name = "PENDING_MEMORY")
    private int pending_memory;
    @Column(name = "OUTSTANDING_MEMORY")
    private int outstanding_memory;

    /**
     * 构造方法
     */
    public SqlServMemoryDataEntity()
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

    public int getTotal_memory()
    {
        return total_memory;
    }

    public void setTotal_memory(int total_memory)
    {
        this.total_memory = total_memory;
    }

    public int getSqlcache_memory()
    {
        return sqlcache_memory;
    }

    public void setSqlcache_memory(int sqlcache_memory)
    {
        this.sqlcache_memory = sqlcache_memory;
    }

    public int getLock_memory()
    {
        return lock_memory;
    }

    public void setLock_memory(int lock_memory)
    {
        this.lock_memory = lock_memory;
    }

    public int getOpt_memory()
    {
        return opt_memory;
    }

    public void setOpt_memory(int opt_memory)
    {
        this.opt_memory = opt_memory;
    }

    public int getConn_memory()
    {
        return conn_memory;
    }

    public void setConn_memory(int conn_memory)
    {
        this.conn_memory = conn_memory;
    }

    public int getWorkspace_memory()
    {
        return workspace_memory;
    }

    public void setWorkspace_memory(int workspace_memory)
    {
        this.workspace_memory = workspace_memory;
    }

    public int getPending_memory()
    {
        return pending_memory;
    }

    public void setPending_memory(int pending_memory)
    {
        this.pending_memory = pending_memory;
    }

    public int getOutstanding_memory()
    {
        return outstanding_memory;
    }

    public void setOutstanding_memory(int outstanding_memory)
    {
        this.outstanding_memory = outstanding_memory;
    }
}
