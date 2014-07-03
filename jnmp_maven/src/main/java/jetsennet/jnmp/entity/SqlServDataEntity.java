package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author
 */
@Table(name = "NMP_SQLSERVDATA")
public class SqlServDataEntity
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "DATABASE_NAME")
    private String database_name;
    @Column(name = "DATABASE_SIZE")
    private int size;
    @Column(name = "LOG_USED")
    private int log_used;

    /**
     * 构造方法
     */
    public SqlServDataEntity()
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

    public String getDatabase_name()
    {
        return database_name;
    }

    public void setDatabase_name(String database_name)
    {
        this.database_name = database_name;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getLog_used()
    {
        return log_used;
    }

    public void setLog_used(int log_used)
    {
        this.log_used = log_used;
    }

}
