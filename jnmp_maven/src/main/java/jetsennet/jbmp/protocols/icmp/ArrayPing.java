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
import java.util.ArrayList;

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
public class ArrayPing
{

    /**
     * 网卡
     */
    private NetworkInterface device;
    /**
     * 源地址IP
     */
    private InetAddress srcIp;
    /**
     * 目标地址mac，一般为默认网关的mac地址。如果是同一局域网内，可为目标主机地址。
     */
    private byte[] nextMac;
    /**
     * 可以Ping通的主机地址
     */
    private ArrayList<String> result;
    private static final Logger logger = Logger.getLogger(ArrayPing.class);

    /**
     * 构造函数
     */
    public ArrayPing()
    {
        device = IcmpUtil.getUsableInterface();
        if (device == null)
        {
            throw new IcmpException("无可用网卡");
        }
        srcIp = IcmpUtil.getUsableInetAddress(device);
        this.nextMac = IcmpUtil.getGetwayMac(ConfigUtil.getPingGateWay());
        result = new ArrayList<String>();
    }

    /**
     * @param ips 参数
     * @return 结果
     */
    public ArrayList<String> ping(ArrayList<String> ips)
    {
        ArrayList<ArrayList<String>> seqs = this.getIpSeq(ips, 20);
        for (int i = 0; i < seqs.size(); i++)
        {
            this.pingMethod(seqs.get(i));
        }
        return result;
    }

    private void pingMethod(ArrayList<String> ips)
    {
        ArrayList<InetAddress> addrs = new ArrayList<InetAddress>();
        for (int i = 0; i < ips.size(); i++)
        {
            try
            {
                InetAddress temp = InetAddress.getByName(ips.get(i));
                addrs.add(temp);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
        JpcapCaptor captor = null;
        JpcapSender sender = null;
        try
        {
            captor = JpcapCaptor.openDevice(device, 20000, false, 2000); // 2s钟后超时
            sender = captor.getJpcapSenderInstance();
            captor.setFilter("icmp and dst host " + srcIp.getHostAddress(), true);
            // captor.setFilter("icmp", true);
            for (InetAddress ip : addrs)
            {
                try
                {
                    ICMPPacket icmp = IcmpPacketGenerator.getInstance().genIcmpEchoPacket(srcIp, ip, device.mac_address, nextMac, 11);
                    sender.sendPacket(icmp);
                }
                catch (Exception ex)
                {
                    logger.error(ex);
                }
            }
            int num = captor.processPacket(400, new IcmpPacketReceiver());
            logger.debug("num = " + num);
        }
        catch (Exception ex)
        {
            logger.error(ex);
        }
        finally
        {
            this.close(captor, sender);
        }

    }

    private void close(JpcapCaptor captor, JpcapSender sender)
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
     * ip分段
     * @param ips
     * @param seq
     * @return
     */
    private ArrayList<ArrayList<String>> getIpSeq(ArrayList<String> ips, int seq)
    {
        ArrayList<ArrayList<String>> seqs = new ArrayList<ArrayList<String>>();
        int begin = 0;
        int end = begin + seq;
        while (begin < ips.size())
        {
            ArrayList<String> temp = new ArrayList<String>(seq);
            for (; begin < end && begin < ips.size(); begin++)
            {
                temp.add(ips.get(begin));
            }
            seqs.add(temp);
            begin = end;
            end = begin + seq;
        }
        return seqs;
    }

    /**
     * @param args 参数
     * @throws UnknownHostException 异常
     * @throws InterruptedException 异常
     */
    public static void main(String[] args) throws UnknownHostException, InterruptedException
    {
        String head = "192.168.8.";
        ArrayList<String> ips = new ArrayList<String>();
        for (int i = 1; i < 255; i++)
        {
            ips.add(head + i);
        }
        ArrayPing ping = new ArrayPing();
        ArrayList<String> res = ping.ping(ips);
        System.out.println(res.size());
        System.out.println(res);
    }

    class IcmpPacketReceiver implements PacketReceiver
    {

        public IcmpPacketReceiver()
        {
        }

        public void receivePacket(Packet packet)
        {
            if (packet == null)
            {
                logger.info("Timeout");
                return;
            }

            // logger.debug("receive : " + packet);
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
                    String tIp = p.src_ip.getHostAddress();
                    if (!result.contains(tIp))
                    {
                        result.add(tIp);
                    }
                    logger.info(p.src_ip.getHostAddress() + " Ping应答");
                    break;
                }
                default:
                    break;
                }
            }
        }
    }

}
