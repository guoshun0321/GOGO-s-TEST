/************************************************************************
日 期: 2012-2-27
作 者: 郭祥
版 本: v1.3
描 述: 批量接收和发送ARP报文
历 史:
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;


/**
 * 批量接收和发送ARP报文
 * @author 郭祥
 */
public class ArrayARP
{
//
//    /**
//     * 捕获器
//     */
//    private JpcapCaptor captor;
//    /**
//     * 发送器
//     */
//    private JpcapSender sender;
//    /**
//     * 接口
//     */
//    private NetworkInterface device;
//    /**
//     * 源IP
//     */
//    private InetAddress srcIp;
//    /**
//     * 结果
//     */
//    private Map<String, byte[]> ip2mac;
//    /**
//     * 超时时间
//     */
//    private int timeout;
//    int num = 0;
//    /**
//     * 日志
//     */
//    private static final Logger logger = Logger.getLogger(ArrayARP.class);
//
//    /**
//     * 构造函数
//     */
//    public ArrayARP()
//    {
//        String localHost = ConfigUtil.getPingLocalHost();
//        logger.debug("配置文件configuration.properties中设置的IP：" + localHost);
//        device = IcmpUtil.getUsableInterface(localHost);
//        if (device == null)
//        {
//            throw new ARPException("无可用网卡设备，配置的IP：" + localHost + "。请确认操作系统登录帐号为管理员帐号。");
//        }
//        srcIp = IcmpUtil.getUsableInetAddress(device, localHost);
//        if (srcIp == null)
//        {
//            throw new ARPException("无可用IP，配置的IP：" + localHost);
//        }
//        timeout = ConnectInfo.ARP_TIMEOUT;
//        ip2mac = new LinkedHashMap<String, byte[]>();
//    }
//
//    /**
//     * 通过ARP协议判断给定IP是否存在，如果存在返回MAC地址
//     * @param ips 参数
//     * @return 结果
//     */
//    public Map<String, byte[]> arp(List<String> ips)
//    {
//        ArrayList<InetAddress> addrs = new ArrayList<InetAddress>();
//        for (int i = 0; i < ips.size(); i++)
//        {
//            try
//            {
//                InetAddress temp = InetAddress.getByName(ips.get(i));
//                addrs.add(temp);
//            }
//            catch (Exception ex)
//            {
//                logger.error(ex);
//            }
//        }
//        try
//        {
//            this.openCaptor();
//            this.send(addrs);
//        }
//        finally
//        {
//            this.close();
//        }
//        return ip2mac;
//    }
//
//    /**
//     * 初始化捕获器和发送器
//     */
//    private void openCaptor()
//    {
//        try
//        {
//            captor = JpcapCaptor.openDevice(device, 20000, false, timeout);
//            captor.setFilter("arp dst " + srcIp.getHostAddress(), true);
//            sender = captor.getJpcapSenderInstance();
//        }
//        catch (Exception ex)
//        {
//            throw new IcmpException(ex);
//        }
//    }
//
//    /**
//     * 发送ARP包
//     * @param ips
//     */
//    private void send(ArrayList<InetAddress> ips)
//    {
//        for (InetAddress dstIp : ips)
//        {
//            ARPPacket arp = IcmpUtil.genArpPacket(device, srcIp, dstIp);
//            sender.sendPacket(arp);
//        }
//        captor.processPacket(-1, new ArpPacketReceiver());
//        System.out.println("num = " + num);
//    }
//
//    /**
//     * 关闭捕获器和发送器
//     */
//    private void close()
//    {
//        if (captor != null)
//        {
//            try
//            {
//                captor.close();
//            }
//            catch (Exception ex)
//            {
//                logger.error(ex);
//            }
//            finally
//            {
//                captor = null;
//            }
//        }
//        if (sender != null)
//        {
//            try
//            {
//                sender.close();
//            }
//            catch (Exception ex)
//            {
//                logger.error(ex);
//            }
//            finally
//            {
//                sender = null;
//            }
//        }
//    }
//
//    /**
//     * 包处理
//     * @author GoGo
//     */
//    class ArpPacketReceiver implements PacketReceiver
//    {
//
//        @Override
//        public void receivePacket(Packet packet)
//        {
//            num++;
//            if (packet instanceof ARPPacket)
//            {
//                ARPPacket p = (ARPPacket) packet;
//                ip2mac.put(IPv4AddressUtil.byteToString(p.sender_protoaddr), p.sender_hardaddr);
//            }
//        }
//    }
//
//    /**
//     * @param args 参数
//     * @throws Exception 异常
//     */
//    public static void main(String[] args) throws Exception
//    {
//        long begin = System.currentTimeMillis();
//        String head = "192.168.10.";
//        ArrayList<String> ips = new ArrayList<String>();
//        for (int i = 1; i <= 255; i++)
//        {
//            ips.add(head + i);
//        }
//        ArrayARP ping = new ArrayARP();
//        Map<String, byte[]> res = ping.arp(ips);
//        for (String key : res.keySet())
//        {
//            System.out.println(key + " : " + IPv4AddressUtil.macByte2String(res.get(key)));
//        }
//        System.out.println(res.size());
//        long end = System.currentTimeMillis();
//        System.out.println("time : " + (end - begin));
//    }

}
