package jetsennet.jbmp.protocols.linklayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

import jetsennet.jbmp.mib.node.CommonSnmpTable;
import jetsennet.jbmp.mib.node.SnmpTable;
import jetsennet.jbmp.protocols.icmp.ArrayARP;
import jetsennet.jbmp.protocols.linklayer.util.SnmpNetSubnet;
import jetsennet.jbmp.protocols.linklayer.util.SubnetDevice;
import jetsennet.jbmp.protocols.snmp.ArraySnmp;
import jetsennet.jbmp.util.IPv4AddressUtil;

public class LinklevelDiscovery
{

	/**
	 * 需要扫描的OID
	 * sysObjectID/sysName/sysDescr/sysServices/ipForwarding
	 */
	private static final String[] SCAN_OIDS = new String[] { "1.3.6.1.2.1.1.2.0", "1.3.6.1.2.1.1.5.0", "1.3.6.1.2.1.1.1.0", "1.3.6.1.2.1.1.7",
			"1.3.6.1.2.1.4.1" };
	/**
	 * 所有路由器和交换机的MAC地址集合
	 */
	private List<String> allMacs;
	/**
	 * 设备
	 */
	private Map<String, SubnetDevice> devs;
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(LinklevelDiscovery.class);

	public LinklevelDiscovery()
	{
		this.allMacs = new ArrayList<String>();
	}

	public List<String> disSubnet(SnmpNetSubnet subnet) throws Exception
	{
		// 获取路由器MAC地址
		String routerMac = subnet.getInterf().getMac();
		List<String> routeIps = subnet.getIps();
		List<SubnetDevice> routeDevs = subnet.getSubDevs();

		for (SubnetDevice routerDev : routeDevs)
		{
			devs.put(routerDev.getIp(), routerDev);
		}

		// arp扫描
		ArrayARP ping = new ArrayARP();
		Map<String, byte[]> pingIps = ping.arp(routeIps);
		List<String> pingArray = new ArrayList<String>();

		for (String pingIp : pingIps.keySet())
		{
			pingArray.add(pingIp);
			devs.get(pingIp).setProp("arp.ping", "1");
		}
		// 找出所有的设备，区分出switch,router,host，记录switch和路由器的MAC地址
		// 获取所有switch的转发表
		// 根据转发表计算switch和switch，switch和router之间的连接关系
		// 计算路由器和
		return null;
	}

	private void scanWithSnmp(List<String> usedIps)
	{
		try
		{
			ArraySnmp snmp = new ArraySnmp();
			List<ResponseEvent> events = snmp.snmp(usedIps, SCAN_OIDS);
			for (ResponseEvent event : events)
			{
				String ip = event.getPeerAddress().toString();
				ip = ip.substring(0, ip.lastIndexOf("/"));
				SubnetDevice dev = devs.get(ip);
				PDU pdu = event.getResponse();
				if (pdu != null && pdu.getVariableBindings().size() > 1)
				{
					Vector<VariableBinding> bingdings = pdu.getVariableBindings();
					String sysObjectID = bingdings.get(0).getVariable().toString();
					String sysName = bingdings.get(1).getVariable().toString();
					String sysDescr = bingdings.get(2).getVariable().toString();
					int sysServices = Integer.valueOf(bingdings.get(3).getVariable().toString());
					int ipForwarding = Integer.valueOf(bingdings.get(4).getVariable().toString());
					if (ipForwarding == 2 && (sysServices == 2 || sysServices == 3))
					{
						dev.setType(SubnetDevice.SUBNET_TYPE_SEC);
					}
					else if (ipForwarding == 1 && sysServices > 3)
					{
						SnmpTable table = CommonSnmpTable.dot1dBasePortTable();
						if (table.getRowNum() <= 0)
						{
							dev.setType(SubnetDevice.SUBNET_TYPE_THIRD_ROUTER);
						}
						else
						{
							dev.setType(SubnetDevice.SUBNET_TYPE_THIRD_SWITCH);
						}
					}
					else
					{
						dev.setType(SubnetDevice.SUBNET_TYPE_HOST);
					}
				}
				else
				{
					dev.setType(SubnetDevice.SUBNET_TYPE_NOSNMP);
				}
			}

		}
		catch (Exception ex)
		{
			logger.debug("", ex);
		}
	}

	private List<String> ensureIpRange(String subnet, String mask)
	{
		List<String> retval = new ArrayList<String>();
		byte[] maskBytes = IPv4AddressUtil.stringToByte(mask);
		int maskLength = this.calNumOneOfMask(maskBytes);
		int maskLastLength = 32 - maskLength;
		long maxLast = (long) Math.pow(2, maskLastLength) - 2;
		long minLast = 1;
		long subnetL = IPv4AddressUtil.stringToLong(subnet);
		long maxSub = subnetL + maxLast;
		long minSub = subnetL + minLast;
		for (long l = minSub; l <= maxSub; l++)
		{
			retval.add(IPv4AddressUtil.longToString(l));
		}
		return retval;
	}

	/**
	 * 判断是否属于交换机
	 * @return
	 */
	public boolean isSwitch()
	{
		return true;
	}

	private int calNumOneOfMask(byte[] maskBytes)
	{
		int retval = 0;
		for (int i = 0; i < maskBytes.length; i++)
		{
			retval = retval + calNumOneOfByte(maskBytes[i]);
		}
		return retval;
	}

	private int calNumOneOfByte(byte b)
	{
		int temp = b & 0xff;
		temp = ((temp & 0xAAAA) >> 1) + (temp & 0x5555);
		temp = ((temp & 0xCCCC) >> 2) + (temp & 0x3333);
		temp = ((temp & 0xF0F0) >> 4) + (temp & 0x0F0F);
		temp = ((temp & 0xFF00) >> 8) + (temp & 0x00FF);
		return temp;
	}

	public static void main(String[] args)
	{
		System.out.println(Math.pow(2, 2));
		LinklevelDiscovery sd = new LinklevelDiscovery();
		sd.ensureIpRange("130.0.0.0", "255.255.255.0");
	}

}
