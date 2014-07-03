package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jetsennet.util.StringUtil;

public class SubnetDiscover
{

	/**
	 * 所有路由器IP
	 */
	private ArrayList<String> allRouter;
	/**
	 * 未查询的路由器
	 * FIFO
	 */
	private Queue<String> unusedRouter;
	/**
	 * 已经确认的路由
	 */
	private List<Router> usedRouter;
	/**
	 * SNMP未开启的路由
	 */
	private List<String> unsnmpRouter;

	public SubnetDiscover()
	{
		allRouter = new ArrayList<String>();
		unusedRouter = new LinkedList<String>();
		usedRouter = new ArrayList<Router>();
		unsnmpRouter = new ArrayList<String>();
	}

	public void discover(String defaultRouter)
	{
		if (!StringUtil.isNullOrEmpty(defaultRouter))
		{
			unusedRouter.add(defaultRouter);
			allRouter.add(defaultRouter);
			String curHost = null;
			while ((curHost = unusedRouter.poll()) != null)
			{
				try
				{
					Router r = Router.genRouter("", "192.168.8.145", 161, "public");
					usedRouter.add(r);
					List<String> nextHops = r.nextHops;
					for (String nextHop : nextHops)
					{
						if (nextHop != null && !allRouter.contains(nextHop))
						{
							unusedRouter.add(nextHop);
							allRouter.add(nextHop);
						}
					}
				}
				catch (Exception ex)
				{
					unsnmpRouter.add(curHost);
				}
			}
		}

	}
}
