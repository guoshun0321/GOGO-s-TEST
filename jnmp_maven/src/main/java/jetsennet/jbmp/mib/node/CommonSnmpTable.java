/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import java.util.Map;
import java.util.Map.Entry;

import jetsennet.jbmp.entity.SnmpNodesEntity;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;
import jetsennet.jbmp.util.SnmpUtil;

import org.snmp4j.smi.VariableBinding;

/**
 * @author Guo
 */
public class CommonSnmpTable
{

    /**
     * 进程消息表
     * @return
     */
    public static SnmpTable processTable()
    {
        SnmpTable table = new SnmpTable("process");
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunIndex", "1.3.6.1.2.1.25.4.2.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunName", "1.3.6.1.2.1.25.4.2.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunID", "1.3.6.1.2.1.25.4.2.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunPath", "1.3.6.1.2.1.25.4.2.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunParameters", "1.3.6.1.2.1.25.4.2.1.5"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunType", "1.3.6.1.2.1.25.4.2.1.6"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunStatus", "1.3.6.1.2.1.25.4.2.1.7"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunPerfCPU", "1.3.6.1.2.1.25.5.1.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("hrSWRunPerfMem", "1.3.6.1.2.1.25.5.1.1.2"), null, null));
        table.setIndex("1.3.6.1.2.1.25.4.2.1.1");
        return table;
    }

    /**
     * @return 结果
     */
    public static SnmpTable mirandaTable()
    {
        SnmpTable table = new SnmpTable("miranda");
        table.addColumn(new EditNode(new SnmpNodesEntity("deviceIndex", "1.3.6.1.4.1.3872.11.1.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("slotIndex", "1.3.6.1.4.1.3872.11.1.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("trapIndex", "1.3.6.1.4.1.3872.11.1.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("type", "1.3.6.1.4.1.3872.11.1.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("name", "1.3.6.1.4.1.3872.11.1.1.5"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("status", "1.3.6.1.4.1.3872.11.1.1.6"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("statusText", "1.3.6.1.4.1.3872.11.1.1.7"), null, null));
        table.setIndex("1.3.6.1.4.1.3872.11.1.1.1");
        return table;
    }

    /**
     * @return 结果
     */
    public static SnmpTable nccDeviceTable()
    {
        SnmpTable table = new SnmpTable("nccDevice");
        table.addColumn(new EditNode(new SnmpNodesEntity("dDeviceTableIndex", "1.3.6.1.4.1.1773.3.1.2.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dDeviceLabel", "1.3.6.1.4.1.1773.3.1.2.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dDisplayLabel", "1.3.6.1.4.1.1773.3.1.2.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dDeviceType", "1.3.6.1.4.1.1773.3.1.2.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dDeviceAddress", "1.3.6.1.4.1.1773.3.1.2.1.5"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dDeviceState", "1.3.6.1.4.1.1773.3.1.2.1.6"), null, null));
        table.setIndex("1.3.6.1.4.1.1773.3.1.2.1.1");
        return table;
    }

    /**
     * 路由表
     * @return
     */
    public static SnmpTable ipRouteTable()
    {
        SnmpTable table = new SnmpTable("ipRouteTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteIfIndex", "1.3.6.1.2.1.4.21.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteDest", "1.3.6.1.2.1.4.21.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMask", "1.3.6.1.2.1.4.21.1.11"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteNextHop", "1.3.6.1.2.1.4.21.1.7"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteType", "1.3.6.1.2.1.4.21.1.8"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteProto", "1.3.6.1.2.1.4.21.1.9"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteAge", "1.3.6.1.2.1.4.21.1.10"), null, null));

        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMetric1", "1.3.6.1.2.1.4.21.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMetric2", "1.3.6.1.2.1.4.21.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMetric3", "1.3.6.1.2.1.4.21.1.5"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMetric4", "1.3.6.1.2.1.4.21.1.6"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteMetric5", "1.3.6.1.2.1.4.21.1.12"), null, null));

        table.addColumn(new EditNode(new SnmpNodesEntity("ipRouteInfo", "1.3.6.1.2.1.4.21.1.13"), null, null));
        return table;
    }

    /**
     * ARP转发表
     * @return
     */
    public static SnmpTable ipNetToMediaTable()
    {
        SnmpTable table = new SnmpTable("ipNetToMediaTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("ipNetToMediaIfIndex", "1.3.6.1.2.1.4.22.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipNetToMediaNetAddress", "1.3.6.1.2.1.4.22.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipNetToMediaType", "1.3.6.1.2.1.4.22.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipNetToMediaPhysAddress", "1.3.6.1.2.1.4.22.1.2"), null, "HEX"));
        return table;
    }

    /**
     * 本机IP、掩码、以及对应的逻辑接口
     * @return
     */
    public static SnmpTable ipAddrTable()
    {
        SnmpTable table = new SnmpTable("ipAddrTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("ipAdEntAddr", "1.3.6.1.2.1.4.20.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipAdEntIfIndex", "1.3.6.1.2.1.4.20.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipAdEntNetMask", "1.3.6.1.2.1.4.20.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipAdEntBcastAddr", "1.3.6.1.2.1.4.20.1.4"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ipAdEntReasmMaxSize", "1.3.6.1.2.1.4.20.1.5"), null, null));
        return table;
    }

    /**
     * 地址转发表，MAC和物理端口的对应关系
     * @return
     */
    public static SnmpTable dot1dTpFdbTable()
    {
        SnmpTable table = new SnmpTable("dot1dTpFdbTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dTpFdbAddress", "1.3.6.1.2.1.17.4.3.1.1"), null, "HEX"));
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dTpFdbPort", "1.3.6.1.2.1.17.4.3.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dTpFdbStatus", "1.3.6.1.2.1.17.4.3.1.3"), null, null));
        return table;
    }

    /**
     * 接口表
     * @return
     */
    public static SnmpTable ifTable()
    {
        SnmpTable table = new SnmpTable("ifTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("ifIndex", "1.3.6.1.2.1.2.2.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ifDescr", "1.3.6.1.2.1.2.2.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ifType", "1.3.6.1.2.1.2.2.1.3"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("ifPhysAddress", "1.3.6.1.2.1.2.2.1.6"), null, "HEX"));
        return table;
    }

    /**
     * 接口到端口的映射，好像只在交换机上可行
     * @return
     */
    public static SnmpTable dot1dBasePortTable()
    {
        SnmpTable table = new SnmpTable("dot1dBasePortTable");
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dBasePort", "1.3.6.1.2.1.17.1.4.1.1"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dBasePortIfIndex", "1.3.6.1.2.1.17.1.4.1.2"), null, null));
        table.addColumn(new EditNode(new SnmpNodesEntity("dot1dBasePortCircuit", "1.3.6.1.2.1.17.1.4.1.3"), null, null));
        return table;
    }

