package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @autho
 */
@Table(name = "NMP_SQLSERVBUFFERDATA")
public class SqlServBufferDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CACHE_HIT")
    private int cache_hit;
    @Column(name = "PAGE_LOOKUPS")
    private int page_lookups;
    @Column(name = "PAGE_READS")
    private int page_reads;
    @Column(name = "PAGE_WRITES")
    private int page_writes;
    @Column(name = "PAGE_TOTAL")
    private int page_total;
    @Column(name = "PAGE_DATABASE")
    private int page_database;
    @Column(name = "PAGE_FREE")
    private int page_free;

    /**
     * 构造方法
     */
    public SqlServBufferDataEntity()
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

    public int getCache_hit()
    {
        return cache_hit;
    }

    public void setCache_hit(int cache_hit)
    {
        this.cache_hit = cache_hit;
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

}
