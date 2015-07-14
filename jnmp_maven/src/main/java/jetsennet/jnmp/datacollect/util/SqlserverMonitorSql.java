package jetsennet.jnmp.datacollect.util;

/**
 * 用于SQL SERVER查询的SQL语句 Microsoft SQL Server 中的 Memory Manager 对象提供了监视总体的服务器内存使用情况的计数器。 监视总体的服务器内存使用情况，以估计用户活动和资源使用，有助于查明性能瓶颈。监视 SQL Server
 * 的实例使用的内存有助于确定： 1.瓶颈的存在是否是因为物理内存不足以存储缓存中被频繁访问的数据。如果内存不足，SQL Server 必须从磁盘检索数据。 2.是否可以通过添加更多内存或使更多内存可用于数据缓存或 SQL Server 内部结构来改善查询性能。 Buffer Manager
 * 对象提供了计数器，用于监视 SQL Server 如何使用： 1.内存存储数据页、内部数据结构和过程缓存。 2.计数器监视 SQL Server 读取和写入数据库页时的物理 I/O。 监视 SQL Server 使用的内存和计数器有助于确定： 1.是否存在物理内存不足的瓶颈。如果 SQL
 * Server 无法将经常访问的数据存储在缓存中，则必须从磁盘检索数据。 2.是否可以通过添加更多内存或增加数据缓存或 SQL Server 内部结构的可用内存来提高查询性能。 3.SQL Server 需要从磁盘读取数据的频率。与其他操作（例如内存访问）相比，物理 I/O
 * 会消耗大量时间。尽可能减少物理 I/O 可以提高查询性能。
 */
public class SqlserverMonitorSql
{

