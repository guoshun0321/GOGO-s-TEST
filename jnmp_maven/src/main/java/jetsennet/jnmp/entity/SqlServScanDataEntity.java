package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_SQLSERVSCANDATA")
public class SqlServScanDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "FULL_SCAN")
    private int full_scan;
    @Column(name = "RANGE_SCAN")
    private int range_scan;
    @Column(name = "PROBE_SCAN")
    private int probe_scan;

    /**
     * 构造方法
     */
    public SqlServScanDataEntity()
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
