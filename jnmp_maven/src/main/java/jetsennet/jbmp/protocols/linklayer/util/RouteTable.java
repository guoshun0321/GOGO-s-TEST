package jetsennet.jbmp.protocols.linklayer.util;

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

	/**
	 * 路由表
	 */
	private ArrayList<RouteTableEntity> table;
	public static int IP_ROUTE_TYPE_OTHER = 1;
	public static int IP_ROUTE_TYPE_INVALID = 2;
	public static int IP_ROUTE_TYPE_DIRECK = 3;
	public static int IP_ROUTE_TYPE_INDIRECK = 4;
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(RouteTable.class);

	public RouteTable()
	{
		table = new ArrayList<RouteTableEntity>();
	}

	public void initRouteTable(String version, String ip, int port, String community)
	{
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
				this.table.add(entity);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	public ArrayList<RouteTableEntity> getTable()
	{
		return table;
	}

	public void setTable(ArrayList<RouteTableEntity> table)
	{
		this.table = table;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String head = String.format("%1$-20s %2$-20s %3$-20s %4$-20s %5$-20s\n", "ipRouteIfIndex", "ipRouteDesk", "ipRouteMask", "ipRouteNextHop",
				"ipRouteType");
		sb.append(head);
		for (RouteTableEntity entity : table)
		{
			sb.append("\n");
			sb.append(entity.toString());
		}
		return sb.toString();
	}

	public static class RouteTableEntity
	{

		/**
		 * 接口编号
		 */
		private int ipRouteIfIndex;
		/**
		 * 目的地址
		 */
		private String ipRouteDesk;
		/**
		 * 掩码
		 */
		private String ipRouteMask;
		/**
		 * 下一跳地址
		 */
		private String ipRouteNextHop;
		/**
		 * 路由条目类型
		 */
		private int ipRouteType;

		@Override
		public String toString()
		{
			return String.format("%1$-20s %2$-20s %3$-20s %4$-20s %5$-20s", this.ipRouteIfIndex, this.ipRouteDesk, this.ipRouteMask,
					this.ipRouteNextHop, this.ipRouteType);
		}

		public int getIpRouteIfIndex()
		{
			return ipRouteIfIndex;
		}

		public void setIpRouteIfIndex(int ipRouteIfIndex)
		{
			this.ipRouteIfIndex = ipRouteIfIndex;
		}

		public String getIpRouteDesk()
		{
			return ipRouteDesk;
		}

		public void setIpRouteDesk(String ipRouteDesk)
		{
			this.ipRouteDesk = ipRouteDesk;
		}

		public String getIpRouteMask()
		{
			return ipRouteMask;
		}

		public void setIpRouteMask(String ipRouteMask)
		{
			this.ipRouteMask = ipRouteMask;
		}

		public String getIpRouteNextHop()
		{
			return ipRouteNextHop;
		}

		public void setIpRouteNextHop(String ipRouteNextHop)
		{
			this.ipRouteNextHop = ipRouteNextHop;
		}

		public int getIpRouteType()
		{
			return ipRouteType;
		}

		public void setIpRouteType(int ipRouteType)
		{
			this.ipRouteType = ipRouteType;
		}

	}

	public static void main(String[] args)
	{
		RouteTable rt = new RouteTable();
		rt.initRouteTable("", "192.168.8.145", 161, "public");
		System.out.println(rt);
	}

}
