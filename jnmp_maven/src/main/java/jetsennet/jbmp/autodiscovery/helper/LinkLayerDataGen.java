package jetsennet.jbmp.autodiscovery.helper;

import java.util.Map;

import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.snmp.AbsSnmpPtl;
import jetsennet.jbmp.protocols.snmp.datahandle.SnmpValueTranser;
import jetsennet.jbmp.util.SnmpUtil;

import org.apache.log4j.Logger;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.VariableBinding;

public class LinkLayerDataGen
{

    /**
     * 需要扫描的OID，sysServices/ipForwarding
     */
    private static final String[] PRODUCT_OIDS = new String[] { "1.3.6.1.2.1.1.7.0", "1.3.6.1.2.1.4.1.0" };
    /**
     * 用于判断设备是否为交换机的OID，dot1dBaseNumPorts
     */
    private static final String[] SWITCH_OID = new String[] { "1.3.6.1.2.1.17.1.2.0" };
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(LinkLayerDataGen.class);

    public static LinkLayerData genData(String ip, int port, String community, String version, String coding)
    {
        LinkLayerData retval = new LinkLayerData();
        AbsSnmpPtl snmp = AbsSnmpPtl.getInstance(version);
        try
        {
            logger.debug("开始获取设备：" + ip + "的数据。");
            snmp.init(ip, port, community);

            boolean isSnmp = false;

            try
            {
                // sysServices/ipForwarding，同时测试SNMP是否通
                Map<String, VariableBinding> oidMap = snmp.snmpGet(PRODUCT_OIDS);
                retval.setSysServices(SnmpValueTranser.getInstance().trans(oidMap.get(PRODUCT_OIDS[0]), coding));
                retval.setIpForwarding(SnmpValueTranser.getInstance().trans(oidMap.get(PRODUCT_OIDS[1]), coding));
                isSnmp = true;
            }
            catch (Exception ex)
            {
                logger.error("获取sysServices/ipForwarding失败，放弃数据获取。", ex);
            }

            if (isSnmp)
            {

                try
                {
                    // dot1dBaseNumPorts
                    Map<String, VariableBinding> portNumMap = snmp.snmpGet(SWITCH_OID);
                    setPortNum(retval, portNumMap);
                }
                catch (Exception ex)
                {
                    logger.error("获取dot1dBaseNumPorts失败", ex);
                }

                // 确定链路层类型
                retval.setLinkType(ensureLinkLayerType(retval.getSysServices(), retval.getIpForwarding(), retval.getDot1dBaseNumPorts()));

                try
                {
                    // 本机接口
                    SnmpTable table = CommonSnmpTable.ifTable();
                    SnmpTableUtil.initSnmpTable(table, snmp, version, ip, port, community);
                    retval.setIfMap(table);

                    // MAC地址
                    logger.debug("开始选择设备：" + ip + "的MAC地址。");
                    String mac = SnmpUtil.ensureMacFromIfTable(table);
                    logger.debug("选择设备：" + ip + "的MAC地址结束，MAC地址为：" + mac);
                    retval.setMac(mac);
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }

                try
                {
                    // 地址转发表
                    SnmpTable aftTable = CommonSnmpTable.dot1dTpFdbTable();
                    SnmpTableUtil.initSnmpTable(aftTable, snmp, version, ip, port, community);
                    retval.setAftMap(aftTable);
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
            logger.debug("获取设备：" + ip + "的数据结束。");
            if (snmp != null)
            {
                try
                {
                    snmp.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return retval;
    }

    /**
     * 解析getPortNum获取的数据
     * @param event
     * @param map
     */
    private static void setPortNum(LinkLayerData data, Map<String, VariableBinding> portNumMap)
    {

        VariableBinding binding = portNumMap.get(SWITCH_OID[0]);
        if (binding != null && binding.getVariable() != null)
        {
            String retval = binding.getVariable() instanceof Null ? null : binding.getVariable().toString();
            data.setDot1dBaseNumPorts(retval);
        }
    }

    /**
     * 解析附加信息
     * @param mo
     * @param autoObj
     */
    protected static int ensureLinkLayerType(String sysServices, String ipForwarding, String portNum)
    {
        int retval = MObjectEntity.LINKLAYER_TYPE_UNKNOWN;
        // 同时通过ARP和SNMP的才进行设置
        retval = AutoDisUtil.ensureLinkType(sysServices, ipForwarding, portNum);
        return retval;
    }

    public static void main(String[] args)
    {
        LinkLayerData data = LinkLayerDataGen.genData("192.168.10.200", 161, "public", "", null);
        System.out.println(data);
    }
}
