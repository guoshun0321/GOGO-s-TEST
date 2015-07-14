/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 生成ICMP报文
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;


/**
 * @author 郭祥
 */
public final class IcmpPacketGenerator
{
//
//    // <editor-fold defaultstate="collapsed" desc="单例">
//    private static IcmpPacketGenerator instance = new IcmpPacketGenerator();
//
//    private IcmpPacketGenerator()
//    {
//    }
//
//    public static IcmpPacketGenerator getInstance()
//    {
//        return instance;
//    }
//
//    // </editor-fold>
//
//    /**
//     * 生成ICMP ECHO包
//     * @param srcIp 源IP
//     * @param id 参数
//     * @param dstIp 目标IP
//     * @param srcMac 源MAC
//     * @param nextMac 下一跳MAC
//     * @return 结果
//     */
//    public ICMPPacket genIcmpEchoPacket(InetAddress srcIp, InetAddress dstIp, byte[] srcMac, byte[] nextMac, int id)
//    {
//        // ICMP报文头
//        ICMPPacket icmp = new ICMPPacket();
//        icmp.type = ICMPPacket.ICMP_ECHO;
//        icmp.seq = (short) PacketSequence.getInstance().nextIcmp();
//        icmp.id = (short) id;
//        icmp.data = "abcdefghijklmnopqrstuvwabcdefghi".getBytes();
//
//        // IP报文头，注意IP标识的累加以及TTL的设置
//        icmp.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, PacketSequence.getInstance().nextIp(), 64, IPPacket.IPPROTO_ICMP,
//            srcIp, dstIp);
//
//        // 数据链路层报文头
//        EthernetPacket ether = new EthernetPacket();
//        ether.frametype = EthernetPacket.ETHERTYPE_IP;
//        ether.src_mac = srcMac;
//        ether.dst_mac = nextMac;
//        icmp.datalink = ether;
//
//        return icmp;
//    }
}
