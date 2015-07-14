/**
 * 日 期： 2012-2-21
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  Syslogd.java
 * 历 史： 2012-2-21 创建
 */
package jetsennet.jbmp.syslog;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;

import org.apache.log4j.Logger;

import jetsennet.jbmp.alarm.bus.CollData;
import jetsennet.jbmp.alarm.bus.CollDataBus;
import jetsennet.jbmp.entity.SyslogEntity;
import jetsennet.jbmp.util.XmlCfgUtil;

/**
 * syslog接收进程
 */
public final class Syslogd implements Runnable
{
    private static final Logger logger = Logger.getLogger(Syslogd.class);

    /**
     * 缓存区大小
     */
    private static final int POOL_SIZE = 0xffff;

    /**
     * 单例
     */
    private static Syslogd instance = new Syslogd();

    /**
     * 接收线程
     */
    private Thread receiveThread;

    /**
     * 接收端口
     */
    private DatagramSocket serverDgSock;

    /**
     * 状态标记
     */
    private volatile boolean isStart;

    private Syslogd()
    {
    }

    public static Syslogd getInstance()
    {
        return instance;
    }

    /**
     * 启动
     */
    public void start()
    {
        if (isStart())
        {
            return;
        }
        try
        {
            isStart = true;
            String ip = XmlCfgUtil.getStringValue(SyslogConstants.SYSLOG_CFG_FILE, SyslogConstants.SYSLOG_IP_CFG, SyslogConstants.DEFAULT_IP);
            int syslogPort = XmlCfgUtil.getIntValue(SyslogConstants.SYSLOG_CFG_FILE, SyslogConstants.SYSLOG_PORT_CFG, SyslogConstants.DEFAULT_PORT);
            serverDgSock = new DatagramSocket(new InetSocketAddress(InetAddress.getByName(ip), syslogPort));
            serverDgSock.setReceiveBufferSize(POOL_SIZE);

            // 启动监听
            logger.info("监听地址：udp:" + ip + "/" + syslogPort);
            receiveThread = new Thread(this);
            receiveThread.start();
            logger.info("开始接收Syslog信息");
        }
        catch (Exception ex)
        {
            logger.error("Syslog接收启动异常", ex);
        }
    }

    /**
     * 停止
     */
    public void stop()
    {
        if (!isStart())
        {
            return;
        }
        try
        {
            if (serverDgSock != null)
            {
                serverDgSock.close();
            }
            if (receiveThread != null)
            {
                receiveThread.interrupt();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            serverDgSock = null;
            receiveThread = null;
        }
        logger.info("停止接收Syslog信息");
        isStart = false;
    }

    @Override
    public void run()
    {
        final byte[] buffer = new byte[POOL_SIZE];
        DatagramPacket pkt = new DatagramPacket(buffer, POOL_SIZE);
        while (isStart)
        {
            try
            {
                serverDgSock.receive(pkt);
            }
            catch (SocketException e)
            {
                break;
            }
            catch (Exception e)
            {
                logger.error("Syslog报文接收异常", e);
                break;
            }

            try
            {
                CollDataBus.getInstance().put(parse(pkt));
            }
            catch (Exception e)
            {
                logger.warn("处理Syslog消息异常", e);
            }

            pkt = new DatagramPacket(buffer, POOL_SIZE);
        }
    }

    /**
     * 解析syslog事件
     * @param pkt syslog报文
     * @return CollData
     * @throws UnsupportedEncodingException
     */
    private CollData parse(DatagramPacket pkt) throws UnsupportedEncodingException
    {
        CollData syslogData = new CollData();
        syslogData.dataType = CollData.DATATYPE_SYSLOG;
        syslogData.time = new Date();
        syslogData.srcIP = pkt.getAddress().getHostAddress();
        byte[] dest = new byte[pkt.getLength()];
        System.arraycopy(pkt.getData(), pkt.getOffset(), dest, 0, pkt.getLength());
        String value = new String(dest);
        syslogData.value = value;
        SyslogEntity entity = new SyslogEntity();
        entity.setCollTime(new Date());
        entity.setIpAddr(pkt.getAddress().getHostAddress());
        entity.setContent(value);
        syslogData.put(CollData.PARAMS_DATA, entity);
        return syslogData;
    }

    public boolean isStart()
    {
        return isStart;
    }

    /**
     * @param args 参数
     * @throws Exception 异常
     * @throws SocketException 异常
     */
    public static void main(String[] args) throws SocketException, Exception
    {
        DatagramSocket socket = new DatagramSocket();
        for (int i = 0; i < 1; i++)
        {
            String msg = "收到 syslog message for 127.0.0.1. Id=" + i;
            String ip = "192.168.8.57";
            byte[] bytes = msg.getBytes();
            DatagramPacket request = new DatagramPacket(bytes, bytes.length, new InetSocketAddress(InetAddress.getByName(ip), 514));
            socket.send(request);
        }
    }
}
