package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author
 */
@Table(name = "NMP_ORACLESESSGATHERDATA")
public class OracleSessionGatherDataEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "MACHINE")
    private String machine;
    @Column(name = "PROGRAM")
    private String program;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "COUNT_NUM")
    private int count_num;

    /**
     * 构造方法
     */
    public OracleSessionGatherDataEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param machine 参数
     * @param program 参数
     * @param status 参数
     * @param count_num 参数
     */
    public OracleSessionGatherDataEntity(int obj_id, long coll_time, String machine, String program, String status, int count_num)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.machine = machine;
        this.program = program;
        this.status = status;
        this.count_num = count_num;
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

    public String getMachine()
    {
        return machine;
    }

    public void setMachine(String machine)
    {
        this.machine = machine;
    }

    public String getProgram()
    {
        return program;
    }

    public void setProgram(String program)
    {
        this.program = program;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getCount_num()
    {
        return count_num;
    }

    public void setCount_num(int count_num)
    {
        this.count_num = count_num;
    }
}
