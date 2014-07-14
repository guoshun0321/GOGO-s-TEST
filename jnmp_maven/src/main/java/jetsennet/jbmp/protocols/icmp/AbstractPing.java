/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

/**
 * @author Guo
 */
public abstract class AbstractPing
{
    /**
     * @param ip ip
     * @return 结果
     */
    public abstract boolean ping(String ip);
}
