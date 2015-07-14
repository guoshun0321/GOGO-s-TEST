package jetsennet.jnmp.datacollect.collector;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jnmp.datacollect.util.OracleMonitorSql;
import jetsennet.jnmp.entity.ObjOracleDBEntity;
import jetsennet.jnmp.entity.OracleLockedDataEntity;
import jetsennet.jnmp.entity.OracleSessWaitDataEntity;
import jetsennet.jnmp.entity.OracleSessionDetailDataEntity;
import jetsennet.jnmp.entity.OracleSessionGatherDataEntity;
import jetsennet.jnmp.entity.OracleTableSpaceInfoEntity;

import org.apache.log4j.Logger;

/**
 * Oracle采集器
 * 
 * @author lianghongjie 
 */
public class OracleCollector extends AbsDBCollector
{

    private static final Logger logger = Logger.getLogger(OracleCollector.class);

    @Override
    public void connect() throws CollectorException
    {
        long startTime = System.currentTimeMillis();
        String driverClassName = getDriverClassName();
        try
        {
            Class.forName(driverClassName);
        }
        catch (ClassNotFoundException e)
        {
            String msg = "找不到数据库的JDBC驱动:" + driverClassName;
            logger.error(msg, e);
            throw new CollectorException(msg, e);
        }
        try
        {
            // 修复 BUG0020121，取消使用管理员登录
            Properties conProps = new Properties();
            conProps.put("user", mo.getUserName());
            conProps.put("password", mo.getUserPwd());
//                        conProps.put("internal_logon", "sysdba");

            con = DriverManager.getConnection(getConnectionURL(), conProps);
        }
        catch (SQLException e)
        {
            String msg = "无法连接数据库：" + mo.getIpAddr();
            logger.error(msg, e);
            throw new CollectorException(msg, e);
        }
        try
        {
            stm = con.createStatement();
        }
        catch (SQLException e)
        {
            throw new CollectorException(e.getMessage(), e);
        }
        long endTime = System.currentTimeMillis();
        connTime = (int) (endTime - startTime);
    }

    @Override
    protected String getDriverClassName()
    {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    protected String getConnectionURL()
    {
        return "jdbc:oracle:thin:@" + mo.getIpAddr() + ":" + mo.getIpPort() + ":" + this.ensureDbName(mo.getField1());
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        Map<ObjAttribEntity, Object> retval = new LinkedHashMap<ObjAttribEntity, Object>();

        reset();
        Map<String, Object> oaValue = new HashMap<String, Object>();

        // 连接时间
        oaValue.put("NMP_ORACLEDBDATA:CONN_TIMELEN", String.valueOf(connTime));

        // 性能数据
        getOracleDBData(oaValue);

        // instance
        this.getColumn(OracleMonitorSql.ORACLE_LOCAL_INSTANCE, new String[] { "NAME", "OPEN_MODE", "LOG_MODE" }, new String[] { "INSTANCE:NAME",
            "INSTANCE:OPEN_MODE", "INSTANCE:LOG_MODE" }, oaValue);

        // tablespace
        this.getColumn(OracleMonitorSql.ORACLE_TABLESPACE_INFO, new String[] { "TABLESPACE_NAME", "TOTAL", "USED", "FREE", "PERCENT" }, new String[] {
            "TABLESPACE:TABLESPACE_NAME", "TABLESPACE:TOTAL", "TABLESPACE:USED", "TABLESPACE:FREE", "TABLESPACE:PERCENT" }, oaValue);

        // session
        this.getColumn(OracleMonitorSql.ORACLE_SESSIONDETAIL_SQL, new String[] { "SID", "STATUS", "USERNAME", "MACHINE", "PHASICAL_READS",
            "LOGICAL_READS", "CACHE_HIT" }, new String[] { "SESSION:SID", "SESSION:STATUS", "SESSION:USERNAME", "SESSION:MACHINE",
            "SESSION:PHASICAL_READS", "SESSION:LOGICAL_READS", "SESSION:CACHE_HIT" }, oaValue);

        // disk io
        this.getColumn(OracleMonitorSql.ORACLE_DISK_IO, new String[] { "DATAFILE", "READ", "WRITE", "AVE" }, new String[] { "DISK:DATAFILE",
            "DISK:READ", "DISK:WRITE", "DISK:AVE" }, oaValue);

        // sql
        this.getColumn(OracleMonitorSql.ORACLE_SQL_SLOW, new String[] { "SQL", "CPU", "EXEC" }, new String[] { "SQL:SQL", "SQL:CPU", "SQL:EXEC" },
            oaValue);

        for (ObjAttribEntity oa : objAttrLst)
        {
            String attribCode = oa.getAttribValue();
            Object retObj = oaValue.get(attribCode);
            if (retObj == null)
            {
                retval.put(oa, null);
            }
            else if (retObj instanceof String)
            {
                CollData data = new CollData();
                data.objID = mo.getObjId();
                data.objAttrID = oa.getObjAttrId();
                data.attrID = oa.getAttribId();
                data.dataType = CollData.DATATYPE_PERF;
                data.value = (String) retObj;
                data.srcIP = mo.getIpAddr();
                data.time = time;
                retval.put(oa, data);
            }
            else
            {
                retval.put(oa, retObj);
            }
        }

        generateFailedData(retval, objAttrLst);
        return retval;
    }

    /**
     * 获得oracle监控对象表空间的信息 表空间名字,总大小,总块数,已使用大小,,使用率,可用大小 表格型数据
     */
    private void getTableSpaceInfo()
    {
        try
        {
            ResultSet rs = stm.executeQuery(OracleMonitorSql.ORACLE_TBSPINFO2_SQL);
            while (rs.next())
            {
                OracleTableSpaceInfoEntity entity = new OracleTableSpaceInfoEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setSpace_name(rs.getString(1));
                entity.setSum_space(rs.getInt(2));
                entity.setSum_blocks(rs.getInt(3));
                entity.setUsed_space(rs.getInt(4));
                entity.setUsed_rate(rs.getInt(5));
                entity.setFree_space(rs.getInt(6));
                dataLst.add(entity);
            }
            rs.close();
            rs = null;
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_TBSPINFO2_SQL, ex);
        }
    }

