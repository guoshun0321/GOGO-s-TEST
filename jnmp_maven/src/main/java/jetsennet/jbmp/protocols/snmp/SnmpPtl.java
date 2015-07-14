/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.protocols.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import jetsennet.jbmp.entity.SnmpHostEntity;
import jetsennet.jbmp.exception.SnmpException;
import jetsennet.jbmp.util.SnmpUtil;

/**
 * @author Guo
 */
public class SnmpPtl
{

    private SnmpHostEntity host;
    // 采集策略
    private int retries;
    private int timeout;
    private Snmp snmp;
    private PDU pdu;
    private CommunityTarget target;
    private Address targetAddress;
    private TransportMapping transport;
    private ResponseListener listener;
    private static final Logger logger = Logger.getLogger(SnmpPtl.class);
    // 常量
    private static final String NOSUCHOBJECT = "noSuchObject";
    private static final String NOSUCHINSTANCE = "noSuchInstance";
    private static final String ENDOFMIBVIEW = "endOfMibView";
    // 单次扫描最大的可扫描量和递减数值
    private int maxScan = 50;
    private int decrease = 5;

    /**
     * 构造函数
     */
    public SnmpPtl()
    {
        this.listener = null;
    }

    public void init(String ip, int port, String community, int version, int retries, int timeout) throws SnmpException
    {
        host = new SnmpHostEntity();
        host.setIpAddr(ip);
        host.setIpPort(port);
        host.setCommunity(community);
        host.setIVersion(version);
        this.retries = retries;
        this.timeout = timeout;
        this.initSnmp();
    }

    public void init(String ip, int port, String community) throws SnmpException
    {
        this.init(ip, port, community, SnmpConstants.version2c, 2, 1500);
    }

    public void init(String ip, int port, String community, String[] oids) throws SnmpException
    {
        this.init(ip, port, community, SnmpConstants.version2c, 2, 1500);
        this.pdu = this.initPDU(PDU.GET, oids);
    }

    private void initSnmp() throws SnmpException
    {
        this.targetAddress = GenericAddress.parse("udp:" + host.getIpAddr() + "/" + host.getIpPort());
        this.target = initCommunity(targetAddress, retries, timeout, host.getIVersion(), host.getCommunity());
        // 在对多个IP进行扫描时，只创建一个的UDP通讯Socket
        if (transport == null)
        {
            this.transport = this.initTransportMapping();
            this.snmp = new Snmp(transport);
        }
    }

    /**
     * 实例化一个Community对象
     * @param retries 重发次数
     * @param timeout 重发间隔时间
     * @param version SNMP版本
     * @param community 共同体
     */
    private CommunityTarget initCommunity(Address targetAddress, int retries, int timeout, int version, String community)
    {
        CommunityTarget tgt = new CommunityTarget();
        // 访问策略
        tgt.setAddress(targetAddress);
        tgt.setRetries(retries);
        tgt.setTimeout(timeout);
        // 设置协议的版本和共同体名称
        tgt.setVersion(version);
        tgt.setCommunity(new OctetString(community));
        return tgt;
    }

    /**
     * 实例化一个PDU对象
     * @param type PDU的类型
     * @param oids 需要查询的OID数组
     */
    private PDU initPDU(int type, String[] oids) throws SnmpException
    {
        PDU iPDU = new PDU();
        // 设置PDU类型和查询用变量
        iPDU.setType(type);
        for (int i = 0; i < oids.length; i++)
        {
            try
            {
                iPDU.add(new VariableBinding(new OID(SnmpUtil.getOid(oids[i]))));
            }
            catch (Exception e)
            {
                throw new SnmpException(e.getMessage(), e);
            }
        }
        return iPDU;
    }

    /**
     * 实例化UDP监听器，并开始监听
     */
    private TransportMapping initTransportMapping() throws SnmpException
    {
        try
        {
            TransportMapping trans = new DefaultUdpTransportMapping();
            trans.listen();
            return trans;
        }
        catch (IOException e)
        {
            throw new SnmpException("初始化TransportMapping出错", e);
        }
    }

    /**
     * 使用SNMP协议的GET方法获取数据。
     * @param iOIDs 参数
     * @return 返回值不为null
     * @throws SnmpException iOIDs为null时，抛出异常。iOIDs大小为0时，不抛出异常。
     */
    public Map<String, VariableBinding> snmpGet(String[] iOIDs) throws SnmpException
    {
        if (iOIDs == null)
        {
            throw new SnmpException("传入OIDS为空");
        }
        Map<String, VariableBinding> results = new LinkedHashMap<String, VariableBinding>();
        int begin = 0;
        if (iOIDs.length <= 0)
        {
            return results;
        }
        for (int i = 0; i < iOIDs.length; i++)
        {
            results.put(iOIDs[i], new VariableBinding());
        }
        while (begin < iOIDs.length)
        {
            begin = this.snmpGetMethod(iOIDs, begin, results);
        }
        return results;
    }

