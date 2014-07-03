/************************************************************************
日 期：2012-05-24
作 者: 郭祥
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.protocols.icmp;

import java.net.InetAddress;

/**
 * @author Guo
 */
public class DefaultPing extends AbstractPing
{

    @Override
    public boolean ping(String ip)
    {
        try
        {
            ICMP icmp = new ICMP(InetAddress.getByName(ip));
            return icmp.ping();
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
