package jetsennet.jbmp.protocols.linklayer.util;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.protocols.linklayer.util.NetToMediaTable.NetToMediaTableEntity;
import jetsennet.jbmp.util.IPv4AddressUtil;

public class SnmpNetSubnet
{

	/**
	 * 子网IP
	 */
	private String subnetIp;
	/**
	 * 子网掩码
	 */
	private String mask;
	/**
	 * 传入接口
	 */
	private SnmpNetInterface interf;
	/**
	 * 本子网的设备
	 */
	private List<SubnetDevice> subDevs;
	/**
	 * 本子网IP；
	 */
	private List<String> ips;

	public SnmpNetSubnet()
	{
		subDevs = new ArrayList<SubnetDevice>();
		ips = new ArrayList<String>();
	}

	public SnmpNetSubnet(String ip, String mask, NetToMediaTable ip2macTable)
	{
		byte[] temp = IPv4AddressUtil.calSubnetIp(IPv4AddressUtil.stringToByte(ip), IPv4AddressUtil.stringToByte(mask));
		this.mask = mask;
		byte[] maskByte = IPv4AddressUtil.stringToByte(mask);
		this.subnetIp = IPv4AddressUtil.byteToString(temp);
		for (NetToMediaTableEntity entity : ip2macTable.getTable())
		{
			String tempIp = entity.getIp();
			if (IPv4AddressUtil.isInSubnet(tempIp, maskByte, temp))
			{
				if (!ips.contains(tempIp))
				{
					ips.add(tempIp);
					SubnetDevice tempDev = new SubnetDevice();
					tempDev.setIp(tempIp);
					tempDev.setIfIndexOfRouter(entity.getIfIndex());
					subDevs.add(tempDev);
				}
			}
		}
	}

	public List<SubnetDevice> filterBy(List<String> filterIps)
	{
		List<SubnetDevice> retval = new ArrayList<SubnetDevice>();
		int length = ips.size();
		for (int i = 0; i < length; i++)
		{
			String tempIp = ips.get(i);
			if (filterIps.contains(tempIp))
			{
				retval.add(subDevs.get(i));
			}
		}
		return retval;
	}

	@Override
	public String toString()
	{
		return "子网IP：" + this.subnetIp + "；子网掩码：" + this.mask;
	}

	public String getSubnetIp()
	{
		return subnetIp;
	}

	public void setSubnetIp(String subnetIp)
	{
		this.subnetIp = subnetIp;
	}

	public String getMask()
	{
		return mask;
	}

	public void setMask(String mask)
	{
		this.mask = mask;
	}

	public SnmpNetInterface getInterf()
	{
		return interf;
	}

	public void setInterf(SnmpNetInterface interf)
	{
		this.interf = interf;
	}

	public List<String> getIps()
	{
		return ips;
	}

	public void setIps(List<String> ips)
	{
		this.ips = ips;
	}

	public List<SubnetDevice> getSubDevs()
	{
		return subDevs;
	}

	public void setSubDevs(List<SubnetDevice> subDevs)
	{
		this.subDevs = subDevs;
	}

}
