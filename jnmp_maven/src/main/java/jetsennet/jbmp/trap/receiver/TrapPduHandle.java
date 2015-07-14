/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.trap.receiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.trap.util.TrapConstants;

/**
 * 默认的Trap处理
 * @author Guo
 */
public class TrapPduHandle implements CommandResponder
{
    private static final Logger logger = Logger.getLogger(TrapPduHandle.class);

    private static final ArrayList<String> GENERIC_TRAPS;
    static
    {
        GENERIC_TRAPS = new ArrayList<String>(6);
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.1"); // coldStart
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.2"); // warmStart
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.3"); // linkDown
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.4"); // linkUp
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.5"); // authenticationFailure
        GENERIC_TRAPS.add("1.3.6.1.6.3.1.1.5.6"); // egpNeighborLoss
    }

    @Override
    public void processPdu(CommandResponderEvent event)
    {
        if (event == null || event.getPDU() == null)
        {
            return;
        }

        logger.debug("收到Trap：" + event.getPDU());

        // 对trap进行转换，将不同版本的trap转换为同一数据结构的CollData对象
        CollData trapData = null;
        PDU pdu = event.getPDU();
        switch (pdu.getType())
        {
        case PDU.V1TRAP:
            trapData = parseV1Trap((PDUv1) pdu);
            if (trapData == null)
            {
                return;
            }
            // 发送trap数据
            handleCollData(trapData);
            break;

        // v2c、v3的trap/inform格式是一样的
        case PDU.TRAP:
        case PDU.INFORM:
            trapData = parseV2Trap(pdu);
            if (trapData == null)
            {
                return;
            }
            if (trapData.srcIP == null)
            {
                trapData.srcIP = parseIP(event.getPeerAddress().toString());
            }
            // 发送trap数据
            handleCollData(trapData);
            break;

        default:
            logger.warn("错误的trap类型:" + event.toString());
        }
    }

    /**
     * 解析v1版本的trap
     * @param pdu
     * @return
     */
    protected CollData parseV1Trap(PDUv1 pdu)
    {
        CollData trapData = new CollData();
        trapData.dataType = CollData.DATATYPE_TRAP;
        trapData.srcIP = pdu.getAgentAddress().toString();
        trapData.time = new Date();

        // v1的trap用genericType标识该trap属于哪种类型，小于6则为通用类型，等于6则为特定类型
        if (pdu.getGenericTrap() < 6)
        {
            trapData.value = GENERIC_TRAPS.get(pdu.getGenericTrap());
        }
        else
        {
            trapData.value = pdu.getEnterprise().toString() + "." + pdu.getSpecificTrap();
        }

        // 补充v2变量
        trapData.put(TrapConstants.SNMP_SYSUPTIME_OID, pdu.getTimestamp());
        trapData.put(TrapConstants.SNMP_TRAP_OID, trapData.value);

        // 解析变量
        for (VariableBinding vb : pdu.toArray())
        {
            String oid = vb.getOid().toString();
            Object value = toString(vb.getVariable());
            trapData.put(oid, value);
        }

        return trapData;
    }

    /**
     * 解析v2版本的trap
     * @param pdu
     * @return
     */
    protected CollData parseV2Trap(PDU pdu)
    {
        Vector<VariableBinding> vbs = pdu.getVariableBindings();

        // 根据v2c/v3的trap/inform的标准格式，第一个vb一定要是SNMP_SYSUPTIME_OID("1.3.6.1.2.1.1.3.0")，第二个vb一定要是SNMP_TRAP_OID("1.3.6.1.6.3.1.1.4.1.0")
        if (vbs.size() < 2)
        {
            return null;
        }
        VariableBinding sysupTimeVb = vbs.get(0);
        VariableBinding trapOIDVb = vbs.get(1);
        if (!(sysupTimeVb.getOid().toString().equals(TrapConstants.SNMP_SYSUPTIME_OID) || trapOIDVb.getOid().toString().equals(
            TrapConstants.SNMP_TRAP_OID)))
        {
            return null;
        }

        CollData trapData = new CollData();
        trapData.dataType = CollData.DATATYPE_TRAP;
        trapData.value = trapOIDVb.getVariable().toString();
        trapData.time = new Date();

        // 解析变量
        for (VariableBinding vb : vbs)
        {
            String oid = vb.getOid().toString();
            Object value = toString(vb.getVariable());
            if (oid.equals(TrapConstants.SNMP_TRAP_ADDRESS_OID))
            {
                // 如果trap/inform是由proxy转发过来的，则vb中包含snmpTrapAddress.0("1.3.6.1.6.3.18.1.3.0")
                trapData.srcIP = value.toString();
            }
            trapData.put(oid, value);
        }

        return trapData;
    }

    /**
     * 处理生成的Trap数据
     * @param trapData
     */
    protected void handleCollData(CollData trapData)
    {
        if (trapData != null)
        {
            CollDataBus.getInstance().put(trapData);
        }
    }

    private Object toString(Variable va)
    {
        if (va instanceof OctetString)
        {
            return va;
        }
        else if (va instanceof TimeTicks)
        {
            return Long.toString(((TimeTicks) va).toLong());
        }
        else
        {
            return va.toString();
        }
    }

    private String parseIP(String address)
    {
        int index = address.indexOf('/');
        if (index > -1)
        {
            return address.substring(0, index);
        }
        else
        {
            return address;
        }
    }
}
