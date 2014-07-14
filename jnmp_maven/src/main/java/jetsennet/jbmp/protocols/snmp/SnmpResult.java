package jetsennet.jbmp.protocols.snmp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ColumnsMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.ScalarMsg;
import jetsennet.jbmp.datacollect.collectorif.transmsg.TransMsg;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.exception.SnmpException;
import jetsennet.jbmp.formula.FormulaCache;
import jetsennet.jbmp.formula.FormulaElement;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.EditNode;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTrans;
import jetsennet.jbmp.util.BMPConstants;
import jetsennet.jbmp.util.OIDUtil;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

/**
 * SNMP采集结果
 * @author 郭祥
 */
public class SnmpResult implements Serializable
{

    /**
     * 数据转换
     */
    private SnmpValueTrans handle;
    /**
     * 单个OID的采集结果，结果可能是多个值
     */
    public Map<String, SnmpResultColl> map;
    /**
     * OID映射到值
     */
    public Map<String, String> resultMap;
    /**
     * OID映射到枚举值
     */
    public Map<String, String> enumValueMap;
    /**
     * 扫描信息
     */
    private SnmpScanInfo info;
    /**
     * 序列化编号
     */
    private static final long serialVersionUID = -1L;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpResult.class);

    /**
     * 构造函数
     */
    public SnmpResult()
    {
        handle = new SnmpValueTrans();
        info = new SnmpScanInfo();
        map = new HashMap<String, SnmpResultColl>();
        resultMap = new HashMap<String, String>();
        enumValueMap = new HashMap<String, String>();
    }

    /**
     * 初始化
     * @param msg
     */
    public void init(TransMsg msg)
    {
        // 添加标量数据
        ScalarMsg scalarMsg = msg.getScalar();
        if (null != scalarMsg)
        {
            List<ObjAttribEntity> objAttrLst = scalarMsg.getInputs();
            for (ObjAttribEntity oa : objAttrLst)
            {
                // 设置field1字段为NULL，用于后面数据扫描时区分标量和表格
                oa.setField1(null);
                this.add(oa.getAttribParam(), oa.getField1());
            }
        }

        // 添加表格数据
        ColumnsMsg columnsMsg = msg.getColumns();
        if (null != columnsMsg)
        {
            List<ColumnMsg> cols = columnsMsg.getColumns();
            if (cols != null)
            {
                for (ColumnMsg col : cols)
                {
                    ObjAttribEntity oa = col.getInput();
                    this.add(oa.getAttribParam(), oa.getField1());
                }
            }
        }
    }

    /**
     * 添加需要解析的公式
     * 
     * @param formulaStr 参数
     * @param index 参数
     */
    public void add(String formulaStr, String index)
    {
        info.parseFormula(formulaStr, index);
    }

    /**
     * 扫描
     * @param ip ip
     * @param version 版本
     * @param port 端口
     * @param community 参数
     */
    public void scan(String ip, String version, int port, String community)
    {
        info.scan(ip, version, port, community);
    }

    /**
     * 确认结果集合
     * @param oriOid
     * @return
     */
    private SnmpResultColl ensureColl(String oriOid)
    {
        SnmpResultColl retval = map.get(oriOid);
        if (retval == null)
        {
            retval = new SnmpResultColl();

            // 计算存储于数据库中的OID
            if (oriOid.endsWith(".0"))
            {
                int oidLength = oriOid.length();
                retval.dbOid = oriOid.substring(0, oidLength - 2);
            }
            else
            {
                retval.dbOid = oriOid;
            }
            map.put(oriOid, retval);
        }
        return retval;
    }

    /**
     * 设置枚举类型数据的值
     * @param mibId
     */
    public void setEnumValue(int mibId)
    {
        SnmpNodesDal sndal = ClassWrapper.wrapTrans(SnmpNodesDal.class);
        enumValueMap = sndal.getEnumValue(mibId, info.oids);
    }

    /**
     * 根据OID获取值
     * 
     * @param oid 
     * @param isEnum 是否从枚举加载数据
     * @return
     */
    public String getValue(String oid, boolean isEnum)
    {
        String retval = null;
        String value = resultMap.get(oid);
        if (isEnum && enumValueMap != null)
        {
            retval = enumValueMap.get(oid + "/" + value);
        }
        if (retval == null)
        {
            retval = value;
        }
        return retval;
    }

    /**
     * 扫描类
     */
    private class SnmpScanInfo implements Serializable
    {

        /**
         * 标量OID
         */
        private ArrayList<String> scalarOids;
        /**
         * 表格
         */
        private ArrayList<SnmpTable> tables;
        /**
         * 需要扫描的OID
         */
        private List<String> oids;
        /**
         * 索引到表格的映射
         */
        private Map<String, SnmpTable> name2Table;
        /**
         * 序列化编号
         */
        private static final long serialVersionUID = -1L;

        public SnmpScanInfo()
        {
            scalarOids = new ArrayList<String>();
            tables = new ArrayList<SnmpTable>();
            name2Table = new HashMap<String, SnmpTable>();
            oids = new ArrayList<String>();
        }

        /**
         * 解析公式
         * @param calFormula
         * @throws InstanceException 在找不到对应节点时，抛出异常
         */
        public void parseFormula(String formulaStr, String index)
        {
            try
            {
                FormulaElement root = FormulaCache.getInstance().getFormula(formulaStr);
                if (root == null)
                {
                    return;
                }
                String[] oids = root.getOids();
                if (oids != null)
                {
                    for (String oid : oids)
                    {
                        this.handleParseResult(oid, index);
                    }
                }
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
        }

        /**
         * 处理公式解析后的结果
         */
        private void handleParseResult(String oid, String index)
        {
            // index为null的全部当标量处理
            if (index == null || OIDUtil.isScalar(oid))
            {
                if (!scalarOids.contains(oid))
                {
                    scalarOids.add(oid);
                }
                oid = oid.substring(0, oid.length() - 2);
                if (!oids.contains(oid))
                {
                    oids.add(oid);
                }
            }
            else
            {
                SnmpTable table = name2Table.get(index);
                if (table == null)
                {
                    table = new SnmpTable(index);
                    table.setIndex(index);
                    name2Table.put(index, table);
                    tables.add(table);
                }
                table.addColumn(oid);
                if (!oids.contains(oid))
                {
                    oids.add(oid);
                }
            }
        }

        /**
         * 扫描，并填充数据
         */
        public void scan(String ip, String version, int port, String community)
        {
            AbsSnmpPtl snmp = null;
            try
            {
                snmp = AbsSnmpPtl.getInstance(version);
                if (snmp == null)
                {
                    throw new InstanceException("无法初始化snmp执行器");
                }
                this.scanNodes(snmp, ip, port, community);
                this.scanTables(snmp, ip, port, community, version);
            }
            catch (Exception ex)
            {
                logger.error("", ex);
            }
            finally
            {
                if (snmp != null)
                {
                    try
                    {
                        snmp.close();
                    }
                    catch (SnmpException ex)
                    {
                        logger.error("", ex);
                    }
                    finally
                    {
                        snmp = null;
                    }
                }
            }
        }

        /**
         * 填充标量类型的数据
         */
        private void scanNodes(AbsSnmpPtl snmp, String ip, int port, String community) throws InstanceException
        {
            if (scalarOids == null || scalarOids.isEmpty())
            {
                return;
            }
            Map<String, VariableBinding> binds = null;
            try
            {
                String[] oids = new String[scalarOids.size()];
                for (int i = 0; i < scalarOids.size(); i++)
                {
                    String ooid = scalarOids.get(i);
                    oids[i] = ooid;
                }
                snmp.init(ip, port, community);
                logger.debug("准备扫描OID集合：" + oids);
                binds = snmp.snmpGet(oids);
            }
            catch (SnmpException ex)
            {
                throw new InstanceException(ex);
            }
            if (binds != null)
            {
                Set<String> oids = binds.keySet();
                for (String oid : oids)
                {
                    SnmpResultColl coll = ensureColl(oid);
                    VariableBinding temp = binds.get(oid);
                    if (temp != null)
                    {
                        SnmpResultSingle srs = new SnmpResultSingle();
                        srs.oriOid = oid;
                        srs.transOid = oid;
                        srs.binding = temp;
                        srs.value = handle.handle(temp, BMPConstants.DEFAULT_SNMP_CODING, -1);
                        coll.single = srs;
                        String enumValue = enumValueMap.get(oid.substring(0, oid.length() - 2) + "/" + srs.value);
                        srs.enumValue = enumValue;
                        enumValueMap.put(srs.transOid + "/" + srs.value, srs.enumValue);
                        resultMap.put(oid, srs.value);
                    }
                    else
                    {
                        coll.wrongStr = "标量<" + oid + ">未取到值。";
                        resultMap.put(oid, null);
                    }
                }
            }
        }

        /**
         * 填充表格类型的数据
         */
        private void scanTables(AbsSnmpPtl snmp, String ip, int port, String community, String version)
        {
            for (int i = 0; i < tables.size(); i++)
            {
                SnmpTable tTable = tables.get(i);
                try
                {
                    logger.debug("准备扫描表格：" + tTable);
                    SnmpTableUtil.initSnmpTable(tTable, snmp, version, ip, port, community);
                    logger.debug("扫描表格结束：" + tTable);
                    this.addTable(tTable);
                }
                catch (SnmpException ex)
                {
                    tTable.setCells(null);
                    logger.error("", ex);
                }
            }
        }

        /**
         * 把Table添加到结果集
         * @param table
         */
        private void addTable(SnmpTable table)
        {
            int colNum = table.getColumnNum();
            String[] headOids = table.getHeaderOids();
            for (int i = 0; i < colNum; i++)
            {
                ArrayList<EditNode> nodes = table.getColumn(i);
                SnmpResultColl coll = ensureColl(headOids[i]);

                // 确保表格型数据的List<SnmpResultSingle>不为null
                coll.addToColl(null);
                for (EditNode node : nodes)
                {
                    SnmpResultSingle single = new SnmpResultSingle();

                    // 原始OID
                    single.oriOid = headOids[i];

                    // 转换后的OID
                    single.transOid = node.getOid();

                    // 值
                    single.binding = node.getValue();
                    single.value = node.getEditValue();
                    
                    // 枚举
                    String enumValue = enumValueMap.get(single.oriOid + "/" + single.value);
                    single.enumValue = enumValue;
                    enumValueMap.put(single.transOid + "/" + single.value, single.enumValue);

                    coll.addToColl(single);
                    resultMap.put(single.transOid, node.getEditValue());
                }
            }
        }
    }

    /**
     * 对单个OID进行采集的结果集
     * 标量型，取single的值
     * 表格型，取rs集合的值
     * 出错时，错误信息存在wrongStr中
     */
    public static class SnmpResultColl implements Serializable
    {

        /**
         * 和数据库里面的OID进行匹配
         */
        public String dbOid;
        /**
         * 结果集
         */
        public List<SnmpResultSingle> rs;
        /**
         * 单个结果
         */
        public SnmpResultSingle single;
        /**
         * 错误信息
         */
        public String wrongStr;
        /**
         * 序列化编号
         */
        private static final long serialVersionUID = -1L;

        /**
         * @param sr 参数
         */
        public void addToColl(SnmpResultSingle sr)
        {
            if (rs == null)
            {
                rs = new ArrayList<SnmpResultSingle>();
            }
            if (sr != null)
            {
                // 当列中不转换后OID时，才添加改行
                if (!containOID(sr.transOid))
                {
                    rs.add(sr);
                }
            }
        }

        private boolean containOID(String oid)
        {
            boolean retval = false;
            for (SnmpResultSingle sr : rs)
            {
                if (sr.transOid.equals(oid))
                {
                    retval = true;
                    break;
                }
            }
            return retval;
        }
    }

    /**
     * 正确取值的一条结果
     */
    public static class SnmpResultSingle implements Serializable
    {
        /**
         * 原始OID
         */
        public String oriOid;
        /**
         * 转换后的OID
         */
        public String transOid;
        /**
         * 取值
         */
        public VariableBinding binding;
        /**
         * 解析后的值
         */
        public String value;
        /**
         * 如果节点为枚举类型，对应的字符串值
         */
        public String enumValue;
        /**
         * 序列化编号
         */
        private static final long serialVersionUID = -1L;
    }
}
