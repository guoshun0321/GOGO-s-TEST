package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_SQLSERVCACHEDATA")
public class SqlServCacheDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CACHE_HIT")
    private int cache_hit;
    @Column(name = "CACHE_USES")
    private int cache_uses;
    @Column(name = "CACHE_PAGES")
    private int cache_pages;
    @Column(name = "CACHE_COUNT")
    private int cache_count;

    /**
     * 构造方法
     */
    public SqlServCacheDataEntity()
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

}