    public static void main(String[] args) throws Exception
    {

        // 本机IP地址
//        SnmpTable table4 = CommonSnmpTable.ipAddrTable();
//        SnmpTableUtil.initSnmpTable(table4, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table4);

        // 本机接口
        SnmpTable table = CommonSnmpTable.ifTable();
        SnmpTableUtil.initSnmpTable(table, null, "", "192.168.8.43", 161, "public");
        System.out.println(table);
        System.out.println(SnmpUtil.ensureMacFromIfTable(table));

        // 接口和端口之间的对应关系（不完整，可能是交换机特有）
//        SnmpTable table1 = CommonSnmpTable.dot1dBasePortTable();
//        SnmpTableUtil.initSnmpTable(table1, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table1);
//
//        // 路由表（接口）
//        SnmpTable table2 = CommonSnmpTable.ipRouteTable();
//        SnmpTableUtil.initSnmpTable(table2, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table2);
//
//        // MAC地址转发表（端口）
//        SnmpTable table3 = CommonSnmpTable.dot1dTpFdbTable();
//        SnmpTableUtil.initSnmpTable(table3, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table3);
//
//        // ARP转发表
//        SnmpTable table5 = CommonSnmpTable.ipNetToMediaTable();
//        SnmpTableUtil.initSnmpTable(table5, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table5);
//
//        // 地址转发表
//        SnmpTable table6 = CommonSnmpTable.dot1dTpFdbTable();
//        SnmpTableUtil.initSnmpTable(table6, null, "", "130.0.0.199", 161, "public");
//        System.out.println(table6);

        //
        // AbsSnmpPtl snmp = AbsSnmpPtl.getInstance("snmpv2c");
        // snmp.init("192.168.10.200", 161, "public", 3, 4000);
        // Map<String, VariableBinding> map = snmp.snmpGetWithBegin("1.3.6.1.2.1.17.4.3.1");
        // System.out.println(map);
        //
        // for (Entry<String, VariableBinding> m : map.entrySet())
        // {
        // System.out.println(m.getKey() + "->" + m.getValue().toString());
        // }

    }
}
