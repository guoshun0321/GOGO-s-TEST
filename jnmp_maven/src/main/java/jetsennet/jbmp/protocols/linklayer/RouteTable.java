package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;

/**
 * 路由表
 * 
 * @author 郭祥
 */
public class RouteTable
{

	public final ArrayList<RouteTableEntity> table;

	public static final Logger logger = Logger.getLogger(RouteTable.class);

	public RouteTable()
	{
		table = new ArrayList<RouteTableEntity>();
	}

	public static void fillRouteTable(RouteTable rt, String version, String ip, int port, String community)
	{
		if (rt == null)
		{
			return;
		}
		try
		{
			SnmpTable table = CommonSnmpTable.ipRouteTable();
			SnmpTableUtil.initSnmpTable(table, null, version, ip, port, community);
			int rowNum = table.getRowNum();
			for (int i = 0; i < rowNum; i++)
			{
				RouteTableEntity entity = new RouteTableEntity();
				entity.ipRouteIfIndex = Integer.valueOf(table.getCell(i, 0).getEditValue());
				entity.ipRouteDesk = table.getCell(i, 1).getEditValue();
				entity.ipRouteMask = table.getCell(i, 2).getEditValue();
				entity.ipRouteNextHop = table.getCell(i, 3).getEditValue();
				entity.ipRouteType = Integer.valueOf(table.getCell(i, 4).getEditValue());
				rt.table.add(entity);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	static class RouteTableEntity
	{

		/**
		 * 接口编号
		 */
		public int ipRouteIfIndex;
		/**
		 * 目的地址
		 */
		public String ipRouteDesk;
		/**
		 * 掩码
		 */
		public String ipRouteMask;
		/**
		 * 下一跳地址
		 */
		public String ipRouteNextHop;
		/**
		 * 路由条目类型
		 */
		public int ipRouteType;

		public static int IP_ROUTE_TYPE_OTHER = 1;
		public static int IP_ROUTE_TYPE_INVALID = 2;
		public static int IP_ROUTE_TYPE_DIRECK = 3;
		public static int IP_ROUTE_TYPE_INDIRECK = 4;
	}

	public static void main(String[] args)
	{
		RouteTable rt = new RouteTable();
		RouteTable.fillRouteTable(rt, "", "192.168.8.145", 161, "public");
		System.out.println(rt);
	}

}
