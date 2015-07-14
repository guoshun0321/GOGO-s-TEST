/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 产生唯一的icmp或ip编号
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 产生唯一的icmp或ip编号
 * @author 郭祥
 */
public final class PacketSequence
{

    private AtomicInteger icmpSeq;
    private AtomicInteger ipSeq;
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static PacketSequence ps = new PacketSequence();

    private PacketSequence()
    {
        icmpSeq = new AtomicInteger(0);
        ipSeq = new AtomicInteger(0);
    }

    public static PacketSequence getInstance()
    {
        return ps;
    }

    // </editor-fold>

    /**
     * @return 结果
     */
    public int nextIp()
    {
        return ipSeq.getAndIncrement();
    }

    /**
     * @return 结果
     */
    public int nextIcmp()
    {
        return icmpSeq.getAndIncrement();
    }
}
