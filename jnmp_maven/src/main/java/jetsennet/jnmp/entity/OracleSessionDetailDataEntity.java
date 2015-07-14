package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author ？
 */
@Table(name = "NMP_ORACLESESSDETAILDATA")
public class OracleSessionDetailDataEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "SESSION_ID")
    private int session_id;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "MACHINE")
    private String machine;
    @Column(name = "PHYSICAL_READS")
    private int physical_reads;
    @Column(name = "LOGICAL_READS")
    private int logical_reads;
    @Column(name = "CACHE_HIT")
    private int cache_hit;
    @Column(name = "CPU_VALUE")
    private int cpu_value;

    /**
     * 构造方法
     */
    public OracleSessionDetailDataEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param session_id 参数
     * @param status 参数
     * @param username 参数
     * @param machine 参数
     * @param physical_reads 参数
     * @param logical_reads 参数
     * @param cache_hit 参数
     * @param cpu_value 参数
     */
    public OracleSessionDetailDataEntity(int obj_id, long coll_time, int session_id, String status, String username, String machine,
            int physical_reads, int logical_reads, int cache_hit, int cpu_value)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.session_id = session_id;
        this.status = status;
        this.username = username;
        this.machine = machine;
        this.physical_reads = physical_reads;
        this.logical_reads = logical_reads;
        this.cache_hit = cache_hit;
        this.cpu_value = cpu_value;
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

    public int getSession_id()
    {
        return session_id;
    }

    public void setSession_id(int session_id)
    {
        this.session_id = session_id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getMachine()
    {
        return machine;
    }

    public void setMachine(String machine)
    {
        this.machine = machine;
    }

    public int getPhysical_reads()
    {
        return physical_reads;
    }

    public void setPhysical_reads(int physical_reads)
    {
        this.physical_reads = physical_reads;
    }

    public int getLogical_reads()
    {
        return logical_reads;
    }

    public void setLogical_reads(int logical_reads)
    {
        this.logical_reads = logical_reads;
    }

    public int getCache_hit()
    {
        return cache_hit;
    }

    public void setCache_hit(int cache_hit)
    {
        this.cache_hit = cache_hit;
    }

    public int getCpu_value()
    {
        return cpu_value;
    }

    public void setCpu_value(int cpu_value)
    {
        this.cpu_value = cpu_value;
    }

}
