package jetsennet.jnmp.datacollect.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jnmp.entity.DB2DBDataEntity;
import jetsennet.jnmp.entity.DB2SysDataEntity;
import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;

import org.apache.log4j.Logger;

/**
 * @author lianghongjie DB2采集器
 */
public class DB2Collector extends AbsDBCollector
{
    private static final Logger logger = Logger.getLogger(DB2Collector.class);

    @Override
    protected String getDriverClassName()
    {
        return "com.ibm.db2.jcc.DB2Driver";
    }

    @Override
    protected String getConnectionURL()
    {
        return "jdbc:db2://" + mo.getIpAddr() + ":" + mo.getIpPort() + "/" + mo.getField1();
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();

        getSysData();
        getDBData();

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        Map<Integer, ObjAttribEntity> idMap = toIdMap(objAttrLst);
        for (Object data : dataLst)
        {
            if (data instanceof DB2SysDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof DB2DBDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    private void getSysData()
    {
        String sql =
            "select REM_CONS_IN,REM_CONS_IN_EXEC,LOCAL_CONS,LOCAL_CONS_IN_EXEC,"
                + "CON_LOCAL_DBASES,AGENTS_REGISTERED,AGENTS_WAITING_ON_TOKEN,IDLE_AGENTS " + "from table(SNAPSHOT_DBM(-2)) as T ";
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery(sql);
            if (rs.next())
            {
                DB2SysDataEntity model = new DB2SysDataEntity();
                model.setObj_id(mo.getObjId());
                model.setColl_time(time.getTime());
                model.setConn_timelen(connTime);
                model.setConn_currnum(rs.getInt("CON_LOCAL_DBASES"));
                model.setConn_localnum(rs.getInt("LOCAL_CONS"));
                model.setConn_localinexec(rs.getInt("LOCAL_CONS_IN_EXEC"));
                model.setConn_remotenum(rs.getInt("REM_CONS_IN"));
                model.setConn_remoteinexec(rs.getInt("REM_CONS_IN_EXEC"));
                model.setAgent_totalnum(rs.getInt("AGENTS_REGISTERED"));
                model.setAgent_idlenum(rs.getInt("IDLE_AGENTS"));
                model.setAgent_waitnum(rs.getInt("AGENTS_WAITING_ON_TOKEN"));
                model.setAgent_activenum(model.getAgent_totalnum() - model.getAgent_idlenum() - model.getAgent_waitnum());
                dataLst.add(model);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            {
                logger.error(ex);
            }
            finally
            {
                rs = null;
            }
        }
    }

    private void getDBData()
    {
        String sql =
            "select TOTAL_LOG_USED,TOTAL_LOG_AVAILABLE,POOL_DATA_L_READS," + "POOL_DATA_P_READS,POOL_INDEX_L_READS,POOL_INDEX_P_READS,DIRECT_READS,"
                + "DIRECT_WRITES,DEADLOCKS,LOCKS_WAITING,COMMIT_SQL_STMTS,ROLLBACK_SQL_STMTS,"
                + "FAILED_SQL_STMTS,TOTAL_SORTS,SORT_OVERFLOWS,PKG_CACHE_LOOKUPS," + "PKG_CACHE_INSERTS,CAT_CACHE_LOOKUPS,CAT_CACHE_INSERTS "
                + "from table(SNAPSHOT_DATABASE(null,-2)) as T";
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery(sql);
            if (rs.next())
            {
                DB2DBDataEntity model = new DB2DBDataEntity();
                model.setObj_id(mo.getObjId());
                model.setColl_time(time.getTime());
                model.setCommit_num(rs.getInt("COMMIT_SQL_STMTS"));
                model.setRollback_num(rs.getInt("ROLLBACK_SQL_STMTS"));
                model.setFail_num(rs.getInt("FAILED_SQL_STMTS"));
                model.setDeadlock_num(rs.getInt("DEADLOCKS"));
                model.setLock_waiting(rs.getInt("LOCKS_WAITING"));
                model.setPackage_hit(100.0f - getRate(rs.getInt("PKG_CACHE_LOOKUPS"), rs.getInt("PKG_CACHE_INSERTS")));
                model.setCatalog_hit(100.0f - getRate(rs.getInt("CAT_CACHE_LOOKUPS"), rs.getInt("CAT_CACHE_INSERTS")));
                model.setSort_overflow(getRate(rs.getInt("TOTAL_SORTS"), rs.getInt("SORT_OVERFLOWS")));
                model.setLog_used(getRate(rs.getInt("TOTAL_LOG_AVAILABLE"), rs.getInt("TOTAL_LOG_USED")));
                model.setCache_hit(100.0f - getRate(rs.getInt("POOL_DATA_L_READS"), rs.getInt("POOL_DATA_P_READS")));
                model.setIndex_hit(100.0f - getRate(rs.getInt("POOL_INDEX_L_READS"), rs.getInt("POOL_INDEX_P_READS")));
                model.setRead_num(rs.getInt("DIRECT_READS"));
                model.setWrite_num(rs.getInt("DIRECT_WRITES"));
                dataLst.add(model);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            {
                logger.error(ex);
            }
            finally
            {
                rs = null;
            }
        }
    }

    private float getRate(int num1, int num2)
    {
        if (num1 == 0)
        {
            return 0.0f;
        }
        if (num2 > num1)
        {
            return 100.0f;
        }
        return num2 * 100.0f / num1;
    }
}
