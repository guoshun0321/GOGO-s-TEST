/************************************************************************
日 期：2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 批量、异步SNMP查询
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.snmp;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.entity.MObjectEntity;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;

/**
 * 批量、异步SNMP查询
 * @author 郭祥
 */
public class ArraySnmp
{

    /**
     * 共同体
     */
    private String community;
    /**
     * 端口
     */
    private int port;
    /**
     * 用于控制是否调用异步处理。解决BUG 0017282
     */
    private boolean isStop;
    /**
     * 版本
     */
    private String version;
    /**
     * 需要扫描的IP端
     */
    private List<String> ips;
    /**
     * 扫描结果
     */
    private ArrayList<ResponseEvent> results;
    /**
     * 扫描时间超时设置
     */
    private static final int timeout = 2 * 60 * 1000;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ArraySnmp.class);

    public ArraySnmp()
    {
        this(MObjectEntity.VERSION_SNMP_V1, 161, "public");
    }

    public ArraySnmp(String version, int port, String community)
    {
        super();
        this.isStop = false;
        this.community = community;
        this.port = port;
        this.version = version;
        results = new ArrayList<ResponseEvent>();
    }

    /**
     * 扫描指定IP段，返回扫描结果
     * @param section IP段
     * @return
     */
    public List<ResponseEvent> snmp(List<String> ips, String[] oids) throws Exception
    {

        if (ips != null && oids != null && !ips.isEmpty() && oids.length > 0)
        {
            this.ips = ips;
            this.send(ips, oids);
        }
        return results;
    }

    /**
     * 异步扫描，当扫描结束后返回
     * @return
     * @throws InterruptedException
     */
    private ArrayList<ResponseEvent> send(List<String> ips, String[] oids) throws Exception
    {
        AbsSnmpPtl snmp = AbsSnmpPtl.getInstance(version);
        snmp.setListener(new AsyRes());
        try
        {
            for (String address : ips)
            {
                snmp.init(address, this.port, community, oids);
                snmp.asyncScan();
            }
            synchronized (results)
            {
                results.wait(timeout);
            }
        }
        catch (Exception e)
        {
            throw new Exception(e.getMessage(), e);
        }
        finally
        {
            try
            {
                this.isStop = true;
                AbsSnmpPtl.closeSnmp();
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                snmp = null;
            }
        }
        return results;
    }

    /**
     * 添加事件，当接收到的数据数量和需要的数据数量相同时。唤醒在results上等待的线程。
     * @param event
     */
    private void add(ResponseEvent event)
    {

        PDU pdu = event.getResponse();
        if (pdu != null)
        {
            logger.debug(event.getPeerAddress().toString() + "响应：" + pdu.toString());
        }
        synchronized (results)
        {
            results.add(event);
            if (ips.size() == results.size())
            {
                results.notifyAll();
            }
        }
    }

    /**
     * 异步消息处理类
     * @author 郭祥
     */
    private class AsyRes implements ResponseListener
    {

        /**
         * 用于异步发送时，处理接收到的信息
         */
        @Override
        public void onResponse(ResponseEvent event)
        {
            if (!isStop)
            {
                synchronized (this)
                {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                }
                add(event);
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        ArrayList<String> ips = new ArrayList<String>();
        for (int i = 1; i < 255; i++)
        {
            ips.add("192.168.8." + i);
        }
        //        ips.clear();
        //        ips.add("192.168.8.42");
        String[] oids = new String[] { "1.3.6.1.2.1.1.1.0", "1.3.6.1.2.1.1.2.0" };
        ArraySnmp snmp = new ArraySnmp(MObjectEntity.VERSION_SNMP_V1, 161, "public");
        logger.debug("begin");
        List<ResponseEvent> res = snmp.snmp(ips, oids);
        logger.debug("end");
        System.out.println(res);
    }
}
