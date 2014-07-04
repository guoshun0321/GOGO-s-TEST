/************************************************************************
日 期: 2012-2-20
作 者: 郭祥
版 本: v1.3
描 述: 使用ARP协议进行自动发现
历 史:
 ************************************************************************/
package jetsennet.jbmp.autodiscovery.helper;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.autodiscovery.AutoDisConstant;
import jetsennet.jbmp.autodiscovery.AutoDisUtil;
import jetsennet.jbmp.autodiscovery.DiscoverException;
import jetsennet.jbmp.protocols.icmp.ArrayARP;
import jetsennet.jbmp.util.IPv4AddressUtil;

/**
 * 使用ARP协议进行自动发现
 * @author 郭祥
 */
public class NetworkDiscover extends AbsDiscover
{

	private static final Logger logger = Logger.getLogger(NetworkDiscover.class);

	/**
	 * 构造方法
	 */
	public NetworkDiscover()
	{
	}

	@Override
	protected AutoDisResult find(AutoDisResult coll, List<SingleResult> irs) throws DiscoverException
	{
		ArrayARP arp = new ArrayARP();
		List<String> ips = AutoDisUtil.getIpColl(irs);
		logger.debug("ARP自动发现开始。");
		// 删除ARP自动发现
//		Map<String, byte[]> res = arp.arp(ips);
//		for (String ip : ips)
//		{
//			logger.debug(ip + " : " + res.get(ip));
//			SingleResult ir = coll.getByIp(ip);
//			if (res.get(ip) != null)
//			{
//				ProResult pr = new ProResult(AutoDisConstant.PRO_NAME_ARP);
//				String mac = IPv4AddressUtil.macByte2String(res.get(ip));
//				pr.addResult(AutoDisConstant.ARP_MAC, mac);
//				ir.addProResult(pr);
//			}
//		}
		logger.debug("ARP自动发现结束。");
		return coll;
	}
}