    /**
     * 获得oracle监控对象会话明细数据信息 数据库会话明细数据，表格型数据
     */
    private void getSessDetailData()
    {
        try
        {
            ResultSet rs = stm.executeQuery(OracleMonitorSql.ORACLE_SESSIONDETAIL_SQL);
            while (rs.next())
            {
                OracleSessionDetailDataEntity entity = new OracleSessionDetailDataEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setSession_id(rs.getInt(1));
                entity.setStatus(rs.getString(2));
                entity.setUsername(rs.getString(3));
                entity.setMachine(rs.getString(4));
                entity.setPhysical_reads(rs.getInt(5));
                entity.setLogical_reads(rs.getInt(6));
                entity.setCache_hit(rs.getInt(7));
                entity.setCpu_value(rs.getInt(8));
                dataLst.add(entity);
            }
            rs.close();
            rs = null;
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_SESSIONDETAIL_SQL, ex);
        }
    }

    /**
     * 获得oracle监控对象会话汇总数据信息 数据库会话汇总数据
     */
    private void getSessGatherData()
    {
        try
        {
            ResultSet rs = stm.executeQuery(OracleMonitorSql.ORACLE_SESSIONGATHER_SQL);
            while (rs.next())
            {
                OracleSessionGatherDataEntity entity = new OracleSessionGatherDataEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setMachine(rs.getString(1));
                entity.setProgram(rs.getString(2));
                entity.setStatus(rs.getString(3));
                entity.setCount_num(rs.getInt(4));
                dataLst.add(entity);
            }
            rs.close();
            rs = null;
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_SESSIONGATHER_SQL, ex);
        }
    }

    /**
     * 获得oracle监控对象会话等待数据信息 表格数据
     */
    private void getSessWaitData()
    {
        try
        {
            ResultSet rs = stm.executeQuery(OracleMonitorSql.ORACLE_SESSIONWAIT_SQL);
            while (rs.next())
            {
                OracleSessWaitDataEntity entity = new OracleSessWaitDataEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setSession_id(rs.getInt(1));
                entity.setUser_name(rs.getString(2));
                entity.setWait_event(rs.getString(3));
                entity.setStatus(rs.getString(4));
                entity.setWait_time(rs.getInt(5));
                dataLst.add(entity);
            }
            rs.close();
            rs = null;
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_SESSIONWAIT_SQL, ex);
        }
    }

    /**
     * 获得oracle监控对象锁数据信息
     */
    private void getLockedData()
    {
        try
        {
            ResultSet rs = stm.executeQuery(OracleMonitorSql.ORACLE_LOCKED_SQL);
            while (rs.next())
            {
                OracleLockedDataEntity entity = new OracleLockedDataEntity();
                entity.setObj_id(mo.getObjId());
                entity.setColl_time(time.getTime());
                entity.setSession_id(rs.getInt(1));
                entity.setUser_name(rs.getString(2));
                entity.setSerial(rs.getInt(3));
                entity.setMachine(rs.getString(4));
                entity.setLogon_time(rs.getDate(5));
                entity.setSql_text(rs.getString(6));
                dataLst.add(entity);
            }
            rs.close();
            rs = null;
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_LOCKED_SQL, ex);
        }
    }

    /**
     * 获得oracle监控对象数据库数据信息 非表格型数据
     */
    private void getOracleDBData(Map<String, Object> oaValue)
    {
        String temp = this.getSingleCell(OracleMonitorSql.ORACLE_CONNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:CON_NUM", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_ACTIVECONNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:ACTIVECON_NUM", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_LOCKNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:LOCK_NUM", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_LOCKEDNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:DEADLOCK_NUM", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_CACHE_HIT_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:CACHE_HIT", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_DIC_HIT_SQL, 3);
        oaValue.put("NMP_ORACLEDBDATA:DIC_HIT", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_LIBCACHE_HIT_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:LABCACHE_HIT", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_CURSORNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:CURSOR_NUM", temp);

        temp = this.getSingleCell(OracleMonitorSql.ORACLE_TRANNUM_SQL, 1);
        oaValue.put("NMP_ORACLEDBDATA:TRAN_NUM", temp);
    }

    /**
     * 获取sql语句结果的第一行第N列的值
     * 
     * @param sql
     * @param column
     * @return
     */
    private String getSingleCell(String sql, int column)
    {
        String retval = null;
        ResultSet rs = null;
        try
        {
            rs = stm.executeQuery(sql);
            if (rs.next())
            {
                retval = rs.getString(column);
            }
        }
        catch (SQLException ex)
        {
            this.oracleError(sql, ex);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    rs = null;
                }
            }
        }
        return retval;
    }

    /**
     * 获取sql语句结果的第一行第N列的值
     * 
     * @param sql
     * @param column
     * @return
     * @throws Exception 
     */
    private void getColumn(String sql, String[] columns, String[] relValues, Map<String, Object> oaValue)
    {
        if (columns.length != relValues.length)
        {
            logger.error(String.format("查询列和关联列长度不匹配:%s/%s", columns.length, relValues.length));
        }

        ResultSet rs = null;

        int length = columns.length;
        for (int i = 0; i < length; i++)
        {
            List<String> tempLst = new ArrayList<String>();
            oaValue.put(relValues[i], tempLst);
        }

        try
        {
            rs = stm.executeQuery(sql);
            while (rs.next())
            {
                for (int i = 0; i < length; i++)
                {
                    String tempValue = rs.getString(columns[i]);
                    ((List<String>) oaValue.get(relValues[i])).add(tempValue);
                }
            }
        }
        catch (SQLException ex)
        {
            this.oracleError(sql, ex);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
                finally
                {
                    rs = null;
                }
            }
        }
    }

    /**
     * 更新oracle数据库监控对象
     * @throws CollectorException 异常
     */
    public void refreshObj() throws CollectorException
    {
        ResultSet rs = null;
        if (mo == null)
        {
            return;
        }

        ObjOracleDBEntity oracle_model = new ObjOracleDBEntity();
        oracle_model.setObj_id(mo.getObjId());

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_DATABASE_SQL);
            if (rs.next())
            {
                oracle_model.setCreate_time(rs.getDate("CREATED"));
                oracle_model.setResetlogs_time(rs.getDate("RESETLOGS_TIME"));
                oracle_model.setLog_mode(rs.getString("LOG_MODE"));
                oracle_model.setOpen_mode(rs.getString("OPEN_MODE"));
                oracle_model.setPlatform_name(rs.getString("PLATFORM_NAME"));
            }
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_DATABASE_SQL, ex);
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            {
                rs = null;
            }
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_INSTANCE_SQL);
            if (rs.next())
            {
                oracle_model.setInstance_name(rs.getString("INSTANCE_NAME"));
                oracle_model.setHost_name(rs.getString("HOST_NAME"));
                oracle_model.setVersion(rs.getString("VERSION"));
                oracle_model.setStartup_time(rs.getDate("STARTUP_TIME"));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_INSTANCE_SQL, ex);
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_SGAFREEMEMORY_SQL);
            if (rs.next())
            {
                oracle_model.setSgafree_memory(rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_SGAFREEMEMORY_SQL, ex);
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_FIXEDSIZE_SQL);
            if (rs.next())
            {
                oracle_model.setFix_size(rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_FIXEDSIZE_SQL, ex);
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_SHAREDSIZE_SQL);
            if (rs.next())
            {
                oracle_model.setShared_size(rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_SHAREDSIZE_SQL, ex);
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_CACHESIZE_SQL);
            if (rs.next())
            {
                oracle_model.setCache_size(rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_CACHESIZE_SQL, ex);
        }

        try
        {
            rs = stm.executeQuery(OracleMonitorSql.ORACLE_REDOLOGSIZE_SQL);
            if (rs.next())
            {
                oracle_model.setRedolog_size(rs.getInt(1));
            }
            rs.close();
        }
        catch (SQLException ex)
        {
            this.oracleError(OracleMonitorSql.ORACLE_REDOLOGSIZE_SQL, ex);
        }
    }

    /**
     * 错误处理
     * @param sql
     * @param ex
     */
    private void oracleError(String sql, Exception ex)
    {
        logger.error(mo.getIpAddr() + ":" + mo.getIpPort() + "上的oracle对象,执行语句:" + sql, ex);
    }

    /**
     * 主方法
     * @param args 参数
     */
    public static void main(String[] args)
    {
        int objId = 721;
        OracleCollector oc = new OracleCollector();
        try
        {
//            MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
//            MObjectEntity mo = modal.get(objId);

//            ObjAttribDal oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
//            List<ObjAttribEntity> oas = oadal.getByID(objId);
            MObjectEntity mo = new MObjectEntity();
            mo.setField1("orcl");
            mo.setUserName("system");
            mo.setUserPwd("orcl");
            mo.setIpAddr("192.168.9.68");
            mo.setIpPort(1521);

            oc.setMonitorObject(mo);
            oc.connect();
            oc.collect(null, null);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            oc.close();
        }
    }
}
