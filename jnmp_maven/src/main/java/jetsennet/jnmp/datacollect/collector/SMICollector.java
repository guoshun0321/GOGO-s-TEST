package jetsennet.jnmp.datacollect.collector;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMInstance;
import javax.wbem.cim.CIMProperty;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collector.AbsCollector;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;
import jetsennet.jbmp.util.PathUtils;
import jetsennet.jbmp.util.SMIBean;
import jetsennet.jnmp.exception.SMIException;
import jetsennet.jnmp.protocols.smi.AbstractSMIPtl;

/**
 * SMI-S采集器
 * @version 1.0
 * @author xli
 */
public class SMICollector extends AbsCollector
{

    private static final Logger logger = Logger.getLogger(SMICollector.class);

    private AbstractSMIPtl smi;

    @Override
    public void connect() throws CollectorException
    {
        logger.debug("连接服务器" + mo.getIpAddr());
        smi = AbstractSMIPtl.getInstance();
        boolean conn = false;
        try
        {
            conn = smi.init(mo.getIpAddr(), mo.getUserName(), mo.getUserPwd());
            // if (conn == false)
            if (!conn)
            {
                close();
                throw new SMIException("CIMOM连接失败");
            }
        }
        catch (SMIException e)
        {
            String msg = mo.getIpAddr() + ":上的SMI-S监控对象连接失败";
            logger.error(msg);
            throw new CollectorException(msg, e);
        }
    }

    @Override
    public Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg)
    {
        setTime(new Date());
        Map<String, String> map = getData(objAttrLst);
        Map<ObjAttribEntity, Object> result = new LinkedHashMap<ObjAttribEntity, Object>();
        for (ObjAttribEntity oa : objAttrLst)
        {
            String value = map.get(oa.getAttribParam());
            if (StringUtils.isEmpty(value))
            {
                continue;
            }
            CollData data = genCollData(oa, value, CollData.DATATYPE_PERF);
            result.put(oa, data);
        }
        return result;
    }

    /**
     * @param objAttrLst
     * @return
     */
    private Map<String, String> getData(List<ObjAttribEntity> objAttrLst)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (ObjAttribEntity obj : objAttrLst)
        {
            SMIBean bean = PathUtils.getStr(obj.getAttribParam());
            try
            {
                Enumeration<CIMInstance> enums = smi.enumerationInstance(bean);
                /**
                 * 确保取的值的行数不大于一
                 */
                while (enums.hasMoreElements())
                {
                    CIMInstance ins = enums.nextElement();
                    Vector<CIMProperty> v = ins.getProperties();
                    map.put(obj.getAttribParam(), v.firstElement().getValue().toString());
                }
            }
            catch (CIMException e)
            {
                logger.error("取值发生错误" + e);
                continue;
            }
        }
        return map;
    }

    /**
     * 生成采集数据
     * @param oa
     * @param value
     * @param dataType
     * @return
     */
    private CollData genCollData(ObjAttribEntity oa, String value, int dataType)
    {
        CollData data = new CollData();
        data.objID = oa.getObjId();
        data.objAttrID = oa.getObjAttrId();
        data.attrID = oa.getAttribId();
        data.dataType = dataType;
        data.value = value;
        data.srcIP = mo.getIpAddr();
        data.time = time;
        return data;
    }

    @Override
    public void close()
    {
        AbstractSMIPtl.closeClient();
    }

    /**
     * 主方法
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception
    {
        MObjectDal dal = ClassWrapper.wrapTrans(MObjectDal.class);
        MObjectEntity mo = dal.get(3382);
        SMICollector coll = new SMICollector();
        coll.setMonitorObject(mo);
        coll.connect();
        ObjAttribDal oad = ClassWrapper.wrapTrans(ObjAttribDal.class);
        List<ObjAttribEntity> list = oad.getByID(3382);
        Map<ObjAttribEntity, Object> map = coll.collect(list, null);
        Set<ObjAttribEntity> set = map.keySet();
        for (ObjAttribEntity objAttribEntity : set)
        {
            CollData colls = (CollData) map.get(objAttribEntity);
            System.out.println(colls.value);
        }
    }

}
