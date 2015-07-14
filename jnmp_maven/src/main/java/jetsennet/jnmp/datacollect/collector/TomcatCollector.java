package jetsennet.jnmp.datacollect.collector;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jnmp.entity.TomcatAppDataEntity;
import jetsennet.jnmp.entity.TomcatSysDataEntity;
import jetsennet.jnmp.entity.TomcatThreadPoolEntity;

/**
 * @author lianghongjie Tomcat采集器
 */
public class TomcatCollector extends AbsCollector
{
    private static final Logger logger = Logger.getLogger(TomcatCollector.class);

    /**
     * 连接信息
     */
    private JMXConnector connector;
    private MBeanServerConnection mbsc;

    @Override
    public void connect() throws CollectorException
    {
        String jmxURL = "service:jmx:rmi:///jndi/rmi://" + mo.getIpAddr() + ":" + mo.getIpPort() + "/jmxrmi";
        Map<String, String[]> map = new HashMap<String, String[]>();
        String[] credentials = new String[] { mo.getUserName(), mo.getUserPwd() };
        map.put("jmx.remote.credentials", credentials);
        try
        {
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
            connector = JMXConnectorFactory.connect(serviceURL, map);
            mbsc = connector.getMBeanServerConnection();
        }
        catch (Exception e)
        {
            String msg = mo.getIpAddr() + ":" + mo.getIpPort() + "上的tomcat连接失败";
            logger.error(msg, e);
            throw new CollectorException(msg, e);
        }
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();

        getSysData();
        getAppData();
        getThreadPoolData();

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        Map<Integer, ObjAttribEntity> idMap = toIdMap(objAttrLst);
        for (Object data : dataLst)
        {
            if (data instanceof TomcatAppDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof TomcatThreadPoolEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof TomcatSysDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    private void getSysData()
    {
        TomcatSysDataEntity entity = new TomcatSysDataEntity();
        entity.setObj_id(mo.getObjId());
        entity.setColl_time(time.getTime());
        ObjectName heapObjName = getObjectName("java.lang:type=Memory");
        if (heapObjName == null)
        {
            logger.error("错误的表示法：java.lang:type=Memory");
        }
        else
        {
            try
            {
                MemoryUsage heapMemoryUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(heapObjName, "HeapMemoryUsage"));
                long maxMemory = heapMemoryUsage.getMax(); // 堆最大
                long commitMemory = heapMemoryUsage.getCommitted(); // 堆当前分配
                long usedMemory = heapMemoryUsage.getUsed();
                double rate = usedMemory * 100 / commitMemory;
                entity.setHeap_max((maxMemory / 1024 / 1024));
                entity.setHeap_commit((commitMemory / 1024 / 1024));
                entity.setHeap_used((usedMemory / 1024 / 1024));
                entity.setHeap_rate(rate);

                MemoryUsage nonheapMemoryUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(heapObjName, "NonHeapMemoryUsage"));
                long nonmaxMemory = nonheapMemoryUsage.getMax();
                long noncommitMemory = nonheapMemoryUsage.getCommitted();
                long nonusedMemory = nonheapMemoryUsage.getUsed();
                double rate1 = nonusedMemory * 100 / noncommitMemory;
                entity.setNonheap_max((nonmaxMemory / 1024 / 1024));
                entity.setNonheap_commit((noncommitMemory / 1024 / 1024));
                entity.setNonheap_used((nonusedMemory / 1024 / 1024));
                entity.setNonheap_rate(rate1);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }

        ObjectName threadObjName = getObjectName("java.lang:type=Threading");
        if (threadObjName == null)
        {
            logger.error("错误的表示法：java.lang:type=Memory");
        }
        else
        {
            try
            {
                int thread_peak = (Integer) mbsc.getAttribute(threadObjName, "PeakThreadCount");
                entity.setThread_peak(thread_peak);
                int thread_current = (Integer) mbsc.getAttribute(threadObjName, "ThreadCount");
                entity.setThread_current(thread_current);
                int thread_daemon = (Integer) mbsc.getAttribute(threadObjName, "DaemonThreadCount");
                entity.setThread_daemon(thread_daemon);
                int thread_started = ((Long) mbsc.getAttribute(threadObjName, "TotalStartedThreadCount")).intValue();
                entity.setThread_started(thread_started);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }

        dataLst.add(entity);
    }

    private void getAppData()
    {
        Set<ObjectName> s = null;
        try
        {
            ObjectName managerObjName = new ObjectName("Catalina:type=Manager,*");
            s = mbsc.queryNames(managerObjName, null);
        }
        catch (Exception e)
        {
            logger.error(e);
            return;
        }

        // 采集数据
        for (ObjectName obj : s)
        {
            TomcatAppDataEntity entity = new TomcatAppDataEntity();
            entity.setObj_id(mo.getObjId());
            entity.setColl_time(time.getTime());
            entity.setApp_name(obj.getKeyProperty("path"));
            try
            {
                ObjectName objname = new ObjectName(obj.getCanonicalName());
                long conn_max = Long.parseLong(mbsc.getAttribute(objname, "maxActiveSessions").toString());
                long conn_active = Long.parseLong(mbsc.getAttribute(objname, "activeSessions").toString());
                long conn_count = Long.parseLong(mbsc.getAttribute(objname, "sessionCounter").toString());
                entity.setConn_max(conn_max);
                entity.setConn_active(conn_active);
                entity.setConn_count(conn_count);
                dataLst.add(entity);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
    }

    private void getThreadPoolData()
    {
        Set<ObjectName> s = null;
        try
        {
            ObjectName threadpoolObjName = new ObjectName("Catalina:type=ThreadPool,*");
            s = mbsc.queryNames(threadpoolObjName, null);
        }
        catch (Exception e)
        {
            logger.error(e);
            return;
        }

        // 采集数据
        for (ObjectName obj : s)
        {
            TomcatThreadPoolEntity entity = new TomcatThreadPoolEntity();
            entity.setObj_id(mo.getObjId());
            entity.setColl_time(time.getTime());
            entity.setPool_name(obj.getKeyProperty("name"));
            try
            {
                ObjectName objname = new ObjectName(obj.getCanonicalName());
                MBeanAttributeInfo[] mbAttributes = mbsc.getMBeanInfo(objname).getAttributes();
                for (int i = 0; i < mbAttributes.length; i++)
                {
                    if ("maxThreads".equals(mbAttributes[i].getName()))
                    {
                        int thread_max = (Integer) mbsc.getAttribute(objname, "maxThreads");
                        entity.setThread_max(thread_max);
                    }
                    if ("currentThreadCount".equals(mbAttributes[i].getName()))
                    {
                        int thread_current = (Integer) mbsc.getAttribute(objname, "currentThreadCount");
                        entity.setThread_current(thread_current);
                    }
                    if ("currentThreadsBusy".equals(mbAttributes[i].getName()))
                    {
                        int thread_busy = (Integer) mbsc.getAttribute(objname, "currentThreadsBusy");
                        entity.setThread_busy(thread_busy);
                    }
                    if ("port".equals(mbAttributes[i].getName()))
                    {
                        int pool_port = (Integer) mbsc.getAttribute(objname, "port");
                        entity.setPool_port(pool_port);
                    }
                }
                dataLst.add(entity);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
    }

    private ObjectName getObjectName(String name)
    {
        ObjectName on = null;
        try
        {
            on = new ObjectName(name);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return on;
    }

    @Override
    public void close()
    {
        try
        {
            if (connector != null)
            {
                connector.close();
            }
        }
        catch (IOException ex)
        {
            logger.error(ex);
        }
        finally
        {
            connector = null;
        }
    }

    /**
     * @param args 参数
     * @throws Exception 主方法
     */
    public static void main(String[] args) throws Exception
    {
        MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
        TomcatCollector coll = new TomcatCollector();
        coll.setMonitorObject(modal.get(2192));
        coll.connect();
        coll.collect(new ArrayList<ObjAttribEntity>(), null);
        coll.close();
    }
}
