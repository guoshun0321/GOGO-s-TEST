package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.ObjAttribValueDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.OutputEntity;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.ObjAttribValueEntity;
import jetsennet.jbmp.entity.QueryResult;
import jetsennet.jbmp.protocols.snmp.SnmpResult;
import jetsennet.jbmp.servlets.BMPServletContextListener;

/**
 * @author？
 */
public final class CollDataQuery
{
    private static final Logger logger = Logger.getLogger(CollDataQuery.class);
    private static CollDataQuery instance = new CollDataQuery();
    private ObjAttribDal objAttrDal = ClassWrapper.wrapTrans(ObjAttribDal.class);
    private ObjAttribValueDal objAttrValDal = ClassWrapper.wrapTrans(ObjAttribValueDal.class);

    private CollDataQuery()
    {
    }

    /**
     * 单例
     * @return 单例
     */
    public static CollDataQuery getInstance()
    {
        return instance;
    }

    /**
     * 从采集器中查询性能数据(只有性能数据)
     * @param objId 对象id
     * @param size 条数
     * @param objAttrIds 对象属性id列表
     * @return 性能数据
     */
    public Map<Integer, List<QueryResult>> query(int objId, int size, int[] objAttrIds)
    {
        Object res =
            BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "queryPerfData", new Object[] { objId, size, objAttrIds },
                new Class[] { int.class, int.class, int[].class }, true);
        return (Map<Integer, List<QueryResult>>) res;
    }

    /**
     * 从数据库中查询自定义属性或者配置信息、表格
     * @param objId 对象id
     * @param objAttrIds 对象属性id列表
     * @param isFresh false：直接从BMP_OBJATTRIBVALUE取值返回；true：采集完后返回值;
     * @return 属性值
     */
    public Map<Integer, QueryResult> query(int objId, int[] objAttrIds, boolean isFresh)
    {
        Map<Integer, QueryResult> result = new HashMap<Integer, QueryResult>();
        for (Integer objAttrId : objAttrIds)
        {
            QueryResult data = new QueryResult();
            result.put(objAttrId, data);
        }
        try
        {
            List<ObjAttribValueEntity> objAttrValLst = null;
            if (isFresh)
            {
                // 刷新配置数据，并将结果保存到数据库
                objAttrValLst = refreshCfgData(objId, objAttrIds);
            }
            if (objAttrValLst == null || objAttrValLst.size() != objAttrIds.length)
            {
                // 直接从数据库中取数据
                objAttrValLst = objAttrValDal.getLst(objId, toString(objAttrIds));
            }

            for (ObjAttribValueEntity entity : objAttrValLst)
            {
                QueryResult data = result.get(entity.getObjAttrId());
                if (data != null)
                {
                    data.setCollTime(entity.getCollTime());
                    data.setValue(entity.getStrValue());
                }
            }
        }
        catch (Exception e)
        {
            logger.error("查询对象属性值异常.", e);
        }
        return result;
    }

    /**
     * 刷新配置数据，并将结果保存到数据库
     * @param objId
     * @param objAttrIds
     * @param result
     * @return
     * @throws Exception
     */
    private List<ObjAttribValueEntity> refreshCfgData(int objId, int[] objAttrIds) throws Exception
    {
        // 组装请求消息
        TransMsg req = new TransMsg();
        req.setObjId(objId);
        ArrayList<ObjAttribEntity> oaLst = objAttrDal.getCfgObjAttrib(objId, toString(objAttrIds));
        for (ObjAttribEntity entity : oaLst)
        {
            if (entity.getAttribType() == AttribClassEntity.CLASS_LEVEL_CONFIG)
            {
                req.addScalar(entity);
            }
            else
            {
                req.addTable(entity);
            }
        }
        
        SnmpResult snmpR = new SnmpResult();
        snmpR.init(req);
        req.setRecInfo(snmpR);

        // 请求下发
        Object res =
            BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "remoteCollData", new Object[] { req },
                new Class[] { TransMsg.class }, true);

        // 处理响应数据
        List<ObjAttribValueEntity> resultLst = new ArrayList<ObjAttribValueEntity>();
        if (res != null)
        {
            TransMsg rsp = (TransMsg) res;

            // 处理标量数据
            if (rsp.getScalar() != null && rsp.getScalar().getOutputs() != null)
            {
                ArrayList<ObjAttribEntity> scaLst = rsp.getScalar().getInputs();
                ArrayList<OutputEntity> scaValLst = rsp.getScalar().getOutputs();
                for (int i = 0; i < scaLst.size(); i++)
                {
                    ObjAttribValueEntity objAttrVal = new ObjAttribValueEntity();
                    objAttrVal.setObjId(objId);
                    objAttrVal.setObjAttrId(scaLst.get(i).getObjAttrId());
                    objAttrVal.setCollTime(System.currentTimeMillis());
                    objAttrVal.setStrValue(scaValLst.get(i).getValue());
                    objAttrValDal.insertOrUpdate(objAttrVal);
                    resultLst.add(objAttrVal);
                }
            }

            // 处理表量数据
            if (rsp.getColumns() != null && rsp.getColumns().getColumns() != null)
            {
                ArrayList<ColumnMsg> colLst = rsp.getColumns().getColumns();
                for (int i = 0; i < colLst.size(); i++)
                {
                    ColumnMsg col = colLst.get(i);
                    ObjAttribValueEntity objAttrVal = new ObjAttribValueEntity();
                    objAttrVal.setObjId(objId);
                    objAttrVal.setObjAttrId(col.getInput().getObjAttrId());
                    objAttrVal.setCollTime(System.currentTimeMillis());
                    objAttrVal.setStrValue(buildColumnValue(col));
                    objAttrValDal.insertOrUpdate(objAttrVal);
                    resultLst.add(objAttrVal);
                }
            }
        }
        return resultLst;
    }

    private String toString(int[] objAttrIds)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objAttrIds.length; i++)
        {
            builder.append(objAttrIds[i]);
            if (i < objAttrIds.length - 1)
            {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    /**
     * 将采集到的列信息组装成字符串格式
     * @param col
     * @return
     */
    private String buildColumnValue(ColumnMsg col)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<DataSource>");
        for (OutputEntity value : col.getOutputs())
        {
            builder.append("<DataTable><VALUE>");
            String columnValue = value.toXmlValue();
            if (!"valueErr".equals(columnValue))
            {
                builder.append(columnValue);
            }
            builder.append("</VALUE></DataTable>");
        }
        builder.append("</DataSource>");
        return builder.toString();
    }

    /**
     * 针对单个对象，从数据库中查询配置信息或者从采集器中查询性能数据(所有类型)
     * @param objId 对象id
     * @param params QueryParam列表
     * @return 属性值或者性能数据
     */
    public Map<Integer, List<QueryResult>> query(int objId, QueryParam[] params)
    {
        Map<Integer, List<QueryResult>> result = new HashMap<Integer, List<QueryResult>>();

        // 分离出普通属性、配置属性和性能指标
        List<Integer> commonAttrs = new ArrayList<Integer>();
        List<Integer> confAttrs = new ArrayList<Integer>();
        Map<Integer, QueryParam> perfMap = new HashMap<Integer, QueryParam>();
        for (QueryParam param : params)
        {
            if (param.attrType == AttribClassEntity.CLASS_LEVEL_CONFIG || param.attrType == AttribClassEntity.CLASS_LEVEL_TABLE)
            {
                commonAttrs.add(param.objAttrId);
                confAttrs.add(param.objAttrId);
            }
            else if (param.attrType == AttribClassEntity.CLASS_LEVEL_CUSTOM)
            {
                commonAttrs.add(param.objAttrId);
            }
            else if (param.attrType == AttribClassEntity.CLASS_LEVEL_PERF)
            {
                QueryParam oldParam = perfMap.get(param.getObjAttrId());
                if (oldParam != null && oldParam.getFetchSize() > param.getFetchSize())
                {
                    continue;
                }
                param.setFetchTimes(param.getFetchSize() * 300);
                perfMap.put(param.getObjAttrId(), param);
            }
        }

        // 刷新配置属性
        if (confAttrs.size() > 0)
        {
            int[] objAttrs = new int[confAttrs.size()];
            for (int i = 0; i < objAttrs.length; i++)
            {
                objAttrs[i] = confAttrs.get(i);
            }
            try
            {
                refreshCfgData(objId, objAttrs);
            }
            catch (Exception e)
            {
                logger.error("刷新对象属性值异常.", e);
            }
        }

        // 先从数据库查询配置信息
        try
        {
            List<ObjAttribEntity> entitys = objAttrDal.getObjAttrAndValByObjId(objId);
            for (ObjAttribEntity entity : entitys)
            {
                if (commonAttrs.indexOf(entity.getObjAttrId()) >= 0)
                {
                    List<QueryResult> datas = result.get(entity.getObjAttrId());
                    if (datas == null)
                    {
                        datas = new ArrayList<QueryResult>();
                        result.put(entity.getObjAttrId(), datas);
                    }
                    QueryResult data = new QueryResult();
                    data.setValue(entity.getAttribValue());
                    datas.add(data);
                    continue;
                }
                if (perfMap.containsKey(entity.getObjAttrId()))
                {
                    QueryParam param = perfMap.get(entity.getObjAttrId());
                    int collTimespan = entity.getCollTimespan() > 0 ? entity.getCollTimespan() : 300;
                    param.setFetchTimes(param.getFetchSize() * collTimespan);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("查询对象属性值异常.", e);
        }

        // 再从采集器查询性能数据
        Map<Integer, List<QueryResult>> res = queryRemote(objId, perfMap);
        if (res != null)
        {
            result.putAll((Map<Integer, List<QueryResult>>) res);
        }

        return result;
    }

    /**
     * 针对多个对象，从数据库中查询配置信息或者从采集器中查询性能数据(所有类型)
     * @param params QueryParam列表
     * @return 属性值或者性能数据
     */
    public Map<Integer, List<QueryResult>> query(QueryParam[] params)
    {
        Map<Integer, List<QueryResult>> result = new HashMap<Integer, List<QueryResult>>();

        // 分离出配置属性和性能指标
        List<Integer> confAttrs = new ArrayList<Integer>();
        Map<Integer, Map<Integer, QueryParam>> objMap = new HashMap<Integer, Map<Integer, QueryParam>>();
        for (QueryParam param : params)
        {
            Map<Integer, QueryParam> perfMap = objMap.get(param.getObjId());
            if (perfMap == null)
            {
                perfMap = new HashMap<Integer, QueryParam>();
                objMap.put(param.getObjId(), perfMap);
            }
            if (param.attrType == AttribClassEntity.CLASS_LEVEL_CUSTOM || param.attrType == AttribClassEntity.CLASS_LEVEL_CONFIG
                || param.attrType == AttribClassEntity.CLASS_LEVEL_TABLE)
            {
                confAttrs.add(param.objAttrId);
            }
            else if (param.attrType == AttribClassEntity.CLASS_LEVEL_PERF)
            {
                QueryParam oldParam = perfMap.get(param.getObjAttrId());
                if (oldParam != null && oldParam.getFetchSize() > param.getFetchSize())
                {
                    continue;
                }
                param.setFetchTimes(param.getFetchSize() * 300);
                perfMap.put(param.getObjAttrId(), param);
            }
        }

        // 先从数据库查询配置信息
        try
        {
            List<ObjAttribEntity> entitys = objAttrDal.getObjAttrAndValByObjIds(toString(objMap.keySet().toArray()));
            for (ObjAttribEntity entity : entitys)
            {
                if (confAttrs.indexOf(entity.getObjAttrId()) >= 0)
                {
                    List<QueryResult> datas = result.get(entity.getObjAttrId());
                    if (datas == null)
                    {
                        datas = new ArrayList<QueryResult>();
                        result.put(entity.getObjAttrId(), datas);
                    }
                    QueryResult data = new QueryResult();
                    data.setValue(entity.getAttribValue());
                    datas.add(data);
                    continue;
                }
                Map<Integer, QueryParam> perfMap = objMap.get(entity.getObjId());
                if (perfMap.containsKey(entity.getObjAttrId()))
                {
                    QueryParam param = perfMap.get(entity.getObjAttrId());
                    int collTimespan = entity.getCollTimespan() > 0 ? entity.getCollTimespan() : 300;
                    param.setFetchTimes(param.getFetchSize() * collTimespan);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("查询对象属性值异常.", e);
        }

        // 对每个对象，通过集群下发性能查询，然后组装结果，若有n个对象，会查询n次
        for (Map.Entry<Integer, Map<Integer, QueryParam>> entry : objMap.entrySet())
        {
            Map<Integer, List<QueryResult>> res = queryRemote(entry.getKey(), entry.getValue());
            if (res != null)
            {
                result.putAll((Map<Integer, List<QueryResult>>) res);
            }
        }

        return result;
    }

    private String toString(Object[] args)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++)
        {
            if (i == 0)
            {
                builder.append(args[i]);
            }
            else
            {
                builder.append(",").append(args[i]);
            }
        }
        return builder.toString();
    }

    private Map<Integer, List<QueryResult>> queryRemote(int objId, Map<Integer, QueryParam> paramMap)
    {
        int size = paramMap.size();
        if (size == 0)
        {
            return null;
        }
        int[] iPerfAttrs = new int[size];
        int[] iPerfSizes = new int[size];
        int[] iFetchTimes = new int[size];
        int i = 0;
        for (QueryParam param : paramMap.values())
        {
            iPerfAttrs[i] = param.getObjAttrId();
            iPerfSizes[i] = param.getFetchSize();
            iFetchTimes[i] = param.getFetchTimes();
            i++;
        }
        Object res =
            BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "queryPerfData",
                new Object[] { objId, iPerfSizes, iFetchTimes, iPerfAttrs }, new Class[] { int.class, int[].class, int[].class, int[].class }, true);
        return (Map<Integer, List<QueryResult>>) res;
    }

    /**
     * 从采集器中查询性能数据
     * @param objId 对象id
     * @param startTime 开始时间(单位是秒)
     * @param endTime 结束时间(单位是秒)
     * @param objAttrIds 对象属性id列表
     * @return 性能数据
     */
    public Map<Integer, List<QueryResult>> query(int objId, long startTime, long endTime, int[] objAttrIds)
    {
        Object res =
            BMPServletContextListener.getInstance().callRemote(Integer.toString(objId), "queryPerfData",
                new Object[] { objId, startTime, endTime, objAttrIds }, new Class[] { int.class, long.class, long.class, int[].class }, true);
        return (Map<Integer, List<QueryResult>>) res;
    }

    /**
     * 性能查询参数
     * @author lianghongjie
     */
    public static class QueryParam
    {
        private int objId;
        private int objAttrId;
        private int attrType;
        private int fetchSize;
        private int fetchTimes;

        /**
         * @return the fetchTimes
         */
        public int getFetchTimes()
        {
            return fetchTimes;
        }

        /**
         * @param fetchTimes the fetchTimes to set
         */
        public void setFetchTimes(int fetchTimes)
        {
            this.fetchTimes = fetchTimes;
        }

        public int getObjId()
        {
            return objId;
        }

        public void setObjId(int objId)
        {
            this.objId = objId;
        }

        public int getObjAttrId()
        {
            return objAttrId;
        }

        public void setObjAttrId(int objAttrId)
        {
            this.objAttrId = objAttrId;
        }

        public int getAttrType()
        {
            return attrType;
        }

        public void setAttrType(int attrType)
        {
            this.attrType = attrType;
        }

        public int getFetchSize()
        {
            return fetchSize;
        }

        public void setFetchSize(int fetchSize)
        {
            this.fetchSize = fetchSize;
        }
    }
}
