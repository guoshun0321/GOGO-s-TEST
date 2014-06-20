package jetsennet.jsmp.nav.service.a7.entity;

import java.util.Date;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class ChannelSelectionStartRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation("channelId")
	private String channelId;

	@IdentAnnocation("startDateTime")
	private Date startDateTime;

	@IdentAnnocation("assetId")
	private Date assetId;

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

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getChannelId()
	{
		return channelId;
	}

	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
	}

	public Date getStartDateTime()
	{
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	public Date getAssetId()
	{
		return assetId;
	}

	public void setAssetId(Date assetId)
	{
		this.assetId = assetId;
	}

}