    /**
     * 采集数据，并去掉错误的数据。 注意：在这里只处理oid不正确的数据
     * @param oids
     * @return
     */
    private int snmpGetMethod(String[] iOIDs, int begin, Map<String, VariableBinding> results) throws SnmpException
    {
        if (iOIDs == null)
        {
            throw new SnmpException("传入OIDS为空");
        }
        String[] sOIDs = this.genScanOIDs(iOIDs, begin);
        String[] vOIDs = this.getValOIDs(sOIDs, results);
        int nextBegin = begin;
        // 初次扫描
        ResponseEvent recv = this.snmpGetBasic(vOIDs);
        PDU recPDU = recv.getResponse();
        int errorStatus = recPDU.getErrorStatus();
        switch (errorStatus)
        {
        // 错误标识为0
        case PDU.noError:
        {
            Vector<VariableBinding> bindings = recPDU.getVariableBindings();
            for (int i = 0; i < sOIDs.length; i++)
            {
                VariableBinding binding = null;
                try
                {
                    binding = bindings.get(i);
                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                    results.put(sOIDs[i], null);
                    continue;
                }
                if (binding.getVariable().toString().equals(NOSUCHOBJECT) || binding.getVariable().toString().equals(NOSUCHINSTANCE))
                {
                    results.put(sOIDs[i], null);
                }
                else
                {
                    results.put(sOIDs[i], binding);
                }
            }
            nextBegin = begin + sOIDs.length;
            break;
        }
        case PDU.tooBig:
        {
            maxScan = maxScan - decrease;
            break;
        }
        case PDU.noSuchName:
        case PDU.genErr:
        {
            int errorIndex = recPDU.getErrorIndex();
            if (errorIndex > 0)
            {
                VariableBinding errVar = (VariableBinding) recPDU.getVariableBindings().get(errorIndex - 1);
                results.put(errVar.getOid().toString(), null);
            }
            break;
        }
        default:
        {
            for (int i = 0; i < iOIDs.length; i++)
            {
                results.put(sOIDs[i], null);
            }
            nextBegin = begin + sOIDs.length;
            logger.error("未知的错误标识。接受到的PDU为：" + recPDU.toString());
        }
        }
        return nextBegin;
    }

    /**
     * snmp的Get操作
     * @param oids
     * @return
     * @throws SnmpException
     */
    private ResponseEvent snmpGetBasic(String[] oids) throws SnmpException
    {

        if (target == null || snmp == null)
        {
            throw new SnmpException("未初始化target：" + target);
        }
        if (snmp == null)
        {
            throw new SnmpException("未初始化snmp：" + snmp);
        }
        PDU sendPDU = null;
        sendPDU = initPDU(PDU.GET, oids);
        if (sendPDU == null)
        {
            throw new SnmpException("未初始化pdu：" + sendPDU);
        }

        ResponseEvent recv = null;
        try
        {
            recv = snmp.send(sendPDU, this.target);
        }
        catch (IOException ex)
        {
            throw new SnmpException(ex.getMessage(), ex);
        }
        if (recv == null)
        {
            throw new SnmpException("发送的PDU格式不正确：" + sendPDU.toString());
        }
        else if (recv.getResponse() == null)
        {
            throw new SnmpException("目标地址：" + this.target.getAddress() + "不可达或数据发送超时；" + "或单次发送的数据量太大，PDU中绑定的变量数量为："
                + sendPDU.getVariableBindings().size() + "；或发送消息使用的community不正确，community：" + host.getCommunity());
        }
        return recv;
    }

    /**
     * 使用SNMP协议的GETBULK方法获取数据。
     * @param iOIDs 参数
     * @param MaxRepetitions 参数
     * @return 结果
     * @throws SnmpException 异常
     */
    public Map<String, VariableBinding> snmpGetBulk(String[] iOIDs, int MaxRepetitions) throws SnmpException
    {
        if (iOIDs == null)
        {
            throw new SnmpException("传入OIDS为空");
        }
        Map<String, VariableBinding> results = new LinkedHashMap<String, VariableBinding>();
        int begin = 0;
        String[] lOIDs = iOIDs;
        if (iOIDs.length <= 0 || MaxRepetitions <= 0)
        {
            return results;
        }
        do
        {
            int rep = this.genBulkRep(begin, iOIDs, MaxRepetitions);
            ResponseEvent recv = this.snmpGetBulkBasic(lOIDs, 0, rep);
            PDU recPDU = recv.getResponse();
            Vector<VariableBinding> bindings = recPDU.getVariableBindings();
            for (int i = 0; i < bindings.size(); i++)
            {
                VariableBinding binding = bindings.get(i);
                if (binding != null && !binding.getVariable().toString().equals(ENDOFMIBVIEW))
                {
                    results.put(binding.getOid().toString(), binding);
                }
                int last = bindings.size() - iOIDs.length;
                if (i >= last)
                {
                    lOIDs[i - last] = binding.getOid().toString();
                }
            }
            begin += rep;
        }
        while (begin < MaxRepetitions);
        return results;
    }

