package jetsennet.jbmp.protocols.linklayer.util;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.mib.MibUtil;
import jetsennet.jbmp.mib.SnmpTableUtil;
import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;

import org.apache.log4j.Logger;

/**
 * IP地址到MAC地址的转换表
 * 
 * @author 郭祥
 */
public class NetToMediaTable
{

	private List<NetToMediaTableEntity> table;

	private static final Logger logger = Logger.getLogger(NetToMediaTable.class);

	public NetToMediaTable()
	{
		table = new ArrayList<NetToMediaTableEntity>();
	}

	public void initNetToMediaTable(String version, String ip, int port, String community)
	{
		try
		{
			SnmpTable table = CommonSnmpTable.ipNetToMediaTable();
			SnmpTableUtil.initSnmpTable(table, null, version, ip, port, community);
			int rowNum = table.getRowNum();
			for (int i = 0; i < rowNum; i++)
			{
				NetToMediaTableEntity temp = new NetToMediaTableEntity();
				temp.ifIndex = Integer.valueOf(table.getCell(i, 0).getEditValue());
				temp.ip = table.getCell(i, 1).getEditValue();
				temp.type = Integer.valueOf(table.getCell(i, 2).getEditValue());
				temp.mac = table.getCell(i, 3).getEditValue();
				this.table.add(temp);
			}
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String head = String.format("%1$-20s %2$-20s %3$-20s", "ipNetToMediaIfIndex", "ipNetToMediaNetAddress", "ipNetToMediaPhysAddress",
				"ipNetToMediaType");
		sb.append(head);
		for (NetToMediaTableEntity entity : table)
		{
			sb.append("\n");
			sb.append(entity.toString());
		}
		return sb.toString();
	}

	public List<NetToMediaTableEntity> getTable()
	{
		return table;
	}

	public void setTable(List<NetToMediaTableEntity> table)
	{
		this.table = table;
	}

	public static class NetToMediaTableEntity
	{
		private int ifIndex;
		private String mac;
		private String ip;
		private int type;

		@Override
		public String toString()
		{
			return String.format("%1$-30s %2$-30s %3$-20s %4$-10s", this.ifIndex, this.ip, this.mac, this.type);
		}

		public int getIfIndex()
		{
			return ifIndex;
		}

		public void setIfIndex(int ifIndex)
		{
			this.ifIndex = ifIndex;
		}

		public String getMac()
		{
			return mac;
		}

		public void setMac(String mac)
		{
			this.mac = mac;
		}

		public String getIp()
		{
			return ip;
		}

		public void setIp(String ip)
		{
			this.ip = ip;
		}

		public int getType()
		{
			return type;
		}

		public void setType(int type)
		{
			this.type = type;
		}
	}

}
