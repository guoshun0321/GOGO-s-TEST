package jetsennet.jnmp.datacollect.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author？
 */
public class OracleMonitorSql
{

    private static Map sqlMap;
    /**
     * oracle实例名,创建时间,日志重设时间,log模式,open模式,操作系统名字
     */
    public static final String ORACLE_DATABASE_SQL = "select NAME,CREATED,RESETLOGS_TIME,LOG_MODE,OPEN_MODE,PLATFORM_NAME from v$database";
    /**
     * oracle实例名,主机名,版本号,启动时间
     */
    public static final String ORACLE_INSTANCE_SQL = "select INSTANCE_NAME,HOST_NAME,VERSION,STARTUP_TIME from v$instance";
    /**
     * SGA区可用内存
     */
    public static final String ORACLE_SGAFREEMEMORY_SQL = "select bytes/1024 from v$sgastat where name='free memory' and pool = 'shared pool'";
    /**
     * 固有区大小
     */
    public static final String ORACLE_FIXEDSIZE_SQL = "select value/1024 from v$SGA where name='Fixed Size'";
    /**
     * 共享池大小
     */
    public static final String ORACLE_SHAREDSIZE_SQL = "select value/1024 from v$SGA where name='Variable Size'";
    /**
     * 缓冲区大小
     */
    public static final String ORACLE_CACHESIZE_SQL = "select value/1024 from v$SGA where name='Database Buffers'";
    /**
     * RedoLog缓冲区大小
     */
    public static final String ORACLE_REDOLOGSIZE_SQL = "select value/1024 from v$SGA where name='Redo Buffers'";
    public static final String ORACLE_SESSIONCOUNT_SQL = "select count(*) from v$session"; // oracle会话数
    /**
     * 表空间名字,物理读次数,物理写次数,块读取数,块写入数,读取总时间,写入总时间
     */
    public static final String ORACLE_TBSPINFO1_SQL =
        "select tablespace_name, sum(phyrds),sum(phywrts),sum(phyblkrd),sum(phyblkwrt),sum(readtim),sum(writetim) "
            + "from v$filestat f,dba_data_files df where f.file#=df.file_id group by tablespace_name";
    /**
     * 表空间名字,总大小,总块数,已使用大小,,使用率,可用大小,
     */
    public static final String ORACLE_TBSPINFO2_SQL =
        "SELECT D.TABLESPACE_NAME,SPACE \"SUM_SPACE(M)\",BLOCKS SUM_BLOCKS,SPACE-NVL(FREE_SPACE,0) \"USED_SPACE(M)\", "
            + "ROUND((1-NVL(FREE_SPACE,0)/SPACE)*100,2) \"USED_RATE(%)\",FREE_SPACE \"FREE_SPACE(M)\" " + "FROM "
            + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) SPACE,SUM(BLOCKS) BLOCKS " + " FROM DBA_DATA_FILES "
            + "GROUP BY TABLESPACE_NAME) D, " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) FREE_SPACE " + "FROM DBA_FREE_SPACE "
            + "GROUP BY TABLESPACE_NAME) F " + "WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+) " + "UNION ALL "
            + "SELECT D.TABLESPACE_NAME,SPACE \"SUM_SPACE(M)\",BLOCKS SUM_BLOCKS, "
            + "USED_SPACE \"USED_SPACE(M)\",ROUND(NVL(USED_SPACE,0)/SPACE*100,2) \"USED_RATE(%)\", " + "NVL(FREE_SPACE,0) \"FREE_SPACE(M)\" "
            + "FROM " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) SPACE,SUM(BLOCKS) BLOCKS " + "FROM DBA_TEMP_FILES "
            + "GROUP BY TABLESPACE_NAME) D, " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES_USED)/(1024*1024),2) USED_SPACE, "
            + "ROUND(SUM(BYTES_FREE)/(1024*1024),2) FREE_SPACE " + "FROM V$TEMP_SPACE_HEADER " + "GROUP BY TABLESPACE_NAME) F "
            + "WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+)";
    /**
     * 文件,物理读次数,物理写次数,平均I/O时间,最小I/O时间,最大I/O写入时间,最大I/O读取时间
     */
    public static final String ORACLE_FILEINFO_SQL =
        "Select name,phyrds,phywrts,avgiotim,miniotim,maxiowtm,maxiortm from v$filestat,v$datafile where v$filestat.file#=v$datafile.file#";
    /**
     * 数据库会话明细数据
     */
    public static final String ORACLE_SESSIONDETAIL_SQL =
        "select N.sid SID,status STATUS,username USERNAME,machine MACHINE,physical_reads PHASICAL_READS, " + "(block_gets+consistent_gets) LOGICAL_READS, "
            + "decode(block_gets+consistent_gets,0,0,round((1-physical_reads/(block_gets+consistent_gets))*100)) CACHE_HIT, "
            + "s.value value from v$session N,v$sess_io I,v$sesstat S " + "where N.sid = I.sid and N.sid = S.sid and S.statistic#=12";
    /**
     * 数据库会话汇总数据
     */
    public static final String ORACLE_SESSIONGATHER_SQL =
        "select machine,decode(program,null,'program',program) program,status,count(*) num "
            + "from v$session where decode(program,null,'program',program) not like 'ORACLE.EXE%' " + "group by machine,program,status";
    /**
     * 数据库会话等待数据
     */
    public static final String ORACLE_SESSIONWAIT_SQL =
        "select w.sid,username,w.event,status,w.seconds_in_wait from v$session s,v$session_wait w where w.sid = s.sid";
    /**
     * 数据库死锁数据
     */
    public static final String ORACLE_LOCKED_SQL =
        "select t2.sid,t2.username,t2.serial#,t2.machine,t2.logon_time,t3.sql_text "
            + "from v$locked_object t1,v$session t2,v$sqltext t3 where t1.session_id = t2.sid "
            + "and t2.sql_address=t3.address order by t2.logon_time";
    /**
     * 连接数
     */
    public static final String ORACLE_CONNUM_SQL = "select count(*) from v$session";
    /**
     * 并发连接数
     */
    public static final String ORACLE_ACTIVECONNUM_SQL = "select count(*) from v$session where status = 'ACTIVE'";
    /**
     * 锁数量
     */
    public static final String ORACLE_LOCKNUM_SQL = "select count(*) from v$lock";
    /**
     * 死锁数量
     */
    public static final String ORACLE_LOCKEDNUM_SQL =
        "select count(*) from v$locked_object t1,v$session t2,v$sqltext t3 "
            + "where t1.session_id = t2.sid and t2.sql_address=t3.address order by t2.logon_time";
    /**
     * 数据缓冲命中率
     */
    public static final String ORACLE_CACHE_HIT_SQL =
        "SELECT  round(100*(1-c.value/(a.value+b.value)),4) hit_ratio FROM v$sysstat a,v$sysstat b,v$sysstat c "
            + "WHERE a.NAME='db block gets' AND b.NAME='consistent gets' AND c.NAME='physical reads'";
    /**
     * 数据字典击中率
     */
    public static final String ORACLE_DIC_HIT_SQL =
        "select sum(gets), sum(getmisses),(1 - (sum(getmisses) / (sum(gets)+ sum(getmisses)))) * 100 HitRate from v$rowcache";
    /**
     * 缓存库击中率
     */
    public static final String ORACLE_LIBCACHE_HIT_SQL = "SELECT SUM(pinhits)/sum(pins)*100 FROM V$LIBRARYCACHE";
    /**
     * 游标数
     */
    public static final String ORACLE_CURSORNUM_SQL = "select count(*) from v$open_cursor";
    /**
     * 当前事务数
     */
    public static final String ORACLE_TRANNUM_SQL = "select count(*) from v$transaction";
    public static final String ORACLE_SGA_SQL = "select * from v$SGA"; // 固有区大小,共享池大小,缓冲区大小,RedoLog缓冲区
    public static final String ORACLE_LOK_SQL =
        "select ls.osuser os_user_name,   ls.username user_name, " + "decode(ls.type, 'RW', 'Row wait enqueue lock', 'TM', 'DML enqueue lock', 'TX',"
            + "'Transaction enqueue lock', 'UL', 'User supplied lock') lock_type,"
            + "o.object_name object,   decode(ls.lmode, 1, null, 2, 'Row Share', 3,"
            + "'Row Exclusive', 4, 'Share', 5, 'Share Row Exclusive', 6, 'Exclusive', null) "
            + "lock_mode,    o.owner,   ls.sid,   ls.serial# serial_num,   ls.id1,   ls.id2 "
            + "from sys.dba_objects o, (   select s.osuser,    s.username,    l.type, "
            + "l.lmode,    s.sid,    s.serial#,    l.id1,    l.id2   from v$session s, "
            + "v$lock l   where s.sid = l.sid ) ls  where o.object_id = ls.id1 and   " + " o.owner<> 'SYS'   order by o.owner,o.object_name";
    // 查看锁（lock）情况
    public static final String[] sqls =
        new String[] {
            // "show parameter db_block_size",//oracle数据库块大小
            "select INSTANCE_NAME,HOST_NAME,VERSION,STARTUP_TIME from v$instance", // oracle实例名,主机名,版本号,启动时间
            "select count(*) from v$session", // oracle会话数
            "select NAME,CREATED,RESETLOGS_TIME,LOG_MODE,OPEN_MODE,PLATFORM_NAME from v$database",
            // oracle实例名,创建时间,日志重设时间,log模式,open模式,操作系统名字

            "select tablespace_name, sum(phyrds),sum(phywrts),sum(phyblkrd),sum(phyblkwrt),sum(readtim),sum(writetim)"
                + " from v$filestat f,dba_data_files df where f.file#=df.file_id group by tablespace_name",
            // 表空间名字,物理读次数,物理写次数,块读取数,块写入数,读取总时间,写入总时间

            "Select name,phyrds,phywrts,avgiotim,miniotim,maxiowtm,maxiortm from v$filestat,v$datafile where v$filestat.file#=v$datafile.file#",
            // 文件,物理读次数,物理写次数,平均I/O时间,最小I/O时间,最大I/O写入时间,最大I/O读取时间

            "SELECT D.TABLESPACE_NAME,SPACE \"SUM_SPACE(M)\",BLOCKS SUM_BLOCKS,SPACE-NVL(FREE_SPACE,0) \"USED_SPACE(M)\", "
                + "ROUND((1-NVL(FREE_SPACE,0)/SPACE)*100,2) \"USED_RATE(%)\",FREE_SPACE \"FREE_SPACE(M)\" " + "FROM "
                + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) SPACE,SUM(BLOCKS) BLOCKS " + " FROM DBA_DATA_FILES "
                + "GROUP BY TABLESPACE_NAME) D, " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) FREE_SPACE " + "FROM DBA_FREE_SPACE "
                + "GROUP BY TABLESPACE_NAME) F " + "WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+) " + "UNION ALL "
                + "SELECT D.TABLESPACE_NAME,SPACE \"SUM_SPACE(M)\",BLOCKS SUM_BLOCKS, "
                + "USED_SPACE \"USED_SPACE(M)\",ROUND(NVL(USED_SPACE,0)/SPACE*100,2) \"USED_RATE(%)\", " + "NVL(FREE_SPACE,0) \"FREE_SPACE(M)\" "
                + "FROM " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES)/(1024*1024),2) SPACE,SUM(BLOCKS) BLOCKS " + "FROM DBA_TEMP_FILES "
                + "GROUP BY TABLESPACE_NAME) D, " + "(SELECT TABLESPACE_NAME,ROUND(SUM(BYTES_USED)/(1024*1024),2) USED_SPACE, "
                + "ROUND(SUM(BYTES_FREE)/(1024*1024),2) FREE_SPACE " + "FROM V$TEMP_SPACE_HEADER " + "GROUP BY TABLESPACE_NAME) F "
                + "WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+)",
            // 表空间名字 ,总大小,总块数,已使用大小,,使用率,可用大小,

            "SELECT  round(100*(1-c.value/(a.value+b.value)),4) hit_ratio FROM v$sysstat a,v$sysstat b,v$sysstat c WHERE a.NAME='db block gets' "
                + "AND b.NAME='consistent gets' AND c.NAME='physical reads'", // 数据缓冲命中率

            "select     sum(gets), sum(getmisses),(1 - (sum(getmisses) / (sum(gets)+ sum(getmisses)))) * 100 HitRate from v$rowcache", // 数据字典击中率

            "SELECT SUM(pinhits)/sum(pins)*100 FROM V$LIBRARYCACHE", // 缓存库击中率

            "select bytes/1024/1024 from v$sgastat where name='free memory' and pool = 'shared pool'", // sga区可用内存
            "select * from v$SGA", // 固有区大小,共享池大小,缓冲区大小,RedoLog缓冲区

            "select ls.osuser os_user_name,   ls.username user_name, "
                + "decode(ls.type, 'RW', 'Row wait enqueue lock', 'TM', 'DML enqueue lock', 'TX',"
                + "'Transaction enqueue lock', 'UL', 'User supplied lock') lock_type,"
                + "o.object_name object,   decode(ls.lmode, 1, null, 2, 'Row Share', 3,"
                + "'Row Exclusive', 4, 'Share', 5, 'Share Row Exclusive', 6, 'Exclusive', null) "
                + "lock_mode,    o.owner,   ls.sid,   ls.serial# serial_num,   ls.id1,   ls.id2 "
                + "from sys.dba_objects o, (   select s.osuser,    s.username,    l.type, "
                + "l.lmode,    s.sid,    s.serial#,    l.id1,    l.id2   from v$session s, "
                + "v$lock l   where s.sid = l.sid ) ls  where o.object_id = ls.id1 and    o.owner<> 'SYS'   order by o.owner,o.object_name",
        // 查看锁（lock）情况
        };

    /**
     * 本地实例状态
     */
    public static final String ORACLE_LOCAL_INSTANCE = "SELECT NAME,OPEN_MODE,LOG_MODE FROM V$DATABASE";

    /**
     * 表空间信息
     */
    public static final String ORACLE_TABLESPACE_INFO =
        "SELECT A.TABLESPACE_NAME, A.BYTES/1024/1024 TOTAL, b.BYTES/1024/1024 FREE, (a.BYTES-b.BYTES)/1024/1024 USED, ROUND(((a.BYTES-b.BYTES)/a.BYTES)*100,2) PERCENT FROM (SELECT TABLESPACE_NAME,SUM(BYTES) BYTES FROM DBA_DATA_FILES GROUP BY TABLESPACE_NAME) a, (SELECT TABLESPACE_NAME,SUM(BYTES) BYTES FROM DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) b WHERE a.TABLESPACE_NAME = b.TABLESPACE_NAME ORDER BY TABLESPACE_NAME";

    /**
     * disk io信息
     */
    public static final String ORACLE_DISK_IO =
        "SELECT df.name DATAFILE,fs.phyrds READ,fs.phywrts WRITE,fs.avgiotim*10 AVE FROM v$datafile df,v$filestat fs WHERE df.file#=fs.file#";

    /**
     * 执行时间大于1秒的前20条sql语句
     */
    public static final String ORACLE_SQL_SLOW =
        "SELECT A.*,ROWNUM RN FROM(SELECT sql_text SQL,cpu_time/1000/1000 CPU,executions EXEC FROM v$sqlstats WHERE cpu_time > 1000000 order by cpu_time desc) A WHERE ROWNUM < 20";

    /**
     * @return 结果
     */
    public static Map getSqlMap()
    {
        if (sqlMap == null)
        {
            sqlMap = new HashMap();
            sqlMap.put("ORACLE_INSTANCE_SQL", ORACLE_INSTANCE_SQL);
            sqlMap.put("ORACLE_SESSIONCOUNT_SQL", ORACLE_SESSIONCOUNT_SQL);
            sqlMap.put("ORACLE_DATABASE_SQL", ORACLE_DATABASE_SQL);
            sqlMap.put("ORACLE_TBSPINFO1_SQL", ORACLE_TBSPINFO1_SQL);
            sqlMap.put("ORACLE_TBSPINFO2_SQL", ORACLE_TBSPINFO2_SQL);
            sqlMap.put("ORACLE_FILEINFO_SQL", ORACLE_FILEINFO_SQL);
            sqlMap.put("ORACLE_CACHE_HIT_SQL", ORACLE_CACHE_HIT_SQL);
            sqlMap.put("ORACLE_DIC_HIT_SQL", ORACLE_DIC_HIT_SQL);
            sqlMap.put("ORACLE_LIBCACHE_HIT_SQL", ORACLE_LIBCACHE_HIT_SQL);
            sqlMap.put("ORACLE_SGA_SQL", ORACLE_SGA_SQL);
            sqlMap.put("ORACLE_LOK_SQL", ORACLE_LOK_SQL);
        }
        return sqlMap;
    }
}
