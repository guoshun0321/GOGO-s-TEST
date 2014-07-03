/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import jetsennet.jbmp.util.ConfigUtil;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.ICMPPacket;
import jpcap.packet.Packet;

/**
 * @author Guo
 */
public class ICMP
{

    private JpcapCaptor captor;
    private JpcapSender sender;
    /**
     * 网卡
     */
    private NetworkInterface device;
    /**
     * 目的地址IP
     */
    private InetAddress dstIp;
    /**
     * 源地址IP
     */
    private InetAddress srcIp;
    /**
     * 目标地址mac，一般为默认网关的mac地址。如果是同一局域网内，可为目标主机地址。
     */
    private byte[] dstMac;
    /**
     * 检查包的次数
     */
    private int retryTime;
    /**
     * 检查包的最短时间
     */
    private int mixCheck;
    private boolean isEnd;
    private static final Logger logger = Logger.getLogger(ICMP.class);

    /**
     * @param ip 参数
     */
    public ICMP(InetAddress ip)
    {
        this.dstIp = ip;
        mixCheck = ConfigUtil.getPingMixCheck(30);
        retryTime = ConfigUtil.getPingTimeout(3000) / mixCheck;
        device = IcmpUtil.getUsableInterface();
        if (device == null)
        {
            throw new IcmpException(dstIp + " is not a local address");
        }
        srcIp = IcmpUtil.getUsableInetAddress(device);
        dstMac = IcmpUtil.getGetwayMac(ConfigUtil.getPingGateWay());
        this.isEnd = false;
    }

    /**
     * @return 结果
     */
    public boolean ping()
    {
        try
        {
            this.openCaptor();
            this.send();
        }
        finally
        {
            this.close();
        }
        return isEnd;
    }

    /**
     * 
     */
    public void openCaptor()
    {
        try
        {
            captor = JpcapCaptor.openDevice(device, 2000, false, mixCheck);
            captor.setFilter("icmp and dst host " + srcIp.getHostAddress(), true);
            sender = captor.getJpcapSenderInstance();
        }
        catch (Exception ex)
        {
            throw new IcmpException(ex);
        }
    }

    /**
     * 发送
     */
    public void send()
    {
        ICMPPacket icmp = IcmpPacketGenerator.getInstance().genIcmpEchoPacket(srcIp, dstIp, device.mac_address, dstMac, (short) 1001);
        sender.sendPacket(icmp);
        for (int i = 0; i < retryTime; i++)
        {
            captor.processPacket(-1, new IcmpPacketReceiver(icmp));
            if (isEnd)
            {
                break;
            }
        }
    }

    /**
     * 关闭
     */
    public void close()
    {
        if (captor != null)
        {
            try
            {
                captor.close();
            }
            finally
            {
                captor = null;
            }
        }
        if (sender != null)
        {
            try
            {
                sender.close();
            }
            finally
            {
                sender = null;
            }
        }
    }

    /**
     * @param args 参数
     * @throws UnknownHostException 异常
     */
    public static void main(String[] args) throws UnknownHostException
    {
        ICMP sac = new ICMP(InetAddress.getByName("192.168.8.121"));
        long s = System.currentTimeMillis();
        System.out.println(sac.ping());
        long e = System.currentTimeMillis();
        System.out.println("last = " + (e - s));
    }

    class IcmpPacketReceiver implements PacketReceiver
    {

        private ICMPPacket icmp;

        public IcmpPacketReceiver(ICMPPacket icmp)
        {
            this.icmp = icmp;
        }

        public void receivePacket(Packet packet)
        {
            if (packet == null)
            {
                logger.info("Timeout");
                return;
            }
            logger.debug("receive : " + packet);
            if (packet instanceof ICMPPacket)
            {
                ICMPPacket p = (ICMPPacket) packet;
                byte recType = p.type;
                byte recCode = p.code;

                switch (recType)
                {
                // 回显应答
                case ICMPPacket.ICMP_ECHOREPLY:
                {
                    // logger.info(p.src_ip.getHostAddress() + " Ping应答");
                    if (p.src_ip.getHostAddress().equals(dstIp.getHostAddress()))
                    {
                        isEnd = true;
                    }
                    break;
                }
                    // 目的不可达
                case ICMPPacket.ICMP_UNREACH:
                    switch (recCode)
                    {
                    case ICMPPacket.ICMP_UNREACH_PORT:
                        logger.info("端口不可达");
                        break;
                    default:
                        break;
                    }
                    break;
                // 超时
                case ICMPPacket.ICMP_TIMXCEED:
                    switch (recCode)
                    {
                    case ICMPPacket.ICMP_TIMXCEED_INTRANS:
                        logger.info("传输期间生存时间（TTL）为0");
                        break;
                    case ICMPPacket.ICMP_TIMXCEED_REASS:
                        logger.info("数据报组装期间生存时间为0");
                        break;
                    default:
                        break;
                    }
                    icmp.hop_limit++;
                    break;
                // 回显请求
                case ICMPPacket.ICMP_ECHO:
                    // logger.info("回显请求");
                    if (p.src_ip.getHostAddress().equals(dstIp.getHostAddress()))
                    {
                        isEnd = true;
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }

}
