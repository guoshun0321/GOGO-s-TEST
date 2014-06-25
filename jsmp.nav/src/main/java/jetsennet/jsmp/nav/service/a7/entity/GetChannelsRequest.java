package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetChannelsRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation(value = "languageCode", def = A7Constants.DEF_LANG)
	private String languageCode;

	@IdentAnnocation("regionCode")
	private String regionCode;

	@IdentAnnocation(value = "startAt", def = A7Constants.DEF_START)
	private int startAt;

	@IdentAnnocation(value = "maxItems", def = A7Constants.DEF_MAX)
	private int maxItems;

	@IdentAnnocation("profile")
	private String profile;

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

	public String getLanguageCode()
	{
		return languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	public String getRegionCode()
	{
		return regionCode;
	}

	public void setRegionCode(String regionCode)
	{
		this.regionCode = regionCode;
	}

	public int getStartAt()
	{
		return startAt;
	}

	public void setStartAt(int startAt)
	{
		this.startAt = startAt;
	}

	public int getMaxItems()
	{
		return maxItems;
	}

	public void setMaxItems(int maxItems)
	{
		this.maxItems = maxItems;
	}

	public String getProfile()
	{
		return profile;
	}

	public void setProfile(String profile)
	{
		this.profile = profile;
	}

}
