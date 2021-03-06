/************************************************************************
日 期: 2012-3-26
作 者: 郭祥
版 本: v1.3
描 述: SNMPV2C
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp;

import java.io.IOException;
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
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import jetsennet.jbmp.exception.SnmpException;
import jetsennet.jbmp.protocols.ConnectInfo;
import jetsennet.jbmp.util.SnmpUtil;

/**
 * snmpv2的数据采集。GETBULK中未处理列过多的情况。
 * @author 郭祥
 */
public class SnmpPtlV2c extends AbsSnmpPtl
{

    private SnmpInfo host;
    // 采集策略
    private int retries;
    private int timeout;
    private Snmp snmp;
    private PDU pdu;
    private CommunityTarget target;
    private TransportMapping transport;
    /**
     * 异步调用时的数据处理接口
     */
    private ResponseListener listener;
    // 单次扫描最大的可扫描量和递减数值
    private int maxScan = 50;
    private int decrease = 5;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(SnmpPtlV2c.class);

    /**
     * 构造函数
     */
    public SnmpPtlV2c()
    {
        this.listener = null;
    }

    @Override
    public void init(String ip, int port, String community, int retries, int timeout) throws SnmpException
    {
        host = new SnmpInfo(ip, port, SnmpConstants.version2c, community);
        this.retries = retries;
        this.timeout = timeout;
        this.initSnmp();
    }

    @Override
    public void init(String ip, int port, String community) throws SnmpException
    {
        this.init(ip, port, community, ConnectInfo.SNMP_RETRY, ConnectInfo.SNMP_TIMEOUT);
    }