    /**
     * 获取以begin开头的所有oid
     * @param begin 开始
     * @return 结果
     * @throws SnmpException 异常
     */
    public Map<String, VariableBinding> snmpGetWithBegin(String begin) throws SnmpException
    {
        if (begin == null)
        {
            throw new SnmpException("传入iOID为空");
        }
        Map<String, VariableBinding> results = new LinkedHashMap<String, VariableBinding>();
        String sOID = begin;

        do
        {
            ResponseEvent recv = this.snmpGetBulkBasic(new String[] { sOID }, 0, maxScan);
            PDU recPDU = recv.getResponse();
            Vector<VariableBinding> bindings = recPDU.getVariableBindings();
            for (int i = 0; i < bindings.size(); i++)
            {
                VariableBinding binding = bindings.get(i);
                if (binding != null && !binding.getVariable().toString().equals(ENDOFMIBVIEW))
                {
                    if (binding.getOid().toString().startsWith(begin + "."))
                    {
                        results.put(binding.getOid().toString(), binding);
                    }
                }
            }
            sOID = bindings.lastElement().getOid().toString();
            if (!sOID.startsWith(begin + ".") || bindings.lastElement().getVariable().toString().equals(ENDOFMIBVIEW))
            {
                break;
            }
        }
        while (true);
        return results;
    }

    private int genBulkRep(int begin, String[] iOIDs, int max)
    {
        int rep = maxScan / iOIDs.length;
        int last = max - begin;
        return rep > last ? last : rep;
    }

    /**
     * SNMP的GetBulk操作
     * @param oids 参数
     * @param NonRepeaters 参数
     * @param MaxRepetitions 参数
     * @return 结果
     * @throws SnmpException 异常
     */
    private ResponseEvent snmpGetBulkBasic(String[] oids, int NonRepeaters, int MaxRepetitions) throws SnmpException
    {

        if (target == null || snmp == null)
        {
            throw new SnmpException("未初始化target：" + target);
        }
        if (snmp == null)
        {
            throw new SnmpException("未初始化snmp：" + snmp);
        }
        PDU sendPDU = null;
        sendPDU = initPDU(PDU.GETBULK, oids);
        sendPDU.setNonRepeaters(NonRepeaters);
        sendPDU.setMaxRepetitions(MaxRepetitions);
        if (sendPDU == null)
        {
            throw new SnmpException("未初始化pdu：" + sendPDU);
        }

        ResponseEvent recv = null;
        try
        {
            recv = snmp.send(sendPDU, this.target);
        }
        catch (IOException ex)
        {
            throw new SnmpException(ex.getMessage(), ex);
        }
        if (recv == null)
        {
            throw new SnmpException("发送的PDU格式不正确：" + sendPDU.toString());
        }
        else if (recv.getResponse() == null)
        {
            throw new SnmpException("目标地址：" + this.target.getAddress() + "不可达\n" + "或单次请求的数据量太大，PDU中绑定的变量数量为：" + sendPDU.getVariableBindings().size()
                + "\n或发送消息使用的community不正确，community：" + host.getCommunity());
        }
        return recv;
    }

