package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class SelectionResumeRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("titleProviderId")
	private String titleProviderId;

	@IdentAnnocation("titleAssetId")
	private String titleAssetId;

	@IdentAnnocation("purchaseToken")
	private String purchaseToken;

	@IdentAnnocation("fromStart")
	private boolean fromStart;

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

	public String getTitleProviderId()
	{
		return titleProviderId;
	}

	public void setTitleProviderId(String titleProviderId)
	{
		this.titleProviderId = titleProviderId;
	}

	public String getTitleAssetId()
	{
		return titleAssetId;
	}

	public void setTitleAssetId(String titleAssetId)
	{
		this.titleAssetId = titleAssetId;
	}

	public String getPurchaseToken()
	{
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken)
	{
		this.purchaseToken = purchaseToken;
	}

	public boolean isFromStart()
	{
		return fromStart;
	}

	public void setFromStart(boolean fromStart)
	{
		this.fromStart = fromStart;
	}

}
