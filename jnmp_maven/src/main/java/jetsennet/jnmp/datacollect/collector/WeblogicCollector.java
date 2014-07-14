package jetsennet.jnmp.datacollect.collector;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jnmp.entity.WeblogicAppDataEntity;
import jetsennet.jnmp.entity.WeblogicEjbDataEntity;
import jetsennet.jnmp.entity.WeblogicSysDataEntity;

/**
 * @author lianghongjie Weblogic采集器
 */
public class WeblogicCollector extends AbsCollector
{
    private static final Logger logger = Logger.getLogger(WeblogicCollector.class);

    private static final ObjectName service;
    static
    {
        try
        {
            service =
                new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
        }
        catch (MalformedObjectNameException e)
        {
            throw new AssertionError(e.getMessage());
        }
    }

    /**
     * 连接信息
     */
    private JMXConnector connector;
    private MBeanServerConnection connection;

    @Override
    public void connect() throws CollectorException
    {
        connect("/jndi/weblogic.management.mbeanservers.runtime");
    }

    private void connect(String urlPath) throws CollectorException
    {
        try
        {
            JMXServiceURL serviceURL = new JMXServiceURL("t3", mo.getIpAddr(), mo.getIpPort(), urlPath);
            Hashtable<String, String> h = new Hashtable<String, String>();
            h.put(Context.SECURITY_PRINCIPAL, mo.getUserName());
            h.put(Context.SECURITY_CREDENTIALS, mo.getUserPwd());
            h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");

            connector = JMXConnectorFactory.connect(serviceURL, h);
            connection = connector.getMBeanServerConnection();
        }
        catch (Exception e)
        {
            logger.error(mo.getIpAddr() + ":" + mo.getIpPort() + "上的weblogic监控对象连接失败!");
            throw new CollectorException(e);
        }
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        reset();

        getSysData();
        getAppAndEjbData();

        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        Map<Integer, ObjAttribEntity> idMap = toIdMap(objAttrLst);
        for (Object data : dataLst)
        {
            if (data instanceof WeblogicAppDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof WeblogicEjbDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
            else if (data instanceof WeblogicSysDataEntity)
            {
                generateCollData(result, idMap, mo, data, CollData.DATATYPE_PERF);
            }
        }
        generateFailedData(result, objAttrLst);
        return result;
    }

    private void getSysData()
    {
        WeblogicSysDataEntity entity = new WeblogicSysDataEntity();
        entity.setObj_id(mo.getObjId());
        entity.setColl_time(time.getTime());
        ObjectName heapObjName = null;
        heapObjName = getObjectName("java.lang:type=Memory");

        if (heapObjName != null)
        {
            try
            {
                MemoryUsage heapMemoryUsage = MemoryUsage.from((CompositeDataSupport) connection.getAttribute(heapObjName, "HeapMemoryUsage"));
                long maxMemory = heapMemoryUsage.getMax(); // 堆最大
                long commitMemory = heapMemoryUsage.getCommitted(); // 堆当前分配
                long usedMemory = heapMemoryUsage.getUsed();
                double rate = usedMemory * 100 / commitMemory;
                entity.setHeap_max((int) (maxMemory / 1024 / 1024));
                entity.setHeap_commit((int) (commitMemory / 1024 / 1024));
                entity.setHeap_used((int) (usedMemory / 1024 / 1024));
                entity.setHeap_rate((int) rate);
                MemoryUsage nonheapMemoryUsage = MemoryUsage.from((CompositeDataSupport) connection.getAttribute(heapObjName, "NonHeapMemoryUsage"));
                long nonmaxMemory = nonheapMemoryUsage.getMax();
                long noncommitMemory = nonheapMemoryUsage.getCommitted();
                long nonusedMemory = nonheapMemoryUsage.getUsed();
                double rate1 = nonusedMemory * 100 / noncommitMemory;
                entity.setNonheap_max((int) (nonmaxMemory / 1024 / 1024));
                entity.setNonheap_commit((int) (noncommitMemory / 1024 / 1024));
                entity.setNonheap_used((int) (nonusedMemory / 1024 / 1024));
                entity.setNonheap_rate((int) rate1);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }

        ObjectName threadObjName = getObjectName("java.lang:type=Threading");
        if (threadObjName != null)
        {
            try
            {
                int thread_peak = (Integer) connection.getAttribute(threadObjName, "PeakThreadCount");
                entity.setThread_peak(thread_peak);
                int thread_current = (Integer) connection.getAttribute(threadObjName, "ThreadCount");
                entity.setThread_current(thread_current);
                int thread_daemon = (Integer) connection.getAttribute(threadObjName, "DaemonThreadCount");
                entity.setThread_daemon(thread_daemon);
                int thread_started = ((Long) connection.getAttribute(threadObjName, "TotalStartedThreadCount")).intValue();
                entity.setThread_started(thread_started);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }

        dataLst.add(entity);
    }

    /**
     * @return 结果
     * @throws Exception 异常
     */
    public ObjectName[] getServerRuntimes() throws Exception
    {
        return (ObjectName[]) connection.getAttribute(service, "ServerRuntimes");
    }

    private void getAppAndEjbData()
    {
        try
        {
            close();
            connect("/jndi/weblogic.management.mbeanservers.domainruntime");
        }
        catch (CollectorException ex)
        {
            logger.error(ex.getMessage());
            return;
        }

        try
        {
            ObjectName[] serverRT = (ObjectName[]) connection.getAttribute(service, "ServerRuntimes");
            int length = (int) serverRT.length;
            for (int i = 0; i < length; i++)
            {
                ObjectName[] appRT = (ObjectName[]) connection.getAttribute(serverRT[i], "ApplicationRuntimes");
                int appLength = (int) appRT.length;
                for (int x = 0; x < appLength; x++)
                {
                    ObjectName[] compRT = (ObjectName[]) connection.getAttribute(appRT[x], "ComponentRuntimes");
                    int compLength = (int) compRT.length;
                    for (int y = 0; y < compLength; y++)
                    {
                        String componentName = (String) connection.getAttribute(compRT[y], "Name");
                        String componentType = (String) connection.getAttribute(compRT[y], "Type");
                        if ("WebAppComponentRuntime".equals(componentType.trim()))
                        {
                            getAppData(compRT[y]);
                        }
                        if ("EJBComponentRuntime".equals(componentType.trim()) && !"mejb".equals(componentName.trim()))
                        {
                            ObjectName[] compEjbRTs = (ObjectName[]) connection.getAttribute(compRT[y], "EJBRuntimes");
                            for (ObjectName compEjbRT : compEjbRTs)
                            {
                                getEjbData(compEjbRT);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
    }

    private void getAppData(ObjectName objectName)
    {
        try
        {
            WeblogicAppDataEntity entity = new WeblogicAppDataEntity();
            entity.setObj_id(mo.getObjId());
            entity.setColl_time(time.getTime());
            String app_name = (String) connection.getAttribute(objectName, "ComponentName");
            if ("console".equals(app_name) || "consolehelp".equals(app_name) || "bea_wls9_async_response.war".equals(app_name)
                || "bea_wls_internal.war".equals(app_name))
            {
                return;
            }
            int conn_max = (Integer) connection.getAttribute(objectName, "OpenSessionsHighCount");
            int conn_active = (Integer) connection.getAttribute(objectName, "OpenSessionsCurrentCount");
            int conn_total = (Integer) connection.getAttribute(objectName, "SessionsOpenedTotalCount");
            entity.setApp_name(app_name);
            entity.setConn_max(conn_max);
            entity.setConn_active(conn_active);
            entity.setConn_total(conn_total);
            dataLst.add(entity);
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
    }

    private void getEjbData(ObjectName objectName)
    {
        WeblogicEjbDataEntity entity = new WeblogicEjbDataEntity();
        entity.setObj_id(mo.getObjId());
        entity.setColl_time(time.getTime());
        String domainString = objectName.getDomain();
        Hashtable<String, String> objectPropertyList = objectName.getKeyPropertyList();
        String name = objectPropertyList.get("Name");
        String type = objectPropertyList.get("Type");
        entity.setEjb_name(name);
        if (name == null || type == null)
        {
            return;
        }
        StringBuffer sb = new StringBuffer(domainString + ":");
        objectPropertyList.put("Type", "EJBPoolRuntime");
        objectPropertyList.put(type, name);
        Set<String> keys = objectPropertyList.keySet();
        for (String key : keys)
        {
            sb.append(key);
            sb.append("=");
            sb.append(objectPropertyList.get(key));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length());
        String ejbPoolString = sb.toString();

        ObjectName ejbPoolName = getObjectName(ejbPoolString);
        if (ejbPoolName != null)
        {
            try
            {
                int access_count = ((Long) connection.getAttribute(ejbPoolName, "AccessTotalCount")).intValue();
                int beans_use = (Integer) connection.getAttribute(ejbPoolName, "BeansInUseCount");
                int beans_usecurr = (Integer) connection.getAttribute(ejbPoolName, "BeansInUseCurrentCount");
                int beans_idle = (Integer) connection.getAttribute(ejbPoolName, "IdleBeansCount");
                int destroyed_count = ((Long) connection.getAttribute(ejbPoolName, "DestroyedTotalCount")).intValue();
                int miss_count = ((Long) connection.getAttribute(ejbPoolName, "MissTotalCount")).intValue();
                int waiter_current = (Integer) connection.getAttribute(ejbPoolName, "WaiterCurrentCount");
                int waiter_total = ((Long) connection.getAttribute(ejbPoolName, "WaiterTotalCount")).intValue();
                entity.setAccess_count(access_count);
                entity.setBeans_use(beans_use);
                entity.setBeans_idle(beans_idle);
                entity.setBeans_usecurr(beans_usecurr);
                entity.setDestroyed_count(destroyed_count);
                entity.setMiss_count(miss_count);
                entity.setWaiter_current(waiter_current);
                entity.setWaiter_total(waiter_total);

            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }

        sb = new StringBuffer(domainString + ":");
        objectPropertyList.put("Type", "EJBTransactionRuntime");
        keys = objectPropertyList.keySet();
        for (String key : keys)
        {
            sb.append(key);
            sb.append("=");
            sb.append(objectPropertyList.get(key));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        String ejbTranString = sb.toString();
        ObjectName ejbTranName = getObjectName(ejbTranString);
        if (ejbTranName != null)
        {
            try
            {
                int tran_commited = ((Long) connection.getAttribute(ejbTranName, "TransactionsCommittedTotalCount")).intValue();
                int tran_rollback = ((Long) connection.getAttribute(ejbTranName, "TransactionsRolledBackTotalCount")).intValue();
                int tran_timeout = ((Long) connection.getAttribute(ejbTranName, "TransactionsTimedOutTotalCount")).intValue();
                entity.setTran_commited(tran_commited);
                entity.setTran_rollback(tran_rollback);
                entity.setTran_timeout(tran_timeout);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
        dataLst.add(entity);
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
}
