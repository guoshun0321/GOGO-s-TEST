/************************************************************************
 日 期：2012-8-20
 作 者: 梁宏杰
 版 本: v1.3
 描 述: 
 历 史:
 ************************************************************************/
package jetsennet.jbmp.datacollect.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.TableInfo;
import jetsennet.jbmp.dataaccess.base.TableInfoMgr;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.CollectorException;

/**
 * 采集器
 * @author 梁宏杰
 */
public abstract class AbsCollector implements ICollector
{

    /**
     * 监控对象
     */
    protected MObjectEntity mo;

    /**
     * 父采集器
     */
    protected ICollector parentColl;

    /**
     * 采集时间
     */
    protected Date time = new Date();

    /**
     * 采集数据列表
     */
    protected List<Object> dataLst = new ArrayList<Object>();

    /**
     * rrd数据是否已经放到CollData对象中
     */
    protected boolean dataFlag = false;

    /**
     * rrd数据
     */
    protected Map<String, Double> rrdValues = null;

    /**
     * 属性id表
     */
    private static Map<Integer, Map<String, Integer>> attrIdMap = new ConcurrentHashMap<Integer, Map<String, Integer>>();

    private static final Logger logger = Logger.getLogger(AbsCollector.class);

    @Override
    public void setMonitorObject(MObjectEntity mo)
    {
        this.mo = mo;
    }

    @Override
    public void setParentCollector(ICollector parentColl)
    {
        this.parentColl = parentColl;
    }

