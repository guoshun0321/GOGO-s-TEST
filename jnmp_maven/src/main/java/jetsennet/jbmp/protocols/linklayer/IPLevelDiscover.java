/**********************************************************************
 * 日 期: 2012-07-05
 * 作 者: 郭祥
 * 版 本: v1.0
 * 描 述: 网络层自动发现
 * 历 史: 
 *********************************************************************/
package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jetsennet.jbmp.protocols.linklayer.util.RouterRelation;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetRouter;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetSubnet;
import jetsennet.util.StringUtil;

import org.apache.log4j.Logger;

public class IPLevelDiscover
{

	/**
	 * 所有路由器IP
	 */
	private ArrayList<String> allIps;
	/**
	 * 未查询的路由器
	 * FIFO
	 */
	private Queue<String> unusedIps;
	/**
	 * 已用过的IP
	 */
	private ArrayList<String> usedIps;
	/**
	 * 已经确认的路由
	 */
	private List<SnmpNetRouter> ensuredRouter;
	/**
	 * SNMP未开启的路由
	 */
	private List<String> unsnmpRouter;
	/**
	 * 子网集合
	 */
	private List<SnmpNetSubnet> subnets;
	/**
	 * 路由关系
	 */
	private RouterRelation relation;
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(IPLevelDiscover.class);

	public IPLevelDiscover()
	{
		allIps = new ArrayList<String>();
		unusedIps = new LinkedList<String>();
		usedIps = new ArrayList<String>();
		ensuredRouter = new ArrayList<SnmpNetRouter>();
		unsnmpRouter = new ArrayList<String>();
		subnets = new ArrayList<SnmpNetSubnet>();
		relation = new RouterRelation();
	}

	public void discover1(String defaultIp, String version, int port, String community)
	{
		if (!StringUtil.isNullOrEmpty(defaultIp))
		{
			unusedIps.add(defaultIp);
			allIps.add(defaultIp);
			String curIp = null;
			while ((curIp = unusedIps.poll()) != null)
			{
				try
				{
					// 被检测IP是否已用
					if (usedIps.contains(curIp))
					{
						break;
					}
					// 初始化路由器信息
					SnmpNetRouter router = new SnmpNetRouter();
					router.init(curIp, version, port, community, this.relation);
					ensuredRouter.add(router);
					// 将该路由的所有IP设置为已用
					List<String> routerIps = router.getAllIps();
					for (String routerIp : routerIps)
					{
						if (!usedIps.contains(routerIp))
						{
							usedIps.add(routerIp);
						}
						if (!allIps.contains(routerIp))
						{
							allIps.add(routerIp);
						}
					}
					// 将下一条IP添加到未用
					List<String> nextIps = router.getNextRouters();
					for (String nextIp : nextIps)
					{
						if (!unusedIps.contains(nextIp))
						{
							unusedIps.add(nextIp);
						}
						if (!allIps.contains(nextIp))
						{
							allIps.add(nextIp);
						}
					}
					// 添加到子网集合
					List<SnmpNetSubnet> subs = router.getSubs();
					for (SnmpNetSubnet sub : subs)
					{
						this.addSub(sub);
					}
				}
				catch (Exception ex)
				{
					logger.error("", ex);
					unsnmpRouter.add(curIp);
				}
			}
		}
	}

	private void addSub(SnmpNetSubnet sub)
	{
		boolean isMatch = false;
		for (SnmpNetSubnet subnet : subnets)
		{
			if (subnet.getMask().equals(sub.getMask()) && subnet.getSubnetIp().equals(sub.getSubnetIp()))
			{
				isMatch = true;
				break;
			}
		}
		if (!isMatch)
		{
			subnets.add(sub);
		}
	}
}
