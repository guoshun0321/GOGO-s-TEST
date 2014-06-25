package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class ValidatePlayEligibility
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation("assetId")
	private String assetId;

	@IdentAnnocation("providerId")
	private String providerId;

	@IdentAnnocation("isPreview")
	private boolean isPreview;

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

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public boolean isPreview()
	{
		return isPreview;
	}

	public void setPreview(boolean isPreview)
	{
		this.isPreview = isPreview;
	}

}
