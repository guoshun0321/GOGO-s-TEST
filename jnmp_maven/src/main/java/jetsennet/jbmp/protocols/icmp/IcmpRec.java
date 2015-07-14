/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: ICMP报文接收
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;


/**
 * @author 郭祥
 */
public class IcmpRec
{
//
//    private JpcapCaptor captor;
//    /**
//     * 网卡
//     */
//    private NetworkInterface device;
//    /**
//     * 源地址IP
//     */
//    private InetAddress srcIp;
//    /**
//     * 目标地址mac，一般为默认网关的mac地址。如果是同一局域网内，可为目标主机地址。
//     */
//    private byte[] nextMac;
//    private static AtomicInteger seq = new AtomicInteger(0);
//    private static final Logger logger = Logger.getLogger(IcmpRec.class);
//
//    /**
//     * 构造函数
//     */
//    public IcmpRec()
//    {
//        device = IcmpUtil.getUsableInterface();
//        if (device == null)
//        {
//            throw new IcmpException("无可用网卡");
//        }
//        srcIp = IcmpUtil.getUsableInetAddress(device);
//        this.nextMac = IcmpUtil.getGetwayMac(ConfigUtil.getPingGateWay());
//    }
//
//    /**
//     *
//     */
//    public void rec()
//    {
//        try
//        {
//            captor = JpcapCaptor.openDevice(device, 2000, false, 20); // 5s钟后超时
//            captor.setNonBlockingMode(true);
//            captor.setFilter("icmp and dst host " + srcIp.getHostAddress(), false);
//
//            while (true)
//            {
//                captor.loopPacket(-1, new IcmpPacketReceiver());
//            }
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex);
//        }
//    }
//
//    /**
//     * @param args
//     * @throws UnknownHostException
//     * @throws InterruptedException
//     */
//    public static void main(String[] args) throws UnknownHostException, InterruptedException
//    {
//        IcmpRec ping = new IcmpRec();
//        ping.rec();
//        TimeUnit.MINUTES.sleep(10);
//    }
//
//    class IcmpPacketReceiver implements PacketReceiver
//    {
//
//        public IcmpPacketReceiver()
//        {
//        }
//
//        public void receivePacket(Packet packet)
//        {
//            if (packet == null)
//            {
//                logger.info("Timeout");
//                return;
//            }
//            logger.debug("receive : " + packet);
//            if (packet instanceof ICMPPacket)
//            {
//                ICMPPacket p = (ICMPPacket) packet;
//                byte recType = p.type;
//                byte recCode = p.code;
//
//                switch (recType)
//                {
//                // 回显应答
//                case ICMPPacket.ICMP_ECHOREPLY:
//                {
//                    logger.info(p.src_ip.getHostAddress() + " Ping应答");
//                    break;
//                }
//                default:
//                    break;
//                }
//            }
//        }
//    }

}
