package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class DeleteBookmarksRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation("titleAssetId")
	private String titleAssetId;

	@IdentAnnocation("titleProviderId")
	private String titleProviderId;

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

	public String getTitleAssetId()
	{
		return titleAssetId;
	}

	public void setTitleAssetId(String titleAssetId)
	{
		this.titleAssetId = titleAssetId;
	}

	public String getTitleProviderId()
	{
		return titleProviderId;
	}

	public void setTitleProviderId(String titleProviderId)
	{
		this.titleProviderId = titleProviderId;
	}

}
