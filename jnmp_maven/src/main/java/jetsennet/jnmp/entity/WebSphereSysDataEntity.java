package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_WEBSPHERESYSDATA")
public class WebSphereSysDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "MEMORY_MAX")
    private int memory_max;
    @Column(name = "MEMORY_FREE")
    private int memory_free;
    @Column(name = "HEAP_SIZE")
    private int heap_size;

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

    public int getMemory_max()
    {
        return memory_max;
    }

    public void setMemory_max(int memory_max)
    {
        this.memory_max = memory_max;
    }

    public int getMemory_free()
    {
        return memory_free;
    }

    public void setMemory_free(int memory_free)
    {
        this.memory_free = memory_free;
    }

    public int getHeap_size()
    {
        return heap_size;
    }

    public void setHeap_size(int heap_size)
    {
        this.heap_size = heap_size;
    }

}
