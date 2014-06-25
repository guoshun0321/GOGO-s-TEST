package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class AddBookmarksRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation("titleAssestId")
	private String titleAssestId;

	@IdentAnnocation("titleProviderId")
	private String titleProviderId;

	@IdentAnnocation("folderAssetId")
	private String folderAssetId;

	@IdentAnnocation("custom")
	private String custom;

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

	public String getTitleAssestId()
	{
		return titleAssestId;
	}

	public void setTitleAssestId(String titleAssestId)
	{
		this.titleAssestId = titleAssestId;
	}

	public String getTitleProviderId()
	{
		return titleProviderId;
	}

	public void setTitleProviderId(String titleProviderId)
	{
		this.titleProviderId = titleProviderId;
	}

	public String getFolderAssetId()
	{
		return folderAssetId;
	}

	public void setFolderAssetId(String folderAssetId)
	{
		this.folderAssetId = folderAssetId;
	}

	public String getCustom()
	{
		return custom;
	}

	public void setCustom(String custom)
	{
		this.custom = custom;
	}

}
