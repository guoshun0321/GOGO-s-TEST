/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.trap.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 本类用于向管理进程发送Trap信息
 * @author 37073 jetsen
 */
public class SendTrap
{

    private Snmp snmp = null;
    private Address targetAddress = null;
    private static final Logger logger = Logger.getLogger(SendTrap.class);

    /**
     * @param ipAddress 地址
     * @param port 端口
     * @throws IOException 异常
     */
    public void initComm(String ipAddress, Integer port) throws IOException
    {

        // 设置管理进程的IP和端口
        targetAddress = GenericAddress.parse("udp:" + ipAddress + "/" + port.toString());
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    /**
     * 向管理进程发送Trap报文
     * @param oid 参数
     * @param msg 参数
     * @throws IOException 参数
     */
    public void sendPDU(String oid, String msg) throws IOException
    {

        // 设置 target
        CommunityTarget target = new CommunityTarget();
        target.setAddress(targetAddress);

        // 通信不成功时的重试次数
        target.setRetries(2);
        // 超时时间
        target.setTimeout(1500);
        // snmp版本
        target.setVersion(SnmpConstants.version2c);

        // 创建 PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid), new OctetString(msg)));
        pdu.setType(PDU.TRAP);

        // 向Agent发送PDU，并接收Response
        ResponseEvent respEvnt = snmp.send(pdu, target);

        // 解析Response
        if (respEvnt != null && respEvnt.getResponse() != null)
        {
            Vector<VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++)
            {
                VariableBinding recVB = recVBs.elementAt(i);
                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
            }
        }
    }

    /**
     * @param varBind 参数
     * @throws IOException 异常
     */
    public void sendPDU(Map<String, String> varBind) throws IOException
    {

        // 设置 target
        CommunityTarget target = new CommunityTarget();
        target.setAddress(targetAddress);

        // 通信不成功时的重试次数
        target.setRetries(2);
        // 超时时间
        target.setTimeout(1500);
        // snmp版本
        target.setVersion(SnmpConstants.version2c);

        // 创建 PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"), new TimeTicks(0)));
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OctetString("1.3.6.1.6.3.1.1.5.3")));
        for (String vbkey : varBind.keySet())
        {
            pdu.add(new VariableBinding(new OID(vbkey), new OctetString(varBind.get(vbkey))));
        }
        pdu.setType(PDU.TRAP);

        // 向Agent发送PDU，并接收Response
        ResponseEvent respEvnt = snmp.send(pdu, target);
    }

    /**
     * 
     */
    public void close()
    {
        if (snmp != null)
        {
            try
            {
                snmp.close();
            }
            catch (IOException ex)
            {
                logger.error(ex);
            }
            finally
            {
                snmp = null;
            }
        }
    }

    /**
     * @param args 参数
     */
    public static void main(String[] args)
    {
        try
        {
            SendTrap util = new SendTrap();
            util.initComm("192.168.8.145", 162);
            Map<String, String> vb = new HashMap();
            vb.put("1.3.6.1.2.1.2.2.1.1.12", "1");
            vb.put("1.3.6.1.2.1.2.2.1.7", "test");
            util.sendPDU(vb);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
