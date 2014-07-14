package jetsennet.jnmp.datacollect.collectorack;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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

import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.AbsCollectorIf;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jnmp.entity.TomcatAppDataEntity;
import jetsennet.jnmp.entity.TomcatSysDataEntity;
import jetsennet.jnmp.entity.TomcatThreadPoolEntity;

/**
 * tomcat采集器
 * @author x.li
 */
public class TomcatACKCollector extends AbsCollectorIf
{
    private static final Logger logger = Logger.getLogger(TomcatACKCollector.class);

    /**
     * 连接信息
     */
    private JMXConnector connector;
    private MBeanServerConnection mbsc;
    Map<String, Object> values = new HashMap<String, Object>();

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

    @Override
    public TransMsg collect(TransMsg tranMsg)
    {
        ArrayList<ObjAttribEntity> oaeList = tranMsg.getScalar().getInputs();
        if (null == oaeList)
        {
            return tranMsg;
        }

        getSysData();
        getAppData();
        getThreadPoolData();

        // 输出集合
        ArrayList<String> valuesList = new ArrayList<String>();
        // 采集属性集合
        List<String> list = new ArrayList<String>();
        AttributeDal dal = ClassWrapper.wrapTrans(AttributeDal.class);
        // 获取采集属性列表
        for (ObjAttribEntity objAttribEntity : oaeList)
        {
            try
            {
                AttributeEntity attributeEntity = dal.get(objAttribEntity.getAttribId());
                if (null != attributeEntity)
                {
                    // 应修改为objAttribEntity.getAttribValue();
                    list.add(dal.get(objAttribEntity.getAttribId()).getAttribCode());
                }
                else
                {
                    list.add("");
                }

            }
            catch (Exception e)
            {
                list.add("");
                logger.error(e);
                continue;
            }

            for (String str : list)
            {
                if (!values.isEmpty())
                {
                    Object object = values.get(str);
                    if (null != object || !"".equals(object))
                    {
                        valuesList.add(object.toString());
                    }
                    else
                    {
                        valuesList.add("");
                    }
                }
            }
        }
        tranMsg.getScalar().addOutput(valuesList);
        return tranMsg;
    }

    private void getSysData()
    {
        TomcatSysDataEntity entity = new TomcatSysDataEntity();
        entity.setObj_id(mo.getObjId());
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
                entity.setHeap_max((int) (maxMemory / 1024 / 1024));
                entity.setHeap_commit((int) (commitMemory / 1024 / 1024));
                entity.setHeap_used((int) (usedMemory / 1024 / 1024));
                entity.setHeap_rate((int) rate);

                MemoryUsage nonheapMemoryUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(heapObjName, "NonHeapMemoryUsage"));
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
            getVauesMap(entity);
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
            entity.setApp_name(obj.getKeyProperty("path"));
            try
            {
                ObjectName objname = new ObjectName(obj.getCanonicalName());
                int conn_max = (Integer) mbsc.getAttribute(objname, "maxActiveSessions");
                int conn_active = (Integer) mbsc.getAttribute(objname, "activeSessions");
                int conn_count = (Integer) mbsc.getAttribute(objname, "sessionCounter");
                entity.setConn_max(conn_max);
                entity.setConn_active(conn_active);
                entity.setConn_count(conn_count);
                getVauesMap(entity);
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
                getVauesMap(entity);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }
    }

    private Map<String, Object> getVauesMap(Object obj)
    {
        Field[] fields = obj.getClass().getDeclaredFields();
        Class<?> clazz = obj.getClass();
        for (int i = 0; i < fields.length; i++)
        {
            String fieldName = fields[i].getName();
            Object value = null;
            try
            {
                value = new PropertyDescriptor(fields[i].getName(), clazz).getReadMethod().invoke(obj);
            }
            catch (Exception e)
            {
                logger.equals(e);
                values.put(fieldName, value);
                continue;
            }
            values.put(fieldName, value);
        }
        return values;
    }

}
