package jetsennet.jnmp.entity;

/**
 * @author
 */
public class SqlServerAttribEntity
{

    private int obj_id;
    private long coll_time;
    private int total_memory;
    private int sqlcache_memory;
    private int lock_memory;
    private int opt_memory;
    private int conn_memory;
    private int workspace_memory;
    private int pending_memory;
    private int outstanding_memory;
    private int cache_hit_buffer;
    private int page_lookups;
    private int page_reads;
    private int page_writes;
    private int page_total;
    private int page_database;
    private int page_free;
    private int conn_time;
    private int user_conn;
    private int logins;
    private int logouts;
    private int cache_hit;
    private int cache_uses;
    private int cache_pages;
    private int cache_count;
    private int lock_request;
    private int lock_wait;
    private int lock_timeout;
    private int lock_dead;
    private int lock_waittime;
    private int full_scan;
    private int range_scan;
    private int probe_scan;

    /**
     * 构造方法
     */
    public SqlServerAttribEntity()
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

    public int getCache_hit_buffer()
    {
        return cache_hit_buffer;
    }

    public void setCache_hit_buffer(int cache_hit_buffer)
    {
        this.cache_hit_buffer = cache_hit_buffer;
    }

    public int getPage_lookups()
    {
        return page_lookups;
    }

    public void setPage_lookups(int page_lookups)
    {
        this.page_lookups = page_lookups;
    }

    public int getPage_reads()
    {
        return page_reads;
    }

    public void setPage_reads(int page_reads)
    {
        this.page_reads = page_reads;
    }

    public int getPage_writes()
    {
        return page_writes;
    }

    public void setPage_writes(int page_writes)
    {
        this.page_writes = page_writes;
    }

    public int getPage_total()
    {
        return page_total;
    }

    public void setPage_total(int page_total)
    {
        this.page_total = page_total;
    }

    public int getPage_database()
    {
        return page_database;
    }

    public void setPage_database(int page_database)
    {
        this.page_database = page_database;
    }

    public int getPage_free()
    {
        return page_free;
    }

    public void setPage_free(int page_free)
    {
        this.page_free = page_free;
    }

    public int getConn_time()
    {
        return conn_time;
    }

    public void setConn_time(int conn_time)
    {
        this.conn_time = conn_time;
    }

    public int getUser_conn()
    {
        return user_conn;
    }

    public void setUser_conn(int user_conn)
    {
        this.user_conn = user_conn;
    }

    public int getLogins()
    {
        return logins;
    }

    public void setLogins(int logins)
    {
        this.logins = logins;
    }

    public int getLogouts()
    {
        return logouts;
    }

    public void setLogouts(int logouts)
    {
        this.logouts = logouts;
    }

    public int getCache_hit()
    {
        return cache_hit;
    }

    public void setCache_hit(int cache_hit)
    {
        this.cache_hit = cache_hit;
    }

    public int getCache_uses()
    {
        return cache_uses;
    }

    public void setCache_uses(int cache_uses)
    {
        this.cache_uses = cache_uses;
    }

    public int getCache_pages()
    {
        return cache_pages;
    }

    public void setCache_pages(int cache_pages)
    {
        this.cache_pages = cache_pages;
    }

    public int getCache_count()
    {
        return cache_count;
    }

    public void setCache_count(int cache_count)
    {
        this.cache_count = cache_count;
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

    public int getFull_scan()
    {
        return full_scan;
    }

    public void setFull_scan(int full_scan)
    {
        this.full_scan = full_scan;
    }

    public int getRange_scan()
    {
        return range_scan;
    }

    public void setRange_scan(int range_scan)
    {
        this.range_scan = range_scan;
    }

    public int getProbe_scan()
    {
        return probe_scan;
    }

    public void setProbe_scan(int probe_scan)
    {
        this.probe_scan = probe_scan;
    }
}
