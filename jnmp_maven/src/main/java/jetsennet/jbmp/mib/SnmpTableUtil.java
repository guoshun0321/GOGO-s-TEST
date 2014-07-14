package jetsennet.jbmp.mib;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.SnmpException;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;
import jetsennet.jbmp.util.OIDUtil;
import jetsennet.jbmp.util.ThreeTuple;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;

public class SnmpTableUtil
{

    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpTableUtil.class);

    /**
     * 初始化Snmp表
     * @param table 表名
     * @param snmp 参数
     * @param version 版本
     * @param ip ip
     * @param port 端口
     * @param community 参数
     * @throws SnmpException 异常
     */
    public static void initSnmpTable(SnmpTable table, AbsSnmpPtl snmp, String version, String ip, int port, String community) throws SnmpException
    {
        if (table.getIndex() == null)
        {
            throw new SnmpException("索引为空，需要添加索引。");
        }

        ThreeTuple<String, ArrayList<String>, Map<String, VariableBinding>> temp = fillDataWithWalk(table, snmp, version, ip, port, community);
        String index = temp.first;
        ArrayList<String> indexOids = temp.second;
        Map<String, VariableBinding> results = temp.third;

        // 填充表格OID
        table.fillTableCellOid(indexOids, index);

        // 填充数据
        table.fillTableCellValue(results);
        table.checkResultTable();
    }

    /**
     * 初始化Snmp表
     * @param table 表
     * @param snmp 外部传入的snmp执行器，可为null
     * @param host 参数
     * @throws SnmpException 异常
     */
    public static void initSnmpTable(SnmpTable table, AbsSnmpPtl snmp, MObjectEntity host) throws SnmpException
    {
        if (table.getIndex() == null)
        {
            throw new SnmpException("索引为空，需要添加索引。");
        }
        if (host == null)
        {
            throw new SnmpException("无采集对象，需要设置采集对象");
        }
        initSnmpTable(table, snmp, host.getVersion(), host.getIpAddr(), host.getIpPort(), host.getUserName());
    }

    /**
     * 初始化Snmp表。 主要用于大数据量的表格数据采集。 对snmpv1而言，使用getnext采集数据，在某些情况下保证数据的完整性。
     * @param table 需要填充的表
     * @param host 目标对象
     * @param snmp 外部传入的snmp执行器，可为空
     * @param index 表行的OID
     * @throws SnmpException 异常
     */
    public static void initSnmpTable1(SnmpTable table, MObjectEntity host, AbsSnmpPtl snmp, String index) throws SnmpException
    {
        if (table.getIndex() == null)
        {
            throw new SnmpException("索引为空，需要添加索引。");
        }
        if (host == null)
        {
            throw new SnmpException("无采集对象，需要设置采集对象");
        }
        // 是否初始化snmp执行器
        boolean self = false;
        if (snmp == null)
        {
            snmp = AbsSnmpPtl.getInstance(host.getVersion());
            if (snmp == null)
            {
                throw new SnmpException("无法获取snmp执行器，SNMP版本：" + host.getVersion());
            }
            self = true;
        }
        try
        {
            table.reset();

            snmp.init(host.getIpAddr(), host.getIpPort(), host.getUserName());

            String indexOid = table.getIndex();
            Map<String, VariableBinding> indexResult = snmp.snmpGetWithBegin(indexOid);
            int row = indexResult.size();
            table.setRowNum(row);
            ArrayList<String> iOIDs = new ArrayList<String>();
            iOIDs.addAll(indexResult.keySet());
            table.fillTableCellOid(iOIDs, indexOid);

            // 采集到的数据
            Map<String, VariableBinding> results = new LinkedHashMap<String, VariableBinding>();
            results = snmp.snmpGetWithBegin(index);
            table.fillTableCellValue(results);
            table.checkResultTable();
        }
        catch (Exception ex)
        {
            throw new SnmpException(ex);
        }
        finally
        {
            if (self && snmp != null)
            {
                snmp.close();
            }
        }
    }

    /**
     * 获取数据。首先尝试使用GET的方法获取数据，如果出现异常，接着尝试使用GetNext或GetBulk的方式获取数据。
     * @param snmp
     * @param index
     * @param oids
     * @return
     * @throws SnmpException
     */
    private static Map<String, VariableBinding> fillData(AbsSnmpPtl snmp, String index, String[] oids, String[] headOids) throws SnmpException
    {
        Map<String, VariableBinding> retval = null;
        // 先全部取值
        try
        {
            retval = snmp.snmpGet(oids);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            retval = null;
        }
        // 根据头部分开取值
        retval = new HashMap<String, VariableBinding>();
        try
        {
            for (String headOid : headOids)
            {

                Map<String, VariableBinding> headMap = snmp.snmpGetWithBegin(headOid);
                for (Entry<String, VariableBinding> entry : headMap.entrySet())
                {
                    retval.put(entry.getKey(), entry.getValue());
                }
            }
        }
        catch (Exception ex)
        {
            logger.debug("", ex);
        }
        // 根据INDEX取值
        // if (retval == null)
        // {
        // retval = snmp.snmpGetWithBegin(index);
        // }
        return retval;
    }

    /**
     * 获取数据。首先尝试使用GET的方法获取数据，如果出现异常，接着尝试使用GetNext或GetBulk的方式获取数据。
     * @param snmp
     * @param index
     * @param oids
     * @return
     * @throws SnmpException
     */
    private static ThreeTuple<String, ArrayList<String>, Map<String, VariableBinding>> fillDataWithWalk(SnmpTable table, AbsSnmpPtl snmp,
            String version, String ip, int port, String community) throws SnmpException
    {
        String index = null;
        ArrayList<String> indexOids = new ArrayList<String>();
        Map<String, VariableBinding> retMap = new HashMap<String, VariableBinding>();

        // 是否初始化snmp执行器
        boolean self = false;
        if (snmp == null)
        {
            snmp = AbsSnmpPtl.getInstance(version);
            self = true;
        }

        try
        {

            table.reset();

            index = table.getIndex();
            String pIndex = OIDUtil.getSuperiorOid(index);
            String[] headOids = table.getHeaderOids();

            snmp.init(ip, port, community);

            for (String headOid : headOids)
            {
                if (headOid != null)
                {
                    Map<String, VariableBinding> headMap = snmp.snmpGetWithBegin(headOid);
                    for (Entry<String, VariableBinding> entry : headMap.entrySet())
                    {
                        retMap.put(entry.getKey(), entry.getValue());
                        if (headOid.equals(index))
                        {
                            indexOids.add(entry.getKey());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw new SnmpException(ex);
        }
        finally
        {
            if (self && snmp != null)
            {
                snmp.close();
            }
        }
        return new ThreeTuple<String, ArrayList<String>, Map<String, VariableBinding>>(index, indexOids, retMap);
    }
    
    public static void main(String[] args)
    {
        System.out.println(new Date(10232706000l));
    }
}
