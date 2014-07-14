/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.node;

import jetsennet.jbmp.entity.SnmpNodesEntity;

/**
 * SNMP进程表
 * @author 郭祥
 */
public class SnmpProcessTable
{

    public static String SNMP_PROCESS_NAME = "hrSWRunName";
    public static String SNMP_PROCESS_NAME_OID = "1.3.6.1.2.1.25.4.2.1.2";
    public static int SNMP_PROCESS_NAME_COLUMN = 1;

    /**
     * @return 结果
     */
    public static SnmpTable getTable()
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
}
