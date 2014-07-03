package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;

import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.linklayer.RouteTable.RouteTableEntity;

import org.apache.log4j.Logger;

/**
 * 路由器实体
 * 
 * @author 郭祥
 */
public class Router
{
	public final ArrayList<String> ips;
	public final ArrayList<Integer> interfs;
	public final ArrayList<String> masks;
	public final RouteTable routeTable;
	public final ArrayList<String> nextHops;
	public final ArrayList<String> subs;
	public final ArrayList<String> subMasks;

	public static final String IP_LOOPBACK = "127.0.0.1";
	public static final String MASK_HOST = "255.255.255.255";

	public static final Logger logger = Logger.getLogger(RouteTable.class);

	public Router()
	{
		ips = new ArrayList<String>();
		interfs = new ArrayList<Integer>();
		masks = new ArrayList<String>();
		routeTable = new RouteTable();
		nextHops = new ArrayList<String>();
		subs = new ArrayList<String>();
		subMasks = new ArrayList<String>();
	}

	public static Router genRouter(String version, String ip, int port, String community) throws Exception
	{
		Router retval = null;
		try
		{
			retval = new Router();
			fillIpInfo(retval, version, ip, port, community);
			RouteTable.fillRouteTable(retval.routeTable, version, ip, port, community);
			parseRouteTable(retval);
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	public static void fillIpInfo(Router router, String version, String ip, int port, String community) throws Exception
	{
		SnmpTable table = CommonSnmpTable.ipAddrTable();
		SnmpTableUtil.initSnmpTable(table, null, version, ip, port, community);
		int rowNum = table.getRowNum();
		for (int i = 0; i < rowNum; i++)
		{
			String ipAdEntAddr = table.getCell(i, 0).getEditValue();
			int ipAdEntIfIndex = Integer.valueOf(table.getCell(i, 1).getEditValue());
			String ipAdEntNetMask = table.getCell(i, 2).getEditValue();
			router.ips.add(ipAdEntAddr);
			router.interfs.add(ipAdEntIfIndex);
			router.masks.add(ipAdEntNetMask);
		}
	}

	/**
	 * 下一条地址，排除本机IP和环回IP
	 * 
	 * @return
	 */
	public static void parseRouteTable(Router router)
	{
		ArrayList<String> nextHops = router.nextHops;
		ArrayList<String> subs = router.subs;
		ArrayList<String> subMasks = router.subMasks;
		for (RouteTableEntity entity : router.routeTable.table)
		{
			String tempNext = entity.ipRouteNextHop;
			String tempMask = entity.ipRouteMask;
			String tempDesk = entity.ipRouteDesk;
			if (tempNext != null && !IP_LOOPBACK.equals(tempNext) && !MASK_HOST.equals(tempMask) && !router.ips.contains(tempNext)
					&& !nextHops.contains(tempNext))
			{
				nextHops.add(tempNext);
			}

			if (tempNext != null && !IP_LOOPBACK.equals(tempNext) && !MASK_HOST.equals(tempMask) && router.ips.contains(tempNext)
					&& !subs.contains(tempDesk))
			{
				subs.add(tempDesk);
				subMasks.add(tempMask);
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		Router r = Router.genRouter("", "192.168.8.145", 161, "public");
		System.out.println(r);
	}
}
