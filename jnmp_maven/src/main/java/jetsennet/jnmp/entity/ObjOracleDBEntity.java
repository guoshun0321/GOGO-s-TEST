package jetsennet.jnmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.sqlclient.ModelBase;

/**
 * @author ？
 */
@Table(name = "NMP_OBJORACLEDB")
public class ObjOracleDBEntity extends ModelBase
{
    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "INSTANCE_NAME")
    private String instance_name;
    @Column(name = "HOST_NAME")
    private String host_name;
    @Column(name = "VERSION")
    private String version;
    @Column(name = "STARTUP_TIME")
    private Date startup_time;
    @Column(name = "CREATE_TIME")
    private Date create_time;
    @Column(name = "RESETLOGS_TIME")
    private Date resetlogs_time;
    @Column(name = "LOG_MODE")
    private String log_mode;
    @Column(name = "OPEN_MODE")
    private String open_mode;
    @Column(name = "PLATFORM_NAME")
    private String platform_name;
    @Column(name = "SGAFREE_MEMORY")
    private int sgafree_memory;
    @Column(name = "FIX_SIZE")
    private int fix_size;
    @Column(name = "SHARED_SIZE")
    private int shared_size;
    @Column(name = "CACHE_SIZE")
    private int cache_size;
    @Column(name = "REDOLOG_SIZE")
    private int redolog_size;

    /**
     * 构造函数
     * @param obj_id 参数
     * @param instance_name 参数
     * @param host_name 参数
     * @param version 参数
     * @param startup_time 参数
     * @param create_time 参数
     * @param resetlogs_time 参数
     * @param log_mode 参数
     * @param open_mode 参数
     * @param platform_name 参数
     * @param sgafree_memory 参数
     * @param fix_size 参数
     * @param shared_size 参数
     * @param cache_size 参数
     * @param redolog_size 参数
     */
    public ObjOracleDBEntity(int obj_id, String instance_name, String host_name, String version, Date startup_time, Date create_time,
            Date resetlogs_time, String log_mode, String open_mode, String platform_name, int sgafree_memory, int fix_size, int shared_size,
            int cache_size, int redolog_size)
    {
        this.obj_id = obj_id;
        this.instance_name = instance_name;
        this.host_name = host_name;
        this.version = version;
        this.startup_time = startup_time;
        this.create_time = create_time;
        this.resetlogs_time = resetlogs_time;
        this.log_mode = log_mode;
        this.open_mode = open_mode;
        this.platform_name = platform_name;
        this.sgafree_memory = sgafree_memory;
        this.fix_size = fix_size;
        this.shared_size = shared_size;
        this.cache_size = cache_size;
        this.redolog_size = redolog_size;
    }

    /**
     * 构造方法
     */
    public ObjOracleDBEntity()
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

    public String getInstance_name()
    {
        return instance_name;
    }

    public void setInstance_name(String instance_name)
    {
        this.instance_name = instance_name;
    }

    public String getHost_name()
    {
        return host_name;
    }

    public void setHost_name(String host_name)
    {
        this.host_name = host_name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Date getStartup_time()
    {
        return startup_time;
    }

    public void setStartup_time(Date startup_time)
    {
        this.startup_time = startup_time;
    }

    public Date getCreate_time()
    {
        return create_time;
    }

    public void setCreate_time(Date create_time)
    {
        this.create_time = create_time;
    }

    public Date getResetlogs_time()
    {
        return resetlogs_time;
    }

    public void setResetlogs_time(Date resetlogs_time)
    {
        this.resetlogs_time = resetlogs_time;
    }

    public String getLog_mode()
    {
        return log_mode;
    }

    public void setLog_mode(String log_mode)
    {
        this.log_mode = log_mode;
    }

    public String getOpen_mode()
    {
        return open_mode;
    }

    public void setOpen_mode(String open_mode)
    {
        this.open_mode = open_mode;
    }

    public String getPlatform_name()
    {
        return platform_name;
    }

    public void setPlatform_name(String platform_name)
    {
        this.platform_name = platform_name;
    }

    public int getSgafree_memory()
    {
        return sgafree_memory;
    }

    public void setSgafree_memory(int sgafree_memory)
    {
        this.sgafree_memory = sgafree_memory;
    }

    public int getFix_size()
    {
        return fix_size;
    }

    public void setFix_size(int fix_size)
    {
        this.fix_size = fix_size;
    }

    public int getShared_size()
    {
        return shared_size;
    }

    public void setShared_size(int shared_size)
    {
        this.shared_size = shared_size;
    }

    public int getCache_size()
    {
        return cache_size;
    }

    public void setCache_size(int cache_size)
    {
        this.cache_size = cache_size;
    }

    public int getRedolog_size()
    {
        return redolog_size;
    }

    public void setRedolog_size(int redolog_size)
    {
        this.redolog_size = redolog_size;
    }

}
