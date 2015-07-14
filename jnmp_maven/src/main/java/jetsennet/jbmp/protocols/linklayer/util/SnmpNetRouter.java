package jetsennet.jbmp.protocols.linklayer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.linklayer.util.RouteTable.RouteTableEntity;

public class SnmpNetRouter
{

	/**
	 * IP集合
	 */
	private Map<String, SnmpNetIp> ips;
	/**
	 * 接口
	 */
	private Map<Integer, SnmpNetInterface> interfMap;
	/**
	 * 路由表
	 */
	private RouteTable routeTable;
	/**
	 * IP转换为MAC表
	 */
	private NetToMediaTable ip2macTable;
	/**
	 * 下一跳路由
	 */
	private List<String> nextRouters;
	/**
	 * 子网
	 */
	private List<SnmpNetSubnet> subs;
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(SnmpNetRouter.class);

	public SnmpNetRouter()
	{
		ips = new HashMap<String, SnmpNetIp>();
		interfMap = new HashMap<Integer, SnmpNetInterface>();
		nextRouters = new ArrayList<String>();
		subs = new ArrayList<SnmpNetSubnet>();
	}

	/**
	 * 初始化
	 * 
	 * @param ip
	 * @param version
	 * @param port
	 * @param community
	 * @throws Exception
	 */
	public void init(String ip, String version, int port, String community, RouterRelation relation) throws Exception
	{
		this.initIps(ip, version, port, community);
		this.initRouteTable(ip, version, port, community);
		this.initNetToMediaTable(ip, version, port, community);
		this.initInterfs(ip, version, port, community);
		this.handleRouteTable();
	}

	public void initIps(String ip, String version, int port, String community) throws Exception
	{
		SnmpTable table = CommonSnmpTable.ipAddrTable();
		SnmpTableUtil.initSnmpTable(table, null, version, ip, port, community);
		logger.debug(ip + "的IP地址表：\n" + table.toString());
		int rowNum = table.getRowNum();
		for (int i = 0; i < rowNum; i++)
		{
			String ipAdEntAddr = table.getCell(i, 0).getEditValue();
			int ipAdEntIfIndex = Integer.valueOf(table.getCell(i, 1).getEditValue());
			String ipAdEntNetMask = table.getCell(i, 2).getEditValue();
			SnmpNetIp temp = new SnmpNetIp(ipAdEntAddr, ipAdEntNetMask, ipAdEntIfIndex);
			this.ips.put(ipAdEntAddr, temp);
		}
	}

	public void initRouteTable(String ip, String version, int port, String community) throws Exception
	{
		routeTable = new RouteTable();
		routeTable.initRouteTable(version, ip, port, community);
		logger.debug(ip + "的路由表：\n" + routeTable.toString());
	}

	public void initNetToMediaTable(String ip, String version, int port, String community) throws Exception
	{
		ip2macTable = new NetToMediaTable();
		ip2macTable.initNetToMediaTable(version, ip, port, community);
		logger.debug(ip + "的IP转MAC表：\n" + ip2macTable.toString());
	}

	public void initInterfs(String ip, String version, int port, String community) throws Exception
	{
		SnmpTable table = CommonSnmpTable.ifTable();
		SnmpTableUtil.initSnmpTable(table, null, version, ip, port, community);
		logger.debug(ip + "的接口表：\n" + table.toString());
		int rowNum = table.getRowNum();
		for (int i = 0; i < rowNum; i++)
		{
			int ifIndex = Integer.valueOf(table.getCell(i, 0).getEditValue());
			String ifDescr = table.getCell(i, 1).getEditValue();
			int ifType = Integer.valueOf(table.getCell(i, 2).getEditValue());
			String ifPhysAddress = table.getCell(i, 3).getEditValue();
			SnmpNetInterface interf = new SnmpNetInterface(ifIndex, ifDescr, ifPhysAddress, ifType);
			interfMap.put(ifIndex, interf);
		}
	}

	public List<String> getAllIps()
	{
		List<String> retval = new ArrayList<String>();
		retval.addAll(ips.keySet());
		return retval;
	}

	public boolean containIp(String ip)
	{
		if (ips.get(ip) == null)
		{
			return false;
		}
		return true;
	}

	public List<String> getNextRouters()
	{
		return nextRouters;
	}

	public List<SnmpNetSubnet> getSubs()
	{
		return subs;
	}

	private void handleRouteTable()
	{
		List<RouteTableEntity> rts = routeTable.getTable();
		for (RouteTableEntity rt : rts)
		{
			logger.debug("处理数据：" + rt.toString());
			// 排除环回端口
			if (!rt.getIpRouteNextHop().equals(SnmpDiscConstants.LOOPBACK_IP))
			{
				int routeType = rt.getIpRouteType();
				int interf = rt.getIpRouteIfIndex();
				String sIp = rt.getIpRouteNextHop();
				Object obj = null;
				SnmpNetInterface snmpIf = interfMap.get(interf);
				if (snmpIf != null)
				{
					if (routeType == RouteTable.IP_ROUTE_TYPE_DIRECK)
					{
						// 子网
						SnmpNetSubnet subnet = new SnmpNetSubnet(sIp, rt.getIpRouteMask(), this.ip2macTable);
						subnet.setInterf(snmpIf);
						subs.add(subnet);
						logger.debug("添加子网：" + subnet.toString());
					}
					else if (routeType == RouteTable.IP_ROUTE_TYPE_INDIRECK)
					{
						// 下一条路由
						if (!this.containIp(sIp) && !nextRouters.contains(sIp))
						{
							nextRouters.add(sIp);
							logger.debug("添加下一条路由：" + sIp);
						}
					}
					else
					{

					}
					snmpIf.setNextHop(obj);
				}
			}
			else
			{
				logger.debug("环回端口");
			}
		}
	}
}