    /**
     * 生成扫描用OID数组
     * @param iOIDs
     * @param begin
     * @param results
     * @return
     */
    private String[] genScanOIDs(String[] iOIDs, int begin)
    {
        int length = iOIDs.length;
        int last = length - begin;
        if (last > 0)
        {
            if (last >= maxScan)
            {
                return Arrays.copyOfRange(iOIDs, begin, (begin + maxScan));
            }
            else
            {
                return Arrays.copyOfRange(iOIDs, begin, begin + last);
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * 提取有效的OID
     * @param iOIDs
     * @param results
     * @return
     */
    private String[] getValOIDs(String[] iOIDs, Map<String, VariableBinding> results)
    {
        ArrayList<String> vOIDs = new ArrayList<String>();
        for (int i = 0; i < iOIDs.length; i++)
        {
            if (results.get(iOIDs[i]) != null)
            {
                vOIDs.add(iOIDs[i]);
            }
        }
        return vOIDs.toArray(new String[vOIDs.size()]);
    }

    public VariableBinding getNext(String oids) throws SnmpException
    {
        VariableBinding result = null;
        String[] soids = new String[] { oids };
        ResponseEvent recv = this.snmpGetNextBasic(soids);
        PDU recPDU = recv.getResponse();
        int errorStatus = recPDU.getErrorStatus();
        switch (errorStatus)
        {
        case PDU.noError:
        {
            Vector<VariableBinding> bindings = recPDU.getVariableBindings();
            if (bindings.size() >= 1)
            {
                result = bindings.get(0);
            }
            break;
        }
        case PDU.tooBig:
        case PDU.noSuchName:
        case PDU.genErr:
        default:
        {
            logger.error(recPDU);
            break;
        }
        }
        return result;
    }

    /**
     * snmp的Get操作
     * @param oids
     * @return
     * @throws SnmpException
     */
    private ResponseEvent snmpGetNextBasic(String[] oids) throws SnmpException
    {

        if (target == null || snmp == null)
        {
            throw new SnmpException("未初始化目标：" + target);
        }
        if (snmp == null)
        {
            throw new SnmpException("未初始化snmp执行器：" + snmp);
        }
        PDU sendPDU = initPDU(PDU.GETNEXT, oids);
        if (sendPDU == null)
        {
            throw new SnmpException("未初始化pdu：" + sendPDU);
        }

        ResponseEvent recv = null;
        try
        {
            recv = snmp.send(sendPDU, this.target);
        }
        catch (IOException ex)
        {
            throw new SnmpException(ex.getMessage(), ex);
        }
        if (recv == null)
        {
            throw new SnmpException("发送的PDU格式不正确：" + sendPDU.toString());
        }
        else if (recv.getResponse() == null)
        {
            throw new SnmpException("目标地址：" + this.target.getAddress() + "不可达或数据发送超时；" + "或单次发送的数据量太大，PDU中绑定的变量数量为："
                + sendPDU.getVariableBindings().size() + "；或发送消息使用的community不正确，community：" + host.getCommunity());
        }
        return recv;
    }

    /**
     * 异步扫描
     * @throws SnmpException 异常
     */
    public void asyncScan() throws SnmpException
    {
        if (listener == null)
        {
            throw new SnmpException("listener对象为空");
        }
        if (pdu == null)
        {
            throw new SnmpException("pdu对象为空");
        }
        try
        {
            snmp.sendPDU(pdu, target, null, listener);
        }
        catch (IOException e)
        {
            throw new SnmpException(e.getMessage(), e);
        }
    }

    /**
     * 设置异步发送时的监听器
     * @param listener 监听
     */
    public void setListener(ResponseListener listener)
    {
        this.listener = listener;
    }

    /**
     * 重置数据，并关闭底层连接。
     * @throws SnmpException 异常
     */
    public void close() throws SnmpException
    {
        try
        {
            if (pdu != null)
            {
                pdu.clear();
                pdu = null;
            }
            if (transport != null)
            {
                transport.close();
                transport = null;
            }
            if (snmp != null)
            {
                snmp.close();
                snmp = null;
            }
        }
        catch (IOException e)
        {
            throw new SnmpException(e.getMessage(), e);
        }
    }

    /**
     * @param args 参数
     * @throws SnmpException 异常
     */
    public static void main(String[] args) throws SnmpException
    {
        SnmpPtl snmp = new SnmpPtl();
        snmp.init("192.168.8.200", 161, "public", SnmpConstants.version2c, 2, 1500);
        String[] sOIDs =
            { "1.3.6.1.2.1.25.2.3.1.1", "1.3.6.1.2.1.25.2.3.1.2", "1.3.6.1.2.1.25.2.3.1.3", "1.3.6.1.2.1.25.2.3.1.4", "1.3.6.1.2.1.25.2.3.1.5",
                "1.3.6.1.2.1.25.2.3.1.6" };
        String[] hrDeviceEntry =
            { "1.3.6.1.2.1.25.3.2.1.1", "1.3.6.1.2.1.25.3.2.1.2", "1.3.6.1.2.1.25.3.2.1.3", "1.3.6.1.2.1.25.3.2.1.4", "1.3.6.1.2.1.25.3.2.1.5",
                "1.3.6.1.2.1.25.3.2.1.6" };
        Map<String, VariableBinding> results = snmp.snmpGetWithBegin("1.3.6.1.2.1.4");
        Set<String> oids = results.keySet();
        for (String oid : oids)
        {
            System.out.println(oid + " = " + results.get(oid).getVariable().toString());
        }
        System.out.println(results.size());
    }
}
