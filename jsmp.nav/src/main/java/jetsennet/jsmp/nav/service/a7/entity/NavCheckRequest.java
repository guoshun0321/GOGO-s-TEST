package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class NavCheckRequest
{
	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("macAddr")
	private String macAddr;

	@IdentAnnocation("smcId")
	private String smcId;

	@IdentAnnocation("userType")
	private String userType;

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getMacAddr()
	{
		return macAddr;
	}

	public void setMacAddr(String macAddr)
	{
		this.macAddr = macAddr;
	}

	public String getSmcId()
	{
		return smcId;
	}

	public void setSmcId(String smcId)
	{
		this.smcId = smcId;
	}

	public String getUserType()
	{
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}

}
