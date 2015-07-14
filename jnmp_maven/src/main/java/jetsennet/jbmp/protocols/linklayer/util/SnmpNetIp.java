package jetsennet.jbmp.protocols.linklayer.util;

public class SnmpNetIp
{

	/**
	 * IP地址
	 */
	private String ip;
	/**
	 * 掩码
	 */
	private String mask;
	/**
	 * 对应的接口
	 */
	private int interf;
	/**
	 * 是否环回端口
	 */
	private boolean isLoopback;

	public SnmpNetIp()
	{
	}

	public SnmpNetIp(String ip, String mask, int interf)
	{
		this.ip = ip;
		this.mask = mask;
		this.interf = interf;
		if (SnmpDiscConstants.LOOPBACK_IP.equals(ip) && SnmpDiscConstants.MASK_C_TYPE.equals(mask))
		{
			this.isLoopback = true;
		}
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getMask()
	{
		return mask;
	}

	public void setMask(String mask)
	{
		this.mask = mask;
	}

	public boolean isLoopback()
	{
		return isLoopback;
	}

	public void setLoopback(boolean isLoopback)
	{
		this.isLoopback = isLoopback;
	}

	public int getInterf()
	{
		return interf;
	}

	public void setInterf(int interf)
	{
		this.interf = interf;
	}

}