    @Override
    public void init(String ip, int port, String community, String[] oids) throws SnmpException
    {
        this.init(ip, port, community, ConnectInfo.SNMP_RETRY, ConnectInfo.SNMP_TIMEOUT);
        this.pdu = this.initPDU(PDU.GET, oids);
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    private void initSnmp() throws SnmpException
    {
        this.target = this.initCommunity(host.getAddress(), retries, timeout, host.getVersion(), host.getCommunity());
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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GET">
    /**
     * 使用SNMP协议的GET方法获取数据。
     * @param oids 参数
     * @return 采集不到数据时，返回一个空的Map
     * @throws SnmpException iOIDs为null时，抛出异常。iOIDs大小为0时，不抛出异常。
     */
    public Map<String, VariableBinding> snmpGet(String[] oids) throws SnmpException
    {
        if (oids == null)
        {
            throw new SnmpException("传入OIDS为空");
        }
        SnmpOidColl manager = new SnmpOidColl(oids, maxScan, decrease);
        while (manager.isMore())
        {
            this.snmpGetMethod(manager);
        }
        return manager.getResult();
    }

    /**
     * 采集数据，并去掉错误的数据。 注意：在这里只处理oid不正确的数据
     * @param oids
     * @return
     */
    private void snmpGetMethod(SnmpOidColl manager) throws SnmpException
    {
        String[] soids = manager.getUsableOids();
        ResponseEvent recv = this.snmpGetBasic(soids);
        PDU recPDU = recv.getResponse();
        int errorStatus = recPDU.getErrorStatus();
        switch (errorStatus)
        {
        case PDU.noError:
        {
            Vector<VariableBinding> bindings = recPDU.getVariableBindings();
            for (int i = 0; i < soids.length; i++)
            {
                String soid = soids[i];
                boolean match = false;
                for (int j = 0; j < bindings.size(); j++)
                {
                    VariableBinding temp = bindings.get(j);
                    if (temp.getOid().toString().equals(soid))
                    {
                        if (temp == null || temp.getVariable() == null)
                        {
                            manager.info(soid, null);
                        }
                        else
                        {
                            if (temp.getVariable().toString().equals(NOSUCHOBJECT) || temp.getVariable().toString().equals(NOSUCHINSTANCE))
                            {
                                manager.info(soid, null);
                            }
                            else
                            {
                                manager.info(soid, temp);
                            }
                        }
                        bindings.remove(temp);
                        match = true;
                        break;
                    }
                }
                if (!match)
                {
                    manager.info(soid, null);
                }
            }
            break;
        }
        case PDU.tooBig:
        {
            manager.decrease();
            break;
        }
        case PDU.noSuchName:
        case PDU.genErr:
        {
            int errorIndex = recPDU.getErrorIndex();
            if (errorIndex > 0)
            {
                VariableBinding errVar = (VariableBinding) recPDU.getVariableBindings().get(errorIndex - 1);
                manager.error(soids[errorIndex - 1]);
            }
            break;
        }
        default:
        {
            manager.setCurUsed();
            logger.error("未知的错误标识。接受到的PDU为：" + recPDU.toString());
        }
        }
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
            throw new SnmpException("未初始化目标：" + target);
        }
        if (snmp == null)
        {
            throw new SnmpException("未初始化snmp执行器：" + snmp);
        }
        PDU sendPDU = initPDU(PDU.GET, oids);
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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GETBULK">
    /**
     * 使用SNMP协议的GETBULK方法获取数据。
     * @param iOIDs 参数
     * @param max 参数
     * @return 结果
     * @throws SnmpException 异常
     */
    public Map<String, VariableBinding> snmpGetBulk(String[] iOIDs, int max) throws SnmpException
    {
        if (iOIDs == null)
        {
            throw new SnmpException("传入OIDS为空");
        }
        Map<String, VariableBinding> results = new LinkedHashMap<String, VariableBinding>();
        int begin = 0;
        String[] lOIDs = iOIDs;
        if (iOIDs.length <= 0 || max <= 0)
        {
            return results;
        }
        do
        {
            int rep = this.genBulkRep(begin, iOIDs, max);
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
        while (begin < max);
        return results;
    }

    /**
     * SNMP的GetBulk操作
     * @param oids 参数
     * @param NonRepeaters 参数
     * @param MaxRepetitions 参数
     * @return结果
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

    private int genBulkRep(int begin, String[] iOIDs, int max)
    {
        int rep = maxScan / iOIDs.length;
        int last = max - begin;
        return rep > last ? last : rep;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="WALK">
    /**
     * 获取以begin开头的所有oid
     * @param begin 开始
     * @return 返回
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

    /*
     * (non-Javadoc)
     * @see jetsennet.jbmp.protocols.snmp.AbsSnmpPtl#getNext(java.lang.String)
     */
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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="异步扫描">
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

    // </editor-fold>

    /**
     * 重置数据，并关闭底层连接。
     * @throws SnmpException 异常
     */
    @Override
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
        SnmpPtlV2c snmp = new SnmpPtlV2c();
        snmp.init("192.168.8.57", 161, "public", 2, 1500);
        String[] sOIDs =
            { "1.3.6.1.2.1.25.2.3.1.1", "1.3.6.1.2.1.25.2.3.1.2", "1.3.6.1.2.1.25.2.3.1.3", "1.3.6.1.2.1.25.2.3.1.4", "1.3.6.1.2.1.25.2.3.1.5",
                "1.3.6.1.2.1.25.2.3.1.6" };
        String[] hrDeviceEntry =
            { "1.3.6.1.2.1.25.3.2.1.1", "1.3.6.1.2.1.25.3.2.1.2", "1.3.6.1.2.1.25.3.2.1.3", "1.3.6.1.2.1.25.3.2.1.4", "1.3.6.1.2.1.25.3.2.1.5",
                "1.3.6.1.2.1.25.3.2.1.6" };
        String[] procOids =
            { "1.3.6.1.2.1.25.4.2.1.1", "1.3.6.1.2.1.25.4.2.1.2", "1.3.6.1.2.1.25.4.2.1.3", "1.3.6.1.2.1.25.4.2.1.4", "1.3.6.1.2.1.25.4.2.1.5",
                "1.3.6.1.2.1.25.4.2.1.6", "1.3.6.1.2.1.25.4.2.1.7" };
        Map<String, VariableBinding> results = snmp.snmpGetWithBegin("1.3.6.1.2.1.25.4.2.1.1");
        // Map<String, VariableBinding> results = snmp.snmpGet(test.split(","));
        // Map<String, VariableBinding> results = snmp.snmpGetNext(test.split(","));
        int length = results.size();
        results = snmp.snmpGetBulk(procOids, length);

        Set<String> oids = results.keySet();
        for (String oid : oids)
        {
            VariableBinding binding = results.get(oid);
            if (binding != null)
            {
                System.out.println(oid + "   " + (binding.getVariable() == null ? "null" : binding.getVariable().toString()));
            }
            else
            {
                System.out.println(oid + "   " + "null");
            }
        }
        System.out.println(results.size());
    }
}
