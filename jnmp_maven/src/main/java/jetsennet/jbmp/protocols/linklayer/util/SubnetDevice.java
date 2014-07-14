package jetsennet.jbmp.protocols.linklayer.util;

import java.util.HashMap;
import java.util.Map;

public class SubnetDevice
{

	/**
	 * IP
	 */
	private String ip;
	/**
	 * 连接到路由器时，路由器的端口
	 */
	private int ifIndexOfRouter;
	/**
	 * 类型
	 */
	private int type;
	/**
	 * 属性
	 */
	private Map<String, String> props;
	/**
	 * 主机
	 */
	public static final int SUBNET_TYPE_HOST = 0;
	/**
	 * 二层设备
	 */
	public static final int SUBNET_TYPE_SEC = 1;
	/**
	 * 三层设备，交换机
	 */
	public static final int SUBNET_TYPE_THIRD_SWITCH = 2;
	/**
	 * 三层设备，路由器
	 */
	public static final int SUBNET_TYPE_THIRD_ROUTER = 3;
	/**
	 * 无SNMP
	 */
	public static final int SUBNET_TYPE_NOSNMP = 4;

	public SubnetDevice()
	{
		props = new HashMap<String, String>();
	}

	public void setProp(String key, String value)
	{
		props.put(key, value);
	}

	public String getProp(String key)
	{
		return props.get(key);
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getIfIndexOfRouter()
	{
		return ifIndexOfRouter;
	}

	public void setIfIndexOfRouter(int ifIndexOfRouter)
	{
		this.ifIndexOfRouter = ifIndexOfRouter;
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
