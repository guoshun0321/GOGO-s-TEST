package jetsennet.jsmp.nav.service.a7.entity;

import java.util.Date;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class AddSavedProgramsRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("assetId")
	private String assetId;

	@IdentAnnocation("providerId")
	private String providerId;

	@IdentAnnocation("folderAssetId")
	private String folderAssetId;

	@IdentAnnocation("purchaseToken")
	private String purchaseToken;

	@IdentAnnocation("rentDateTime")
	private Date rentDateTime;

	@IdentAnnocation("resumePointDisplay")
	private int resumePointDisplay;

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

	public String getFolderAssetId()
	{
		return folderAssetId;
	}

	public void setFolderAssetId(String folderAssetId)
	{
		this.folderAssetId = folderAssetId;
	}

	public String getPurchaseToken()
	{
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken)
	{
		this.purchaseToken = purchaseToken;
	}

	public Date getRentDateTime()
	{
		return rentDateTime;
	}

	public void setRentDateTime(Date rentDateTime)
	{
		this.rentDateTime = rentDateTime;
	}

	public int getResumePointDisplay()
	{
		return resumePointDisplay;
	}

	public void setResumePointDisplay(int resumePointDisplay)
	{
		this.resumePointDisplay = resumePointDisplay;
	}

}
