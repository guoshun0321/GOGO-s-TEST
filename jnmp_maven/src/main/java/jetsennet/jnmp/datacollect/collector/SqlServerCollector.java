package jetsennet.jnmp.datacollect.collector;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.UncheckedSQLException;
import jetsennet.jnmp.datacollect.util.SqlserverMonitorSql;
import jetsennet.jnmp.entity.SqlServBufferDataEntity;
import jetsennet.jnmp.entity.SqlServCacheDataEntity;
import jetsennet.jnmp.entity.SqlServConnDataEntity;
import jetsennet.jnmp.entity.SqlServDataEntity;
import jetsennet.jnmp.entity.SqlServLockDataEntity;
import jetsennet.jnmp.entity.SqlServMemoryDataEntity;
import jetsennet.jnmp.entity.SqlServScanDataEntity;
import jetsennet.jnmp.entity.SqlServerAttribEntity;

/**
 * @author lianghongjie Sqlserver采集器
 */
public class SqlServerCollector extends AbsDBCollector
{

    private static final Logger logger = Logger.getLogger(SqlServerCollector.class);
    private static Map<String, SqlServerAttribEntity> map = new ConcurrentHashMap<String, SqlServerAttribEntity>();

    /*
     * (non-Javadoc)
     * @see jetsennet.jnmp.datacollect.coll.AbsDBCollector#getDriverClassName()
     */
    protected String getDriverClassName()
    {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    /*
     * (non-Javadoc)
     * @see jetsennet.jnmp.datacollect.coll.AbsDBCollector#getConnectionURL()
     */
    protected String getConnectionURL()
    {
        return "jdbc:sqlserver://" + mo.getIpAddr() + ":" + mo.getIpPort() + ";DatabaseName=" + this.ensureDbName(mo.getField1());
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();

        SqlServerAttribEntity sqlserverAttrib = createSqlServerAttrib();
        sqlserverAttrib.setConn_time(connTime);
        SqlServerAttribEntity sqlserverAttribOld = getSqlServerAttrib(String.valueOf(mo.getObjId()));
        if (sqlserverAttribOld == null)
        {
            addSqlServerAttrib(String.valueOf(sqlserverAttrib.getObj_id()), sqlserverAttrib);
            return new HashMap<ObjAttribEntity, Object>();
        }
        if (sqlserverAttrib.getColl_time() - sqlserverAttribOld.getColl_time() > 1000 * 60 * 60 * 24)
        {
            addSqlServerAttrib(String.valueOf(sqlserverAttrib.getObj_id()), sqlserverAttrib);
            return new HashMap<ObjAttribEntity, Object>();
        }
        getMemoryData(sqlserverAttrib, sqlserverAttribOld);
        getBufferData(sqlserverAttrib, sqlserverAttribOld);
        getConnData(sqlserverAttrib, sqlserverAttribOld);
        getCacheData(sqlserverAttrib, sqlserverAttribOld);
        getLockedData(sqlserverAttrib, sqlserverAttribOld);
        getScanData(sqlserverAttrib, sqlserverAttribOld);
        addSqlServerAttrib(String.valueOf(sqlserverAttrib.getObj_id()), sqlserverAttrib);
        getSqlServData();

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        Map<Integer, ObjAttribEntity> idMap = toIdMap(objAttrLst);
        for (Object data : dataLst)
        {
            if (data instanceof SqlServMemoryDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServBufferDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServConnDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServCacheDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServLockDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServScanDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof SqlServDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    private SqlServerAttribEntity createSqlServerAttrib()
    {
        SqlServerAttribEntity sqlserverAttrib = new SqlServerAttribEntity();
        sqlserverAttrib.setObj_id(mo.getObjId());
        sqlserverAttrib.setColl_time(time.getTime());
        getSqlServerAttrib(sqlserverAttrib, "total_memory", SqlserverMonitorSql.SQLSERVER_TOTAL_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "sqlcache_memory", SqlserverMonitorSql.SQLSERVER_SQLCACHE_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "lock_memory", SqlserverMonitorSql.SQLSERVER_LOCK_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "opt_memory", SqlserverMonitorSql.SQLSERVER_OPTIMIZER_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "conn_memory", SqlserverMonitorSql.SQLSERVER_CONNECTION_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "workspace_memory", SqlserverMonitorSql.SQLSERVER_GRANTEDWORKSPACE_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "pending_memory", SqlserverMonitorSql.SQLSERVER_GRANTSPENDING_MEMORY_SQL);
        getSqlServerAttrib(sqlserverAttrib, "outstanding_memory", SqlserverMonitorSql.SQLSERVER_GRANTSOUTSTANDING_MEMORY_SQL);

        getSqlServerAttrib(sqlserverAttrib, "cache_hit_buffer", SqlserverMonitorSql.SQLSERVER_BUFFERCACHE_HIT_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_lookups", SqlserverMonitorSql.SQLSERVER_LOOKUPS_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_reads", SqlserverMonitorSql.SQLSERVER_READS_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_writes", SqlserverMonitorSql.SQLSERVER_WRITES_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_total", SqlserverMonitorSql.SQLSERVER_TOTAL_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_database", SqlserverMonitorSql.SQLSERVER_DATABASE_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "page_free", SqlserverMonitorSql.SQLSERVER_FREE_PAGE_SQL);

        // getSqlServerAttrib(sqlserverAttrib,"conn_time",SqlserverMonitorSql.SQLSERVER_USER_CONNECTION_SQL);
        getSqlServerAttrib(sqlserverAttrib, "user_conn", SqlserverMonitorSql.SQLSERVER_USER_CONNECTION_SQL);
        getSqlServerAttrib(sqlserverAttrib, "logins", SqlserverMonitorSql.SQLSERVER_LOGINS_SQL);
        getSqlServerAttrib(sqlserverAttrib, "logouts", SqlserverMonitorSql.SQLSERVER_LOGOUTS_SQL);

        getSqlServerAttrib(sqlserverAttrib, "cache_hit", SqlserverMonitorSql.SQLSERVER_CACHE_HIT_SQL);
        getSqlServerAttrib(sqlserverAttrib, "cache_uses", SqlserverMonitorSql.SQLSERVER_CACHE_USE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "cache_pages", SqlserverMonitorSql.SQLSERVER_CACHE_PAGE_SQL);
        getSqlServerAttrib(sqlserverAttrib, "cache_count", SqlserverMonitorSql.SQLSERVER_CACHE_COUNT_SQL);

        getSqlServerAttrib(sqlserverAttrib, "lock_request", SqlserverMonitorSql.SQLSERVER_LOCK_REQUEST_SQL);
        getSqlServerAttrib(sqlserverAttrib, "lock_wait", SqlserverMonitorSql.SQLSERVER_LOCK_WAIT_SQL);
        getSqlServerAttrib(sqlserverAttrib, "lock_timeout", SqlserverMonitorSql.SQLSERVER_LOCK_TIMEOUT_SQL);
        getSqlServerAttrib(sqlserverAttrib, "lock_dead", SqlserverMonitorSql.SQLSERVER_DEADLOCK_NUMBER_SQL);
        getSqlServerAttrib(sqlserverAttrib, "lock_waittime", SqlserverMonitorSql.SQLSERVER_LOCK_WAITTIME_SQL);

        getSqlServerAttrib(sqlserverAttrib, "full_scan", SqlserverMonitorSql.SQLSERVER_FULL_SCANS_SQL);
        getSqlServerAttrib(sqlserverAttrib, "range_scan", SqlserverMonitorSql.SQLSERVER_RANGE_SCANS_SQL);
        getSqlServerAttrib(sqlserverAttrib, "probe_scan", SqlserverMonitorSql.SQLSERVER_PROBE_SCANS_SQL);

        return sqlserverAttrib;
    }

    private void getSqlServerAttrib(SqlServerAttribEntity sqlserverAttrib, String field_name, String sql)
    {
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery(sql);
            if (rs.next())
            {
                setValue(sqlserverAttrib, field_name, rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException e)
        {
            logger.error(mo.getIpAddr() + ":" + mo.getIpPort() + "上的sqlserver对象采集数据有错,执行语句:" + sql);
        }
    }

    private void setValue(Object o, String field_name, int value)
    {
        try
        {
            Method method = o.getClass().getMethod("set" + field_name.substring(0, 1).toUpperCase() + field_name.substring(1), int.class);
            method.invoke(o, value);
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
    }

    /**
     * 获得sqlserver监控对象内存的信息 非表格数据
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getMemoryData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServMemoryDataEntity entity = new SqlServMemoryDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        entity.setTotal_memory(sqlserverAttrib.getTotal_memory());
        entity.setSqlcache_memory(sqlserverAttrib.getSqlcache_memory());
        entity.setLock_memory(sqlserverAttrib.getLock_memory());
        entity.setOpt_memory(sqlserverAttrib.getOpt_memory());
        entity.setConn_memory(sqlserverAttrib.getConn_memory());
        entity.setWorkspace_memory(sqlserverAttrib.getWorkspace_memory());
        entity.setPending_memory(sqlserverAttrib.getPending_memory());
        entity.setOutstanding_memory(sqlserverAttrib.getOutstanding_memory());
        try
        {
            dataLst.add(entity);
        }
        catch (UncheckedSQLException e)
        {
            logger.error(e);
        }
    }

    /**
     * 获得sqlserver监控对象缓冲区管理统计数据信息 非表格数据
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getBufferData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServBufferDataEntity entity = new SqlServBufferDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        entity.setCache_hit(sqlserverAttrib.getCache_hit_buffer());
        if ((sqlserverAttrib.getPage_lookups() - sqlserverAttribOld.getPage_lookups()) > 0)
        {
            entity.setPage_lookups((int) ((sqlserverAttrib.getPage_lookups() - sqlserverAttribOld.getPage_lookups()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getPage_reads() - sqlserverAttribOld.getPage_reads()) > 0)
        {
            entity.setPage_reads((int) ((sqlserverAttrib.getPage_reads() - sqlserverAttribOld.getPage_reads()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getPage_writes() - sqlserverAttribOld.getPage_writes()) > 0)
        {
            entity.setPage_writes((int) ((sqlserverAttrib.getPage_writes() - sqlserverAttribOld.getPage_writes()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        entity.setPage_total(sqlserverAttrib.getPage_total());
        entity.setPage_database(sqlserverAttrib.getPage_database());
        entity.setPage_free(sqlserverAttrib.getPage_free());
        try
        {
            dataLst.add(entity);
        }
        catch (UncheckedSQLException e)
        {
            logger.error(e);
        }
    }

    /**
     * 获得sqlserver监控对象连接统计数据信息 非表格数据
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getConnData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServConnDataEntity entity = new SqlServConnDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        entity.setConn_time(sqlserverAttrib.getConn_time());
        entity.setUser_conn(sqlserverAttrib.getUser_conn());
        if ((sqlserverAttrib.getLogins() - sqlserverAttribOld.getLogins()) > 0)
        {
            entity
                .setLogins((int) ((sqlserverAttrib.getLogins() - sqlserverAttribOld.getLogins()) * 1000l / (sqlserverAttrib.getColl_time() - sqlserverAttribOld
                    .getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getLogouts() - sqlserverAttribOld.getLogouts()) > 0)
        {
            entity
                .setLogouts((int) ((sqlserverAttrib.getLogouts() - sqlserverAttribOld.getLogouts()) * 1000l / (sqlserverAttrib.getColl_time() - sqlserverAttribOld
                    .getColl_time()))); // 单位:/分
        }
        try
        {
            dataLst.add(entity);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }

    /**
     * 获得sqlserver监控对象缓存明细数据信息 非表格数据
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getCacheData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServCacheDataEntity entity = new SqlServCacheDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        entity.setCache_hit(sqlserverAttrib.getCache_hit());
        if ((sqlserverAttrib.getCache_uses() - sqlserverAttribOld.getCache_uses()) > 0)
        {
            entity.setCache_uses((int) ((sqlserverAttrib.getCache_uses() - sqlserverAttribOld.getCache_uses()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        entity.setCache_pages(sqlserverAttrib.getCache_pages());
        entity.setCache_count(sqlserverAttrib.getCache_count());
        try
        {
            dataLst.add(entity);
        }
        catch (UncheckedSQLException e)
        {
            logger.error(e);
        }
    }

    /**
     * 获得sqlserver监控对象锁明细数据信息 非表格数据
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getLockedData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServLockDataEntity entity = new SqlServLockDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        if ((sqlserverAttrib.getLock_request() - sqlserverAttribOld.getLock_request()) > 0)
        {
            entity.setLock_request((int) ((sqlserverAttrib.getLock_request() - sqlserverAttribOld.getLock_request()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getLock_wait() - sqlserverAttribOld.getLock_wait()) > 0)
        {
            entity
                .setLock_wait((int) ((sqlserverAttrib.getLock_wait() - sqlserverAttribOld.getLock_wait()) * 1000l / (sqlserverAttrib.getColl_time() - sqlserverAttribOld
                    .getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getLock_timeout() - sqlserverAttribOld.getLock_timeout()) > 0)
        {
            entity.setLock_timeout((int) ((sqlserverAttrib.getLock_timeout() - sqlserverAttribOld.getLock_timeout()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getLock_dead() - sqlserverAttribOld.getLock_dead()) > 0)
        {
            entity
                .setLock_dead((int) ((sqlserverAttrib.getLock_dead() - sqlserverAttribOld.getLock_dead()) * 1000l / (sqlserverAttrib.getColl_time() - sqlserverAttribOld
                    .getColl_time()))); // 单位:/分
        }
        entity.setLock_waittime(sqlserverAttrib.getLock_waittime());
        try
        {
            dataLst.add(entity);
        }
        catch (UncheckedSQLException e)
        {
            logger.error(e);
        }
    }

    /**
     * 获得sqlserver监控对象访问方法的明细数据信息
     * @param sqlserverAttrib
     * @param sqlserverAttribOld
     */
    private void getScanData(SqlServerAttribEntity sqlserverAttrib, SqlServerAttribEntity sqlserverAttribOld)
    {
        SqlServScanDataEntity entity = new SqlServScanDataEntity();
        entity.setObj_id(sqlserverAttrib.getObj_id());
        entity.setColl_time(sqlserverAttrib.getColl_time());
        if ((sqlserverAttrib.getFull_scan() - sqlserverAttribOld.getFull_scan()) > 0)
        {
            entity
                .setFull_scan((int) ((sqlserverAttrib.getFull_scan() - sqlserverAttribOld.getFull_scan()) * 1000l / (sqlserverAttrib.getColl_time() - sqlserverAttribOld
                    .getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getRange_scan() - sqlserverAttribOld.getRange_scan()) > 0)
        {
            entity.setRange_scan((int) ((sqlserverAttrib.getRange_scan() - sqlserverAttribOld.getRange_scan()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        if ((sqlserverAttrib.getProbe_scan() - sqlserverAttribOld.getProbe_scan()) > 0)
        {
            entity.setProbe_scan((int) ((sqlserverAttrib.getProbe_scan() - sqlserverAttribOld.getProbe_scan()) * 1000l / (sqlserverAttrib
                .getColl_time() - sqlserverAttribOld.getColl_time()))); // 单位:/分
        }
        try
        {
            dataLst.add(entity);
        }
        catch (UncheckedSQLException e)
        {
            logger.error(e);
        }
    }

    /**
     * 表格数据
     */
    private void getSqlServData()
    {
        List<SqlServDataEntity> list = new ArrayList<SqlServDataEntity>();
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery("DBCC SQLPERF(LOGSPACE)");
            while (rs.next())
            {
                SqlServDataEntity entity = new SqlServDataEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setDatabase_name(rs.getString(1));
                entity.setLog_used(rs.getInt(3));
                list.add(entity);
            }
            rs.close();
        }
        catch (SQLException e)
        {
            logger.error(e);
        }

        for (SqlServDataEntity entity : list)
        {
            String sql = "select size*8 from [" + entity.getDatabase_name() + "].[dbo].[sysfiles] where name not like '%_log'";
            try
            {
                rs = stm.executeQuery(sql);
                if (rs.next())
                {
                    entity.setSize(rs.getInt(1));
                }
                rs.close();
            }
            catch (SQLException e)
            {
                logger.error(e);
            }
            try
            {
                dataLst.add(entity);
            }
            catch (UncheckedSQLException e)
            {
                logger.error(e);
            }
        }
    }

    /**
     * @param obj_id 对象id
     * @param obj 对象
     */
    public static void addSqlServerAttrib(String obj_id, SqlServerAttribEntity obj)
    {
        map.put(obj_id, obj);
    }

    /**
     * @param obj_id 对象id
     * @return 结果
     */
    public static SqlServerAttribEntity getSqlServerAttrib(String obj_id)
    {
        return map.get(obj_id);
    }

    /**
     * @param obj_id 对象id
     */
    public static void delSqlServerAttrib(String obj_id)
    {
        map.remove(obj_id);
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 主方法
     */
    public static void main(String[] args) throws Exception
    {
        Connection conn = DriverManager.getConnection("jdbc:sqlserver://192.168.8.41:1433;DatabaseName=", "sa", "jetsen");
        MObjectEntity mo = new MObjectEntity();
        mo.setIpAddr("192.168.8.41");
        mo.setIpPort(1433);
        mo.setUserName("sa");
        mo.setUserPwd("jetsen");
        mo.setField1("");
        SqlServerCollector ssc = new SqlServerCollector();
        ssc.setMonitorObject(mo);
        ssc.connect();
        ssc.collect(null, null);
        ssc.collect(null, null);
        ssc.close();
    }
}
