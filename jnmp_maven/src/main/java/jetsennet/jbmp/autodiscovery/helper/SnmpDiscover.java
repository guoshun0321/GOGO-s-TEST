/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 使用SNMP协议进行自动发现
历 史：
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.protocols.snmp.ArraySnmp;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.VariableBinding;

/**
 * 使用SNMP协议进行自动发现
 * @author 郭祥
 */
public class SnmpDiscover extends AbsDiscover
{

    /**
     * 端口（默认值：161）
     */
    private int port;
    /**
     * 共同体
     */
    private String community;
    /**
     * 需要扫描的OID，sysObjectID/sysName/sysDescr/sysServices/ipForwarding
     */
    private static final String[] PRODUCT_OIDS =
        new String[] { "1.3.6.1.2.1.1.2.0", "1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1.0", "1.3.6.1.2.1.1.7.0", "1.3.6.1.2.1.4.1.0" };
    /**
     * 用于判断设备是否为交换机的OID，dot1dBaseNumPorts
     */
    private static final String[] SWITCH_OID = new String[] { "1.3.6.1.2.1.17.1.2.0" };
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpDiscover.class);

    /**
     * 构造函数
     */
    public SnmpDiscover()
    {
        this(161, "public");
    }

    /**
     * 构造函数
     * @param port 端口
     * @param community 共同体
     */
    public SnmpDiscover(int port, String community)
    {
        super();
        this.community = community;
        this.port = port;
    }

    /**
     * 扫描指定IP段，返回扫描结果
     * @param irs IP段
     * @param coll 参数
     * @return 结果
     * @throws DiscoverException 异常
     */
    @Override
    public AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
    {

        try
        {
            List<String> ips = AutoDisUtil.getIpColl(irs);

            // v1扫描
            String version = MObjectEntity.VERSION_SNMP_V1;
            ArraySnmp scanner = new ArraySnmp(version, this.port, this.community);
            List<ResponseEvent> resultV1 = scanner.snmp(ips, PRODUCT_OIDS);

            // v2c扫描
            version = MObjectEntity.VERSION_SNMP_V2C;
            scanner = new ArraySnmp(version, this.port, this.community);
            List<ResponseEvent> resultV2C = scanner.snmp(ips, PRODUCT_OIDS);

            // 合并结果
            Map<String, ResponseEvent> ip2Event = new HashMap<String, ResponseEvent>();
            Map<String, String> ip2Version = new HashMap<String, String>();
            for (ResponseEvent event : resultV1)
            {
                PDU pdu = event.getResponse();
                if (pdu != null && pdu.getVariableBindings().size() > 1)
                {
                    String ip = event.getPeerAddress().toString();
                    ip = ip.substring(0, ip.lastIndexOf("/"));
                    ip2Event.put(ip, event);
                    ip2Version.put(ip, MObjectEntity.VERSION_SNMP_V1);
                }
            }

            // v2c的结果会覆盖v1的结果
            for (ResponseEvent event : resultV2C)
            {
                PDU pdu = event.getResponse();
                if (pdu != null && pdu.getVariableBindings().size() > 1)
                {
                    String ip = event.getPeerAddress().toString();
                    ip = ip.substring(0, ip.lastIndexOf("/"));
                    ip2Event.put(ip, event);
                    ip2Version.put(ip, MObjectEntity.VERSION_SNMP_V2C);
                }
            }
            
         // 获取接口数目，用于判断设备是否为交换机
            Map<String, String> ip2PortNum = this.getPortNum(ip2Version);

            // 处理结果
            for (Entry<String, ResponseEvent> entry : ip2Event.entrySet())
            {
                String ip = entry.getKey();
                PDU pdu = entry.getValue().getResponse();
                String vers = ip2Version.get(ip);
                String portNum = ip2PortNum.get(ip);
                this.dateHandling(coll.getByIp(ip), pdu, ip, vers, portNum);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new DiscoverException(ex);
        }
        return coll;
    }

    /**
     * 处理获得的数据
     * @param pdu 获得的PDU
     * @param ip 主机IP
     * @return
     */
    private void dateHandling(SingleResult ir, PDU pdu, String ip, String version, String portNum)
    {
        if (ir == null)
        {
            return;
        }
        ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_SNMP);

        // 添加基本信息
        pr.addResult(AutoDisConstant.PORT, port);
        pr.addResult(AutoDisConstant.SNMP_COMMUNITY, community);
        pr.addResult(AutoDisConstant.SNMP_VERSION, version);

        // 添加OID信息
        Vector<VariableBinding> bingdings = pdu.getVariableBindings();
        String sysObjectID = getVariableValue(bingdings.get(0));
        String sysName = getVariableValue(bingdings.get(1));
        String sysDescr = getVariableValue(bingdings.get(2));
        String sysService = getVariableValue(bingdings.get(3));
        String ipForwarding = getVariableValue(bingdings.get(4));
        pr.addResult(AutoDisConstant.SNMP_SYSOBJECTID, sysObjectID);
        pr.addResult(AutoDisConstant.SNMP_SYSNAME, sysName);
        pr.addResult(AutoDisConstant.SNMP_SYSDESCR, sysDescr);
        pr.addResult(AutoDisConstant.SNMP_SYSSERVICES, sysService);
        pr.addResult(AutoDisConstant.SNMP_IPFORWORDING, ipForwarding);
        pr.addResult(AutoDisConstant.SNMP_DOT1DBASENUMPORTS, portNum);

        // 添加到结果
        ir.addProResult(pr);
    }

    /**
     * 获取PortNum
     * @param ip2Version
     * @return
     */
    private Map<String, String> getPortNum(Map<String, String> ip2Version)
    {
        Map<String, String> retval = new HashMap<String, String>();
        try
        {
            List<String> ipsV1 = new ArrayList<String>();
            List<String> ipsV2C = new ArrayList<String>();
            for (Entry<String, String> entry : ip2Version.entrySet())
            {
                if (entry.getValue().equals(MObjectEntity.VERSION_SNMP_V1))
                {
                    ipsV1.add(entry.getKey());
                }
                else
                {
                    ipsV2C.add(entry.getKey());
                }
            }

            // v1扫描
            String version = MObjectEntity.VERSION_SNMP_V1;
            ArraySnmp scanner = new ArraySnmp(version, this.port, this.community);
            List<ResponseEvent> resultV1 = scanner.snmp(ipsV1, SWITCH_OID);

            // v2c扫描
            version = MObjectEntity.VERSION_SNMP_V2C;
            scanner = new ArraySnmp(version, this.port, this.community);
            List<ResponseEvent> resultV2C = scanner.snmp(ipsV2C, SWITCH_OID);

            // 生成结果
            for (ResponseEvent event : resultV1)
            {
                addPortNum(event, retval);
            }
            for (ResponseEvent event : resultV2C)
            {
                addPortNum(event, retval);
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        return retval;
    }

    /**
     * 解析getPortNum获取的数据
     * @param event
     * @param map
     */
    private void addPortNum(ResponseEvent event, Map<String, String> map)
    {
        String retval = "NULL";
        PDU pdu = event.getResponse();
        if (pdu != null && pdu.getVariableBindings().size() == 1)
        {
            String ip = event.getPeerAddress().toString();
            ip = ip.substring(0, ip.lastIndexOf("/"));
            Vector<VariableBinding> bingdings = pdu.getVariableBindings();
            VariableBinding bingding = bingdings.get(0);
            retval = bingding.getVariable() instanceof Null ? "NULL" : bingding.getVariable().toString();
            map.put(ip, retval);
        }
    }

    private String getVariableValue(VariableBinding binding)
    {
        String retval = null;
        if (binding != null && binding.getVariable() != null && !(binding.getVariable() instanceof Null))
        {
            retval = binding.getVariable().toString();
        }
        return retval;
    }

}