    /*
     * (non-Javadoc)
     * @see jetsennet.jnmp.datacollect.collector.ICollector#getParentCollector()
     */
    @Override
    public ICollector getParentCollector()
    {
        return this.parentColl;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public List<Object> getDataLst()
    {
        return dataLst;
    }

    public void setDataLst(List<Object> dataLst)
    {
        this.dataLst = dataLst;
    }

    /**
     * 重设
     */
    public void reset()
    {
        time = new Date();
        dataLst.clear();
        dataFlag = false;
        rrdValues = null;
    }

    @Override
    public abstract void connect() throws CollectorException;

    @Override
    public abstract Map<ObjAttribEntity, Object> collect(List<ObjAttribEntity> objAttrLst, TransMsg msg);

    @Override
    public TransMsg collectForIns(TransMsg msg)
    {
        try
        {
            List<ObjAttribEntity> oas = msg.getObjAttribs();
            this.connect();
            Map<ObjAttribEntity, Object> valMap = this.collect(oas, msg);
            msg.fillWithResult(valMap);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            this.close();
        }
        return msg;
    }

    @Override
    public abstract void close();

    /**
     * 转换为map
     * @param objAttrLst
     * @return
     */
    protected static Map<Integer, ObjAttribEntity> toIdMap(List<ObjAttribEntity> objAttrLst)
    {
        Map<Integer, ObjAttribEntity> idMap = new HashMap<Integer, ObjAttribEntity>();
        for (ObjAttribEntity objAttr : objAttrLst)
        {
            idMap.put(objAttr.getAttribId(), objAttr);
        }
        return idMap;
    }

    /**
     * 补充采集失败的属性到采集结果中
     * @param result
     * @param objAttrLst
     */
    protected static void generateFailedData(Map<ObjAttribEntity, Object> result, List<ObjAttribEntity> objAttrLst)
    {
        for (ObjAttribEntity objAttr : objAttrLst)
        {
            if (!result.containsKey(objAttr))
            {
                result.put(objAttr, null);
            }
        }
    }

    /**
     * 生成采集数据表
     * @param result 参数
     * @param idMap 参数
     * @param mo 参数
     * @param obj 参数
     * @param dataType 参数
     */
    protected void generateCollData(Map<ObjAttribEntity, Object> result, Map<Integer, ObjAttribEntity> idMap, MObjectEntity mo, Object obj,
            int dataType)
    {
        if (rrdValues == null)
        {
            rrdValues = new HashMap<String, Double>(result.size());
        }
        TableInfo info = TableInfoMgr.getTableInfo(obj.getClass());
        for (int i = 0; i < info.columns.length; i++)
        {
            Integer attriId = getAttriId(mo.getClassId(), info.tableName + ":" + info.columnNames[i]);
            ObjAttribEntity oa = idMap.get(attriId);
            if (oa == null)
            {
                continue;
            }
            try
            {
                CollData data = createCollData(mo, oa, info.getterMethods[i].invoke(obj), dataType);
                addToMap(result, oa, data);
                if (oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_PERF)
                {
                    rrdValues.put(Integer.toString(oa.getObjAttrId()), Double.parseDouble(data.value));
                    if (!dataFlag)
                    {
                        data.put(CollData.PARAMS_DATA, rrdValues);
                        dataFlag = true;
                    }
                }
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 生成采集数据
     * @param mo 参数
     * @param oa 参数
     * @param object 参数
     * @param dataType 参数
     * @return
     */
    protected CollData createCollData(MObjectEntity mo, ObjAttribEntity oa, Object object, int dataType)
    {
        CollData data = new CollData();
        data.objID = mo.getObjId();
        data.objAttrID = oa.getObjAttrId();
        data.attrID = oa.getAttribId();
        data.dataType = dataType;
        data.value = toString(object);
        data.srcIP = mo.getIpAddr();
        data.time = time;
        return data;
    }

    /**
     * 转换为字符串
     * @param object
     * @return
     */
    private static String toString(Object object)
    {
        if (object == null)
        {
            return "";
        }
        return object.toString();
    }

    /**
     * 将采集数据添加到采集结果中
     * @param map
     * @param oa
     * @param data
     */
    private static void addToMap(Map<ObjAttribEntity, Object> map, ObjAttribEntity oa, CollData data)
    {
        Object obj = map.get(oa);
        if (obj == null)
        {
            map.put(oa, data);
            return;
        }

        if (obj instanceof List)
        {
            ((List<CollData>) obj).add(data);
            return;
        }

        List<CollData> list = new ArrayList<CollData>();
        list.add((CollData) obj);
        list.add(data);
        map.put(oa, list);
    }

    /**
     * 查找属性id
     * @param classId
     * @param key
     * @return
     */
    private static Integer getAttriId(int classId, String key)
    {
        Map<String, Integer> idMap = attrIdMap.get(classId);
        if (idMap == null)
        {
            idMap = loadIdMap(classId);
        }
        return idMap.get(key);
    }

    /**
     * 加载classId对应的属性信息
     * @param classId
     * @return
     */
    private synchronized static Map<String, Integer> loadIdMap(int classId)
    {
        Map<String, Integer> idMap = attrIdMap.get(classId);
        if (idMap != null)
        {
            return idMap;
        }
        idMap = new HashMap<String, Integer>();
        try
        {
            AttributeDal abService = ClassWrapper.wrapTrans(AttributeDal.class);
            ArrayList<AttributeEntity> abLst = abService.getByType(classId);
            if (abLst != null)
            {
                for (AttributeEntity ab : abLst)
                {
                    if (ab.getAttribCode() != null && ab.getAttribCode().length() > 0)
                    {
                        idMap.put(ab.getAttribCode(), ab.getAttribId());
                    }
                }
            }
            attrIdMap.put(classId, idMap);
            return idMap;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return idMap;
        }
    }

    /**
     * 生成空结果
     * @param oaList
     * @return
     */
    protected Map<ObjAttribEntity, Object> genEmptyResult(List<ObjAttribEntity> oaList)
    {
        Map<ObjAttribEntity, Object> retval = new HashMap<ObjAttribEntity, Object>();
        if (oaList != null)
        {
            for (ObjAttribEntity oa : oaList)
            {
                retval.put(oa, null);
            }
        }
        return retval;
    }
}
