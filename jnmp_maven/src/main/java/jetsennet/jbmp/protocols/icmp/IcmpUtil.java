/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: ICMP工具
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;


/**
 * ICMP工具
 * @author 郭祥
 */
public class IcmpUtil
{
//
//    private static final Logger logger = Logger.getLogger(IcmpUtil.class);
//    /**
//     * 广播的MAC地址
//     */
//    public static final byte[] broadcastMac = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
//
//    // <editor-fold defaultstate="collapsed" desc="网络设备和端口选择">
//    /**
//     * 获取有IP地址和ip在同一局域网的网卡
//     * @param ip 参数
//     * @return 结果
//     */
//    public static NetworkInterface getLocalInterface(InetAddress ip)
//    {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        NetworkInterface device = null;
//
//        for (NetworkInterface d : devices)
//        {
//            for (NetworkInterfaceAddress addr : d.addresses)
//            {
//                if (!(addr.address instanceof Inet4Address))
//                {
//                    continue;
//                }
//                // 判断是否在同一个局域网
//                byte[] bip = ip.getAddress();
//                byte[] subnet = addr.subnet.getAddress();
//                byte[] bif = addr.address.getAddress();
//                for (int i = 0; i < 4; i++)
//                {
//                    bip[i] = (byte) (bip[i] & subnet[i]);
//                    bif[i] = (byte) (bif[i] & subnet[i]);
//                }
//                if (Arrays.equals(bip, bif))
//                {
//                    device = d;
//                    break;
//                }
//            }
//            if (device != null)
//            {
//                break;
//            }
//        }
//        return device;
//    }
//
//    /**
//     * 获取可用的网卡
//     * @return 结果
//     */
//    public static NetworkInterface getUsableInterface()
//    {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        NetworkInterface device = null;
//
//        for (NetworkInterface d : devices)
//        {
//            for (NetworkInterfaceAddress addr : d.addresses)
//            {
//                if (!(addr.address instanceof Inet4Address))
//                {
//                    continue;
//                }
//                else
//                {
//                    device = d;
//                    break;
//                }
//            }
//            if (device != null)
//            {
//                break;
//            }
//        }
//        return device;
//    }
//
//    /**
//     * 获取给定网卡上的可用IP地址
//     * @param device 参数
//     * @return 结果
//     */
//    public static InetAddress getUsableInetAddress(NetworkInterface device)
//    {
//        InetAddress srcip = null;
//        for (NetworkInterfaceAddress addr : device.addresses)
//        {
//            if (addr.address instanceof Inet4Address)
//            {
//                srcip = addr.address;
//                break;
//            }
//        }
//        return srcip;
//    }
//
//    /**
//     * 获取可用的网卡
//     * @param srcIp 参数
//     * @return 结果
//     */
//    public static NetworkInterface getUsableInterface(String srcIp)
//    {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        NetworkInterface device = null;
//
//        for (NetworkInterface d : devices)
//        {
//            for (NetworkInterfaceAddress addr : d.addresses)
//            {
//                logger.info("检查网卡：" + d.name + " -> " + d.description);
//                if (addr.address instanceof Inet4Address)
//                {
//                    String tempIp = addr.address.getHostAddress();
//                    logger.info("检查IP：" + tempIp);
//                    if (tempIp.equals(srcIp))
//                    {
//                        logger.info("选择网卡：" + d.name + " -> " + d.description);
//                        device = d;
//                        break;
//                    }
//                }
//            }
//        }
//        return device;
//    }
//
//    /**
//     * 获取给定网卡上的可用IP地址
//     * @param device 参数
//     * @param srcIp 参数
//     * @return 结果
//     */
//    public static InetAddress getUsableInetAddress(NetworkInterface device, String srcIp)
//    {
//        InetAddress srcip = null;
//        for (NetworkInterfaceAddress addr : device.addresses)
//        {
//            String tempIp = addr.address.getHostAddress();
//            logger.info("检查IP：" + tempIp);
//            if (addr.address instanceof Inet4Address && tempIp.equals(srcIp))
//            {
//                logger.info("IP：" + tempIp + "合格。");
//                srcip = addr.address;
//                break;
//            }
//        }
//        return srcip;
//    }
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="ARP 和 ICMP 协议包">
//    /**
//     * 生成ARP包
//     * @param device 参数
//     * @param srcIp 参数
//     * @param dstIp 参数
//     * @return 结果
//     */
//    public static ARPPacket genArpPacket(NetworkInterface device, InetAddress srcIp, InetAddress dstIp)
//    {
//        ARPPacket arp = new ARPPacket();
//        arp.hardtype = ARPPacket.HARDTYPE_ETHER;
//        arp.prototype = ARPPacket.PROTOTYPE_IP;
//        arp.operation = ARPPacket.ARP_REQUEST;
//        arp.hlen = 6;
//        arp.plen = 4;
//        arp.sender_hardaddr = device.mac_address;
//        arp.sender_protoaddr = srcIp.getAddress();
//        arp.target_hardaddr = broadcastMac;
//        arp.target_protoaddr = dstIp.getAddress();
//
//        EthernetPacket ether = new EthernetPacket();
//        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
//        ether.src_mac = device.mac_address;
//        ether.dst_mac = broadcastMac;
//        arp.datalink = ether;
//        return arp;
//    }
//
//    // </editor-fold>
//
//    /**
//     * 获取给定网关MAC地址
//     * @param gateway 网关
//     * @return 结果
//     */
//    public static byte[] getGetwayMac(String gateway)
//    {
//        byte[] mac = null;
//        try
//        {
//            ARP arp = new ARP();
//            mac = arp.arp(gateway);
//        }
//        catch (Exception ex)
//        {
//            throw new IcmpException("无效的网关：" + gateway);
//        }
//        return mac;
//    }
}
