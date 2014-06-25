package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class DeleteSavedProgramRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("purchaseToken")
	private String purchaseToken;

	@IdentAnnocation("providerId")
	private String providerId;

	@IdentAnnocation("assetId")
	private String assetId;

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

	public String getPurchaseToken()
	{
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken)
	{
		this.purchaseToken = purchaseToken;
	}

	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

}
