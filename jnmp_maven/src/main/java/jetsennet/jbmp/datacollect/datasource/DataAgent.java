package jetsennet.jbmp.datacollect.datasource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collector.CollectorStatistic;
import jetsennet.jbmp.datacollect.collector.EmptyCollector;
import jetsennet.jbmp.datacollect.collector.ICollector;
import jetsennet.jbmp.datacollect.collector.SNMPCollector;
import jetsennet.jbmp.datacollect.collector.SNMPCollectorProc;
import jetsennet.jbmp.datacollect.collectorif.transmsg.OutputEntity;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ScalarMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.datacollect.util.CollConstants;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * 数据代理
 * @author 郭祥
 */
public class DataAgent implements IDataAgent
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(DataAgent.class);

    @Override
    public TransMsg getData(TransMsg msg)
    {
        MObjectEntity mo = msg.getMo();
        if (mo != null)
        {
            List<ObjAttribEntity> oas = msg.getObjAttribs();

            // 根据监控对象的对象类型classType，查找配置文件，创建并初始化相应的采集器ICollector和ICollectorErrorHandler
            String classType = mo.getClassType();
            ICollector coll = (ICollector) createInstance(CollConstants.COLL_CLASS_CFG_PRE, classType, mo.getParentId() > 0);
            if (coll != null)
            {
                coll.setMonitorObject(mo);
                this.collect(msg, coll, mo, oas);
            }
            else
            {
                msg.setCollState(TransMsg.COLL_STATE_FAILD);
                msg.setMsg(String.format("找不到对象类型(%s)对应的采集器。", classType));
            }
        }
        else
        {
            msg.setCollState(TransMsg.COLL_STATE_FAILD);
            msg.setMsg("TransMsg未携带采集对象。");
        }
        return msg;
    }

    @Override
    public TransMsg getDataForIns(TransMsg msg)
    {
        MObjectEntity mo = msg.getMo();

        if (mo != null)
        {
            String classType = mo.getClassType();
            ICollector coll = (ICollector) createInstance(CollConstants.COLL_CLASS_CFG_PRE, classType, mo.getParentId() > 0);
            if (coll != null)
            {
                coll.setMonitorObject(mo);
                msg = coll.collectForIns(msg);
            }
        }
        else
        {
            msg.setCollState(TransMsg.COLL_STATE_FAILD);
            msg.setMsg("TransMsg未携带采集对象。");
        }
        return msg;
    }

    private void collect(TransMsg msg, ICollector coll, MObjectEntity mo, List<ObjAttribEntity> oas)
    {
        if (coll instanceof EmptyCollector)
        {
            logger.debug("空采集器，返回。");
            return;
        }

        try
        {
            long startTime = System.currentTimeMillis();

            // 建立连接
            coll.connect();

            // 采集数据
            Map<ObjAttribEntity, Object> tempMap = coll.collect(oas, msg);
            Map<Integer, Object> dataMap = new HashMap<Integer, Object>();
            for (ObjAttribEntity key : tempMap.keySet())
            {
                dataMap.put(key.getObjAttrId(), tempMap.get(key));
            }

            ScalarMsg scalars = msg.getScalar();
            if (scalars != null)
            {
                List<ObjAttribEntity> inputs = scalars.getInputs();
                for (ObjAttribEntity input : inputs)
                {
                    int objAttrId = input.getObjAttrId();
                    Object obj = dataMap.get(objAttrId);
                    scalars.addOutput(OutputEntity.genOutput(obj));
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            msg.setDuration(duration);
            CollectorStatistic.getInstance().add(coll.getClass(), mo.getObjId(), duration);
        }
        catch (Exception ex)
        {
            String errorMsg = String.format("监控对象:%s采集异常,异常原因:%s", mo.getObjName(), ex.getMessage());
            msg.setCollState(TransMsg.COLL_STATE_FAILD);
            msg.setMsg(errorMsg);
            logger.error(errorMsg, ex);
        }
        finally
        {
            // 关闭连接
            coll.close();
        }
    }

    /**
     * 创建类实例
     * @param pre
     * @param name
     * @param isSubObj
     * @return
     */
    private static Object createInstance(String pre, String name, boolean isSubObj)
    {
        String className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + name + XmlCfgUtil.CFG_POS, null);
        if (className == null)
        {
            if (!isSubObj)
            {
                className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + XmlCfgUtil.DEFAULT + XmlCfgUtil.CFG_POS, null);
            }
            else
            {
                className = XmlCfgUtil.getStringValue(CollConstants.COLL_CFG_FILE, pre + XmlCfgUtil.DEFAULT + "Sub" + XmlCfgUtil.CFG_POS, null);
            }
            if (className == null)
            {
                return null;
            }
        }
        try
        {
            Class target = Class.forName(className.trim());
            return target.newInstance();
        }
        catch (Exception e)
        {
            logger.error("创建类实例失败,类名:" + className, e);
            return null;
        }
    }

    @Override
    public Map<String, Map<String, VariableBinding>> snmpGetSubInfo(MObjectEntity mo, String[] oids)
    {
        Map<String, Map<String, VariableBinding>> retval = new LinkedHashMap<String, Map<String, VariableBinding>>();
        if (oids == null || oids.length == 0)
        {
            return retval;
        }
        AbsSnmpPtl snmp = AbsSnmpPtl.getInstance(mo.getVersion());
        try
        {
            snmp.init(mo.getIpAddr(), mo.getIpPort(), mo.getUserName());
            for (String column : oids)
            {
                try
                {
                    Map<String, VariableBinding> temp = snmp.snmpGetWithBegin(column);
                    retval.put(column, temp);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            try
            {
                snmp.close();
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                snmp = null;
            }
        }
        return retval;
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        SNMPCollectorProc proc = new SNMPCollectorProc();
        System.out.println(proc instanceof SNMPCollector);
        Class.forName("jetsennet.jbmp.datacollect.collector.SNMPCollector");
    }
}
