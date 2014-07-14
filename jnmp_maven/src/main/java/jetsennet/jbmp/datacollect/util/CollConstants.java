package jetsennet.jbmp.datacollect.util;

import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 采集模块使用的常量
 * @author GUO
 */
public class CollConstants
{
    /**
     * 采集器配置文件名
     */
    public static final String COLL_CFG_FILE = "collector";
    /**
     * 采集器和采集错误处理器配置
     */
    public static final String COLL_CLASS_CFG_PRE = "collector" + XmlCfgUtil.CFG_PRE;
    public static final String COLL_THREAD_POOL_SIZE_CFG = "collector" + XmlCfgUtil.CFG_PRE + "coreThreadPoolSize" + XmlCfgUtil.CFG_POS;
    public static final String COLL_THREAD_POOL_MAX_SIZE_CFG = "collector" + XmlCfgUtil.CFG_PRE + "threadPoolMaxSize" + XmlCfgUtil.CFG_POS;
    public static final String COLL_THREAD_KEEP_ALIVE_CFG = "collector" + XmlCfgUtil.CFG_PRE + "keepAliveTime" + XmlCfgUtil.CFG_POS;
    public static final String COLL_DATA_UPLOAD_TIME_CFG = "collector" + XmlCfgUtil.CFG_PRE + "uploadTime" + XmlCfgUtil.CFG_POS;
    public static final String COLL_ID_CFG = "collector" + XmlCfgUtil.CFG_PRE + "id" + XmlCfgUtil.CFG_POS;
    public static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final int DEFAULT_THREAD_POOL_MAX_SIZE = Integer.MAX_VALUE;
    public static final int DEFAULT_THREAD_KEEP_ALIVE = 1;

    /**
     * rrd文件配置
     */
    public static final String RRD_ROOT_CFG = "rrd" + XmlCfgUtil.CFG_PRE + "root" + XmlCfgUtil.CFG_POS;
    public static final String REAL_PERF_DATA_KEEP_DAYS_CFG = "RealPerfDataKeepDays";
    public static final String DEFAULT_RRD_ROOT = "E:/rrdfile/";
    public static final int DEFAULT_REAL_PERF_DATA_KEEP_DAYS = 5;

    /**
     * 代表状态属性的value
     */
    public static final String STATUS_UP_VALUE = "1";
    public static final String STATUS_DOWN_VALUE = "0";

    /**
     * SNMP oid常量
     */
    public static final String SNMP_OID_PREFIX = "1.3.6";
}