    // 带“/分”的都要按时间差来取值，(end_value-start_value)/(end_time-start_time)
    /**
     * 从缓冲池提交的内存(KB)。注意，这不是 SQL Server 使用的总内存。
     */
    public static final String SQLSERVER_TOTAL_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Total Server Memory (KB)'";
    /**
     * 服务器正在用于动态 SQL 高速缓存的动态内存总数
     */
    public static final String SQLSERVER_SQLCACHE_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'SQL Cache Memory (KB)'";
    /**
     * 服务器用于锁的动态内存总量
     */
    public static final String SQLSERVER_LOCK_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Lock Memory (KB)'";
    /**
     * 服务器正在用于查询优化的动态内存总数
     */
    public static final String SQLSERVER_OPTIMIZER_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Optimizer Memory (KB)'";
    /**
     * 服务器正在用来维护连接的动态内存总量
     */
    public static final String SQLSERVER_CONNECTION_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Connection Memory (KB)'";
    /**
     * 当前给予执行哈希、排序、大容量复制和索引创建操作等进程的内存总量
     */
    public static final String SQLSERVER_GRANTEDWORKSPACE_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Granted Workspace Memory (KB)'";
    /**
     * 等待工作空间内存授权的进程总数
     */
    public static final String SQLSERVER_GRANTSPENDING_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Memory Grants Pending'";
    /**
     * 成功获得工作空间内存授权的进程总数
     */
    public static final String SQLSERVER_GRANTSOUTSTANDING_MEMORY_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Memory Grants Outstanding'";
    /**
     * 缓冲区击中率 Buffer cache hit ratio 和 Buffer cache hit ratio base 在缓冲区高速缓存中找到而不需要从磁盘中读取的页的百分比。 该比率是缓存命中总次数与过去几千页访问以来的缓存查找总次数之比。经过很长时间后，该比率的变化很小。
     * 由于从缓存中读取数据比从磁盘中读取数据的开销小得多，一般希望该比率高一些。 通常，可以通过增加 SQL Server 的可用内存量来提高缓冲区高速缓存命中率。
     */
    public static final String SQLSERVER_BUFFERCACHE_HIT_SQL =
        "select (a.cntr_value*100)/(b.cntr_value) from sys.dm_os_performance_counters a,sys.dm_os_performance_counters b "
            + " where a.counter_name ='Buffer cache hit ratio' and b.counter_name ='Buffer cache hit ratio base'";
    /**
     * 每秒要求在缓冲池中查找页的请求数 查找页数/秒
     */
    public static final String SQLSERVER_LOOKUPS_PAGE_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Page lookups/sec'";
    /**
     * 每秒发出的物理数据库页读取数。 此统计信息显示的是所有数据库间的物理页读取总数。 由于物理 I/O 的开销大，可以通过使用更大的数据缓存、智能索引、更有效的查询或更改数据库设计等方法，将开销降到最低。 已读页数/秒
     */
    public static final String SQLSERVER_READS_PAGE_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Page reads/sec'";
    /**
     * 每秒执行的物理数据库页写入数 已写页数/秒
     */
    public static final String SQLSERVER_WRITES_PAGE_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Page writes/sec'";
    /**
     * 缓冲池中的页数（包括数据库页、可用页和被盗页） 总页数
     */
    public static final String SQLSERVER_TOTAL_PAGE_SQL = "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Total pages'";
    /**
     * 缓冲池中有数据库内容的页数 数据库页
     */
    public static final String SQLSERVER_DATABASE_PAGE_SQL =
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Database pages'";
    /**
     * 所有可用列表的总页数
     */
    public static final String SQLSERVER_FREE_PAGE_SQL = // 空闲页
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Free pages' and object_name='SQLServer:Buffer Manager'";
    /**
     * 当前与 SQL Server 连接的用户数
     */
    public static final String SQLSERVER_USER_CONNECTION_SQL = // 用户连接
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'User Connections'";
    /**
     * 每秒启动的登录数。 注意： 此数包括连接池中的登录数。 登录/秒
     */
    public static final String SQLSERVER_LOGINS_SQL = "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Logins/sec'";
    /**
     * 每秒开始的注销操作总数 退出/秒
     */
    public static final String SQLSERVER_LOGOUTS_SQL = "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Logouts/sec'";
    public static final String SQLSERVER_CACHE_HIT_SQL = // 缓存击中率
        "select (a.cntr_value*100)/(b.cntr_value) from sys.dm_os_performance_counters a,sys.dm_os_performance_counters b "
            + "where a.counter_name ='Cache hit ratio' and b.counter_name ='Cache hit ratio base' "
            + "and a.instance_name='_Total' and b.instance_name='_Total' "
            + "and a.object_name='SQLServer:Plan Cache' and b.object_name='SQLServer:Plan Cache'";
    /**
     * 每种缓存的游标的使用次数,_Total为所有游标
     */
    public static final String SQLSERVER_CACHE_USE_SQL = // 使用的游标缓存/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Cursor Cache Use Counts/sec' and instance_name='_Total'";
    /**
     * 高速缓存对象所使用的 8 (KB) 页的数目
     */
    public static final String SQLSERVER_CACHE_PAGE_SQL = // 缓存页
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Cache Pages' and instance_name='_Total'";
    /**
     * 高速缓存中高速缓存的对象数
     */
    public static final String SQLSERVER_CACHE_COUNT_SQL = // 缓存数
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Cache Object Counts' and instance_name='_Total'";
    /**
     * 锁管理器每秒请求的新锁和锁转换数
     */
    public static final String SQLSERVER_LOCK_REQUEST_SQL = // 锁请求/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Lock Requests/sec' and instance_name='_Total'";
    /**
     * 每秒要求调用者等待的锁请求数
     */
    public static final String SQLSERVER_LOCK_WAIT_SQL = // 锁等待/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Lock Waits/sec' and instance_name='_Total'";
    /**
     * 每秒超时的锁请求数，但不包括对 NOWAIT 锁的请求
     */
    public static final String SQLSERVER_LOCK_TIMEOUT_SQL = // 锁超时/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Lock Timeouts (timeout > 0)/sec' and instance_name='_Total'";
    /**
     * 每秒导致死锁的锁请求数
     */
    public static final String SQLSERVER_DEADLOCK_NUMBER_SQL = // 死锁数/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Number of Deadlocks/sec' and instance_name='_Total'";
    /**
     * 锁在最后一秒内的总等待时间（毫秒）。
     */
    public static final String SQLSERVER_LOCK_WAITTIME_SQL = // 平均锁等待时间
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Lock Wait Time (ms)' and instance_name='_Total'";
    /**
     * 每秒不受限制的完全扫描数。这些扫描可以是基表扫描，也可以是全文索引扫描
     */
    public static final String SQLSERVER_FULL_SCANS_SQL = // 完全扫描/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Full Scans/sec'";
    /**
     * 每秒通过索引进行的限定范围的扫描数
     */
    public static final String SQLSERVER_RANGE_SCANS_SQL = // 范围扫描/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Range Scans/sec'";
    /**
     * 每秒内用于直接在索引或基本表中查找最多一个限定行的探测扫描数
     */
    public static final String SQLSERVER_PROBE_SCANS_SQL = // 探针扫描/秒
        "select cntr_value from sys.dm_os_performance_counters where counter_name = 'Probe Scans/sec'";
}
