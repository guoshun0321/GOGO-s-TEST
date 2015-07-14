package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author
 */
@Table(name = "NMP_ORACLETABLESPACEINFO")
public class OracleTableSpaceInfoEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "SPACE_NAME")
    private String space_name;
    @Column(name = "SUM_SPACE")
    private int sum_space;
    @Column(name = "SUM_BLOCKS")
    private int sum_blocks;
    @Column(name = "USED_SPACE")
    private int used_space;
    @Column(name = "USED_RATE")
    private int used_rate;
    @Column(name = "FREE_SPACE")
    private int free_space;

    /**
     * 构造方法
     */
    public OracleTableSpaceInfoEntity()
    {

    }

    /**
     * 构造方法
     * @param obj_id 参数
     * @param coll_time 参数
     * @param space_name 参数
     * @param sum_space 参数
     * @param sum_blocks 参数
     * @param used_space 参数
     * @param used_rate 参数
     * @param free_space 参数
     */
    public OracleTableSpaceInfoEntity(int obj_id, long coll_time, String space_name, int sum_space, int sum_blocks, int used_space, int used_rate,
            int free_space)
    {
        this.obj_id = obj_id;
        this.coll_time = coll_time;
        this.space_name = space_name;
        this.sum_space = sum_space;
        this.sum_blocks = sum_blocks;
        this.used_space = used_space;
        this.used_rate = used_rate;
        this.free_space = free_space;
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

    public String getSpace_name()
    {
        return space_name;
    }

    public void setSpace_name(String space_name)
    {
        this.space_name = space_name;
    }

    public int getSum_space()
    {
        return sum_space;
    }

    public void setSum_space(int sum_space)
    {
        this.sum_space = sum_space;
    }

    public int getSum_blocks()
    {
        return sum_blocks;
    }

    public void setSum_blocks(int sum_blocks)
    {
        this.sum_blocks = sum_blocks;
    }

    public int getUsed_space()
    {
        return used_space;
    }

    public void setUsed_space(int used_space)
    {
        this.used_space = used_space;
    }

    public int getUsed_rate()
    {
        return used_rate;
    }

    public void setUsed_rate(int used_rate)
    {
        this.used_rate = used_rate;
    }

    public int getFree_space()
    {
        return free_space;
    }

    public void setFree_space(int free_space)
    {
        this.free_space = free_space;
    }

}
